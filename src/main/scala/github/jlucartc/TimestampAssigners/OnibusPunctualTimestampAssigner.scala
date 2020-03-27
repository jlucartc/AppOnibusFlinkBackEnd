package github.jlucartc.TimestampAssigners

import java.sql.Timestamp

import github.jlucartc.Model.OnibusData
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks
import org.apache.flink.streaming.api.watermark.Watermark

class OnibusPunctualTimestampAssigner extends AssignerWithPunctuatedWatermarks[OnibusData]{
    
    var counter : Int = 0
    var eventsUntilNextWatermark : Int = 0
    var lastTimestamp : Long = _
    
    override def checkAndGetNextWatermark(lastElement: OnibusData, extractedTimestamp: Long): Watermark = {
    
        if(counter == eventsUntilNextWatermark){
            
            counter = 0
            
            var time = new Timestamp(lastTimestamp)
            println("Watermark: ",time.toString)
            new Watermark(lastTimestamp)
            
        }else{
            
            null
        
        }
    
    }
    
    override def extractTimestamp(element: OnibusData, previousElementTimestamp: Long): Long = {
    
        
        lastTimestamp = Math.max(lastTimestamp,element.timestamp)
        
        //counter = counter + 1
        
        element.timestamp
    
    }
}
