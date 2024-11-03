package io.nyando.factorix.rest.order;

public class OrderRequest {

    private final String productType;
    private final int quantity;

    public OrderRequest(String productType, int quantity) {
        this.productType = productType;
        this.quantity = quantity;
    }

    public String productType() {
        return productType;
    }

    public int quantity() {
        return quantity;
    }
}
