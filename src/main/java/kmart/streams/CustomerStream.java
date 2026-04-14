package kmart.streams;

import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import tools.jackson.databind.ObjectMapper;

public class CustomerStream {

    public static void build(StreamsBuilder builder) {

        ObjectMapper mapper = new ObjectMapper();

        KStream<String, String> sales =
                builder.stream("sales-raw",
                        Consumed.with(Serdes.String(), Serdes.String()));

        KStream<String, String> customers =
                sales.mapValues(value -> {
                    try {
                        Map<String, Object> sale = mapper.readValue(value, Map.class);

                        Map<String, Object> customer = Map.of(
                                "customer_id", sale.get("customer_id"),
                                "first_name", sale.get("first_name"),
                                "last_name", sale.get("last_name"),
                                "email", sale.get("email"),
                                "country", sale.get("country"),
                                "city", sale.get("city")
                        );

                        return mapper.writeValueAsString(customer);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .peek((key, value) -> System.out.println("CUSTOMER:  " + value));

        customers.to("customer-profile",
                Produced.with(Serdes.String(), Serdes.String()));
    }
}
