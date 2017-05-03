package org.deeplearning4j.datasets.iterator.parallel;

import lombok.extern.slf4j.Slf4j;
import org.nd4j.linalg.exception.ND4JIllegalStateException;

/**
 * This is utility class, that allows easy handling of multiple joint boolean states
 *
 * @author raver119@gmail.com
 */
@Slf4j
public class MultiBoolean {
    private final int numEntries;
    private int holder = 0;
    private int max = 0;
    private boolean oneTime;
    private MultiBoolean timeTracker;

    public MultiBoolean(int numEntries) {
        this(numEntries, false);
    }

    public MultiBoolean(int numEntries, boolean initialValue) {
        this(numEntries, initialValue, false);
    }

    public MultiBoolean(int numEntries, boolean initialValue, boolean oneTime) {
        this.oneTime = oneTime;
        this.numEntries = numEntries;
        for (int i = 1; i <= numEntries; i++) {
            this.max |= 1 << i;
        }

        if (initialValue)
            this.holder = this.max;

        if (oneTime)
            this.timeTracker = new MultiBoolean(numEntries, false, false);
    }

    /**
     * Sets specified entry to specified state
     *
     * @param value
     * @param entry
     */
    public void set(boolean value, int entry){
        if (entry > numEntries)
            throw new ND4JIllegalStateException("Entry index given (" + entry + ")in is higher then configured one (" + numEntries + ")");

        if (oneTime && this.timeTracker.get(entry))
            return;

        if (value)
            this.holder |= 1 << (entry + 1);
        else
            this.holder &= ~(1 << (entry + 1));

        if (oneTime)
            this.timeTracker.set(true, entry);
    }

    /**
     * Gets current state for specified entry
     *
     * @param entry
     * @return
     */
    public boolean get(int entry) {
        if (entry > numEntries)
            throw new ND4JIllegalStateException("Entry index given (" + entry + ")in is higher then configured one (" + numEntries + ")");

        return (this.holder & 1 << (entry + 1)) != 0;
    }

    /**
     * This method returns true if ALL states are true. False otherwise.
     *
     * @return
     */
    public boolean allTrue() {
        //log.info("Holder: {}; Max: {}", holder, max);
        return holder == max;
    }

    /**
     * This method returns true if ALL states are false. False otherwise
     * @return
     */
    public boolean allFalse() {
        return holder == 0;
    }
}