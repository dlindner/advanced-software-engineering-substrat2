package io.nyando.factorix.rest.config;

import java.util.List;

public interface ConfigSerializer {

    String getContentTypeHeader();

    String marshalProductTypes(List<ProductTypeRenderModel> productTypes);

}
