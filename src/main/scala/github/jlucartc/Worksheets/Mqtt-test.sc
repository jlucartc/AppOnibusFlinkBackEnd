import java.util.Properties
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ArrayBlockingQueue
import java.util.{Properties, UUID}
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import github.jlucartc.MQTTMessageConsumer

var messageBuffer : ArrayBlockingQueue[String] = new ArrayBlockingQueue[String](1000)


val mqttUser = sys.env.get("GITHUB_JLUCARTC_MQTTMESSAGECONSUMER_USER") match { case Some(res) => { res } case None => { "" } }
val mqttPassword = sys.env.get("GITHUB_JLUCARTC_MQTTMESSAGECONSUMER_PASSWORD") match { case Some(res) => { res } case None => { "" } }
val mqttUri = sys.env.get("GITHUB_JLUCARTC_MQTTMESSAGECONSUMER_URI") match { case Some(res) => { res } case None => { "" } }
val mqttTopic = sys.env.get("GITHUB_JLUCARTC_MQTTMESSAGECONSUMER_TOPIC") match { case Some(res) => { res } case None => { "" } }


val mqttProps : Properties = new Properties()
mqttProps.setProperty("user","bus_gps_data")
mqttProps.setProperty("password","ttn-account-v2.IFJLbrAmGz8RoPRF-_DF0MRiIp-hmtB2it2HRdn9_yA")
mqttProps.setProperty("uri",mqttUri)
mqttProps.setProperty("topic","+/devices/+/up")

println(mqttProps.toString)

println("Run...")

val user: String = mqttProps.getProperty("user")
val password: Array[Char] = mqttProps.getProperty("password").toCharArray
val publisher: MqttClient = new MqttClient(mqttProps.getProperty("uri"), UUID.randomUUID().toString, new MemoryPersistence())
val topic: String = mqttProps.getProperty("topic")

val options = new MqttConnectOptions()

options.setUserName(user)
options.setPassword(password)

val callback = new MqttCallback {
    
    
    @throws(classOf[Exception])
    override def messageArrived(topic: String, message: MqttMessage): Unit = {
            
            println("Message received")
            println("Receiving Data, Topic : %s, Message : %s".format(topic, message.toString))
            messageBuffer.put(message.toString)
        
    }
    
    override def connectionLost(cause: Throwable): Unit = {
        println("Connection lost. Closing subscriber...")
        publisher.close()
    }
    
    override def deliveryComplete(token: IMqttDeliveryToken): Unit = {
        println("Complete")
    }
}

println("Setting callback...")

publisher.setCallback(callback)

println("Connecting...")

try {
    
    publisher.connect(options)
    
}catch{
    
    case e: MqttSecurityException =>
        e.printStackTrace()
    case e: MqttException =>
        println("Conection error")
        e.printStackTrace()
    
}

println("Subscribing")

try {
    
    publisher.subscribe(topic)
    
}catch{
    
    case e : MqttException =>
        println("Subscription error")
        e.printStackTrace()
    
}
