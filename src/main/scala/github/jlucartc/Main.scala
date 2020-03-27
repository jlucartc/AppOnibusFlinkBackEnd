package github.jlucartc

import github.jlucartc.EventProducers.PlacasEventProducer

object Main {

    def main(args : Array[String]): Unit ={
    
        //val manager = new Manager()
        //manager.start()
        //val pipeline = new Pipeline()
        
        val CONTINUO = false
        val placasEventProducer = new PlacasEventProducer(!CONTINUO,10000)
        
        placasEventProducer.start()
        
        val testPipeline = new TestPipeline()

    }

}
