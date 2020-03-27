package github.jlucartc.Functions

import github.jlucartc.Model.{AlertaPerseguicao, ParPerseguicao}
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor}
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector

class AgruparParPerseguidores(intervaloSegundos : Long, qPontos : Int) extends KeyedProcessFunction[String,ParPerseguicao,String]{
    
    private var mapaAlertas : MapState[String,(ParPerseguicao,Int)] = _
    
    override def processElement(value: ParPerseguicao, ctx: KeyedProcessFunction[String, ParPerseguicao, String]#Context, out: Collector[String]): Unit = {
        
        if(mapaAlertas == null){
            
            mapaAlertas = getRuntimeContext.getMapState(new MapStateDescriptor[String,(ParPerseguicao,Int)]("mapaAlertas",classOf[String],classOf[(ParPerseguicao,Int)]))
            
        }
        
        val q = mapaAlertas.get(value.perseguido+"."+value.perseguidor)
        
        if(q == null){
    
            mapaAlertas.put(value.perseguido+"."+value.perseguidor,(value,1))
            
        }else{
    
            if(value.timestamp - q._1.timestamp > intervaloSegundos*1000 ){
                mapaAlertas.put(value.perseguido+"."+value.perseguidor,(value,1))
            }else{
        
                mapaAlertas.put(value.perseguido+"."+value.perseguidor,(value,mapaAlertas.get(value.perseguido+"."+value.perseguidor)._2+1))
        
                if(mapaAlertas.get(value.perseguido+"."+value.perseguidor)._2 > qPontos){
            
                    out.collect(AlertaPerseguicao(value.perseguido,value.perseguidor,value.timestamp).toString)
            
                }
        
            }
            
        }
        
    }
}
