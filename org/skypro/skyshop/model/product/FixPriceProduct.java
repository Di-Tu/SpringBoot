package org.skypro.skyshop.model.product;

public class FixPriceProduct extends Product {

    private static final int FIX_PRICE = 30;

    public FixPriceProduct(String name) {
        super(name);
    }

    @Override
    public int getPrice() {
        return FIX_PRICE;
    }

    @Override
    public String toString() {
        return this.getName() + ": Фиксированная цена " + this.getPrice();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}

