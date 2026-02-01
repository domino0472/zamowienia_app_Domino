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
    // Nowy klucz dla wersji (musi pasować do appsettings.properties)
    private static final String VERSION_KEY = "app.version";

    private static final String PROPERTIES_FILE_NAME = "appsettings.properties";

    private AppConfigLoader() {
    }

    public static AppSettings load() throws Exception {
        // Tworzymy obiekt ustawień od razu
        AppSettings settings = new AppSettings();

        // KROK 1: Wczytanie pliku konfiguracyjnego (zawsze, bo potrzebujemy wersji)
        Configurations configs = new Configurations();
        Configuration cfg = null;
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
                    System.err.println("⚠️ Ostrzeżenie: Nie znaleziono pliku " + PROPERTIES_FILE_NAME);
                }
            }
        } catch (ConfigurationException ex) {
            throw new IOException("Błąd parsowania pliku konfiguracyjnego.", ex);
        }

        // KROK 2: Ustawienie wersji (jeśli udało się wczytać plik)
        if (cfg != null) {
            // Drugi parametr to wartość domyślna, gdyby klucza nie było
            String version = cfg.getString(VERSION_KEY, "nieznana-wersja");
            settings.setVersion(version);
        } else {
            settings.setVersion("brak-konfiguracji");
        }

        // KROK 3: Ustalenie katalogu (Priorytet: ENV -> Plik -> Domyślny)
        String envDir = System.getenv(ENV_VAR_NAME);
        String fileDir = (cfg != null) ? cfg.getString(CONFIG_KEY) : null;
        String finalDir;

        if (envDir != null && !envDir.trim().isEmpty()) {
            finalDir = envDir.trim();
            System.out.println(" [ENV] Ścieżka katalogu pobrana ze zmiennej: " + ENV_VAR_NAME + " = " + finalDir);
        } else if (fileDir != null && !fileDir.isBlank()) {
            finalDir = fileDir.trim();
            System.out.println("✅ [FILE] Ścieżka katalogu zamówień: " + finalDir);
        } else {
            System.out.println("⚠️ Klucz '" + CONFIG_KEY + "' pusty lub brak zmiennej ENV. Używam katalogu domyślnego './data'");
            finalDir = "./data";
        }

        settings.setOrdersDirectory(finalDir);
        return settings;
    }
}