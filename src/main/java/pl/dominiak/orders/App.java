package pl.dominiak.orders;

import pl.dominiak.orders.config.AppConfigLoader;
import pl.dominiak.orders.config.AppSettings;

import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) throws Exception {
        AppSettings settings = AppConfigLoader.load();
        Path ordersDir = Path.of(settings.getOrdersDirectory());
        if (!Files.isDirectory(ordersDir)) {
            System.err.println("ERROR: directory not found: " + ordersDir.toAbsolutePath());
            System.exit(1);
        }
    }
}
