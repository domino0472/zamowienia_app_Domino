package pl.dominiak.orders.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.dominiak.orders.model.Customer;
import pl.dominiak.orders.model.OrderRequest;
import pl.dominiak.orders.model.ProductItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class OrderValidator {

    private static final Logger logger = LogManager.getLogger();
    private static final Set<String> ALLOWED_UNITS = Set.of("GRAM", "KILOGRAM", "TONA", "G", "KG", "T");
    private static final BigDecimal MAX_WEIGHT_KG = new BigDecimal("2000");

    public boolean validate(OrderRequest request) {
        if (request == null) {
            return false;
        }

        Customer customer = request.getCustomer();
        if (customer == null || isEmpty(customer.getFirstName()) || isEmpty(customer.getLastName())) {
            logger.warn("Walidacja nieudana: Brak danych klienta.");
            return false;
        }

        List<ProductItem> products = request.getProducts();
        if (products == null || products.isEmpty() || products.size() > 9) {
            logger.warn("Walidacja nieudana: Nieprawidłowa liczba produktów ({}).", (products == null ? 0 : products.size()));
            return false;
        }

        BigDecimal totalWeightInKg = BigDecimal.ZERO;

        for (ProductItem item : products) {
            if (item.getQuantity() == null || item.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Walidacja nieudana: Ilość produktu musi być dodatnia.");
                return false;
            }

            String unit = item.getUnit() == null ? "" : item.getUnit().toUpperCase().trim();
            if (!ALLOWED_UNITS.contains(unit)) {
                logger.warn("Walidacja nieudana: Nieobsługiwana jednostka miary: {}", unit);
                return false;
            }

            BigDecimal weightInKg = convertToKg(item.getQuantity(), unit);
            totalWeightInKg = totalWeightInKg.add(weightInKg);
        }

        if (totalWeightInKg.compareTo(MAX_WEIGHT_KG) > 0) {
            logger.warn("Walidacja nieudana: Przekroczono limit wagi (Limit: 2000kg, Obecnie: {}kg).", totalWeightInKg);
            return false;
        }

        return true;
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    private BigDecimal convertToKg(BigDecimal quantity, String unit) {
        return switch (unit) {
            case "GRAM", "G" -> quantity.divide(BigDecimal.valueOf(1000));
            case "TONA", "T" -> quantity.multiply(BigDecimal.valueOf(1000));
            default -> quantity;
        };
    }
}