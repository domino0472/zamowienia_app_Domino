package pl.dominiak.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.dominiak.orders.config.AppConfigLoader;
import pl.dominiak.orders.config.AppSettings;
import pl.dominiak.orders.model.OrderRequest;
import pl.dominiak.orders.util.OrderNumberGenerator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Loads settings, scans for *.json in configured directory,
 * deserializes with Jackson, generates request numbers, prints results.
 */
public class App {
    public static void main(String[] args) throws Exception {
        // 1) Load app settings
        AppSettings settings = AppConfigLoader.load();
        Path ordersDir = Path.of(settings.getOrdersDirectory());
        if (!Files.isDirectory(ordersDir)) {
            System.err.println("ERROR: directory not found: " + ordersDir.toAbsolutePath());
            System.exit(1);
        }

        // 2) Read JSON files
        ObjectMapper mapper = new ObjectMapper();
        AtomicInteger ok = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();

        try (Stream<Path> paths = Files.list(ordersDir)) {
            paths.filter(p -> p.toString().toLowerCase().endsWith(".json"))
                    .sorted()
                    .forEach(p -> {
                        try {
                            OrderRequest or = mapper.readValue(p.toFile(), OrderRequest.class);
                            String reqNo = OrderNumberGenerator.generate(or);
                            System.out.println("OK: " + p.getFileName() + " -> requestNo=" + reqNo);
                            ok.incrementAndGet();
                        } catch (Exception ex) {
                            System.err.println("FAIL: " + p.getFileName() + " -> " + ex.getMessage());
                            failed.incrementAndGet();
                        }
                    });
        }

        System.out.println("SUMMARY: ok=" + ok.get() + ", failed=" + failed.get());


        if (failed.get() > 0) {
            System.exit(1);
        }
    }
}