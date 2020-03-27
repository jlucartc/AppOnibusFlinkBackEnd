package github.jlucartc

import java.sql.{Connection, DriverManager, ResultSet}
import java.util

import Model.{OnibusData, Posicao, UltimoEvento}
import org.apache.flink.api.common.state._
import org.apache.flink.streaming.api.functions.{KeyedProcessFunction, ProcessFunction}
import org.apache.flink.util.Collector
import org.postgresql.Driver


class OnibusSaindoChegando(timeBetweenQueries : Long) extends KeyedProcessFunction[String,OnibusData,String]{
    
    private var timeBetweenQuery : ValueState[Long] = _
    
    private var timeCounter : ValueState[Long] = _
    
    private var pointList : ListState[Posicao] = _
    
    private var onibusUltimoEvento : ValueState[UltimoEvento] = _
    
    override def processElement(value: OnibusData, ctx: KeyedProcessFunction[String,OnibusData,String]#Context, out: Collector[String]): Unit = {
        
        println(value.deviceId+"."+value.appId)
        
        /*
       
            Se a variável 'timeBetweenQuery' não estiver inicializada, então todas as variáveis ainda não foram
            inicializadas.
       
        */
        
        if(timeBetweenQuery == null){
            
            timeBetweenQuery = getRuntimeContext.getState(new ValueStateDescriptor[Long]("github.jlucartc.OnibusSaindoChegando.timeBetweenQuery",classOf[Long]))
            timeCounter = getRuntimeContext.getState(new ValueStateDescriptor[Long]("github.jlucartc.OnibusSaindoChegando.timeCounter",classOf[Long]))
            pointList = getRuntimeContext.getListState[Posicao](new ListStateDescriptor[Posicao]("github.jlucartc.OnibusSaindoChegando.rowList",classOf[Posicao]))
            onibusUltimoEvento = getRuntimeContext.getState[UltimoEvento](new ValueStateDescriptor[UltimoEvento]("github.jlucartc.OnibusSaindoChegando.onibusUltimoEvento",classOf[UltimoEvento]))
            timeBetweenQuery.update(timeBetweenQueries)
            timeCounter.update(0)
            pointList.update(new util.ArrayList[Posicao]())
            onibusUltimoEvento.update(null)
            
            // Faz requisição
            val res = queryDB()
            
            // Cria lista para receber novos pontos
            val newList = new util.ArrayList[Posicao]()
            
            // Coloca pontos dentro da lista
            while(res != null && res.next()){
                
                println(Posicao(res.getString("nome"),res.getDouble("latitude"),res.getDouble("longitude")))
                
                newList.add(Posicao(res.getString("nome"),res.getDouble("latitude"),res.getDouble("longitude")))
                
            }
            
            // Atualiza pontos
            pointList.update(newList)
            
        }
        
        /*
        
         Se o contador não tiver chegado ao valor especificado, procede normalmente
        
        */
        if(timeBetweenQuery.value() <= timeCounter.value()) {
    
            timeCounter.update(0)
    
            // Faz requisição
            val res = queryDB()
    
            // Cria lista para receber novos pontos
            val newList = new util.ArrayList[Posicao]()
    
            // Coloca pontos dentro da lista
            while(res != null && res.next()){
        
                println(Posicao(res.getString("nome"),res.getDouble("latitude"),res.getDouble("longitude")))
        
                newList.add(Posicao(res.getString("nome"),res.getDouble("latitude"),res.getDouble("longitude")))
        
            }
    
            // Atualiza pontos
            pointList.update(newList)
            
        }
            
        timeCounter.update(timeCounter.value()+1)
        
        var closerPtName : String = null
        var closerPtDistance : Double = 0
        val oldEvento : UltimoEvento = onibusUltimoEvento.value
        var startingValue = 0
        
        pointList.get.forEach( row => {
            
            if(startingValue == 0){
                
                startingValue = 1
                closerPtDistance = distancia(value.latitude,value.longitude,row.latitude,row.longitude)
                closerPtName = row.nome
                
            }else{
                
                if(distancia(value.latitude,value.longitude,row.latitude,row.longitude) <= closerPtDistance){
    
                    closerPtDistance = distancia(value.latitude,value.longitude,row.latitude,row.longitude)
                    closerPtName = row.nome
                    
                }
                
            }
            
        })
        
        if(onibusUltimoEvento.value() == null){
            
            if(closerPtName != null){
                
                val ultimoEvento = UltimoEvento(value.appId+"."+value.deviceId,false,closerPtName)
                
                onibusUltimoEvento.update(ultimoEvento)
                out.collect(ultimoEvento.toString)
                
            }
            
        }else{
        
            if(closerPtName != null){
            
                if(closerPtName != onibusUltimoEvento.value().ultimoPontoId){
    
                    if(onibusUltimoEvento.value().isSaindo){
    
                        val ultimoEvento = UltimoEvento(value.appId+"."+value.deviceId,false,closerPtName)
    
                        onibusUltimoEvento.update(ultimoEvento)
                        out.collect(ultimoEvento.toString)
                        
                    }else{
    
                        val ultimoEvento1 = UltimoEvento(value.appId+"."+value.deviceId,true,onibusUltimoEvento.value().ultimoPontoId)
                        val ultimoEvento2 = UltimoEvento(value.appId+"."+value.deviceId,false,closerPtName)
    
                        onibusUltimoEvento.update(ultimoEvento2)
                        out.collect(ultimoEvento1.toString)
                        out.collect(ultimoEvento2.toString)
                        
                    }
                
                }
            
            }
            
        }
        
    }
    
    
    def distancia(latitude: Double, longitude: Double, latitude1: Double, longitude1: Double): Double = {

        val R = 6371 // Radius of the earth
        val latDistance = Math.toRadians(latitude - latitude1)
        val lonDistance = Math.toRadians(longitude - longitude1)
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        var distance = R * c * 1000 // convert to meters
        val height = 0
        distance = Math.pow(distance, 2) + Math.pow(height, 2)
        Math.sqrt(distance)
        
    }
    
    def queryDB(): ResultSet = {
    
        
        println("Querying...")
        
        var connection : Connection = null
    
        val url = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_POSTGRES_URL")  match {

            case Some(res) => { res }
            case None => { "" }

        }
        val driver = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_POSTGRES_DRIVER") match {

            case Some(res) => { res }
            case None => { "" }

        }
        val username = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_POSTGRES_USERNAME") match {

            case Some(res) => { res }
            case None => { "" }

        }
        val password = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_POSTGRES_PASSWORD") match {

            case Some(res) => { res }
            case None => { "" }

        }
        
        val table = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_POSTGRES_APP_TABLE") match {
    
            case Some(res) => { res }
            case None => { "" }
    
        }

        var resultSet : ResultSet = null
        val query = "SELECT * FROM "+table
    
        try {
            // make the connection
            Class.forName(driver)
            connection = DriverManager.getConnection(url, username, password)
        
            // create the statement, and run the select query
            val statement = connection.createStatement()
            resultSet = statement.executeQuery(query)
            
        } catch {
            case e: Exception => e.printStackTrace
        }
        
        if(connection != null){
    
            connection.close()
            
        }
        
        resultSet
        
        
    }
}
