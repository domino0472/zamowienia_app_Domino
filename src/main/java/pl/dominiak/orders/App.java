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


public class App {
    public static void main(String[] args) throws Exception {

        AppSettings settings = AppConfigLoader.load();
        Path ordersDir = Path.of(settings.getOrdersDirectory());
        if (!Files.isDirectory(ordersDir)) {
            System.err.println("ERROR: directory not found: " + ordersDir.toAbsolutePath());
            System.exit(1);
        }



}