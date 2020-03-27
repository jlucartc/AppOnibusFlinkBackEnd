package github.jlucartc.Functions

import java.sql.Timestamp
import java.text.{DateFormat, SimpleDateFormat}
import java.util.{Base64, Date}

import github.jlucartc.Model.OnibusData
import org.apache.flink.api.common.functions.MapFunction

class FormatarOnibusMapFunction() extends MapFunction[String,OnibusData]{
    override def map(value: String): OnibusData = {
        
        val fieldsArray = value.substring(1,value.length-1).split(',')
        
        val isRetry : String = fieldsArray(5).split(':')(0)
        
        var metadata: String = ""
        
        var payload: String = ""
        
        var rawFieldsArray : Array[String] = Array("")
        
        if(isRetry != "\"is_retry\""){
            
            val newField = "\"is_retry\":false"
            
            payload = fieldsArray(5)
            
            metadata = fieldsArray(6)
            
            val coordsBytes = payload.split(':')(1).replaceAllLiterally("\"","").getBytes()
            
            val coordsStr  = new String(Base64.getDecoder.decode(coordsBytes)).replaceAllLiterally(" ","")
            
            val coords = coordsStr.slice(0,coordsStr.length-1).split(';')
            
            val leftFields = fieldsArray.slice(0,5)
            
            rawFieldsArray = leftFields ++ Array(newField,payload,"\"latitude\":"+coords(0),"\"longitude\":"+coords(1))
            
        }else{
            
            payload = fieldsArray(6)
            
            metadata = fieldsArray(7)
            
            rawFieldsArray = fieldsArray.slice(0,6) ++ Array(fieldsArray(6),"\"latitude\":0","\"longitude\":0")
            
        }
        
        val timestamp = metadata.replaceAllLiterally("\"metadata\":{","")
        
        rawFieldsArray = (rawFieldsArray ++ Array(timestamp)).mkString(",").replaceAllLiterally("{","").split(',')
        
        val formatter: DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val date: Date = formatter.parse(rawFieldsArray(9).split("\\\"time\\\":")(1).replaceAllLiterally("\"",""));
        val timeStampDate: Long = date.getTime
        
        OnibusData(rawFieldsArray(0).split(':')(1).replaceAllLiterally("\"",""),rawFieldsArray(1).split(':')(1).replaceAllLiterally("\"",""),timeStampDate,rawFieldsArray(7).split(':')(1).replaceAllLiterally("\"","").toDouble,rawFieldsArray(8).split(':')(1).replaceAllLiterally("\"","").toDouble)
        
    }
}