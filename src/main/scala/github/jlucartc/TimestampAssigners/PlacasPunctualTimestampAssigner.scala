package github.jlucartc.TimestampAssigners

import github.jlucartc.Model.PlacaData
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks
import org.apache.flink.streaming.api.watermark.Watermark

class PlacasPunctualTimestampAssigner extends AssignerWithPunctuatedWatermarks[PlacaData]{
    
    override def checkAndGetNextWatermark(lastElement: PlacaData, extractedTimestamp: Long): Watermark = {
    
        new Watermark(extractedTimestamp)
    
    }
    
    override def extractTimestamp(element: PlacaData, previousElementTimestamp: Long): Long = {
        
        element.timestamp
        
    }
}
