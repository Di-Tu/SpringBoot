package org.skypro.skyshop.model.service;

import org.skypro.skyshop.model.basket.BasketItem;
import org.skypro.skyshop.model.basket.ProductBasket;
import org.skypro.skyshop.model.basket.UserBasket;
import org.skypro.skyshop.model.exceptions.NoSuchProductException;
import org.skypro.skyshop.model.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BasketService {
    private final ProductBasket productBasket;
    private final StorageService storageService;

    public BasketService(ProductBasket productBasket, StorageService storageService) {
        this.productBasket = productBasket;
        this.storageService = storageService;
    }

    public void addProductToBasket(UUID productId) {
        Optional<Product> result = this.storageService.getProductById(productId);
        if (!result.isPresent()) {
            throw new NoSuchProductException("Продукт с ID " + productId + " не найден");
        }
        this.productBasket.addProduct(productId);
    }

    public UserBasket getUserBasket() {
        List<BasketItem> basketItems = productBasket.getBasket().entrySet().stream()
                .map(entry -> {
                    UUID productId = entry.getKey();
                    int quantity = entry.getValue();
                    Product product = storageService.getProductById(productId).get();
                    return new BasketItem(product, quantity);
                })
                .collect(Collectors.toList());

        return new UserBasket(basketItems);
    }
}
