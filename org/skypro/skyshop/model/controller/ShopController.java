package org.skypro.skyshop.model.controller;

import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.basket.UserBasket;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.search.SearchResult;
import org.skypro.skyshop.model.service.BasketService;
import org.skypro.skyshop.model.service.SearchService;
import org.skypro.skyshop.model.service.StorageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class ShopController {
    private final StorageService storageService;
    private final SearchService searchService;
    private final BasketService basketService;


    public ShopController(StorageService storageService, SearchService searchService, BasketService basketService) {
        this.storageService = storageService;
        this.searchService = searchService;
        this.basketService = basketService;

    }

    @GetMapping("/shop/basket/{id}")
    public String addProduct(@PathVariable("id") UUID id) {
        try {
            basketService.addProductToBasket(id);
            return "Продукт успешно добавлен";
        } catch (IllegalArgumentException e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    @GetMapping("/shop/basket")
    public UserBasket getUserBasket() {
        return basketService.getUserBasket();
    }

    @GetMapping("/products")
    public Map<UUID, Product> getAllProducts() {
        return storageService.getProductRepository();
    }

    @GetMapping("/articles")
    public Map<UUID, Article> getAllArticles() {
        return storageService.getArticleRepository();
    }

    @GetMapping("/search")
    public List<SearchResult> searchBar(@RequestParam("pattern") String query) {
        return searchService.search(query);
    }
}
