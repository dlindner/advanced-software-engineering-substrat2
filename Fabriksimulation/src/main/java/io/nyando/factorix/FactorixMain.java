package io.nyando.factorix;

import io.nyando.factorix.comms.kafka.WorkplaceKafkaProducer;
import io.nyando.factorix.config.InitialOrderParser;
import io.nyando.factorix.config.adapters.FileReadException;
import io.nyando.factorix.config.adapters.XMLConfigAdapter;
import io.nyando.factorix.config.adapters.YAMLConfigAdapter;
import io.nyando.factorix.event.EventProxy;
import io.nyando.factorix.model.order.Order;
import io.nyando.factorix.model.order.StashRetrieve;
import io.nyando.factorix.services.time.TimeFactor;
import io.nyando.factorix.services.time.Timer;
import io.nyando.factorix.report.ReportGenerator;
import io.nyando.factorix.rest.config.ConfigRESTInterface;
import io.nyando.factorix.rest.timer.TimerRESTInterface;
import io.nyando.factorix.rest.config.ConfigJSONSerializer;
import io.nyando.factorix.services.interfaces.EventBroker;
import io.nyando.factorix.rest.order.OrderRESTInterface;
import io.nyando.factorix.rest.workplace.WorkplaceRESTInterface;
import io.nyando.factorix.report.TimeTracker;
import io.nyando.factorix.rest.order.OrderRequest;
import io.nyando.factorix.rest.order.OrderJSONSerializer;
import io.nyando.factorix.rest.workplace.WorkplaceJSONSerializer;
import io.nyando.factorix.services.*;
import io.nyando.factorix.services.interfaces.SimConfiguration;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public class FactorixMain {

    private static final Logger log = LoggerFactory.getLogger(FactorixMain.class);

    private static CommandLine parseCommandLine(String[] args) {
        Options options = new Options();
        options.addRequiredOption("f", "format", true, "format of the configuration files");
        options.addRequiredOption("w", "workplaces", true, "path of the workplace configuration file");
        options.addRequiredOption("p", "products", true, "path of the product configuration file");
        options.addOption("l", true, "time limit of the simulation run");
        options.addOption("x", true, "simulation speedup factor (2, 5, 10, 25, 50, 100, 250, 500, 1000)");
        options.addOption("i", true, "path to file with initial order set (CSV with product types and quantities)");
        options.addOption("o", true, "path for simulation result output folder");
        options.addOption("k", true, "Apache Kafka hostname (if in use)");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException ex) {
            log.error("error while parsing command line arguments");
            ex.printStackTrace();
            System.exit(1);
        }

        return cmd;
    }

    private static TimeFactor parseTimeFactor(String timeFactorArg) {
        return switch (timeFactorArg) {
            case "2"    -> TimeFactor.X2;
            case "5"    -> TimeFactor.X5;
            case "10"   -> TimeFactor.X10;
            case "25"   -> TimeFactor.X25;
            case "50"   -> TimeFactor.X50;
            case "100"  -> TimeFactor.X100;
            case "250"  -> TimeFactor.X250;
            case "500"  -> TimeFactor.X500;
            case "1000" -> TimeFactor.X1000;
            default     -> TimeFactor.X1;
        };
    }

    private static Timer createTimer(CommandLine cmd) {
        Timer timer;
        boolean simTime = cmd.hasOption("l");
        boolean hasSpeedup = cmd.hasOption("x");
        if (simTime) {
            int timeLimit = Integer.parseInt(cmd.getOptionValue("l"));
            timer = new Timer(timeLimit);
        } else if (hasSpeedup) {
            timer = new Timer(parseTimeFactor(cmd.getOptionValue("x")));
        } else {
            timer = new Timer(TimeFactor.X1);
        }
        return timer;
    }

    private static SimConfiguration createSimConfiguration(CommandLine cmd) {
        SimConfiguration config = null;
        String configFileType = cmd.getOptionValue("f");
        String workplaceFilename = cmd.getOptionValue("w");
        String productFilename = cmd.getOptionValue("p");
        try {
            if (configFileType.equalsIgnoreCase("yaml")) {
                config = new YAMLConfigAdapter(workplaceFilename, productFilename);
            } else if (configFileType.equalsIgnoreCase("xml")) {
                config = new XMLConfigAdapter(workplaceFilename, productFilename);
            } else {
                log.error("missing configuration file type");
                System.exit(1);
            }
        } catch (FileReadException ex) {
            log.error("failed to read configuration files");
            System.exit(1);
        }
        return config;
    }

    private static void createInitialOrders(String filePath, OrderManager orderManager, TaskManager taskManager) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            InitialOrderParser orderParser = new InitialOrderParser(reader);
            for (OrderRequest req : orderParser.readInitialOrders()) {
                Order order = orderManager.createOrder(req.productType(), req.quantity());
                taskManager.createAndDistributeTasks(order);
            }
        } catch (FileNotFoundException ex) {
            log.error("initial order file {} not found, exiting...", filePath);
            System.exit(1);
        } catch (IOException ex) {
            log.error("error while reading initial order file {}, exiting", filePath);
            System.exit(1);
        } catch (ParseException | NumberFormatException ex) {
            log.error("error while parsing initial order file {}, exiting...", filePath);
            System.exit(1);
        }
    }

    private static void setupRESTInterfaces(Timer timer,
                                            SimConfiguration config,
                                            OrderManager orderManager,
                                            TaskManager taskManager,
                                            WorkplaceManager workplaceManager,
                                            TimeTracker tracker,
                                            String reportOutputPath) {
        TimerRESTInterface timerInterface = new TimerRESTInterface(timer);
        ConfigRESTInterface configInterface = new ConfigRESTInterface(config, new ConfigJSONSerializer());
        OrderRESTInterface orderInterface = new OrderRESTInterface(orderManager, taskManager, new OrderJSONSerializer());
        WorkplaceRESTInterface workplaceInterface = new WorkplaceRESTInterface(workplaceManager, new WorkplaceJSONSerializer());

        timerInterface.createRESTInterface();
        timerInterface.setSimTerminator(() -> {
            log.info("received termination signal from REST interface, writing report data and exiting...");
            writeReport(tracker, reportOutputPath);
            System.exit(0);
        });
        configInterface.createRESTInterface();
        orderInterface.createRESTInterface();
        workplaceInterface.createRESTInterface();
    }

    private static void writeReport(TimeTracker tracker, String outputPath) {
        try {
            ReportGenerator gen = new ReportGenerator(tracker, outputPath);
            gen.generateReport();
        } catch (IOException ex) {
            log.error("error while writing simulation report");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        CommandLine cmd = parseCommandLine(args);

        // create timer
        Timer timer = createTimer(cmd);

        // read configuration file and create simconfig
        SimConfiguration config = createSimConfiguration(cmd);

        // create manager classes
        StashRetrieve repo = new ProductStorage();
        EventBroker broker = new EventProxy();
        WorkplaceManager workplaceManager = new WorkplaceManager(config, repo, timer, broker);
        TaskManager taskManager = new TaskManager(config, workplaceManager.getWorkplaces());
        OrderManager orderManager = new OrderManager(config, repo, timer, broker);

        // set up reporting
        TimeTracker tracker = new TimeTracker(timer, workplaceManager, orderManager, broker);

        Collection<WorkplaceKafkaProducer> producers = new ArrayList<>();

        if (cmd.hasOption("k")) {
            String kafkaHost = cmd.getOptionValue("k");
            workplaceManager.getWorkplaces().forEach(wp -> {
                try {
                    producers.add(new WorkplaceKafkaProducer(kafkaHost, wp, broker));
                } catch (IOException e) {
                    log.error("failed to create Kafka producer {}", e.getMessage());
                    System.exit(1);
                }
            });
        }

        // set up initial orders, if any
        if (cmd.hasOption("i")) {
            String filePath = cmd.getOptionValue("i");
            createInitialOrders(filePath, orderManager, taskManager);
        }

        String reportOutputPath;
        if (cmd.hasOption("o")) {
            reportOutputPath = cmd.getOptionValue("o");
        } else {
            reportOutputPath = "";
        }

        if (cmd.hasOption("l")) {
            // if running in simulation time, don't set up a REST API
            timer.runSimulation();
            // create report after running the simulation
            writeReport(tracker, reportOutputPath);
        } else {
            // start up REST interfaces
            setupRESTInterfaces(timer, config, orderManager, taskManager, workplaceManager, tracker, reportOutputPath);
        }
    }

}
