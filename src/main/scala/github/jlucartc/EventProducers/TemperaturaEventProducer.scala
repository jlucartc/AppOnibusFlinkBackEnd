package github.jlucartc.EventProducers

import java.text.SimpleDateFormat
import java.util.{Date, Properties, Random}

import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerRecord}

class TemperaturaEventProducer(valorMinimo : Double, valorMaximo : Double, quantidade : Int, tempoEntreEventosSegundos: Int,continuo : Boolean) extends Thread{
    
    
    override def run: Unit = {
    
        Thread.sleep(10000)
    
        val temperaturaBootstrapServers = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_BOOTSTRAP_SERVERS") match { case Some(res) => { res } case None => { "" } }
        val temperaturaZookeeperConnect = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ZOOKEEPER_CONNECT") match { case Some(res) => { res } case None => { "" } }
        val temperaturaAcks = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_TEMPERATURAEVENTPRODUCER_ACKS") match { case Some(res) => { res } case None => { "" } }
        val temperaturaTopico = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_TEMPERATURAEVENTPRODUCER_TOPICO") match { case Some(res) => { res } case None => { "" } }
        val temperaturaValueSerializer = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_TEMPERATURAEVENTPRODUCER_VALUE_SERIALIZER") match { case Some(res) => { res } case None => { "" } }
        val temperaturaKeySerializer = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_TEMPERATURAEVENTPRODUCER_KEY_SERIALIZER") match { case Some(res) => { res } case None => { "" } }
    
        val temperaturaProducerProps = new Properties()
    
        temperaturaProducerProps.setProperty("bootstrap.servers",temperaturaBootstrapServers)
        temperaturaProducerProps.setProperty("zookeeper.connect",temperaturaZookeeperConnect)
        temperaturaProducerProps.setProperty("acks",temperaturaAcks)
        temperaturaProducerProps.setProperty("topico",temperaturaTopico)
        temperaturaProducerProps.setProperty("key.serializer",temperaturaKeySerializer)
        temperaturaProducerProps.setProperty("value.serializer",temperaturaValueSerializer)
        
        gerarEventos(temperaturaProducerProps)
        
    }
    
    def gerarEventos(props: Properties): Unit = {
        
        var nextTimestamp = new Date().getTime;
        
        var counter = 0;
        
        while(counter < quantidade){
            
            val rand = new Random();
            
            val valorTemperatura = rand.nextDouble()*(valorMaximo-valorMinimo) + valorMinimo
            
            val producer : Producer[String,String] = new KafkaProducer[String,String](props)
            
            val timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nextTimestamp)
            
            val msg = valorTemperatura.toString+","+timestamp
            
            val record: ProducerRecord[String,String] = new ProducerRecord[String,String](props.getProperty("topico"),msg)
            
            producer.send(record)
            
            producer.close()
            
            nextTimestamp = nextTimestamp + tempoEntreEventosSegundos;
            
            counter = counter + 1;
            
        }
        
    }
    
    
}
