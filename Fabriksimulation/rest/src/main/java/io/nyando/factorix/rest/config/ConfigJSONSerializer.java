package io.nyando.factorix.rest.config;

import com.google.gson.Gson;

import java.util.List;

public class ConfigJSONSerializer implements ConfigSerializer {

    private final Gson gson;

    public ConfigJSONSerializer() {
        this.gson = new Gson();
    }

    @Override
    public String getContentTypeHeader() {
        return "application/json";
    }

    @Override
    public String marshalProductTypes(List<ProductTypeRenderModel> productTypes) {
        return this.gson.toJson(productTypes.toArray());
    }

}
