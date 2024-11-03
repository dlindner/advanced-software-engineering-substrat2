package io.nyando.factorix.report;

import io.nyando.factorix.services.time.Timer;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportGenerator {

    // velocity template inputs
    private static final String TEMPLATE_PATH = "report/src/main/resources/";
    private static final String TEMPLATE_HTML = "report.vm";
    private static final String BARCHART_JS = "barchart.vm";
    private static final String TABLE_JS = "table.vm";

    // output directory and filenames
    public static final String REPORT_DIRNAME = "simreport/";
    private static final String OUTPUT_JS = "charts.js";
    private static final String OUTPUT_HTML = "testreport.html";

    private final TimeTracker tracker;
    private final String outputPath;

    public ReportGenerator(TimeTracker tracker, String outputPath) throws IOException {
        this.tracker = tracker;
        this.outputPath = outputPath;

        File file = new File(outputPath + REPORT_DIRNAME);
        if (!file.exists() && !file.mkdir()) {
            throw new IOException("could not create report directory in path " + this.outputPath);
        }
    }

    private VelocityContext createOccupancyContext(String occupancyDivName) {
        Map<String, Double> occupancyResults = this.tracker.getWorkplaceOccupancy();
        VelocityContext occupancyContext = new VelocityContext();
        occupancyContext.put("chart", new BarChartRenderModel(
                occupancyDivName,
                "Workplace Occupancy",
                occupancyResults.entrySet(),
                "Workplace ID",
                "Workplace Occupancy"
        ));
        return occupancyContext;
    }

    private VelocityContext createTurnaroundContext(String turnaroundDivName) {
        Map<String, Integer> turnaroundResults = this.tracker.getOrderTurnaround();
        VelocityContext turnaroundContext = new VelocityContext();
        turnaroundContext.put("chart", new BarChartRenderModel(
                turnaroundDivName,
                "Order Turnaround",
                turnaroundResults.entrySet(),
                "Order ID",
                "Turnaround Time (seconds)"
        ));
        return turnaroundContext;
    }

    public void generateReport() throws IOException {
        VelocityEngine engine = new VelocityEngine();
        engine.init();

        Template htmlTemplate = engine.getTemplate(TEMPLATE_PATH + TEMPLATE_HTML);
        Template barChartTemplate = engine.getTemplate(TEMPLATE_PATH + BARCHART_JS);
        Template tableTemplate = engine.getTemplate(TEMPLATE_PATH + TABLE_JS);

        List<String> charts = new ArrayList<>();

        String orderTableDiv = "orders";
        VelocityContext orderTableContext = new VelocityContext();
        orderTableContext.put("chart", new OrderTableRenderModel(this.tracker.getOrders()));
        charts.add(orderTableDiv);

        String occupancyGraphDiv = "occupancy";
        VelocityContext occupancyContext = createOccupancyContext(occupancyGraphDiv);
        charts.add(occupancyGraphDiv);

        String turnaroundGraphDiv = "turnaround";
        VelocityContext turnaroundContext = createTurnaroundContext(turnaroundGraphDiv);
        charts.add(turnaroundGraphDiv);

        VelocityContext htmlContext = new VelocityContext();
        htmlContext.put("chartJSFile", OUTPUT_JS);
        htmlContext.put("charts", charts);
        htmlContext.put("simTime", this.tracker.getSimulationTime());
        htmlContext.put("completedTasks", this.tracker.getCompletedTaskCount());

        StringWriter jsWriter = new StringWriter();
        barChartTemplate.merge(occupancyContext, jsWriter);
        barChartTemplate.merge(turnaroundContext, jsWriter);
        tableTemplate.merge(orderTableContext, jsWriter);
        try (FileWriter fileWriter = new FileWriter(this.outputPath + REPORT_DIRNAME + OUTPUT_JS)) {
            fileWriter.write(jsWriter.toString());
        }

        StringWriter htmlWriter = new StringWriter();
        htmlTemplate.merge(htmlContext, htmlWriter);
        try (FileWriter fileWriter = new FileWriter(this.outputPath + REPORT_DIRNAME + OUTPUT_HTML)) {
            fileWriter.write(htmlWriter.toString());
        }
    }

}
