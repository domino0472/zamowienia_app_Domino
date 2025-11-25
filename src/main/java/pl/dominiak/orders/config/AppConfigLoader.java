package pl.dominiak.orders.config;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public final class AppConfigLoader {

    private static final String ENV_VAR_NAME = "ORDERS_DATA_DIR";
    private static final String CONFIG_KEY = "orders.directory";

    private static final String PROPERTIES_FILE_NAME = "appsettings.properties";

    private AppConfigLoader() {
    }

    public static AppSettings load() throws Exception {

        String dir = System.getenv(ENV_VAR_NAME);

        if (dir != null && !dir.trim().isEmpty()) {
            String finalDir = dir.trim();
            System.out.println(" [ENV] Ścieżka katalogu pobrana ze zmiennej: " + ENV_VAR_NAME + " = " + finalDir);

            AppSettings s = new AppSettings();
            s.setOrdersDirectory(finalDir);
            return s;
        }

        System.out.println(" Zmienna '" + ENV_VAR_NAME + "' pusta. Próba wczytania z pliku: " + PROPERTIES_FILE_NAME);

        Configurations configs = new Configurations();
        Configuration cfg;

        File file = new File(PROPERTIES_FILE_NAME);

        try {
            if (file.exists()) {
                cfg = configs.properties(file);
                System.out.println(" Wczytano konfigurację z pliku na dysku: " + file.getAbsolutePath());
            } else {
                URL resource = AppConfigLoader.class.getClassLoader().getResource(PROPERTIES_FILE_NAME);
                if (resource != null) {
                    cfg = configs.properties(resource);
                    System.out.println(" Wczytano konfigurację z zasobów classpath (JAR/IDE).");
                } else {
                    throw new IOException("Nie znaleziono pliku: " + PROPERTIES_FILE_NAME + " ani na dysku, ani w classpath.");
                }
            }
        } catch (ConfigurationException ex) {
            throw new IOException("Błąd parsowania pliku konfiguracyjnego.", ex);
        }

        dir = cfg.getString(CONFIG_KEY);

        if (dir == null || dir.isBlank()) {
            System.out.println("⚠️ Klucz '" + CONFIG_KEY + "' pusty. Używam katalogu domyślnego './data'");
            dir = "./data";
        }

        String finalDir = dir.trim();
        System.out.println("✅ [FILE] Ścieżka katalogu zamówień: " + finalDir);

        AppSettings s = new AppSettings();
        s.setOrdersDirectory(finalDir);
        return s;
    }
}