package github.jlucartc

import java.util.Properties
import java.util.concurrent.ArrayBlockingQueue

import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerRecord}

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
        
        producer.send(record)
        producer.close()
        
    }
    
    
    
}
