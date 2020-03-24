package github.jlucartc

import java.util.Properties
import java.util.concurrent.ArrayBlockingQueue

class Manager extends Thread {

    private var messageBuffer : ArrayBlockingQueue[String] = new ArrayBlockingQueue[String](1000)
    
    override def run(): Unit = {
    
        val props : Properties = new Properties()
        val mqttProps : Properties = new Properties()
        
        val bootstrapServers = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_BOOTSTRAP_SERVERS") match { case Some(res) => { res } case None => { "" } }
        val keySerializer = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_KEY_SERIALIZER") match { case Some(res) => { res } case None => { "" } }
        val valueSerializer  = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_VALUE_SERIALIZER") match { case Some(res) => { res } case None => { "" } }
        val producerTopic = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_SINK_TOPIC") match { case Some(res) => { res } case None => { "" } }
        val acks = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_ACKS") match { case Some(res) => { res } case None => { "" } }
        
        
        val mqttUser = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_MQTTMESSAGECONSUMER_USER") match { case Some(res) => { res } case None => { "" } }
        val mqttPassword = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_MQTTMESSAGECONSUMER_PASSWORD") match { case Some(res) => { res } case None => { "" } }
        val mqttUri = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_MQTTMESSAGECONSUMER_URI") match { case Some(res) => { res } case None => { "" } }
        val mqttConsumerTopic = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_MQTTMESSAGECONSUMER_TOPIC") match { case Some(res) => { res } case None => { "" } }
        
    
        props.put("bootstrap.servers",bootstrapServers)
        props.put("key.serializer",keySerializer)
        props.put("value.serializer",valueSerializer)
        props.put("acks",acks)

        mqttProps.setProperty("user",mqttUser)
        mqttProps.setProperty("password",mqttPassword)
        mqttProps.setProperty("uri",mqttUri)
        mqttProps.setProperty("topic",mqttConsumerTopic)
        
    
        val mqttSubscriber : MQTTMessageConsumer = new MQTTMessageConsumer(messageBuffer,mqttProps)
        
        val bufferConsumer : KafkaTopicMessageProducer = new KafkaTopicMessageProducer(messageBuffer,props,producerTopic)
        
        mqttSubscriber.start()
        bufferConsumer.start()
        
    }
    
}
