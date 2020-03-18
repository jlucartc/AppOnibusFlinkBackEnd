package github.jlucartc.Functions

import java.sql.Timestamp
import java.text.SimpleDateFormat

import github.jlucartc.Model.OnibusData
import org.apache.flink.api.common.functions.MapFunction


class S2TMapFunction extends MapFunction[String,OnibusData]{
    
    override def map(value: String): OnibusData = {
    
        OnibusData("","",1,1.0,1.0)
        
    }
}
