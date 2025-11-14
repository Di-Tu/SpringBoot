package org.skypro.skyshop.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.product.SimpleProduct;
import org.skypro.skyshop.model.search.SearchResult;
import org.skypro.skyshop.model.search.Searchable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private SearchService searchService;
    private Article article1;
    private Article article2;
    private Article article3;
    private SimpleProduct product1;
    private SimpleProduct product2;
    private SimpleProduct product3;

    @BeforeEach
    void setUp() {
        article1 = new Article("Свежий Хлеб", "Статья о свежем хлебе");
        article2 = new Article("Молочные Продукты", "Статья о молочных продуктах");
        article3 = new Article("Хлеб Домашний", "Рецепт домашнего хлеба");
        product1 = new SimpleProduct("Хлеб Белый", 50);
        product2 = new SimpleProduct("Молоко", 80);
        product3 = new SimpleProduct("Хлеб Ржаной", 60);
    }

    // Если не находит, то возвращает пустой список
    @Test
    void search_WhenNoMatches_ShouldReturnEmptyList() {
        String query = "Рыба";
        List<Searchable> allItems = Arrays.asList(article1, article2, product1, product2);
        when(storageService.getAllSearchableItems()).thenReturn(allItems);
        List<SearchResult> result = searchService.search(query);
        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isEmpty(), "Список должен быть пустым при отсутствии совпадений");
    }

    //Проверяется точное совпадение в названии продукта
    @Test
    void search_WhenExactMatchInProduct_ShouldReturnOneProduct() {
        String query = "Хлеб Белый";
        List<Searchable> allItems = Arrays.asList(article1, article2, product1, product2, product3);
        when(storageService.getAllSearchableItems()).thenReturn(allItems);
        List<SearchResult> result = searchService.search(query);
        assertNotNull(result, "Результат не должен быть null");
        assertEquals(1, result.size(), "Должен вернуться один результат");
        SearchResult searchResult = result.get(0);
        assertEquals(product1.getId().toString(), searchResult.getId(), "ID должен совпадать");
        assertEquals("PRODUCT", searchResult.getContentType(), "Тип контента должен быть PRODUCT");
    }

    //Проверяется точное совпадение в заголовке статьи
    @Test
    void search_WhenExactMatchInArticle_ShouldReturnOneArticle() {
        // Arrange
        String query = "Свежий Хлеб";
        List<Searchable> allItems = Arrays.asList(article1, article2, product1, product2);
        when(storageService.getAllSearchableItems()).thenReturn(allItems);
        List<SearchResult> result = searchService.search(query);
        assertNotNull(result, "Результат не должен быть null");
        assertEquals(1, result.size(), "Должен вернуться один результат");
        SearchResult searchResult = result.get(0);
        assertEquals(article1.getId().toString(), searchResult.getId(), "ID должен совпадать");
        assertEquals("ARTICLE", searchResult.getContentType(), "Тип контента должен быть ARTICLE");
    }

    //Проверяется частичное совпадение в нескольких элементах
    @Test
    void search_WhenPartialMatch_ShouldReturnMultipleResults() {
        String query = "Хлеб";
        List<Searchable> allItems = Arrays.asList(article1, article2, article3, product1, product2, product3);
        when(storageService.getAllSearchableItems()).thenReturn(allItems);
        List<SearchResult> result = searchService.search(query);
        assertNotNull(result, "Результат не должен быть null");
        assertEquals(4, result.size(), "Должны вернуться четыре результата (2 статьи и 2 продукта)");
        long articleCount = result.stream().filter(r -> "ARTICLE".equals(r.getContentType())).count();
        long productCount = result.stream().filter(r -> "PRODUCT".equals(r.getContentType())).count();
        assertEquals(2, articleCount, "Должно быть 2 статьи");
        assertEquals(2, productCount, "Должно быть 2 продукта");
    }

    //Проверяется регистрозависимость поиска
    @Test
    void search_WhenCaseSensitiveQuery_ShouldRespectCase() {
        String queryLowercase = "хлеб";
        String queryUppercase = "ХЛЕБ";
        String queryMixed = "Хлеб";
        List<Searchable> allItems = Arrays.asList(article1, article3, product1, product3);
        when(storageService.getAllSearchableItems()).thenReturn(allItems);
        List<SearchResult> resultLowercase = searchService.search(queryLowercase);
        assertEquals(0, resultLowercase.size(), "Не должно быть результатов для 'хлеб' в нижнем регистре");
        List<SearchResult> resultUppercase = searchService.search(queryUppercase);
        assertEquals(0, resultUppercase.size(), "Не должно быть результатов для 'ХЛЕБ' в верхнем регистре");
        List<SearchResult> resultMixed = searchService.search(queryMixed);
        assertEquals(4, resultMixed.size(), "Должно найти 4 результата для 'Хлеб' в правильном регистре");
    }

    //Проверяем поведение при пустом запросе
    @Test
    void search_WhenQueryIsEmpty_ShouldReturnAllItems() {
        String query = "";
        List<Searchable> allItems = Arrays.asList(article1, article2, product1, product2);
        when(storageService.getAllSearchableItems()).thenReturn(allItems);
        List<SearchResult> result = searchService.search(query);
        assertNotNull(result, "Результат не должен быть null");
        assertEquals(allItems.size(), result.size(), "Должны вернуться все элементы при пустом запросе");
    }

    //Проверяем поведение при пустом хранилище
    @Test
    void search_WhenStorageServiceReturnsEmptyList_ShouldReturnEmptyList() {
        String query = "Хлеб";
        List<Searchable> emptyList = Arrays.asList();
        when(storageService.getAllSearchableItems()).thenReturn(emptyList);
        List<SearchResult> result = searchService.search(query);
        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isEmpty(), "Список должен быть пустым когда хранилище пустое");
    }

    // Тест проверяет поиск слова в разных позициях строки.
    @Test
    void search_WhenWordAtDifferentPositions_ShouldFindAll() {
        String query = "Молок";
        Article articleWithMilk = new Article("Польза Молока", "О пользе молока"); // содержит "Молок" в "Молока"
        SimpleProduct milkProduct = new SimpleProduct("Молоко Пастеризованное", 85); // содержит "Молок" в "Молоко"
        SimpleProduct yogurtProduct = new SimpleProduct("Йогурт Молочный", 45); // НЕ содержит "Молок" - содержит "Молочный"
        List<Searchable> allItems = Arrays.asList(articleWithMilk, milkProduct, yogurtProduct, article1, product1);
        when(storageService.getAllSearchableItems()).thenReturn(allItems);
        List<SearchResult> result = searchService.search(query);
        assertNotNull(result, "Результат не должен быть null");
        assertEquals(2, result.size(), "Должны вернуться два результата с точной подстрокой 'Молок'");
        for (SearchResult searchResult : result) {
            assertTrue(searchResult.getName().contains("Молок"), "Каждый результат должен содержать точную подстроку 'Молок'");
        }
        boolean yogurtFound = result.stream().anyMatch(r -> r.getName().contains("Йогурт"));
        assertFalse(yogurtFound, "Йогурт Молочный не должен быть найден, так как не содержит 'Молок'");
    }
}
