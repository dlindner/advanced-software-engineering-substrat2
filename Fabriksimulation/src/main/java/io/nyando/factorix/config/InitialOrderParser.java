package io.nyando.factorix.config;

import io.nyando.factorix.rest.order.OrderRequest;
import org.apache.commons.cli.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InitialOrderParser {

    private final BufferedReader initialOrdersReader;

    public InitialOrderParser(BufferedReader reader) {
        this.initialOrdersReader = reader;
    }

    public OrderRequest parseOrder(String line) throws ParseException, NumberFormatException {
        if (!line.contains(";")) {
            throw new ParseException("Order parameters should be separated by semicolon (;)");
        }

        String[] orderParams = line.strip().split(";");

        if (orderParams.length < 2) {
            throw new ParseException("Incorrect parameter count; order specified by product type and quantity.");
        }

        String productType = orderParams[0].strip();
        int quantity = Integer.parseInt(orderParams[1].strip());
        return new OrderRequest(productType, quantity);
    }

    public List<OrderRequest> readInitialOrders() throws IOException, ParseException, NumberFormatException {
        List<OrderRequest> requests = new ArrayList<>();

        String line;
        while ((line = this.initialOrdersReader.readLine()) != null) {
            if (line.matches("[\s\t]*")) {
                continue;
            }
            requests.add(parseOrder(line));
        }

        return requests;
    }

}
