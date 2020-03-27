package github.jlucartc.EventProducers

import java.util
import java.util.Properties

import github.jlucartc.Model.OnibusData

class OnibusEventProducer extends Thread{

    override def run() ={
        
        val onibusBootstrapServers = sys.env.get("") match { case Some(res) => { res } case None => { "" } }
        val onibusZookeeperConnect = sys.env.get("") match { case Some(res) => { res } case None => { "" } }
        val onibusAcks = sys.env.get("") match { case Some(res) => { res } case None => { "" } }
        
        val onibusProducerProps = new Properties()
        
        onibusProducerProps.setProperty("bootstrap.servers",onibusBootstrapServers)
        onibusProducerProps.setProperty("zookeeper.connect",onibusZookeeperConnect)
        onibusProducerProps.setProperty("acks",onibusAcks)
        
    }
    
    def generateEvent = {
    
        val listaDeOnibus = new util.ArrayList[String]
        
        listaDeOnibus.add("A")
        listaDeOnibus.add("B")
        listaDeOnibus.add("C")
        
    }

}
