
#Utilize as seguintes variáveis de ambiente nos containers "taskmanagers"

# KafkaTopicMessageProducer env vars

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_BOOTSTRAP_SERVERS=172.17.0.1:60000,172.17.0.1:60001,172.17.0.1:60002,172.17.0.1:60003,172.17.0.1:60004
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_ZOOKEEPER_CONNECT=172.17.0.1:2181
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_VALUE_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_ACKS=all
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_SINK_TOPIC=onibus-raw-data

# MQTTMessageConsumer env vars

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_MQTTMESSAGECONSUMER_TOPIC=+/devices/+/up
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_MQTTMESSAGECONSUMER_USER=bus_gps_data
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_MQTTMESSAGECONSUMER_PASSWORD=ttn-account-v2.IFJLbrAmGz8RoPRF-_DF0MRiIp-hmtB2it2HRdn9_yA
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_MQTTMESSAGECONSUMER_URI=tcp://brazil.thethings.network:1883

# Variáveis do evento de ônibus

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_VALUE_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_ACKS=all
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_TOPIC=teste-onibus-alert
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_PRODUCER_TRANSACTION_TIMEOUT=20000

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_CONSUMER_GROUP_ID=teste-onibus
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_CONSUMER_AUTO_OFFSET_RESET=latest
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT=true
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ONIBUS_KAFKA_CONSUMER_TOPIC=teste-onibus

# Variáveis do evento de placas

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_PRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_PRODUCER_VALUE_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_PRODUCER_ACKS=all
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_PRODUCER_TOPIC=teste-placas-alert
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_PRODUCER_TRANSACTION_TIMEOUT=20000

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_CONSUMER_GROUP_ID=teste-placas
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_CONSUMER_AUTO_OFFSET_RESET=latest
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT=true
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_PLACAS_KAFKA_CONSUMER_TOPIC=teste-placas

# Variáveis do evento de temperatura

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_PRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_PRODUCER_VALUE_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_PRODUCER_ACKS=all
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_PRODUCER_TOPIC=teste-temperatura-alert
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_PRODUCER_TRANSACTION_TIMEOUT=20000

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_CONSUMER_GROUP_ID=teste-temperatura
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_CONSUMER_AUTO_OFFSET_RESET=latest
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT=true
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_TEMPERATURA_KAFKA_CONSUMER_TOPIC=teste-temperatura


# PlacasEventProducer vars

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_PLACASEVENTPRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_PLACASEVENTPRODUCER_VALUE_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_PLACASEVENTPRODUCER_ACKS=all
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_PLACASEVENTPRODUCER_TOPICO=teste-placas

# OnibusEventProducer vars

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_ONIBUSEVENTPRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_ONIBUSEVENTPRODUCER_VALUE_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_ONIBUSEVENTPRODUCER_ACKS=all
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_ONIBUSEVENTPRODUCER_TOPICO=teste-onibus

# TemperaturaEventProducer vars

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_TEMPERATURAEVENTPRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_TEMPERATURAEVENTPRODUCER_VALUE_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_TEMPERATURAEVENTPRODUCER_ACKS=all
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_TEMPERATURAEVENTPRODUCER_TOPICO=teste-temperatura

# Flink Job env vars

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_BOOTSTRAP_SERVERS=172.17.0.1:60000,172.17.0.1:60001,172.17.0.1:60002,172.17.0.1:60003,172.17.0.1:60004
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ZOOKEEPER_CONNECT=172.17.0.1:2181

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_PRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_PRODUCER_VALUE_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_PRODUCER_ACKS=all
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_PRODUCER_TOPIC=onibus-alert-data
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_PRODUCER_TRANSACTION_TIMEOUT=20000

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_CONSUMER_TOPIC=onibus-raw-data
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_CONSUMER_AUTO_OFFSET_RESET=latest
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_CONSUMER_GROUP_ID=onibus-kafka-consumer
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT=true

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_PARALLELISM=1
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_CHECKPOINTINGTIMEINTERVAL=1000
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_RESTARTATTEMPTS=1
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_TIMEBEFORERETRY=10000
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_INPUTFILEURL1=/home/createdFiles/input1
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_INPUTFILEURL2=/home/createdFiles/input2
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_OUTPUTFILEURL1=/home/createdFiles/output1
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_OUTPUTFILEURL2=/home/createdFiles/output2

# POSTGRES connection env vars

export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_POSTGRES_URL=jdbc:postgresql://172.17.0.1:5433/app-onibus
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_POSTGRES_DRIVER=org.postgresql.Driver
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_POSTGRES_USERNAME=app-onibus
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_POSTGRES_PASSWORD=123
export GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_POSTGRES_APP_TABLE=pontos
