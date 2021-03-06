package github.jlucartc

import java.util.Properties
import java.util.concurrent.TimeUnit

import Functions.{OnibusSaindoChegando, FormatarOnibusMapFunction}
import KeySelectors.TupleKeySelector
import Model.OnibusData
import github.jlucartc.TimestampAssigners.OnibusPunctualTimestampAssigner
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.core.fs.FileSystem
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}


class Pipeline {
    
    println("Pipeline setup...")
    
    
    // Variáveis de configuração da Pipeline
    
    val parallelism = 1
    val checkPointingTimeInterval = 10
    val restartAttempts = 1
    val timeBeforeRetry = 10000
    
    
    // Variáveis de configuração de Sinks, Sources e Operadores
    
    val bootstrapServers = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_BOOTSTRAP_SERVERS") match { case Some(res) => {res} case None => { "" } }
    val zookeeperConnect = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ZOOKEEPER_CONNECT") match { case Some(res) => {res} case None => { "" } }
    val groupId = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_CONSUMER_GROUP_ID")  match { case Some(res) => {res} case None => { "" } }
    
    val keySerializer = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_KEY_SERIALIZER") match { case Some(res) => {res} case None => { "" } }
    val valueSerializer = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_VALUE_SERIALIZER") match { case Some(res) => {res} case None => { "" } }
    val acks = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_ACKS") match { case Some(res) => {println("ACKS: "+res); res } case None => { "" } }
    val onibusOutputTopic = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_TOPIC") match { case Some(res) => {res} case None => { "" } }
    val transactionTimeout = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_TRANSACTION_TIMEOUT") match { case Some(res) => {res} case None => { "" } }
    
    val autoOffsetReset = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_CONSUMER_AUTO_OFFSET_RESET")  match { case Some(res) => {res} case None => { "" } }
    val enableAutoCommit = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT")  match { case Some(res) => {res} case None => { "" } }
    val onibusInputTopic = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_CONSUMER_TOPIC") match { case Some(res) => {res} case None => { "" } }
    
    val inputFileURL1 = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_CONFIG_INPUTFILEURL1") match { case Some(res) => {res} case None => { "" } }
    val outputFileURL1 = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_CONFIG_OUTPUTFILEURL1") match { case Some(res) => {res} case None => { "" } }
    val outputFileURL2 = sys.env.get("GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_CONFIG_OUTPUTFILEURL2") match { case Some(res) => {res} case None => { "" } }
    
    val timeBetweenQueries = 1
    
    
    // Configurando pipeline
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.setParallelism(parallelism)
    env.enableCheckpointing(checkPointingTimeInterval)
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(restartAttempts,org.apache.flink.api.common.time.Time.of(timeBeforeRetry,TimeUnit.MILLISECONDS)))
    
    
    // Criando propriedades de configuração de Sinks e Sources
    
    var consumerprops : Properties = new Properties()
    consumerprops.setProperty("bootstrap.servers",bootstrapServers)
    consumerprops.setProperty("zookeeper.connect",zookeeperConnect)
    consumerprops.setProperty("group.id",groupId)
    consumerprops.setProperty("auto.offset.reset",autoOffsetReset)
    consumerprops.setProperty("enable.auto.commit",enableAutoCommit)

    var producerprops : Properties = new Properties()
    producerprops.put("bootstrap.servers",bootstrapServers)
    producerprops.put("key.serializer",keySerializer)
    producerprops.put("value.serializer",valueSerializer)
    producerprops.put("acks",acks)
    producerprops.put("transaction.timeout.ms",transactionTimeout)
    
    
    // Criando e adicionando Source
    
    var stream : DataStream[String] = env.addSource(new FlinkKafkaConsumer[String](onibusInputTopic,new SimpleStringSchema(),consumerprops)).uid("KafkaConsumerInput")
    
    
    // Criando DataStream e adicionando operadores
    
    var tupleStream : DataStream[OnibusData] = stream
    .map(new FormatarOnibusMapFunction()).uid("S2TMapFunction").name("S2TMapFunction")
    .assignTimestampsAndWatermarks(new OnibusPunctualTimestampAssigner()).uid("PlacasPunctualTimestampAssigner")
    
    var newTupleStream = tupleStream.keyBy(new TupleKeySelector()).process(new OnibusSaindoChegando(timeBetweenQueries)).uid("FollowDetectorProcessFunction").name("newTupleStream")
    
    
    // Criando e adicionando Sink
    
    val kafkaProducer = new FlinkKafkaProducer[String](bootstrapServers,onibusOutputTopic,new SimpleStringSchema())
    kafkaProducer.setWriteTimestampToKafka(true)
    newTupleStream.addSink(kafkaProducer).name("KafkaProducer").uid("KafkaProducer")
    
    stream.writeAsText(outputFileURL1,FileSystem.WriteMode.OVERWRITE).name("StreamOutputFile").uid("StreamOutputFile")
    newTupleStream.writeAsText(outputFileURL2,FileSystem.WriteMode.OVERWRITE).name("TupleStreamOutputFile").uid("TupleStreamOutputFile")
    
    println("Pipeline begin...")
    
    
    // Iniciando execução da Pipeline
    
    env.execute("OnibusPipeline")
    
}

