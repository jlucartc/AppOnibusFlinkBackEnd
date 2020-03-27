package github.jlucartc.KeySelectors

import github.jlucartc.Model.PlacaData
import org.apache.flink.api.java.functions.KeySelector

class PlacasKeyByPontoID extends KeySelector[PlacaData,String]{
    
    override def getKey(value: PlacaData): String = {
    
        value.pontoId
        
    }
    
}
