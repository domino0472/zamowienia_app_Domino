package pl.dominiak.orders.util;

import pl.dominiak.orders.model.Customer;
import pl.dominiak.orders.model.OrderRequest;
import pl.dominiak.orders.model.ProductItem;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


public final class OrderNumberGenerator {
    private OrderNumberGenerator() {}

    public static String generate(OrderRequest req) {
        int items = req.getProducts() == null ? 0 : req.getProducts().size();
        String initials = init(req.getCustomer());

        // Canonical string with all fields
        StringBuilder sb = new StringBuilder();
        if (req.getCustomer() != null) {
            Customer c = req.getCustomer();
            sb.append(nz(c.getFirstName())).append('|')
                    .append(nz(c.getLastName())).append('|')
                    .append(nz(c.getEmail())).append('|')
                    .append(nz(c.getPhone())).append('|');
        }
        if (req.getProducts() != null) {
            req.getProducts().stream()
                    .sorted((a, b) -> nz(a.getProductCode()).compareToIgnoreCase(nz(b.getProductCode())))
                    .forEach(p -> sb.append(nz(p.getProductCode())).append(':')
                            .append(p.getQuantity() != null ? p.getQuantity().toPlainString() : "0")
                            .append(':')
                            .append(nz(p.getUnit())).append('|'));
        }

        // Use 16-character hash for better uniqueness (no date - fully deterministic)
        String hash16 = sha256Hex(sb.toString()).substring(0, 16).toUpperCase();
        return "OR-" + initials + "-" + items + "-" + hash16;
    }

    private static String nz(String v) {
        return v == null ? "" : v.trim();
    }

    private static String init(Customer c) {
        if (c == null) return "XX";
        char f = c.getFirstName() != null && !c.getFirstName().isEmpty()
                ? c.getFirstName().charAt(0) : 'X';
        char l = c.getLastName() != null && !c.getLastName().isEmpty()
                ? c.getLastName().charAt(0) : 'X';
        return ("" + Character.toUpperCase(f) + Character.toUpperCase(l));
    }

    private static String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(d.length * 2);
            for (byte b : d) {
                String h = Integer.toHexString(b & 0xff);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}