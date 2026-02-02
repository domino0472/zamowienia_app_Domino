package pl.dominiak.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.dominiak.orders.config.AppConfigLoader;
import pl.dominiak.orders.config.AppSettings;
import pl.dominiak.orders.logic.OrderValidator;
import pl.dominiak.orders.model.Customer;
import pl.dominiak.orders.model.OrderRequest;
import pl.dominiak.orders.util.EmailService;
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
                            if (validator.validate(or)) {
                                String reqNo = OrderNumberGenerator.generate(or);
                                logger.info("ZAMÓWIENIE: {}", reqNo);


                                Customer c = or.getCustomer();
                                logger.info("Klient: {} {}, Email: {}", c.getFirstName(), c.getLastName(), c.getEmail());

                                java.util.Random random = new java.util.Random();
                                logger.info("Produkty:");
                                or.getProducts().forEach(prod -> {
                                    double price = 10 + (500 - 10) * new java.util.Random().nextDouble();

                                    logger.info("- {} | Ilość: {} | Cena: {} PLN",
                                            prod.getProductCode(), prod.getQuantity(), String.format("%.2f", price));
                                });

                                ok.incrementAndGet();
                                String emailBody = "Dziękujemy za zamówienie! \nNumer: " + reqNo;
                                EmailService.send(or.getCustomer().getEmail(), "Podsumowanie zamówienia", emailBody, settings);
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