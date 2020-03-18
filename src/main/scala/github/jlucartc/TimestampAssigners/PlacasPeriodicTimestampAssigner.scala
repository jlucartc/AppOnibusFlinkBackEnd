package github.jlucartc.TimestampAssigners

import java.sql.Timestamp
import java.text.SimpleDateFormat

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.watermark.Watermark

class PlacasPeriodicTimestampAssigner extends AssignerWithPeriodicWatermarks[(String,Double,Double,String,Int,Int)]{
   
    // Limite de atraso - 10000 ms
    private var limite = 10000
    private var timestampMaisRecente : Long = 0
    
    override def getCurrentWatermark: Watermark = {
        
        if((timestampMaisRecente - limite) < 0){
            new Watermark(0)
        }else{
            new Watermark(timestampMaisRecente-limite)
        }
        
    }
    
    override def extractTimestamp(element: (String,Double,Double,String,Int,Int), previousElementTimestamp: Long): Long = {
        
        val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = formatter.parse(element._4)
        val timestampAtual = new Timestamp(date.getTime).getTime
        timestampMaisRecente = Math.max(timestampMaisRecente,timestampAtual)
        
        timestampAtual
    
    }
}
