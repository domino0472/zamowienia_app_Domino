package pl.dominiak.orders.config;

public class AppSettings {
    private String ordersDirectory;
    private String version;

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public String getOrdersDirectory() {return ordersDirectory;}
    public void setOrdersDirectory(String ordersDirectory) {this.ordersDirectory = ordersDirectory;}

}
