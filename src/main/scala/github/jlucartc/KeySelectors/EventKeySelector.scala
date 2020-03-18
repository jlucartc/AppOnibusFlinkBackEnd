package github.jlucartc.KeySelectors

import org.apache.flink.api.java.functions.KeySelector

class EventKeySelector() extends KeySelector[(Int,Int,String,String,Long),String]{
    override def getKey(value: (Int, Int, String, String, Long)): String = {
    
        value._3+"."+value._4
    
    }
}
