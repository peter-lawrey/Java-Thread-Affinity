package vanilla.java.clock.impl;


import vanilla.java.clock.IClock;

/**
 * Default implementation, use plain {@link System#nanoTime()}
 *
 * @author cheremin
 * @since 29.12.11,  18:54
 */
public enum DefaultClock implements IClock {

    INSTANCE;


    @Override
    public long nanoTime() {
        return System.nanoTime();
    }
}
