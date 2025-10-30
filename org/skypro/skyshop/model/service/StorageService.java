package org.skypro.skyshop.model.service;

import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.product.DiscountedProduct;
import org.skypro.skyshop.model.product.FixPriceProduct;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;
import org.skypro.skyshop.model.search.Searchable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StorageService {
    private final Map<UUID, Product> productRepository = new HashMap<>();
    private final Map<UUID, Article> articleRepository = new HashMap<>();

    public StorageService() {
        libraryProducts();
    }

    public List<Searchable> getAllSearchableItems() {
        List<Searchable> searchableItems = new ArrayList<>();
        searchableItems.addAll(productRepository.values());
        searchableItems.addAll(articleRepository.values());
        return searchableItems;
    }

    public Map<UUID, Product> getProductRepository() {
        return this.productRepository;
    }

    public Map<UUID, Article> getArticleRepository() {
        return this.articleRepository;
    }

    private void libraryProducts() {
        Product orange = new SimpleProduct("Апельсин", 130);
        Product apple = new SimpleProduct("Яблоко", 72);
        Product bread = new SimpleProduct("Хлеб", 57);
        Product sourCream = new SimpleProduct("Сметана", 103);
        Product sugar = new SimpleProduct("Сахар", 78);
        Product mango = new SimpleProduct("Манго", 251);
        Product salt = new DiscountedProduct("Соль", 50, 10);
        Product milk = new DiscountedProduct("Молоко", 90, 15);
        Product saltShaker = new FixPriceProduct("Солонка");
        Product soap = new FixPriceProduct("Мыло");
        Product bread1 = new SimpleProduct("Хлеб1", 51);
        Product bread2 = new SimpleProduct("Хлеб2", 52);
        Product bread3 = new SimpleProduct("Хлеб3", 53);
        Product bread5 = new SimpleProduct("Хлеб5", 83);
        Product bread4 = new SimpleProduct("Хлеб9", 73);
        Product bread6 = new SimpleProduct("Хлеб6", 76);
        Product bread7 = new SimpleProduct("Хлеб6", 53);
        Article first = new Article("Первый", "Первый он и есть первый");
        Article second = new Article("Второй", "Второй он и есть второй");
        Article breadHead = new Article("ХлебГолова", "Хлеб всему голова");
        Article breadHead1 = new Article("ХлебГолова1", "Хлеб и Соль всему  голова и еще голова");
        Article third = new Article("Хлеб4", "Третий он всегда третий Хлеб");
        Article third1 = new Article("Хлеб4", "Третий Хлеб");
        Article third3 = new Article("Хлеб 4 по 4", "Четвертый по четвертому");
        Article third2 = new Article("Хлеб 4 по 4", "Четвертый хлеб ");

        this.productRepository.put(orange.getId(), orange);
        this.productRepository.put(apple.getId(), apple);
        this.productRepository.put(bread.getId(), bread);
        this.productRepository.put(sourCream.getId(), sourCream);
        this.productRepository.put(sugar.getId(), sugar);
        this.productRepository.put(mango.getId(), mango);
        this.productRepository.put(salt.getId(), salt);
        this.productRepository.put(milk.getId(), milk);
        this.productRepository.put(saltShaker.getId(), saltShaker);
        this.productRepository.put(soap.getId(), soap);
        this.productRepository.put(bread1.getId(), bread1);
        this.productRepository.put(bread2.getId(), bread2);
        this.productRepository.put(bread3.getId(), bread3);
        this.productRepository.put(bread4.getId(), bread4);
        this.productRepository.put(bread5.getId(), bread5);
        this.productRepository.put(bread6.getId(), bread6);
        this.productRepository.put(bread7.getId(), bread7);
        this.articleRepository.put(first.getId(), first);
        this.articleRepository.put(second.getId(), second);
        this.articleRepository.put(breadHead.getId(), breadHead);
        this.articleRepository.put(breadHead1.getId(), breadHead1);
        this.articleRepository.put(third.getId(), third);
        this.articleRepository.put(third1.getId(), third1);
        this.articleRepository.put(third2.getId(), third2);
        this.articleRepository.put(third3.getId(), third3);
    }
}
