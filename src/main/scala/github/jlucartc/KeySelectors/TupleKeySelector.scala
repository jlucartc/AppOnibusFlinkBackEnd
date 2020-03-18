package github.jlucartc.KeySelectors

import github.jlucartc.Model.OnibusData
import org.apache.flink.api.java.functions.KeySelector

class TupleKeySelector() extends KeySelector[OnibusData,String]{
    
    override def getKey(value: OnibusData): String = {
    
        value.deviceId.toString+"."+value.appId.toString
        
    }
}
