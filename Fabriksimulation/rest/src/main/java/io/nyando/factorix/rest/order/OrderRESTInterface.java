package io.nyando.factorix.rest.order;

import io.nyando.factorix.model.order.Order;
import io.nyando.factorix.services.OrderManager;
import io.nyando.factorix.services.TaskManager;

import java.util.Optional;

import static spark.Spark.*;
import static java.net.HttpURLConnection.*;

public class OrderRESTInterface {

    private final OrderManager orderManager;
    private final TaskManager taskManager;
    private final OrderSerializer serializer;

    public OrderRESTInterface(OrderManager orderManager,
                              TaskManager taskManager,
                              OrderSerializer serializer) {
        this.orderManager = orderManager;
        this.taskManager = taskManager;
        this.serializer = serializer;
    }

    public void createRESTInterface() {

        get("/order", (req, res) -> {
            res.status(HTTP_OK);
            res.header("Content-Type", this.serializer.getContentTypeHeader());
            return this.serializer.marshalCollection(this.orderManager.getOrders());
        });

        post("/order", (req, res) -> {
            OrderRequest request = this.serializer.unmarshalRequest(req.body());
            Order order = this.orderManager.createOrder(request.productType(), request.quantity());
            this.taskManager.createAndDistributeTasks(order);
            res.status(HTTP_CREATED);
            res.header("Content-Type", this.serializer.getContentTypeHeader());
            return this.serializer.marshal(order);
        });

        get("/order/:orderID", (req, res) -> {
            res.header("Content-Type", this.serializer.getContentTypeHeader());
            Optional<Order> order = this.orderManager.getOrder(req.params(":orderID"));
            if (order.isPresent()) {
                res.status(HTTP_OK);
                return this.serializer.marshal(order.get());
            } else {
                res.status(HTTP_NOT_FOUND);
                return "{}";
            }
        });

    }

}
