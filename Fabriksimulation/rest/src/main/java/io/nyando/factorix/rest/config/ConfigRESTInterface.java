package io.nyando.factorix.rest.config;

import io.nyando.factorix.services.interfaces.SimConfiguration;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;
import static java.net.HttpURLConnection.*;

public class ConfigRESTInterface {

    private final SimConfiguration config;
    private final ConfigSerializer serializer;

    public ConfigRESTInterface(SimConfiguration config, ConfigSerializer serializer) {
        this.config = config;
        this.serializer = serializer;
    }

    public void createRESTInterface() {

        get("/config/products", (req, res) -> {
            res.status(HTTP_OK);
            res.header("Content-Type", this.serializer.getContentTypeHeader());
            List<ProductTypeRenderModel> productTypes = new ArrayList<>();
            for (String productType : this.config.getProductTypes()) {
                productTypes.add(new ProductTypeRenderModel(
                        productType,
                        this.config.getProductProcessSequences(productType).orElseThrow().toArray(new String[0]))
                );
            }
            return this.serializer.marshalProductTypes(productTypes);
        });

    }

}
