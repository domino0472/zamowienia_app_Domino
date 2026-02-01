package pl.dominiak.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.dominiak.orders.config.AppConfigLoader;
import pl.dominiak.orders.config.AppSettings;
import pl.dominiak.orders.logic.OrderValidator;
import pl.dominiak.orders.model.OrderRequest;
import pl.dominiak.orders.util.OrderNumberGenerator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class App {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws Exception {

        AppSettings settings = AppConfigLoader.load();
        Path ordersDir = Path.of(settings.getOrdersDirectory());
        if (!Files.isDirectory(ordersDir)) {
            logger.error("Katalog nie został znaleziony: {}", ordersDir.toAbsolutePath());
            System.exit(1);
        }

        ObjectMapper mapper = new ObjectMapper();
        OrderValidator validator = new OrderValidator();

        AtomicInteger ok = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();

        try (Stream<Path> paths = Files.list(ordersDir)) {
            paths.filter(p -> p.toString().toLowerCase().endsWith(".json"))
                    .sorted()
                    .forEach(p -> {
                        try {
                            OrderRequest or = mapper.readValue(p.toFile(), OrderRequest.class);

                            if (!validator.validate(or)) {
                                throw new RuntimeException("Walidacja nieudana dla pliku");
                            }

                            String reqNo = OrderNumberGenerator.generate(or);
                            logger.info("OK: {} -> requestNo={}", p.getFileName(), reqNo);
                            ok.incrementAndGet();
                        } catch (Exception ex) {
                            logger.warn("FAIL: {} -> {}", p.getFileName(), ex.getMessage());
                            failed.incrementAndGet();
                        }
                    });
        }

        logger.info("PODSUMOWANIE: ok={}, failed={}", ok.get(), failed.get());
        if (failed.get() > 0) {
            System.exit(1);
        }
    }
}