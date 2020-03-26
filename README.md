# AppOnibusFlinkBackEnd

Bus event processing application made with Apache Flink.

## General observations

1. You should rename all `.model` configuration files if you wanna run the application.
E.g. : rename `.env.model` file to `.env` before running.<br>
2. Don't forget to fill in all configuration files mentioned in this guide.<br>
3. There should be at least one `Kafka` container running in the port range from `60000` through `60004`. If there's none, 
the brokers will not be discovered by the application.


## Kafka and Flink Setup

This job needs some configuration variables to be set on the environment. These variables will configure Kafka consumers
and producers, Flink connectors and pipeline behaviour. Here's a simple guide to each group of variables. You may find all
these variables in a single file in `src/main/scala/github/jlucartc/Env/.env.model`. If you'd rather put these variables
manually in some place like `/etc/profile.d/` or so, there's a `src/main/scala/github/jlucartc/Env/env.sh.model` file.

#### MQTT variables configuration
MQTT is an [application layer protocol](https://en.wikipedia.org/wiki/MQTT) which is of common use in IoT communication.
In this project, a MQTT broker sends data to a Kafka topic, for later processing. 
To configure MQTT subscriber and MQTT -> Kafka publisher, these are the variables you'll need: (For further
information, see [this](https://www.eclipse.org/paho/clients/java/) and [this](https://kafka.apache.org/documentation/#producerconfigs))

```
# KafkaTopicMessageProducer env vars

GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_BOOTSTRAP_SERVERS=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_ZOOKEEPER_CONNECT=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_KEY_SERIALIZER=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_VALUE_SERIALIZER=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_ACKS=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_KAFKATOPICMESSAGEPRODUCER_SINK_TOPIC=

# MQTTMessageConsumer env vars

GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_MQTTMESSAGECONSUMER_TOPIC=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_MQTTMESSAGECONSUMER_USER=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_MQTTMESSAGECONSUMER_PASSWORD=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_MQTTMESSAGECONSUMER_URI=

```

#### Flink Job variables configuration

Apache Flink is a stream/batch processing framework. It will be used in this project to collect data from Kafka
topics, do some processing over this data, and possibly generate new data stream, and persist the data for later uses.
For further information about Flink's configuration 
parameters, roam through [this](https://ci.apache.org/projects/flink/flink-docs-release-1.10/).
Some parameters are similar to the ones from the previous section, since here Kafka is connected to Flink.
```
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_BOOTSTRAP_SERVERS=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_ZOOKEEPER_CONNECT=

GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_PRODUCER_KEY_SERIALIZER=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_PRODUCER_VALUE_SERIALIZER=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_PRODUCER_ACKS=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_PRODUCER_TOPIC=

GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_CONSUMER_TOPIC=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_CONSUMER_AUTO_OFFSET_RESET=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_CONSUMER_GROUP_ID=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_KAFKA_CONSUMER_ENABLE_AUTO_COMMIT=

GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_PARALLELISM=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_CHECKPOINTINGTIMEINTERVAL=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_RESTARTATTEMPTS=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_TIMEBEFORERETRY=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_INPUTFILEURL1=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_INPUTFILEURL2=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_OUTPUTFILEURL1=
GITHUB_JLUCARTC_APPONIBUSFLINKBACKEND_FLINKJOB_CONFIG_OUTPUTFILEURL2=
```

## Docker setup
There's a `docker-compose.yml` file in `src/main/scala/github/jlucartc/Env` which can be used to deploy all necessary
services as docker containers. This guide doesn't have a Kubernetes or Docker Swarm guide section ready yet, so this project is
intended to be run by setting the docker-compose file up directly. The steps are these:

1. Copy `src/main/scala/github/jlucartc/Env/.env.model` to `src/main/scala/github/jlucartc/Env/.env` and fill in all
   environment variables in it (or just rename it).<br>
2. execute `sudo docker-compose -f src/main/scala/github/jlucartc/Env/docker-compose.yml up -d` to create all
   necessary containers.<br>
3. (Optional) execute the previous commando with `--scale kafka=N` or `--scale taskmanager=N` to scale any of these services.
4. Build the project and execute `sbt assembly` to generate a `.jar` file in `/target/scala-<scala-version>/`<br>
5. Access `localhost:8081`, submit the `.jar` file and execute it.

## Postgres Setup

Postgres has it's own `.env` file in `src/main/scala/github/jlucartc/Env/postgres-setup.env.model`.
Apart from it, it may be necessary to change some fields inside `docker-compose.yml`, if you already have a postgres
instance running in your `5432` port, for example.

Since it isn't possible to send some files to the container using `docker-compose.yml`'s options yet, you should create the directory `/var/tmp/postgres-sql-import.d`
in your local machine and put your `.sql` files there <b>before instantiating the services</b>, if you wanna import your database schema.
You may also change it's path by changing the `SOURCE` side of the `SOURCE:TARGET` in it's `volume` option.
Here goes Postgres' environment variables:

```
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_DB=
```