package github.jlucartc.Functions

import github.jlucartc.Model.Temperatura
import org.apache.flink.api.common.functions.MapFunction

class FormatarTemperaturaMapFunction extends MapFunction[String,Temperatura]{
    
    override def map(value: String): Temperatura = {
        
        val arr = value.split(",")
        
        Temperatura(arr(0).toDouble,arr(1))
        
    }
    
}
