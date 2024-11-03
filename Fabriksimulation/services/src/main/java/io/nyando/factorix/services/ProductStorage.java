package io.nyando.factorix.services;

import io.nyando.factorix.model.order.Product;
import io.nyando.factorix.model.order.StashRetrieve;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProductStorage implements StashRetrieve {

    private final Map<String, Boolean> available;
    private final Map<String, Product> productStore;

    public ProductStorage() {
        this.available = new HashMap<>();
        this.productStore = new HashMap<>();
    }

    @Override
    public void create(Product product) {
        if (this.productStore.containsKey(product.getProductID())) {
            throw new IllegalStateException("productID already in storage");
        }
        this.productStore.put(product.getProductID(), product);
        this.available.put(product.getProductID(), true);
    }

    @Override
    public void stash(Product product) {
        this.available.put(product.getProductID(), true);
    }

    @Override
    public Optional<Product> retrieve(String productID) {
        if (!this.productStore.containsKey(productID) || !this.available.get(productID)) {
            return Optional.empty();
        } else if (this.available.get(productID)) {
            this.available.put(productID, false);
            return Optional.of(this.productStore.get(productID));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Product check(String productID) {
        return this.productStore.get(productID);
    }

}
