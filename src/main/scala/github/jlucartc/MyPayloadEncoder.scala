package github.jlucartc

import java.lang

import org.apache.flink.api.common.serialization.SerializationSchema

class MyPayloadEncoder() extends SerializationSchema[String]{
  
  override def serialize(element: String): Array[Byte] = {
  
    //val record = new ProducerRecord[Array[Byte],Array[Byte]](topico,topic.getBytes())
  
    element.getBytes()
    
  }
}
