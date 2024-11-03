package io.nyando.factorix.config.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.nyando.factorix.config.ProductConfig;
import io.nyando.factorix.config.WorkplaceConfig;

import java.io.IOException;

public class YAMLConfigAdapter extends ConfigFileAdapter {

    public YAMLConfigAdapter(String workplaceConfigPath,
                             String productConfigPath) throws FileReadException {
        super(workplaceConfigPath, productConfigPath);
    }

    protected void readWorkplaceFile() throws FileReadException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            WorkplaceConfig[] workplaces = mapper.readValue(workplaceConfigFile, WorkplaceConfig[].class);

            for (WorkplaceConfig config : workplaces) {
                workplaceProcesses.put(config.getWorkplaceID(), config.getProcessType());
                for (var entry : config.getProcessTimes().entrySet()) {
                    workplaceProductTimes.put(config.getWorkplaceID(), entry.getKey(), entry.getValue());
                }
            }
        } catch (IOException ex) {
            throw new FileReadException();
        }
    }

    protected void readProductFile() throws FileReadException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            ProductConfig[] products = mapper.readValue(productConfigFile, ProductConfig[].class);

            for (ProductConfig prod : products) {
                productProcesses.put(prod.getProductType(), prod.getProcesses());
            }
        } catch (IOException ex) {
            throw new FileReadException();
        }
    }

}
