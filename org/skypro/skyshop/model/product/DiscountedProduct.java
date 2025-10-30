package org.skypro.skyshop.model.product;

public class DiscountedProduct extends Product {

    private int price;
    private int discountInWholePercentages;

    public DiscountedProduct(String name, int price, int discountInWholePercentages) {
        super(name);
        if (price <= 0) {
            throw new IllegalArgumentException("Ошибка ввода стоимости");
        }
        if (discountInWholePercentages < 0 || discountInWholePercentages > 100) {
            throw new IllegalArgumentException("Ошибка ввода процента скидки");
        }
        this.price = price;
        this.discountInWholePercentages = discountInWholePercentages;
    }

    @Override
    public int getPrice() {
        return (this.price - discountInWholePercentages);
    }

    @Override
    public String toString() {
        return this.getName() + ": " + this.getPrice() + " ( " + this.discountInWholePercentages + "% )";
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
