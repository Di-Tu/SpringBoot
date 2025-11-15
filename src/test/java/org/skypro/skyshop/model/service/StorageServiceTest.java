package org.skypro.skyshop.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;
import org.skypro.skyshop.model.search.Searchable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    private StorageService storageService;
    private Map<UUID, Product> productRepository;
    private Map<UUID, Article> articleRepository;

    @BeforeEach
    void setUp() throws Exception {
        storageService = new StorageService();
        Field productRepoField = StorageService.class.getDeclaredField("productRepository");
        Field articleRepoField = StorageService.class.getDeclaredField("articleRepository");
        productRepoField.setAccessible(true);
        articleRepoField.setAccessible(true);
        productRepository = (Map<UUID, Product>) productRepoField.get(storageService);
        articleRepository = (Map<UUID, Article>) articleRepoField.get(storageService);
        productRepository.clear();
        articleRepository.clear();
    }

    // 1. Тесты на модуль getAllSearchableItems
    //  Если оба репозитория пусты, то возвращается пустой список
    @Test
    void getAllSearchableItems_WhenBothRepositoriesEmpty_ShouldReturnEmptyList() {
        List<Searchable> result = storageService.getAllSearchableItems();
        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isEmpty(), "Список должен быть пустым");
        assertEquals(0, productRepository.size(), "Репозиторий продуктов должен быть пустым");
        assertEquals(0, articleRepository.size(), "Репозиторий статей должен быть пустым");
    }

    //  Если репозиторий продуктов пустой
    @Test
    void getAllSearchableItems_WhenProductRepositoryEmptyButArticleRepositoryNotEmpty_ShouldReturnOnlyArticles() {
        Article article1 = new Article("Статья 1", "Текст статьи 1");
        Article article2 = new Article("Статья 2", "Текст статьи 2");
        articleRepository.put(article1.getId(), article1);
        articleRepository.put(article2.getId(), article2);
        List<Searchable> result = storageService.getAllSearchableItems();
        assertNotNull(result, "Результат не должен быть null");
        assertEquals(articleRepository.size(), result.size(), "Количество элементов должно совпадать с количеством статей");
        assertEquals(0, productRepository.size(), "Репозиторий продуктов должен быть пустым");
        assertEquals(2, articleRepository.size(), "Репозиторий статей должен содержать 2 статьи");
        for (Searchable item : result) {
            assertEquals("ARTICLE", item.contentType(), "Все элементы должны быть статьями");
        }
    }

    //  Если репозиторий статей пустой
    @Test
    void getAllSearchableItems_WhenArticleRepositoryEmptyButProductRepositoryNotEmpty_ShouldReturnOnlyProducts() {
        Product product1 = new SimpleProduct("Продукт 1", 100);
        Product product2 = new SimpleProduct("Продукт 2", 200);
        Product product3 = new SimpleProduct("Продукт 3", 300);
        productRepository.put(product1.getId(), product1);
        productRepository.put(product2.getId(), product2);
        productRepository.put(product3.getId(), product3);
        List<Searchable> result = storageService.getAllSearchableItems();
        assertNotNull(result, "Результат не должен быть null");
        assertEquals(productRepository.size(), result.size(), "Количество элементов должно совпадать с количеством продуктов");
        assertEquals(3, productRepository.size(), "Репозиторий продуктов должен содержать 3 продукта");
        assertEquals(0, articleRepository.size(), "Репозиторий статей должен быть пустым");
        for (Searchable item : result) {
            assertEquals("PRODUCT", item.contentType(), "Все элементы должны быть продуктами");
        }
    }

    //  Оба репозитория заполнены
    @Test
    void getAllSearchableItems_WhenBothRepositoriesNotEmpty_ShouldReturnAllItems() {
        Product product1 = new SimpleProduct("Продукт 1", 100);
        Product product2 = new SimpleProduct("Продукт 2", 200);
        Article article1 = new Article("Статья 1", "Текст статьи 1");
        Article article2 = new Article("Статья 2", "Текст статьи 2");
        Article article3 = new Article("Статья 3", "Текст статьи 3");
        productRepository.put(product1.getId(), product1);
        productRepository.put(product2.getId(), product2);
        articleRepository.put(article1.getId(), article1);
        articleRepository.put(article2.getId(), article2);
        articleRepository.put(article3.getId(), article3);
        int expectedTotalSize = productRepository.size() + articleRepository.size();
        List<Searchable> result = storageService.getAllSearchableItems();
        assertNotNull(result, "Результат не должен быть null");
        assertEquals(expectedTotalSize, result.size(), "Количество элементов должно совпадать с суммой продуктов и статей");
        assertEquals(2, productRepository.size(), "Репозиторий продуктов должен содержать 2 продукта");
        assertEquals(3, articleRepository.size(), "Репозиторий статей должен содержать 3 статьи");
        long productCount = result.stream().filter(item -> "PRODUCT".equals(item.contentType())).count();
        long articleCount = result.stream().filter(item -> "ARTICLE".equals(item.contentType())).count();
        assertEquals(2, productCount, "Должно быть 2 продукта в результате");
        assertEquals(3, articleCount, "Должно быть 3 статьи в результате");
    }

    // 2. Тесты на модуль getProductById

    //  Проверяем на возврат пустого Optional при пустом репозитории
    @Test
    void getProductById_WhenProductRepositoryEmpty_ShouldReturnEmptyOptional() {
        UUID anyId = UUID.randomUUID();
        Optional<Product> result = storageService.getProductById(anyId);
        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isEmpty(), "Optional должен быть пустым");
        assertEquals(0, productRepository.size(), "Репозиторий продуктов должен быть пустым");
    }

    //  Проверяем поведение при поиске несуществующего ID
    @Test
    void getProductById_WhenProductRepositoryNotEmptyButIdNotFound_ShouldReturnEmptyOptional() {
        Product product1 = new SimpleProduct("Продукт 1", 100);
        Product product2 = new SimpleProduct("Продукт 2", 200);
        productRepository.put(product1.getId(), product1);
        productRepository.put(product2.getId(), product2);
        UUID nonExistingId = UUID.randomUUID(); // ID, которого точно нет в репозитории
        Optional<Product> result = storageService.getProductById(nonExistingId);
        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isEmpty(), "Optional должен быть пустым для несуществующего ID");
        assertEquals(2, productRepository.size(), "Репозиторий продуктов должен содержать 2 продукта");
    }

    //  Проверяем корректное возвращение продукта
    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        Product testProduct = new SimpleProduct("Тестовый продукт", 150);
        productRepository.put(testProduct.getId(), testProduct);
        UUID existingProductId = testProduct.getId();
        Optional<Product> result = storageService.getProductById(existingProductId);
        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isPresent(), "Optional должен содержать продукт");
        assertEquals(existingProductId, result.get().getId(), "ID продукта должен совпадать");
        assertEquals("Тестовый продукт", result.get().getName(), "Название продукта должно совпадать");
        assertEquals(150, result.get().getPrice(), "Цена продукта должна совпадать");
    }

    //  Проверяем обработку null значения
    @Test
    void getProductById_WhenIdIsNull_ShouldReturnEmptyOptional() {
        Optional<Product> result = storageService.getProductById(null);
        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isEmpty(), "Optional должен быть пустым для null ID");
    }
}
