package io.nyando.factorix.services.time;

import io.nyando.factorix.model.time.TimerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Keeps time within a simulation run.
 * Objects with time dependency register here, then get a notification for every second of virtual time.
 */
public class Timer {

    private final static Logger log = LoggerFactory.getLogger(Timer.class);

    private final List<TimerListener> listeners;

    private long currentTime;
    private TimeFactor factor;

    private ScheduledExecutorService timerService;
    private ScheduledFuture<?> timer;
    private List<FutureTask<?>> timerTasks;

    private Timer() {
        this.listeners = new ArrayList<>();
        this.currentTime = 0L;
    }

    /**
     * Run simulation indefinitely, either in real time or with some speedup factor.
     * @param timeFactor Time speedup factor.
     */
    public Timer(TimeFactor timeFactor) {
        this();
        log.info("Starting simulation timer at x{} speed.", timeFactor.getFactor());
        this.factor = timeFactor;
        this.timerService = Executors.newSingleThreadScheduledExecutor();
        this.timer = this.timerService.scheduleWithFixedDelay(
                this::trigger, 0L, 1000L / this.factor.getFactor(), TimeUnit.MILLISECONDS);
    }

    /**
     * Run simulation in simulation time (as fast as possible) up to a time limit.
     * When running the simulation like this, the run must be triggered after setup by calling {@see runSimulation()}.
     * @param timeLimit Number of seconds of to run simulation for.
     */
    public Timer(int timeLimit) {
        this();
        log.info("Setting up simulation time run with time limit of {} seconds.", timeLimit);
        this.timerTasks = new ArrayList<>();
        for (int i = 0; i < timeLimit; i++) {
            timerTasks.add(new FutureTask<>(Executors.callable(this::trigger)));
        }
    }

    public long getCurrentTime() {
        return this.currentTime;
    }

    /**
     * Change time factor to a new value.
     * @param factor TimeFactor with desired simulation speedup.
     */
    public void setTimeFactor(TimeFactor factor) {
        log.info("Changing Timer to x{} speed", factor.getFactor());
        this.factor = factor;
        this.timer.cancel(false);
        this.timer = this.timerService.scheduleWithFixedDelay(
                this::trigger, 0L, 1000L / this.factor.getFactor(), TimeUnit.MILLISECONDS);
    }

    public TimeFactor getTimeFactor() {
        return this.factor;
    }

    public boolean paused() {
        return this.timer.isCancelled();
    }

    public void pause() {
        log.info("Pausing simulation.");
        this.timer.cancel(false);
    }

    public void play() {
        log.info("Resuming simulation.");
        this.setTimeFactor(this.factor);
    }

    public void register(TimerListener listener) {
        log.info("Registering new TimerListener instance {}.", listener.getClass().getSimpleName());
        this.listeners.add(listener);
    }

    public void speedUp() {
        this.setTimeFactor(this.factor.speedUp());
    }

    public void slowDown() {
        this.setTimeFactor(this.factor.slowDown());
    }

    /**
     * Starts the simulation time run (as fast as possible, no delays).
     */
    public void runSimulation() {
        log.info("Starting simulation run.");
        for (FutureTask<?> task : this.timerTasks) {
            try {
                task.run();
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void trigger() {
        this.listeners.forEach(listener -> listener.time(this.currentTime));
        this.currentTime++;
    }

}
