package io.nyando.factorix.report;

import io.nyando.factorix.model.order.Order;

import java.util.*;

public class OrderTableRenderModel {

    private final String divName;
    private final String title;
    private final LinkedHashMap<String, String> columns; // must be linked to preserve column ordering in table
    private final Collection<Order> rows;

    public OrderTableRenderModel(Collection<Order> orders) {
        this.divName = "orders";
        this.title = "Generated Orders";

        this.columns = new LinkedHashMap<>();
        this.columns.put("Order ID", "string");
        this.columns.put("Product Type", "string");
        this.columns.put("Quantity", "number");
        this.columns.put("Completed", "boolean");

        this.rows = orders;
    }

    public String getDivName() {
        return divName;
    }

    public String getTitle() {
        return title;
    }

    public Map<String, String> getColumns() {
        return columns;
    }

    public Collection<Order> getRows() {
        return rows;
    }
}
