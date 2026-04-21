package kmart.streams;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

public class CountryStream {

    public static void build(StreamsBuilder builder) {

        ObjectMapper mapper = new ObjectMapper();

        KStream<String, String> sales =
                builder.stream("sales-raw",
                        Consumed.with(Serdes.String(), Serdes.String()));

        // Extract country from JSON
        KStream<String, String> countries = sales.mapValues(value -> {
            try {
                JsonNode json = mapper.readTree(value);
                return json.get("country").asText();
            } catch (Exception e) {
                return null;
            }
        });

        // Count customers per country
        KTable<String, Long> countByCountry =
                countries
                        .filter((k, v) -> v != null)
                        .selectKey((k, v) -> v) // use country as key
                        .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
                        .count(Materialized.as("country-store"));

        // Send results to Kafka topic
        countByCountry.toStream()
                .peek((k, v) -> System.out.println("AGG: " + k + " = " + v))
                .map((country, count) -> KeyValue.pair(country, count.toString()))
                .to("customer-count-by-country",
                        Produced.with(Serdes.String(), Serdes.String()));
    }
}