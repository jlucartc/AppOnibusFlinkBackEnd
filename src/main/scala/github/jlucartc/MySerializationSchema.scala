package github.jlucartc

import java.lang

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.flink.api.scala._
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord

class MySerializationSchema(topico : String) extends KafkaSerializationSchema[String]{

    override def serialize(element: String, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
    
        val record = new ProducerRecord[Array[Byte],Array[Byte]](topico,element.getBytes())
        
        record
        
    }

}
