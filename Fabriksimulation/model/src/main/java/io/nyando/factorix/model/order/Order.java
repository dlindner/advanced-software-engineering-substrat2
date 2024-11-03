package io.nyando.factorix.model.order;

import java.util.Collection;
import java.util.HashSet;

/**
 * Orders require production of a number of products of a certain type.
 * Every order has only one product type associated to it, the number is set at the time of creation.
 * Orders have a unique ID and are complete once all associated products are completed.
 */
public class Order {

    private final String orderID;
    private final String productType;
    private final Collection<Product> products;

    public Order(String orderID,
                 String productType,
                 int quantity,
                 Iterable<String> processSequence) {
        this.orderID = orderID;
        this.productType = productType;
        this.products = new HashSet<>();

        for (int i = 0; i < quantity; i++) {
            String id = orderID + "-" + i;
            this.products.add(new Product(id, this.productType, processSequence));
        }
    }

    public String getOrderID() {
        return orderID;
    }

    public String getProductType() {
        return productType;
    }

    public Collection<Product> getProducts() {
        return this.products;
    }

    public boolean completed() {
        return this.products.stream().allMatch(Product::completed);
    }

}
