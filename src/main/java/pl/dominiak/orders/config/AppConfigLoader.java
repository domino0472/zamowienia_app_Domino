package pl.dominiak.orders.config;


import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;



public final class AppConfigLoader {
    private AppConfigLoader() {
    }

    public static AppSettings load() throws Exception {
        Configurations configs = new Configurations();
        Configuration cfg = configs.properties("Proporties/appsettings.properties");

        String dir = cfg.getString("orders.directory");
        if (dir == null || dir.isBlank()) {
            throw new IllegalStateException("orders.directory is missing in appsettings.properties");

        }

        AppSettings s = new AppSettings();
        s.setOrdersDirectory(dir.trim());
        return s;

    }
}
