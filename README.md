![Java](https://img.shields.io/badge/Java-17+-orange?logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?logo=apache-maven&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-4.0-black?logo=apache-kafka&logoColor=white)
![Kafka Streams](https://img.shields.io/badge/Kafka%20Streams-Real%20Time%20Processing-black?logo=apache-kafka&logoColor=white)
![Kafka Connect](https://img.shields.io/badge/Kafka%20Connect-Integration%20Layer-black?logo=apache-kafka&logoColor=white)
![Cassandra](https://img.shields.io/badge/Apache%20Cassandra-Distributed%20DB-1287B1?logo=apache-cassandra&logoColor=white)
![HTTPie](https://img.shields.io/badge/HTTPie-Modern%20HTTP%20Client-73DC8C?logo=httpie&logoColor=black)
![curl](https://img.shields.io/badge/curl-CLI-073551?logo=curl&logoColor=white)
![jq](https://img.shields.io/badge/jq-JSON%20Processor-5A5A5A)



## Designing a Scalable Real-Time Data Pipeline with Kafka Streams, Kafka Connect & Cassandra


A real-time streaming pipeline that processes transaction events using Kafka Streams, aggregates data in 1-minute tumbling windows, and persists results into Apache Cassandra via Kafka Connect.

This project demonstrates how to build a production-style event-driven analytics system for high-throughput data processing.



#### Architecture Diagram

<img width="1461" height="433" alt="Untitled Diagram-Page-8 drawio(1)" src="https://github.com/user-attachments/assets/75b0624d-7ecc-489b-a6d5-b932172b2491" />




#### Prerequisites

| Tool            | Version                        | Purpose                                               |
| --------------- | ------------------------------ | ----------------------------------------------------- |
| Java            | 17+                            | Runtime for Kafka Streams applications                |
| Maven           | 3.8+                           | Build and dependency management                       |
| Kafka           | 4.0.0+                         | Distributed event streaming platform                  |
| Kafka Connect   | Compatible with Kafka          | Data integration framework for sink/source connectors |
| Kafka Streams | Latest                         | Stream processing engine               |
| Cassandra     | 4+                            | Stores aggregated and structured data       |
| httpie          | Latest                         | API testing and interacting with REST endpoints       |
| curl            | Latest                         | Command-line tool for testing APIs and services       |
| jq              | Latest                         | JSON parsing and formatting in CLI                    |





### Project Setup


**Clone the repository**

```sh
git clone git@github.com:zablon-oigo/kstream-cassandra-pipeline.git

cd kstream-cassandra-pipeline
```

**Build the Project**


Compile the application

```sh
mvn clean compile
```

### Running the Application

Start Producer

```sh
# Run producer
mvn exec:java -Dexec.mainClass=kmart.App
```

**Run Kafka Streams Applications**

Start Kafka Streams Processor

```sh
# In different tab
mvn exec:java -Dexec.mainClass=kmart.SalesProcessor
```
#### Verify Kafka Topics

**List topics**

```sh
kafka-topics.sh --list --bootstrap-server localhost:9095 
```


### Kafka Connectors


##### Deploy Connectors

**Transaction records**

```sh
curl -X POST http://localhost:8083/connectors \
  -H "Content-Type: application/json" \
  -d @cassandra-sink.json
```

**Top Product per Window Sink**

```sh

curl -X POST http://localhost:8083/connectors \
  -H "Content-Type: application/json" \
  -d @top-product-sink.json

```

**Customer Count by Country Sink**

```sh
curl -X POST http://localhost:8083/connectors \
  -H "Content-Type: application/json" \
  -d @customer-count-sink.json

```


### Kafka Connect Management


**List Available Plugins**

```sh
curl -s localhost:8083/connector-plugins | jq '.[].class'
```

**Check Connector Status**

```sh
curl http://localhost:8083/connectors/top-product-sink/status | jq
```

**Restart Connector**

```sh

curl -X POST localhost:8083/connectors/top-product-sink/restart

```

**Delete Connector**

```sh
curl -X DELETE http://localhost:8083/connectors/top-product-sink

```


### Consuming Aggregated Data


**Customer Count by Country**

```sh
kafka-console-consumer.sh \
  --topic customer-count-by-country \
  --bootstrap-server localhost:9095 \
  --from-beginning \
  --property print.key=true \
  --property key.separator=" | "
```


**Top Product per 1-Minute Window**

```sh
kafka-console-consumer.sh \
  --topic top-product-1min-window \
  --bootstrap-server localhost:9095 \
  --from-beginning \
  --property print.key=true \
  --property key.separator=" | "
```


#### Geo-Location Visualization (Elasticsearch + Kibana)

**Mapping Configuration**

```sh
{
  "mappings": {
    "properties": {
      "id": { "type": "keyword" },
      "latitude": { "type": "double" },
      "longitude": { "type": "double" },
      "location": {
        "type": "geo_point"
      }
    }
  }
}
```


#### Read on Medium

[How I Built a Scalable Real-Time Analytics Pipeline with Kafka Streams, Kafka Connect & Cassandra.](https://medium.com/brilliant-programmer/how-i-built-a-scalable-real-time-analytics-pipeline-with-kafka-streams-kafka-connect-cassandra-c201e83a8851)
