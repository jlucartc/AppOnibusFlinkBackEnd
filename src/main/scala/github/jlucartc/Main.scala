package github.jlucartc

import github.jlucartc.EventProducers.{PlacasEventProducer, TemperaturaEventProducer}

object Main {

    def main(args : Array[String]): Unit ={
    
        //val manager = new Manager()
        //manager.start()
        //val pipeline = new Pipeline()
        
        val CONTINUO = false
        //val placasEventProducer = new PlacasEventProducer(!CONTINUO,20)
        val temperaturaEventProducer = new TemperaturaEventProducer(-30,60,20,30,!CONTINUO)
    
        temperaturaEventProducer.start()
        //placasEventProducer.start()
    
        //val testPipeline = new TestPipeline()

    }

}
