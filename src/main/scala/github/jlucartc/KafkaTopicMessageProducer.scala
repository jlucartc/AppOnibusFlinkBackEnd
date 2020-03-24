package github.jlucartc

import java.util.Properties
import java.util.concurrent.{ArrayBlockingQueue, ExecutionException}

import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerRecord, RecordMetadata}

/*

 Remove mensagens do buffer e insere no tópico do Kafka

*/

class KafkaTopicMessageProducer(buffer : ArrayBlockingQueue[String], props: Properties, topic : String) extends Thread{
    
    override def run(): Unit ={
        
        checkBuffer()
        
    }
    
    def checkBuffer(): Unit = {
        
        val msg : String = buffer.take()
        
        publicarMensagem(msg)
        
        checkBuffer()
        
    }
    
    def publicarMensagem(msg : String) {
        
        val producer : Producer[String,String] = new KafkaProducer(props)
        
        val record : ProducerRecord[String,String] = new ProducerRecord(topic,msg)
        
        val status = producer.send(record)
        
        try {
            
            val metadata = status.get()
            
            if(metadata != null && metadata.getClass == classOf[RecordMetadata]){
                
                println(metadata.toString)
                
            }
            
        }catch{
            case e : InterruptedException => { println("Kafka producer error"); e.printStackTrace() }
            case e : ExecutionException => { println("Kafka producer error"); e.printStackTrace() }
        }
        producer.close()
        
    }
    
    
    
}
