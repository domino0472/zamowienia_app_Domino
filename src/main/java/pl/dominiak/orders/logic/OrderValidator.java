package pl.dominiak.orders.logic;

import pl.dominiak.orders.model.Customer;
import pl.dominiak.orders.model.OrderRequest;
import pl.dominiak.orders.model.ProductItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class OrderValidator {

    // Zbiór dozwolonych jednostek (wielkość liter nie będzie miała znaczenia w logice)
    private static final Set<String> ALLOWED_UNITS = Set.of("GRAM", "KILOGRAM", "TONA", "G", "KG", "T");
    private static final BigDecimal MAX_WEIGHT_KG = new BigDecimal("2000"); // 2 tony

    public boolean validate(OrderRequest request) {
        if (request == null) {
            return false;
        }

        // --- WARUNEK 1: Imię i nazwisko nie mogą być puste ---
        Customer customer = request.getCustomer();
        if (customer == null || isEmpty(customer.getFirstName()) || isEmpty(customer.getLastName())) {
            System.err.println("Walidacja nieudana: Brak danych klienta.");
            return false;
        }

        // --- WARUNEK 2: Liczba produktów od 1 do 9 ---
        List<ProductItem> products = request.getProducts();
        if (products == null || products.isEmpty() || products.size() > 9) {
            System.err.println("Walidacja nieudana: Nieprawidłowa liczba produktów (" + (products == null ? 0 : products.size()) + ").");
            return false;
        }

        BigDecimal totalWeightInKg = BigDecimal.ZERO;

        for (ProductItem item : products) {
            // --- WARUNEK 4: Ilość musi być dodatnia ---
            if (item.getQuantity() == null || item.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                System.err.println("Walidacja nieudana: Ilość produktu musi być dodatnia.");
                return false;
            }

            // --- WARUNEK 3: Dozwolone jednostki (gram, kilogram, tona) ---
            String unit = item.getUnit() == null ? "" : item.getUnit().toUpperCase().trim();
            if (!ALLOWED_UNITS.contains(unit)) {
                System.err.println("Walidacja nieudana: Nieobsługiwana jednostka miary: " + unit);
                return false;
            }

            // Przeliczamy wagę elementu na KG, aby sprawdzić limit całkowity
            BigDecimal weightInKg = convertToKg(item.getQuantity(), unit);
            totalWeightInKg = totalWeightInKg.add(weightInKg);
        }

        // --- WARUNEK 5: Całkowita waga <= 2 tony (2000 kg) ---
        if (totalWeightInKg.compareTo(MAX_WEIGHT_KG) > 0) {
            System.err.println("Walidacja nieudana: Przekroczono limit wagi (Limit: 2000kg, Obecnie: " + totalWeightInKg + "kg).");
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
            default -> quantity; // Zakładamy KILOGRAM/KG jako bazę
        };
    }
}