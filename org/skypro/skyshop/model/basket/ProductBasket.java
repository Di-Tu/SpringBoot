package org.skypro.skyshop.model.basket;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ProductBasket {
    private final Map<UUID, Integer> basket = new HashMap<>();

    public void addProduct(UUID productId) {
        this.basket.merge(productId, 1, (oldValue, newValue) -> oldValue + newValue);
    }

    public Map<UUID, Integer> getBasket() {
        return Collections.unmodifiableMap(this.basket);
    }
}
