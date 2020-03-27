package github.jlucartc.Functions

import java.util
import java.util.ArrayList

import github.jlucartc.Model.{ParPerseguicao, PlacaData}
import org.apache.flink.api.common.state.{ListState, ListStateDescriptor, MapState, MapStateDescriptor}
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector

class GerarParPerseguidores(intervaloSegundos: Int) extends KeyedProcessFunction[String,PlacaData,ParPerseguicao]{
    
    private var listaUltimosVeiculos : ListState[PlacaData] = _
    
    override def processElement(value: PlacaData, ctx: KeyedProcessFunction[String, PlacaData, ParPerseguicao]#Context, out: Collector[ParPerseguicao]): Unit = {
    
        if(listaUltimosVeiculos == null){
            
            listaUltimosVeiculos = getRuntimeContext.getListState(new ListStateDescriptor[PlacaData]("listaUltimosVeiculos",classOf[PlacaData]))
            
        }
    
        val lista = listaUltimosVeiculos.get()
        
        var length = 0
        
        lista.forEach( item => {
            
            println(length.toString)
            
            length = length + 1
            
        })
        
        if( length > 0){

            val novaLista = new util.ArrayList[PlacaData]()
            
            lista.forEach( item => {
                
                if((value.timestamp - item.timestamp ) <= intervaloSegundos*1000 ){
                    
                    novaLista.add(item)
                    
                    out.collect(ParPerseguicao(item.placa,value.placa,value.timestamp))
                    
                }
                
            })
            
            novaLista.add(value)
            
            listaUltimosVeiculos.update(novaLista)
            
        }else{
            
            listaUltimosVeiculos.add(value)
            
        }
        
    }

}
