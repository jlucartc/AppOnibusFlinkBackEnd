package github.jlucartc

import java.sql.{Connection, DriverManager, ResultSet}
import java.util

import Model.{OnibusData, Posicao, UltimoEvento}
import org.apache.flink.api.common.state._
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.postgresql.Driver


class OnibusSaindoChegando(timeBetweenQueries : Long) extends ProcessFunction[OnibusData,(String)]{
    
    private var timeBetweenQuery : ValueState[Long] = _
    
    private var timeCounter : ValueState[Long] = _
    
    private var pointList : ListState[Posicao] = _
    
    private var onibusUltimoEvento : MapState[String,UltimoEvento] = _

    override def processElement(value: OnibusData, ctx: ProcessFunction[OnibusData,(String)]#Context, out: Collector[(String)]): Unit = {
    
        /*
       
            Se a variável 'timeBetweenQuery' não estiver inicializada, então todas as variáveis ainda não foram
            inicializadas.
       
        */
        
        if(timeBetweenQuery == null){

            timeBetweenQuery = getRuntimeContext.getState(new ValueStateDescriptor[Long]("github.jlucartc.OnibusSaindoChegando.timeBetweenQuery",classOf[Long]))
            timeCounter = getRuntimeContext.getState(new ValueStateDescriptor[Long]("github.jlucartc.OnibusSaindoChegando.timeCounter",classOf[Long]))
            pointList = getRuntimeContext.getListState[Posicao](new ListStateDescriptor[Posicao]("github.jlucartc.OnibusSaindoChegando.rowList",classOf[Posicao]))
            onibusUltimoEvento = getRuntimeContext.getMapState[String,UltimoEvento](new MapStateDescriptor[String,UltimoEvento]("OnibusSaindoChegand& onibusUltimoEvento",classOf[String],classOf[UltimoEvento]))
            timeBetweenQuery.update(timeBetweenQueries)
    
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
        if(timeBetweenQuery.value() <= timeCounter.value()){

            timeCounter.update(0)

            var closerPtName = ""
            var closerPtDistance : Double = 0
            val oldEvento : UltimoEvento = onibusUltimoEvento.get(value.deviceId+"."+value.appId)
            var startingValue = 0


            pointList.get.forEach( row => {

                if(closerPtDistance == 0 && startingValue == 0){

                    startingValue = 1
                    closerPtDistance = distancia(row.latitude,row.longitude,value.latitude,value.longitude)
                    closerPtName = row.nome

                }else if( distancia(row.latitude,row.longitude,value.latitude,value.longitude) < closerPtDistance && onibusUltimoEvento.get(value.deviceId.toString+"."+value.appId) != null && onibusUltimoEvento.get(value.deviceId+"."+value.appId).ultimoPontoId != closerPtName){

                    closerPtDistance = distancia(row.latitude,row.longitude,value.latitude,value.longitude)
                    closerPtName = row.nome

                }else if( distancia(row.latitude,row.longitude,value.latitude,value.longitude) < closerPtDistance && onibusUltimoEvento.get(value.deviceId.toString+"."+value.appId) == null ){
    
                    closerPtDistance = distancia(row.latitude,row.longitude,value.latitude,value.longitude)
                    closerPtName = row.nome
                    
                }

            })

            if(oldEvento != null && oldEvento.isSaindo){

                if(closerPtName == oldEvento.ultimoPontoId){
    
                    onibusUltimoEvento.put(value.deviceId.toString+"."+value.appId.toString,UltimoEvento(value.deviceId.toString+"."+value.appId.toString,true,closerPtName))
                    out.collect((value.deviceId+"."+value.appId,closerPtName,"saindo",ctx.timestamp()).toString())
                    
                }else{
    
                    onibusUltimoEvento.put(value.deviceId.toString+"."+value.appId.toString,UltimoEvento(value.deviceId.toString+"."+value.appId.toString,false,closerPtName))
                    out.collect((value.deviceId+"."+value.appId,closerPtName,"entrando",ctx.timestamp()).toString())
                    
                }

            }else if(oldEvento != null && !oldEvento.isSaindo){
                
                onibusUltimoEvento.put(value.deviceId.toString+"."+value.appId.toString,UltimoEvento(value.deviceId.toString+"."+value.appId.toString,true,closerPtName))
                out.collect((value.deviceId+"."+value.appId,closerPtName,"saindo",ctx.timestamp()).toString())

            }else{
    
                onibusUltimoEvento.put(value.deviceId.toString+"."+value.appId.toString,UltimoEvento(value.deviceId.toString+"."+value.appId.toString,false,closerPtName))
                out.collect((value.deviceId+"."+value.appId,closerPtName,"entrando",ctx.timestamp()).toString())
                
            }
        
        /*
        
         Se o contador tiver chegado ao valor especifidado, uma consulta ao banco é feita para atualizar os pontos
         
        */
        }else{
            
            timeCounter.update(timeCounter.value()+1)
    
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
            
            var closerPtName = ""
            var closerPtDistance : Double = 0
            val oldEvento : UltimoEvento = onibusUltimoEvento.get(value.deviceId.toString+"."+value.appId.toString)
            var startingValue = 0


            pointList.get.forEach( row => {

                if(closerPtDistance == 0 && startingValue == 0){

                    startingValue = 1
                    closerPtDistance = distancia(row.latitude,row.longitude,value.latitude,value.longitude)
                    closerPtName = row.nome

                }else if( distancia(row.latitude,row.longitude,value.latitude,value.longitude) < closerPtDistance && onibusUltimoEvento.get(value.deviceId.toString+"."+value.appId.toString).ultimoPontoId != closerPtName){

                    closerPtDistance = distancia(row.latitude,row.longitude,value.latitude,value.longitude)
                    closerPtName = row.nome

                }

            })
    
            if(oldEvento != null && oldEvento.isSaindo){
        
                if(closerPtName == oldEvento.ultimoPontoId){
            
                    onibusUltimoEvento.put(value.deviceId.toString+"."+value.appId.toString,UltimoEvento(value.deviceId.toString+"."+value.appId.toString,true,closerPtName))
                    out.collect((value.deviceId+"."+value.appId,closerPtName,"saindo",ctx.timestamp()).toString())
            
                }else{
            
                    onibusUltimoEvento.put(value.deviceId.toString+"."+value.appId.toString,UltimoEvento(value.deviceId.toString+"."+value.appId.toString,false,closerPtName))
                    out.collect((value.deviceId+"."+value.appId,closerPtName,"entrando",ctx.timestamp()).toString())
            
                }
        
            }else if(oldEvento != null && !oldEvento.isSaindo){
        
                onibusUltimoEvento.put(value.deviceId.toString+"."+value.appId.toString,UltimoEvento(value.deviceId.toString+"."+value.appId.toString,true,closerPtName))
                out.collect((value.deviceId+"."+value.appId,closerPtName,"saindo",ctx.timestamp()).toString())
        
            }else{
    
                onibusUltimoEvento.put(value.deviceId.toString+"."+value.appId.toString,UltimoEvento(value.deviceId.toString+"."+value.appId.toString,false,closerPtName))
                out.collect((value.deviceId+"."+value.appId,closerPtName,"entrando",ctx.timestamp()).toString())
                
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
