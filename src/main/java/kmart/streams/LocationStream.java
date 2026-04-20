package kmart.streams;


import java.util.Map;
import java.util.UUID;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import tools.jackson.databind.ObjectMapper;


public class LocationStream {

    public static void build(StreamsBuilder builder) {
        ObjectMapper mapper = new ObjectMapper();

        KStream<String, String> sales =
                builder.stream("sales-raw",
                        Consumed.with(Serdes.String(), Serdes.String()));

        KStream<String, String> location =
                sales.mapValues(value -> {
                    try {
                        Map<String, Object> sale = mapper.readValue(value, Map.class);

                        Map<String, Object> geo = Map.of(
                                    "id",       UUID.randomUUID().toString(),
                                    "latitude", sale.get("latitude"),   
                                    "longitude", sale.get("longitude")   
                                );

                        return mapper.writeValueAsString(geo);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .peek((key, value) -> System.out.println("LOCATION: " + value));

        location.to("location",
                Produced.with(Serdes.String(), Serdes.String()));
    }
}