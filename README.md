# kafka-tweet-stream

Proof of concept using Kafka to obtain a real-time data feed from Twitter 

Usage:
Obtain secrets from Twitter developer and place in /src/main/resources/properties.xml 

Starting Kafka 
1. Start zookeeper 
```
zookeeper-server-start.sh config/zookeeper.properties
```
2. Start kafka on different terminal window
```
kafka-server-start.sh config/server.properties
```
3. Create topic
```
kafka-topics.sh --bootstrap-server localhost:9092 --topic twitter --create --partitions 3 --replication-factor 1
```
Note: Project environment is using Java on Windows with Kafka running on WSL 2.0, the following changes must be made to kafka server.properties
```
advanced.listeners=PLAINTEXT://**insert your own ip address**
listener.security.protocol.map=PLAINTEXT:PLAINTEXT
listeners=PLAINTEXT://0.0.0.0:9092
```
