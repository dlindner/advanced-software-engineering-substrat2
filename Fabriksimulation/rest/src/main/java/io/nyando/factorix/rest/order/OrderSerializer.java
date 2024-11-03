package io.nyando.factorix.rest.order;

import io.nyando.factorix.model.order.Order;
import io.nyando.factorix.rest.order.OrderRequest;

import java.util.Collection;

public interface OrderSerializer {

    String getContentTypeHeader();

    String marshal(Order order);

    String marshalCollection(Collection<Order> orders);

    OrderRequest unmarshalRequest(String serializedRequest);

}
