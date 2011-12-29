package vanilla.java.clock;

/**
 * High precision time source
 *
 * @author cheremin
 * @since 29.12.11,  18:53
 */
public interface IClock {

    /**
     * The general contract is same, as {@link System#nanoTime()}:
     *
     * Returns the current value of the most precise available system
     * timer, in nanoseconds.
     *
     * <p>This method can only be used to measure elapsed time and is
     * not related to any other notion of system or wall-clock time.
     * The value returned represents nanoseconds since some fixed but
     * arbitrary time (perhaps in the future, so values may be
     * negative).  This method provides nanosecond precision, but not
     * necessarily nanosecond accuracy. No guarantees are made about
     * how frequently values change. Differences in successive calls
     * that span greater than approximately 292 years (2<sup>63</sup>
     * nanoseconds) will not accurately compute elapsed time due to
     * numerical overflow.
     *
     * <p> For example, to measure how long some code takes to execute:
     * <pre>
     *   long startTime = clock.nanoTime();
     *   // ... the code being measured ...
     *   long estimatedTime = clock.nanoTime() - startTime;
     * </pre>
     *
     * @return The current value of the system timer, in nanoseconds.
     */
    public long nanoTime();
}
