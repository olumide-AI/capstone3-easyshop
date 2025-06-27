package org.yearup.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ShoppingCartItem
{
    private Product product = null;
    private int quantity = 1;
    private BigDecimal discountPercent = BigDecimal.ZERO;

    public ShoppingCartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct()
    {
        return product;
    }

    public void setProduct(Product product)
    {
        this.product = product;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getDiscountPercent()
    {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent)
    {
        this.discountPercent = discountPercent;
    }

    @JsonIgnore
    public int getProductId()
    {
        return this.product.getProductId();
    }

    //Chat gpt chat bot was used to assist in generating method
    /**
     * Calculates the total price for this line item, applying quantity and discount.
     * @return the final line total after discount.
     * @throws IllegalStateException if product or price is missing.
     */
    public BigDecimal getLineTotal()
    {
        if (product == null || product.getPrice() == null) {
            throw new IllegalStateException("Product or product price is missing for cart item");
        }

        BigDecimal basePrice = product.getPrice();
        BigDecimal qty = BigDecimal.valueOf(this.quantity);

        BigDecimal subTotal = basePrice.multiply(qty);
        BigDecimal discount = discountPercent != null ? discountPercent : BigDecimal.ZERO;
        BigDecimal discountAmount = subTotal.multiply(discount);

        BigDecimal lineTotal = subTotal.subtract(discountAmount);
        return lineTotal.setScale(2, RoundingMode.HALF_UP);
    }

}
