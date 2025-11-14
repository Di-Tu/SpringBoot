package org.skypro.skyshop.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.skyshop.model.basket.BasketItem;
import org.skypro.skyshop.model.basket.ProductBasket;
import org.skypro.skyshop.model.basket.UserBasket;
import org.skypro.skyshop.model.exceptions.NoSuchProductException;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private ProductBasket productBasket;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private BasketService basketService;
    private Product existingProduct;
    private Product anotherProduct;
    private UUID existingProductId;
    private UUID anotherProductId;
    private UUID nonExistingProductId;

    @BeforeEach
    void setUp() {
        existingProduct = new SimpleProduct("Хлеб", 50);
        anotherProduct = new SimpleProduct("Молоко", 80);
        existingProductId = existingProduct.getId();
        anotherProductId = anotherProduct.getId();
        nonExistingProductId = UUID.randomUUID();
    }

    // Тест проверяет, что при попытке добавить несуществующий товар:
    // Выбрасывается исключение NoSuchProductException с правильным сообщением
    @Test
    void addProductToBasket_WhenProductDoesNotExist_ShouldThrowNoSuchProductException() {
        when(storageService.getProductById(nonExistingProductId)).thenReturn(Optional.empty());
        NoSuchProductException exception = assertThrows(NoSuchProductException.class, () -> basketService.addProductToBasket(nonExistingProductId), "Должно быть выброшено NoSuchProductException для несуществующего товара");
        assertEquals("Продукт с ID " + nonExistingProductId + " не найден", exception.getMessage(), "Сообщение об ошибке должно содержать ID несуществующего товара");
        verify(productBasket, never()).addProduct(any(UUID.class));
        verify(storageService, times(1)).getProductById(nonExistingProductId);
        verifyNoMoreInteractions(storageService, productBasket);
    }

    // Тест проверяет, что при добавлении существующего товара, не возникает исключений
    // и вызывается метод addProduct у ProductBasket с правильным ID
    @Test
    void addProductToBasket_WhenProductExists_ShouldCallProductBasketAdd() {
        when(storageService.getProductById(existingProductId)).thenReturn(Optional.of(existingProduct));
        assertDoesNotThrow(() -> basketService.addProductToBasket(existingProductId), "Не должно быть исключений при добавлении существующего товара");
        verify(storageService, times(1)).getProductById(existingProductId);
        verify(productBasket, times(1)).addProduct(existingProductId);
        verifyNoMoreInteractions(storageService, productBasket);
    }

    // Тест проверяет, что при пустой корзине возвращается пустой UserBasket
    @Test
    void getUserBasket_WhenProductBasketIsEmpty_ShouldReturnEmptyUserBasket() {
        when(productBasket.getBasket()).thenReturn(Collections.emptyMap());
        UserBasket result = basketService.getUserBasket();
        assertNotNull(result, "UserBasket не должен быть null");
        assertTrue(result.getItems().isEmpty(), "Список товаров должен быть пустым");
        assertEquals(0, result.getTotal(), "Общая стоимость должна быть 0");
        verify(productBasket, times(1)).getBasket();
        verifyNoInteractions(storageService);
    }

    // Тест проверяет, что при наличии товаров в ProductBasket
    // возвращается корректный UserBasket с правильными данными:
    //  - Правильное количество элементов
    //  - Корректная общая стоимость
    //  - Правильные данные о товарах и их количествах
    @Test
    void getUserBasket_WhenProductBasketHasItems_ShouldReturnCorrectUserBasket() {
        Map<UUID, Integer> basketItems = new HashMap<>();
        basketItems.put(existingProductId, 2); // 2 хлеба
        basketItems.put(anotherProductId, 1);  // 1 молоко
        when(productBasket.getBasket()).thenReturn(basketItems);
        when(storageService.getProductById(existingProductId)).thenReturn(Optional.of(existingProduct));
        when(storageService.getProductById(anotherProductId)).thenReturn(Optional.of(anotherProduct));
        int expectedTotal = 180;
        UserBasket result = basketService.getUserBasket();
        assertNotNull(result, "UserBasket не должен быть null");
        assertEquals(2, result.getItems().size(), "Должно быть 2 элемента в корзине");
        assertEquals(expectedTotal, result.getTotal(), "Общая стоимость должна быть рассчитана правильно");
        List<BasketItem> items = result.getItems();
        BasketItem breadItem = findBasketItemByProductId(items, existingProductId);
        assertNotNull(breadItem, "Должен присутствовать хлеб в корзине");
        assertEquals(existingProduct, breadItem.getProduct(), "Продукт должен совпадать");
        assertEquals(2, breadItem.getQuantity(), "Количество хлеба должно быть 2");
        BasketItem milkItem = findBasketItemByProductId(items, anotherProductId);
        assertNotNull(milkItem, "Должно присутствовать молоко в корзине");
        assertEquals(anotherProduct, milkItem.getProduct(), "Продукт должен совпадать");
        assertEquals(1, milkItem.getQuantity(), "Количество молока должно быть 1");
        verify(productBasket, times(1)).getBasket();
        verify(storageService, times(1)).getProductById(existingProductId);
        verify(storageService, times(1)).getProductById(anotherProductId);
    }

    // Тест проверяет корректность расчета общей стоимости при разных количествах товаров
    @Test
    void getUserBasket_WithDifferentQuantities_ShouldCalculateTotalCorrectly() {
        Map<UUID, Integer> basketItems = new HashMap<>();
        basketItems.put(existingProductId, 5); // 5 хлебов по 50 = 250
        when(productBasket.getBasket()).thenReturn(basketItems);
        when(storageService.getProductById(existingProductId)).thenReturn(Optional.of(existingProduct));
        UserBasket result = basketService.getUserBasket();
        assertEquals(250, result.getTotal(), "Общая стоимость: 5 * 50 = 250");
        assertEquals(1, result.getItems().size(), "Должен быть один товар в корзине");
        BasketItem item = result.getItems().get(0);
        assertEquals(5, item.getQuantity(), "Количество должно быть 5");
        assertEquals(existingProduct, item.getProduct(), "Продукт должен совпадать");
    }

    // Тест проверяет, что несколько вызовов addProduct для одного товара
    // корректно обрабатываются ProductBasket - каждый вызов приводит
    // к увеличению количества товара в корзине
    @Test
    void addProductToBasket_WhenCalledMultipleTimesForSameProduct_ShouldCallProductBasketEachTime() {
        when(storageService.getProductById(existingProductId)).thenReturn(Optional.of(existingProduct));
        basketService.addProductToBasket(existingProductId);
        basketService.addProductToBasket(existingProductId);
        basketService.addProductToBasket(existingProductId);
        verify(storageService, times(3)).getProductById(existingProductId);
        verify(productBasket, times(3)).addProduct(existingProductId);
    }

    // Вспомогательный метод для поиска BasketItem по ID продукта
    private BasketItem findBasketItemByProductId(List<BasketItem> items, UUID productId) {
        return items.stream().filter(item -> item.getProduct().getId().equals(productId)).findFirst().orElse(null);
    }
}