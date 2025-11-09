package pl.dominiak.orders.config;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

public final class AppConfigLoader {

    // Nazwy stałych, zgodne z logiką aplikacji
    private static final String ENV_VAR_NAME = "ORDERS_DATA_DIR";
    private static final String CONFIG_KEY = "orders.directory";
    private static final String PROPERTIES_FILE_PATH = "Properties/appsettings.properties"; // Zgodne z Twoim kodem

    private AppConfigLoader() {
    }

    public static AppSettings load() throws Exception {

        // Krok 1: Próba odczytu ze zmiennej środowiskowej
        String dir = System.getenv(ENV_VAR_NAME);

        if (dir != null && !dir.trim().isEmpty()) {
            // Użycie zmiennej środowiskowej
            String finalDir = dir.trim();
            System.out.println("✅ Ścieżka katalogu zamówień pobrana ze zmiennej środowiskowej: " + ENV_VAR_NAME + " = " + finalDir);

            AppSettings s = new AppSettings();
            s.setOrdersDirectory(finalDir);
            return s;
        }

        System.out.println("⚠️ Zmienna środowiskowa '" + ENV_VAR_NAME + "' nieustawiona/pusta. Wczytywanie z pliku...");

        // Krok 2: Wczytanie z pliku properties, jeśli zmienna środowiskowa jest pusta

        Configurations configs = new Configurations();
        Configuration cfg;
        try {
            // Wczytanie pliku properties z podanej ścieżki
            // Użycie new File() zamiast ścieżki (stringa) jest często bezpieczniejsze
            cfg = configs.properties(new File(PROPERTIES_FILE_PATH));
        } catch (ConfigurationException ex) {
            // Wyrzuć błąd, jeśli plik konfiguracyjny nie istnieje lub jest źle sformatowany
            throw new IOException("Błąd ładowania pliku konfiguracyjnego: " + PROPERTIES_FILE_PATH, ex);
        }

        // Odczyt klucza z pliku
        dir = cfg.getString(CONFIG_KEY);

        if (dir == null || dir.isBlank()) {
            // Zgodnie z Twoją oryginalną logiką: jeśli brakuje w pliku, wyrzuć błąd
            throw new IllegalStateException("ERROR: Klucz '" + CONFIG_KEY +
                    "' jest brakujący lub pusty w pliku: " + PROPERTIES_FILE_PATH);
        }

        // Użycie wartości z pliku
        String finalDir = dir.trim();
        System.out.println("✅ Ścieżka katalogu zamówień pobrana z pliku: " + CONFIG_KEY + " = " + finalDir);

        AppSettings s = new AppSettings();
        s.setOrdersDirectory(finalDir);
        return s;
    }
}