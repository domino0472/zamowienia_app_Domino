package pl.dominiak.orders.model;

import java.util.List;

public class OrderRequest {

    private Customer customer;
    private List<ProductItem> products;

    public Customer getCustomer() {return customer;}
    public void setCustomer(Customer customer) {this.customer = customer;}
    public List<ProductItem> getProducts() {return products;}
    public void setProducts(List<ProductItem> products) {this.products = products;}
}
