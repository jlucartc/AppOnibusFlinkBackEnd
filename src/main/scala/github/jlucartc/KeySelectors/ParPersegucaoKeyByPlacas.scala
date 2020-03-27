package github.jlucartc.KeySelectors

import github.jlucartc.Model.ParPerseguicao
import org.apache.flink.api.java.functions.KeySelector

class ParPersegucaoKeyByPlacas() extends KeySelector[ParPerseguicao,String]{
    override def getKey(value: ParPerseguicao): String = {
        
        value.perseguido+"."+value.perseguidor
        
    }
}
