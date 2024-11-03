package io.nyando.factorix.services;

import io.nyando.factorix.model.order.Order;
import io.nyando.factorix.model.order.Product;
import io.nyando.factorix.model.order.StashRetrieve;
import io.nyando.factorix.services.events.OrderCreated;
import io.nyando.factorix.services.interfaces.EventBroker;
import io.nyando.factorix.services.interfaces.SimConfiguration;
import io.nyando.factorix.services.time.Timer;

import java.util.*;

/**
 * Manages order lifecycles.
 * Generates orders with input from an external configuration (SimConfiguration).
 * Orders are created given inputs of product type and quantity.
 */
public class OrderManager {

    private final static int ORDER_ID_LENGTH = 6;
    private final static int RANDOM_SEED = 0;
    private final static char[] ID_ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    private final SimConfiguration config;

    private final Random random;

    private final Map<String, Order> orders;
    private final StashRetrieve productRepository;
    private final io.nyando.factorix.services.time.Timer timer;
    private final EventBroker broker;

    public OrderManager(SimConfiguration config, StashRetrieve productRepository, Timer timer, EventBroker broker) {
        this.config = config;
        this.random = new Random(RANDOM_SEED);
        this.orders = new HashMap<>();
        this.productRepository = productRepository;
        this.timer = timer;
        this.broker = broker;
    }

    /**
     * Generate a new order object and its corresponding product objects.
     * @param productType Type descriptor of product.
     * @param quantity Number of product instances to associate with this order.
     * @return New order object.
     */
    public Order createOrder(String productType, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Product quantity must be larger than zero.");
        }

        Optional<List<String>> processSequence = this.config.getProductProcessSequences(productType);
        Order newOrder = new Order(
                this.generateOrderID(),
                productType,
                quantity,
                processSequence.orElseThrow()
        );
        this.orders.put(newOrder.getOrderID(), newOrder);
        for (Product product : newOrder.getProducts()) {
            this.productRepository.create(product);
        }
        this.broker.post(new OrderCreated(timer.getCurrentTime(), newOrder.getOrderID(), newOrder));
        return newOrder;
    }

    public Optional<Order> getOrder(String orderID) {
        return Optional.ofNullable(this.orders.get(orderID));
    }

    public Collection<Order> getOrders() {
        return this.orders.values();
    }

    private String generateOrderID() {
        String id;
        do {
            id = this.genRandomString(ORDER_ID_LENGTH);
        } while (this.orders.containsKey(id));
        return id;
    }

    private String genRandomString(int length) {
        return random.ints(0, ID_ALPHABET.length)
                .limit(length)
                .map(operand -> ID_ALPHABET[operand])
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
