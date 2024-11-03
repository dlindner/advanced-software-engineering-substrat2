package io.nyando.factorix.config;

import java.util.List;

public class ProductConfig {

    private String productType;
    private List<String> processes;

    public ProductConfig() { }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public List<String> getProcesses() {
        return processes;
    }

    public void setProcesses(List<String> processes) {
        this.processes = processes;
    }
}
