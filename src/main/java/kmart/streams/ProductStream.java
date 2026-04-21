package kmart.streams;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductStream {

    public static void build(StreamsBuilder builder) {
        ObjectMapper mapper = new ObjectMapper();

        // 1. Read raw sales
        KStream<String, String> sales = builder.stream("sales-raw",
                Consumed.with(Serdes.String(), Serdes.String()));

        // 2. Extract product_name and quantity
        KStream<String, Long> productQuantity = sales
                .mapValues(value -> {
                    try {
                        JsonNode json = mapper.readTree(value);
                        String product = json.path("product_name").asText(null);
                        long quantity = json.path("quantity").asLong(0);

                        if (product == null || product.isBlank() || quantity <= 0) {
                            return null;
                        }
                        return KeyValue.pair(product, quantity);
                    } catch (Exception e) {
                        System.err.println("Failed to parse record: " + value);
                        return null;
                    }
                })
                .filter((k, v) -> v != null)
                .map((k, kv) -> kv);   // KStream<product, quantity>

        // 3. Aggregate total quantity per product per 1-minute window
        KTable<Windowed<String>, Long> windowedTotals = productQuantity
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1)))
                .reduce(Long::sum, Materialized.as("product-1min-store"));

        windowedTotals.toStream()
                .map((windowedKey, totalQuantity) -> {
                    String windowStartStr = String.valueOf(windowedKey.window().start());
                    String productName = windowedKey.key();

                    // Create clean JSON string
                    String jsonValue = String.format(
                            "{\"product_name\":\"%s\",\"quantity\":%d}",
                            productName.replace("\"", "\\\""),
                            totalQuantity
                    );

                    return KeyValue.pair(windowStartStr, jsonValue);
                })
                .to("top-product-1min-window",
                     Produced.with(Serdes.String(), Serdes.String()));
    }
}