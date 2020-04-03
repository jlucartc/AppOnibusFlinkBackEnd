package github.jlucartc.EventProducers

import java.text.SimpleDateFormat
import java.util
import java.util.{Date, Properties, Random, UUID}

import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerRecord}

class PlacasEventProducer(modo: Boolean, quantidadeEventos : Int) extends Thread{

    private val listaPlacas = new util.ArrayList[String]
    private val listaPontos = new util.ArrayList[Int]
    private val baseTimestamp = new Date()
    
    override def run = {
    
        Thread.sleep(10000)
        
        val placasBootstrapServers = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_BOOTSTRAP_SERVERS") match { case Some(res) => { res } case None => { "" } }
        val placasZookeeperConnect = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ZOOKEEPER_CONNECT") match { case Some(res) => { res } case None => { "" } }
        val placasAcks = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_PLACASEVENTPRODUCER_ACKS") match { case Some(res) => { res } case None => { "" } }
        val placasTopico = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_PLACASEVENTPRODUCER_TOPICO") match { case Some(res) => { res } case None => { "" } }
        val placasValueSerializer = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_PLACASEVENTPRODUCER_VALUE_SERIALIZER") match { case Some(res) => { res } case None => { "" } }
        val placasKeySerializer = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_PLACASEVENTPRODUCER_KEY_SERIALIZER") match { case Some(res) => { res } case None => { "" } }
        
        val placasProducerProps = new Properties()
        
        placasProducerProps.setProperty("bootstrap.servers",placasBootstrapServers)
        placasProducerProps.setProperty("zookeeper.connect",placasZookeeperConnect)
        placasProducerProps.setProperty("acks",placasAcks)
        placasProducerProps.setProperty("topico",placasTopico)
        placasProducerProps.setProperty("key.serializer",placasKeySerializer)
        placasProducerProps.setProperty("value.serializer",placasValueSerializer)
        
        val segundosEntreTimestamps = 30
        val quantidadePlacas = 3
        val quantidadePontos = 5
        
        gerarPlacas(quantidadePlacas)
        gerarPontos(quantidadePontos)
        
        if(modo){
            
            gerarEventos(quantidadeEventos,segundosEntreTimestamps,placasProducerProps)
            
        }else{
            
            val intervaloEntreMensagensMilisegundos = 500
            
            gerarEventosContinuo(intervaloEntreMensagensMilisegundos,segundosEntreTimestamps,placasProducerProps)
            
        }
        
        
    }
    
    def gerarEventos(quantidade: Int, segundosEntreTimestamps : Int, props : Properties): Unit = {
        
        val step = segundosEntreTimestamps*1000
        
        var nextTimestamp = baseTimestamp.getTime
        
        for(i <- 0 to quantidade-1){
            
            val rand = new Random
            
            val rPlaca = rand.nextInt(listaPlacas.size());
            val rPonto = rand.nextInt(listaPontos.size())
            val timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nextTimestamp)
            val msg = listaPlacas.get(rPlaca)+","+timestamp+","+listaPontos.get(rPonto).toString
            
            val producer : Producer[String,String]= new KafkaProducer(props)
            
            val record = new ProducerRecord[String,String](props.getProperty("topico"),msg)
    
            producer.send(record)
            producer.close()
            
            nextTimestamp = nextTimestamp + step
            
        }
    
    }
    
    def gerarEventosContinuo(intervaloEntreMensagensMilisegundos : Long, segundosEntreTimestamps: Int, props : Properties) = {
    
        val step = segundosEntreTimestamps*1000
    
        var nextTimestamp = baseTimestamp.getTime
        
        while(true){
            
            val rand = new Random
        
            val rPlaca = rand.nextInt(listaPlacas.size());
            val rPonto = rand.nextInt(listaPontos.size())
            val timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nextTimestamp)
            val msg = listaPlacas.get(rPlaca)+","+timestamp+","+listaPontos.get(rPonto).toString
        
            val producer : Producer[String,String]= new KafkaProducer(props)
        
            val record = new ProducerRecord[String,String](props.getProperty("topico"),msg)
            
            producer.send(record)
            producer.close()
        
            nextTimestamp = nextTimestamp + step
        
            Thread.sleep(intervaloEntreMensagensMilisegundos)
            
        }
        
    }
    
    def gerarPlacas(quantidade : Int): Unit = {
        
        for( i <- 0 to quantidade-1){
            
            var placa = UUID.randomUUID().toString.substring(0,7)
            
            while(listaPlacas.indexOf(placa) >= 0){
    
                placa = UUID.randomUUID().toString.substring(0,7)
                
            }
            
            listaPlacas.add(placa)
            
        }
        
    }
    
    def gerarPontos(quantidade : Int) : Unit = {
        
        for(i <- 0 to quantidade-1){
            
            listaPontos.add(i)
            
        }
        
    }
    
}
