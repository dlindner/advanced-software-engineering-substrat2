package io.nyando.factorix.rest.order;

import com.google.gson.Gson;
import io.nyando.factorix.model.order.Order;

import java.util.Collection;

public class OrderJSONSerializer implements OrderSerializer {

    private final Gson gson;

    public OrderJSONSerializer() {
        this.gson = new Gson();
    }

    @Override
    public String getContentTypeHeader() {
        return "application/json";
    }

    @Override
    public String marshal(Order order) {
        return this.gson.toJson(order);
    }

    @Override
    public String marshalCollection(Collection<Order> orders) {
        return this.gson.toJson(orders.toArray());
    }

    @Override
    public OrderRequest unmarshalRequest(String serializedRequest) {
        return this.gson.fromJson(serializedRequest, OrderRequest.class);
    }

}
