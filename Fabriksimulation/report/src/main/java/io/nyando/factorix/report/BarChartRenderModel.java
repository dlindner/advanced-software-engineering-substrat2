package io.nyando.factorix.report;

public record BarChartRenderModel(String divName, String title, Object data,
                                  String keyName, String valueName) {

    public String getDivName() {
        return divName;
    }

    public String getTitle() {
        return title;
    }

    public Object getData() {
        return data;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getValueName() {
        return valueName;
    }
}