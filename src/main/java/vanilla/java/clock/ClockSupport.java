package vanilla.java.clock;


import vanilla.java.clock.impl.DefaultClock;
import vanilla.java.clock.impl.JNIClock;

/**
 * Static factory for available {@link IClock} interface implementation
 *
 * @author cheremin
 * @since 29.12.11,  19:02
 */
public final class ClockSupport {
    private static final IClock clockImpl;

    static {
        if ( JNIClock.LOADED ) {
            clockImpl = JNIClock.INSTANCE;
        } else {
            clockImpl = DefaultClock.INSTANCE;
        }
    }

    public static IClock clock() {
        return clockImpl;
    }

    /**
     * @return The current value of the system timer, in nanoseconds.
     * @see vanilla.java.clock.IClock#nanoTime()
     */
    public static long nanoTime() {
        return clockImpl.nanoTime();
    }
}
