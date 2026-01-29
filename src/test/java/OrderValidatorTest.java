package pl.dominiak.orders.logic;

import com.github.nylle.javafixture.JavaFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.dominiak.orders.model.OrderRequest;
import pl.dominiak.orders.model.ProductItem;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderValidatorTest {

    private OrderValidator validator;
    private JavaFixture fixture;

    @BeforeEach
    void setUp() {
        validator = new OrderValidator();
        fixture = new JavaFixture();
    }

    @Test
    @DisplayName("Powinien zaakceptować poprawne zamówienie (Happy Path)")
    void shouldAcceptValidOrder() {
        // Arrange
        OrderRequest order = createValidOrder();

        // Act
        boolean result = validator.validate(order);

        // Assert
        assertTrue(result, "Poprawne zamówienie powinno zostać zaakceptowane");
    }

    @Test
    @DisplayName("Powinien odrzucić zamówienie bez danych klienta")
    void shouldRejectMissingCustomer() {
        OrderRequest order = createValidOrder();
        order.setCustomer(null); // Psucie danych

        assertFalse(validator.validate(order));
    }

    @Test
    @DisplayName("Powinien odrzucić zamówienie z pustym imieniem")
    void shouldRejectEmptyFirstName() {
        OrderRequest order = createValidOrder();
        order.getCustomer().setFirstName(""); // Psucie danych

        assertFalse(validator.validate(order));
    }

    @Test
    @DisplayName("Powinien odrzucić zamówienie bez produktów")
    void shouldRejectEmptyProductList() {
        OrderRequest order = createValidOrder();
        order.setProducts(Collections.emptyList()); // 0 produktów

        assertFalse(validator.validate(order));
    }

    @Test
    @DisplayName("Powinien odrzucić zamówienie ze zbyt dużą liczbą produktów (>9)")
    void shouldRejectTooManyProducts() {
        OrderRequest order = createValidOrder();
        // Generujemy listę 10 produktów
        List<ProductItem> manyProducts = IntStream.range(0, 10)
                .mapToObj(i -> {
                    ProductItem item = fixture.create(ProductItem.class);
                    item.setQuantity(BigDecimal.ONE);
                    item.setUnit("KG");
                    return item;
                })
                .toList();
        order.setProducts(manyProducts);

        assertFalse(validator.validate(order));
    }

    @Test
    @DisplayName("Powinien odrzucić produkt z nieznaną jednostką miary")
    void shouldRejectInvalidUnit() {
        OrderRequest order = createValidOrder();
        order.getProducts().get(0).setUnit("LITR"); // Błędna jednostka

        assertFalse(validator.validate(order));
    }

    @Test
    @DisplayName("Powinien odrzucić produkt z ujemną ilością")
    void shouldRejectNegativeQuantity() {
        OrderRequest order = createValidOrder();
        order.getProducts().get(0).setQuantity(new BigDecimal("-5"));

        assertFalse(validator.validate(order));
    }

    @Test
    @DisplayName("Powinien odrzucić zamówienie przekraczające 2 tony")
    void shouldRejectOverweightOrder() {
        OrderRequest order = createValidOrder();
        // Ustawiamy 3000 KG (limit to 2000 KG)
        order.getProducts().get(0).setQuantity(new BigDecimal("3000"));
        order.getProducts().get(0).setUnit("KG");

        assertFalse(validator.validate(order));
    }

    /**
     * Metoda pomocnicza tworząca poprawne zamówienie na bazie losowych danych z JavaFixture.
     * Ponieważ JavaFixture generuje całkowicie losowe stringi i liczby, musimy "poprawić"
     * kluczowe pola, aby spełniały reguły walidacji (np. jednostka musi być KG, ilość > 0).
     */
    private OrderRequest createValidOrder() {
        // 1. Generujemy losowy obiekt (wszystkie pola wypełnione losowo)
        OrderRequest order = fixture.create(OrderRequest.class);

        // 2. Naprawiamy dane klienta (na wypadek gdyby wylosował pusty string)
        order.getCustomer().setFirstName("Jan");
        order.getCustomer().setLastName("Kowalski");

        // 3. Naprawiamy produkty
        // Zostawiamy tylko 1 produkt dla uproszczenia
        ProductItem validItem = new ProductItem();
        validItem.setProductCode("TEST-SKU-1");
        validItem.setQuantity(new BigDecimal("10")); // 10 jednostek
        validItem.setUnit("KG"); // Poprawna jednostka

        order.setProducts(List.of(validItem));

        return order;
    }
}