package io.nyando.factorix.config;

import java.util.Map;

public class WorkplaceConfig {
    private String workplaceID;
    private String processType;
    private Map<String, Integer> processTimes;

    WorkplaceConfig() { }

    public String getWorkplaceID() {
        return workplaceID;
    }

    public String getProcessType() {
        return processType;
    }

    public Map<String, Integer> getProcessTimes() {
        return processTimes;
    }

    public void setWorkplaceID(String workplaceID) {
        this.workplaceID = workplaceID;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public void setProcessTimes(Map<String, Integer> processTimes) {
        this.processTimes = processTimes;
    }
}