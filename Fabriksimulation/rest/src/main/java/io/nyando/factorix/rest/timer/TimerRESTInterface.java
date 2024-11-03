package io.nyando.factorix.rest.timer;

import io.nyando.factorix.services.time.Timer;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static spark.Spark.*;
import static java.net.HttpURLConnection.*;

public class TimerRESTInterface {

    private final Timer timer;
    private SimTerminator terminator;

    public TimerRESTInterface(Timer timer) {
        this.timer = timer;
    }

    public void setSimTerminator(SimTerminator terminator) {
        this.terminator = terminator;
    }

    public void createRESTInterface() {

        post("/timer/pause", (req, res) -> {
            res.header("Content-Type", "text/plain");
            if (this.timer.paused()) {
                res.status(HTTP_BAD_REQUEST);
            } else {
                res.status(HTTP_OK);
                this.timer.pause();
            }
            return Boolean.toString(this.timer.paused());
        });

        post("/timer/play", (req, res) -> {
            res.header("Content-Type", "text/plain");
            if (this.timer.paused()) {
                res.status(HTTP_OK);
                this.timer.play();
            } else {
                res.status(HTTP_BAD_REQUEST);
            }
            return Boolean.toString(this.timer.paused());
        });

        get("/timer/paused", (req, res) -> {
            res.status(HTTP_OK);
            res.header("Content-Type", "text/plain");
            return Boolean.toString(this.timer.paused());
        });

        get("/timer/current", (req, res) -> {
            res.status(HTTP_OK);
            res.header("Content-Type", "text/plain");
            return Long.toString(this.timer.getCurrentTime());
        });

        post("/timer/speedup", (req, res) -> {
            this.timer.speedUp();
            res.status(HTTP_OK);
            res.header("Content-Type", "text/plain");
            return Integer.toString(this.timer.getTimeFactor().getFactor());
        });

        post("/timer/slowdown", (req, res) -> {
            this.timer.slowDown();
            res.status(HTTP_OK);
            res.header("Content-Type", "text/plain");
            return Integer.toString(this.timer.getTimeFactor().getFactor());
        });

        post("/timer/shutdown", (req, res) -> {
            res.status(HTTP_OK);
            res.header("Content-Type", "text/plain");
            this.timer.pause();
            Executors.newSingleThreadScheduledExecutor().schedule(() -> this.terminator.terminateSimulation(), 1, TimeUnit.SECONDS);
            return "BYE";
        });

    }

}
