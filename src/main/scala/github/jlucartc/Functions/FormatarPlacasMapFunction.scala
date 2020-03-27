package github.jlucartc.Functions

import java.text.{DateFormat, SimpleDateFormat}
import java.util.Date

import github.jlucartc.Model.PlacaData
import org.apache.flink.api.common.functions.MapFunction

class FormatarPlacasMapFunction() extends MapFunction[String,PlacaData]{
    
    override def map(value: String): PlacaData = {
    
        val arr = value.split(",")
    
        val formatter: DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date: Date = formatter.parse(arr(1));
        val timeStampDate: Long = date.getTime
        
        PlacaData(arr(0),timeStampDate,arr(2))
    
    }

}
