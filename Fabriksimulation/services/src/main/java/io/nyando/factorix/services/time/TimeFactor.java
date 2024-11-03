package io.nyando.factorix.services.time;

/**
 * TimeFactor determines the speedup in simulation runtime.
 * Speedup can be between 1 (real time) and 1000 (1 ms real time = 1 s simulation time).
 */
public enum TimeFactor {
    X1(1),
    X2(2),
    X5(5),
    X10(10),
    X25(25),
    X50(50),
    X100(100),
    X250(250),
    X500(500),
    X1000(1000);

    private final int factor;

    TimeFactor(int factor) {
        this.factor = factor;
    }

    public int getFactor() {
        return this.factor;
    }

    public TimeFactor speedUp() {
        return switch (this) {
            case X1   -> X2;
            case X2   -> X5;
            case X5   -> X10;
            case X10  -> X25;
            case X25  -> X50;
            case X50  -> X100;
            case X100 -> X250;
            case X250 -> X500;
            case X500 -> X1000;
            default   -> this;
        };
    }

    public TimeFactor slowDown() {
        return switch (this) {
            case X2    -> X1;
            case X5    -> X2;
            case X10   -> X5;
            case X25   -> X10;
            case X50   -> X25;
            case X100  -> X50;
            case X250  -> X100;
            case X500  -> X250;
            case X1000 -> X500;
            default    -> this;
        };
    }

}
