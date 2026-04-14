package kmart;

import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

import kmart.streams.CustomerStream;

public class SalesProcessor {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("application.id", "kmart-processor");
        props.put("bootstrap.servers", "localhost:9095");

        StreamsBuilder builder = new StreamsBuilder();

        // Build topology
        CustomerStream.build(builder);

        Topology topology = builder.build();

        System.out.println(topology.describe());

        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        System.out.println("Kafka Streams started");
    }
}
