package github.jlucartc

import java.util.Properties
import java.util.concurrent.TimeUnit

import github.jlucartc.Functions.{AgruparParPerseguidores, FormatarOnibusMapFunction, FormatarPlacasMapFunction, GerarParPerseguidores, OnibusSaindoChegando}
import github.jlucartc.KeySelectors.{ParPersegucaoKeyByPlacas, PlacasKeyByPontoID, TupleKeySelector}
import github.jlucartc.Model.{AlertaPerseguicao, OnibusData, ParPerseguicao, PlacaData}
import github.jlucartc.TimestampAssigners.{OnibusPunctualTimestampAssigner, PlacasPunctualTimestampAssigner}
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.core.fs.FileSystem
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}

class TestPipeline {
    
    
    // Variaveis de configuração da Pipeline
    
    val pipelineParallelism = 1
    val pipelineCheckPointingTimeInterval = 10
    val pipelineRestartAttempts = 1
    val pipelineTimeBeforeRetry = 10000
    
    
    // Setando configuraões da Pipeline
    
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.setParallelism(pipelineParallelism)
    env.enableCheckpointing(pipelineCheckPointingTimeInterval)
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(pipelineRestartAttempts,org.apache.flink.api.common.time.Time.of(pipelineTimeBeforeRetry,TimeUnit.MILLISECONDS)))
    
    
    // Variáveis de configuração de Sinks, Sources e Operadores
    
    val bootstrapServers = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_BOOTSTRAP_SERVERS") match { case Some(res) => {res} case None => { "" } }
    val zookeeperConnect = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ZOOKEEPER_CONNECT") match { case Some(res) => {res} case None => { "" } }
    
    
    // Variáveis de configuração do evento 'Onibus'
    
    val onibusTimeBetweenQueries = 100
    
    val onibusProducerKeySerializer = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_KEY_SERIALIZER") match { case Some(res) => {res} case None => { "" } }
    val onibusProducerValueSerializer = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_VALUE_SERIALIZER") match { case Some(res) => {res} case None => { "" } }
    val onibusProducerAcks = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_ACKS") match { case Some(res) => {println("ACKS: "+res); res } case None => { "" } }
    val onibusProducerTopic = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_TOPIC") match { case Some(res) => {res} case None => { "" } }
    val onibusProducerTransactionTimeout = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_TRANSACTION_TIMEOUT") match { case Some(res) => {res} case None => { "" } }
    
    val onibusConsumerGroupId = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_CONSUMER_GROUP_ID")  match { case Some(res) => {res} case None => { "" } }
    val onibusConsumerAutoOffsetReset = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_CONSUMER_AUTO_OFFSET_RESET")  match { case Some(res) => {res} case None => { "" } }
    val onibusConsumerEnableAutoCommit = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT")  match { case Some(res) => {res} case None => { "" } }
    val onibusConsumerTopic = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_CONSUMER_TOPIC") match { case Some(res) => {res} case None => { "" } }
    
    
    // Criando Properties de Sinks e Sources do evento 'Onibus'
    
    var onibusConsumerProps : Properties = new Properties()
    onibusConsumerProps.setProperty("bootstrap.servers",bootstrapServers)
    onibusConsumerProps.setProperty("zookeeper.connect",zookeeperConnect)
    onibusConsumerProps.setProperty("group.id",onibusConsumerGroupId)
    onibusConsumerProps.setProperty("auto.offset.reset",onibusConsumerAutoOffsetReset)
    onibusConsumerProps.setProperty("enable.auto.commit",onibusConsumerEnableAutoCommit)
    
    var onibusProducerProps : Properties = new Properties()
    onibusProducerProps.put("bootstrap.servers",bootstrapServers)
    onibusProducerProps.put("key.serializer",onibusProducerKeySerializer)
    onibusProducerProps.put("value.serializer",onibusProducerValueSerializer)
    onibusProducerProps.put("acks",onibusProducerAcks)
    onibusProducerProps.put("transaction.timeout.ms",onibusProducerTransactionTimeout)
    
    
    // Criando e adicionando Source do evento 'Onibus'
    
    var onibusRawStream : DataStream[String] = env
    .addSource(new FlinkKafkaConsumer[String](onibusConsumerTopic,new SimpleStringSchema(),onibusConsumerProps))
    .uid("OnibusKafkaConsumerInput")
    
    
    // Criando DataStream do evento 'Onibus' e adicionando operadores
    
    var onibusFormattedStream : DataStream[OnibusData] = onibusRawStream
    .map(new FormatarOnibusMapFunction())
    .uid("FormatarOnibusMapFunction")
    .name("FormatarOnibusMapFunction")
    .assignTimestampsAndWatermarks(new OnibusPunctualTimestampAssigner())
    .uid("OnibusPunctualTimestampAssigner")
    
    var onibusAlertStream = onibusFormattedStream
    .keyBy(new TupleKeySelector())
    .process(new OnibusSaindoChegando(onibusTimeBetweenQueries))
    .uid("FollowDetectorProcessFunction").name("FollowDetectorProcessFunction")
    
    
    // Criando e adicionando Sink do evento 'Onibus'
    
    val onibuskafkaProducer = new FlinkKafkaProducer[String](bootstrapServers,onibusProducerTopic,new SimpleStringSchema())
    
    onibuskafkaProducer.setWriteTimestampToKafka(true)
    
    onibusAlertStream
    .addSink(onibuskafkaProducer)
    .name("OnibusKafkaProducer")
    .uid("OnibusKafkaProducer")
    
    // Variáveis de configuração do evento 'Placas'
    
    val placasEventoPerseguicaoIntervaloSegundos = 300
    val placasAlertaPerseguicaoIntervaloSegundos = 1500
    val placasQuantidadeMinimaPontos = 3
    
    val placasProducerKeySerializer = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_PRODUCER_KEY_SERIALIZER") match { case Some(res) => {res} case None => { "" } }
    val placasProducerValueSerializer = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_PRODUCER_VALUE_SERIALIZER") match { case Some(res) => {res} case None => { "" } }
    val placasProducerAcks = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_PRODUCER_ACKS") match { case Some(res) => {println("ACKS: "+res); res } case None => { "" } }
    val placasProducerTopic = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_PRODUCER_TOPIC") match { case Some(res) => {res} case None => { "" } }
    val placasProducerTransactionTimeout = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_PRODUCER_TRANSACTION_TIMEOUT") match { case Some(res) => {res} case None => { "" } }
    
    val placasConsumerGroupId = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_CONSUMER_GROUP_ID")  match { case Some(res) => {res} case None => { "" } }
    val placasConsumerAutoOffsetReset = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_CONSUMER_AUTO_OFFSET_RESET")  match { case Some(res) => {res} case None => { "" } }
    val placasConsumerEnableAutoCommit = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT")  match { case Some(res) => {res} case None => { "" } }
    val placasConsumerTopic = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_CONSUMER_TOPIC") match { case Some(res) => {res} case None => { "" } }
    
    
    // Criando Properties de Sinks e Sources do evento 'Placas'
    
    var placasConsumerProps : Properties = new Properties()
    placasConsumerProps.setProperty("bootstrap.servers",bootstrapServers)
    placasConsumerProps.setProperty("zookeeper.connect",zookeeperConnect)
    placasConsumerProps.setProperty("group.id",placasConsumerGroupId)
    placasConsumerProps.setProperty("auto.offset.reset",placasConsumerAutoOffsetReset)
    placasConsumerProps.setProperty("enable.auto.commit",placasConsumerEnableAutoCommit)
    
    var placasProducerProps : Properties = new Properties()
    placasProducerProps.put("bootstrap.servers",bootstrapServers)
    placasProducerProps.put("key.serializer",placasProducerKeySerializer)
    placasProducerProps.put("value.serializer",placasProducerValueSerializer)
    placasProducerProps.put("acks",placasProducerAcks)
    placasProducerProps.put("transaction.timeout.ms",placasProducerTransactionTimeout)
    
    
    // Criando e adicionando Source do evento 'Placas'
    
    var placasRawStream : DataStream[String] = env
    .addSource(new FlinkKafkaConsumer[String](placasConsumerTopic,new SimpleStringSchema(),placasConsumerProps))
    .uid("PlacasKafkaConsumerInput")
    
    
    // Criando DataStream do evento 'Placas' e adicionando operadores
    
    var placasFormattedStream : DataStream[PlacaData] = placasRawStream
    .map(new FormatarPlacasMapFunction())
    .uid("FormatarPlacasMapFunction")
    .name("FormatarPlacasMapFunction")
    .assignTimestampsAndWatermarks(new PlacasPunctualTimestampAssigner())
    .uid("PlacasPunctualTimestampAssigner")
    
    var placasParPerseguicaoStream : DataStream[ParPerseguicao]= placasFormattedStream
    .keyBy(new PlacasKeyByPontoID())
    .process(new GerarParPerseguidores(placasEventoPerseguicaoIntervaloSegundos))
    
    var placasAlertStream = placasParPerseguicaoStream
    .keyBy(new ParPersegucaoKeyByPlacas())
    .process(new AgruparParPerseguidores(placasAlertaPerseguicaoIntervaloSegundos,placasQuantidadeMinimaPontos))
    
    
    // Criando e adicionando Sink do evento 'Placas'
    
    val placasKafkaProducer = new FlinkKafkaProducer[String](bootstrapServers,placasProducerTopic,new SimpleStringSchema())
   
    placasKafkaProducer
    .setWriteTimestampToKafka(true)
    
    placasAlertStream
    .addSink(placasKafkaProducer)
    .name("PlacasKafkaProducer")
    .uid("PlacasKafkaProducer")
    
    // Variáveis de configuração do evento Temperatura
    
    val temperaturaProducerKeySerializer = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_PRODUCER_KEY_SERIALIZER") match { case Some(res) => {res} case None => { "" } }
    val temperaturaProducerValueSerializer = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_PRODUCER_VALUE_SERIALIZER") match { case Some(res) => {res} case None => { "" } }
    val temperaturaProducerAcks = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_PRODUCER_ACKS") match { case Some(res) => {println("ACKS: "+res); res } case None => { "" } }
    val temperaturaProducerTopic = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_PRODUCER_TOPIC") match { case Some(res) => {res} case None => { "" } }
    val temperaturaProducerTransactionTimeout = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_PRODUCER_TRANSACTION_TIMEOUT") match { case Some(res) => {res} case None => { "" } }
    
    val temperaturaConsumerGroupId = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_CONSUMER_GROUP_ID")  match { case Some(res) => {res} case None => { "" } }
    val temperaturaConsumerAutoOffsetReset = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_CONSUMER_AUTO_OFFSET_RESET")  match { case Some(res) => {res} case None => { "" } }
    val temperaturaConsumerEnableAutoCommit = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT")  match { case Some(res) => {res} case None => { "" } }
    val temperaturaConsumerTopic = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_CONSUMER_TOPIC") match { case Some(res) => {res} case None => { "" } }
    
    var temperaturaConsumerProps : Properties = new Properties()
    temperaturaConsumerProps.setProperty("bootstrap.servers",bootstrapServers)
    temperaturaConsumerProps.setProperty("zookeeper.connect",zookeeperConnect)
    temperaturaConsumerProps.setProperty("group.id",temperaturaConsumerGroupId)
    temperaturaConsumerProps.setProperty("auto.offset.reset",temperaturaConsumerAutoOffsetReset)
    temperaturaConsumerProps.setProperty("enable.auto.commit",temperaturaConsumerEnableAutoCommit)
    
    var temperaturaProducerProps : Properties = new Properties()
    temperaturaProducerProps.put("bootstrap.servers",bootstrapServers)
    temperaturaProducerProps.put("key.serializer",temperaturaProducerKeySerializer)
    temperaturaProducerProps.put("value.serializer",temperaturaProducerValueSerializer)
    temperaturaProducerProps.put("acks",temperaturaProducerAcks)
    temperaturaProducerProps.put("transaction.timeout.ms",temperaturaProducerTransactionTimeout)
    
    var temperaturaRawStream : DataStream[String] = env
    .addSource(new FlinkKafkaConsumer[String](temperaturaConsumerTopic,new SimpleStringSchema(),temperaturaConsumerProps))
    .uid("KafkaConsumerInput")

    /*var temperaturaFormattedStream : DataStream[OnibusData] = temperaturaRawStream
    .map(new S2TMapFunction()).uid("S2TMapFunction").name("S2TMapFunction")
    .assignTimestampsAndWatermarks(new PlacasPunctualTimestampAssigner()).uid("PlacasPunctualTimestampAssigner")
    
    var temperaturaAlertStream = ???
    
    val temperaturaKafkaProducer = new FlinkKafkaProducer[String](bootstrapServers,temperaturaProducerTopic,new SimpleStringSchema())
    temperaturaKafkaProducer.setWriteTimestampToKafka(true)
    temperaturaAlertStream.addSink(temperaturaKafkaProducer).name("KafkaProducer").uid("KafkaProducer") */
    
    println("TestPipeline begin...")

    env.execute("TestPipeline")

}
