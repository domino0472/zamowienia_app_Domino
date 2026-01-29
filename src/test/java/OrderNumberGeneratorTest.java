package pl.dominiak.orders.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.dominiak.orders.model.Customer;
import pl.dominiak.orders.model.OrderRequest;
import pl.dominiak.orders.model.ProductItem;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderNumberGeneratorTest {

    @Test
    @DisplayName("Powinien wygenerować poprawny format numeru zamówienia")
    void shouldGenerateCorrectOrderNumberFormat() {
        // Arrange
        OrderRequest request = new OrderRequest();

        Customer customer = new Customer();
        customer.setFirstName("Jan");
        customer.setLastName("Kowalski");
        request.setCustomer(customer);

        // Dodajemy 1 produkt, aby licznik produktów wynosił 1
        ProductItem item = new ProductItem();
        request.setProducts(List.of(item));

        // Act
        String result = OrderNumberGenerator.generate(request);

        // Assert
        // Oczekiwany format: OR-<Inicjały>-<LiczbaProduktów>-<Hash16>
        // Dla Jan Kowalski i 1 produktu: OR-JK-1-...

        assertNotNull(result);
        System.out.println("Wygenerowany numer: " + result); // Podgląd w konsoli

        // Sprawdzamy prefiks
        assertTrue(result.startsWith("OR-JK-1-"), "Numer powinien zaczynać się od OR-JK-1-");

        // Sprawdzamy długość:
        // "OR-" (3) + "JK" (2) + "-" (1) + "1" (1) + "-" (1) + Hash (16) = 24 znaki
        assertEquals(24, result.length(), "Numer zamówienia powinien mieć dokładnie 24 znaki");
    }

    @Test
    @DisplayName("Powinien obsłużyć brakujące dane klienta (inicjały XX)")
    void shouldHandleMissingCustomer() {
        // Arrange - puste zamówienie
        OrderRequest request = new OrderRequest();
        request.setProducts(Collections.emptyList());

        // Act
        String result = OrderNumberGenerator.generate(request);

        // Assert
        // Brak klienta -> Inicjały XX, 0 produktów -> OR-XX-0-...
        assertTrue(result.startsWith("OR-XX-0-"));
    }
}