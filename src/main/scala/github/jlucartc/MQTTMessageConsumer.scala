package github.jlucartc

import java.util.concurrent.ArrayBlockingQueue
import java.util.{Properties, UUID}

import org.eclipse.paho.client.mqttv3._

class MQTTMessageConsumer(buffer: ArrayBlockingQueue[String],props : Properties) extends Thread {
    
    /*

    Busca mensagens do broker MQTT e adiciona no buffer de mensagens
    
    */
    
    
    override def run(): Unit = {
        
        println("Running...")
        
        val user : String = props.getProperty("user")
        val password : Array[Char] = props.getProperty("password").toCharArray
        val publisher : MqttClient = new MqttClient(props.getProperty("uri"),UUID.randomUUID().toString);
        val topic : String = props.getProperty("topic")
        val options = new MqttConnectOptions()
        
        options.setUserName(user)
        options.setPassword(password)
        
        try {
            
            publisher.connect(options)
            
        }catch{
            
            case e: MqttSecurityException =>
                e.printStackTrace()
            case e: MqttException =>
                println("Conection error")
                e.printStackTrace()
            
        }
        
        try {
            
            publisher.subscribe(topic)
        }catch{
            
            case e : MqttException =>
                println("Subscription error")
                e.printStackTrace()
            
        }
        
        val callback = new MqttCallback {
            
            
            @throws(classOf[Exception])
            override def messageArrived(topic: String, message: MqttMessage): Unit = {
                
                if(message.toString == "close"){
    
                    println("Closing MQTT subscriber connection...")
                    publisher.close()
                    
                }else{
    
                    println("Receiving Data, Topic : %s, Message : %s".format(topic, message))
                    buffer.put(message.toString)
                    
                }
                
            }
            
            override def connectionLost(cause: Throwable): Unit = {
                println("Connection lost. Closing subscriber...")
                publisher.close()
            }
            
            override def deliveryComplete(token: IMqttDeliveryToken): Unit = {
                //println("Complete")
            }
        }
        
        publisher.setCallback(callback)
        
        
    }
    
    
}
