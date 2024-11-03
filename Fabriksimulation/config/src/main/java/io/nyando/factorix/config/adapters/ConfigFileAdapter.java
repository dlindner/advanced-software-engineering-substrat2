package io.nyando.factorix.config.adapters;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.nyando.factorix.config.WorkplaceTimeSupplier;
import io.nyando.factorix.model.work.ProductionTimeSupplier;
import io.nyando.factorix.services.interfaces.SimConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class ConfigFileAdapter implements SimConfiguration {

    protected final File workplaceConfigFile;
    protected final File productConfigFile;

    protected final Map<String, String> workplaceProcesses;
    protected final Map<String, List<String>> productProcesses;
    protected final Table<String, String, Integer> workplaceProductTimes;

    public ConfigFileAdapter(String workplaceConfigFilePath,
                             String productConfigFilePath) throws FileReadException {
        this.workplaceConfigFile = new File(workplaceConfigFilePath);
        this.productConfigFile = new File(productConfigFilePath);

        this.workplaceProcesses = new HashMap<>();
        this.productProcesses = new HashMap<>();
        this.workplaceProductTimes = HashBasedTable.create();

        this.readWorkplaceFile();
        this.readProductFile();
    }

    protected abstract void readWorkplaceFile() throws FileReadException;

    protected abstract void readProductFile() throws FileReadException;

    @Override
    public Map<String, String> getWorkplaceConfiguration() {
        return this.workplaceProcesses;
    }

    @Override
    public Optional<List<String>> getProductProcessSequences(String productType) {
        return Optional.ofNullable(this.productProcesses.get(productType));
    }

    @Override
    public ProductionTimeSupplier getProductionTimeSupplier(String processType, String workplaceID) {
        return new WorkplaceTimeSupplier(this.workplaceProductTimes.row(workplaceID));
    }

    @Override
    public Iterable<String> getProductTypes() {
        return this.productProcesses.keySet();
    }
}
