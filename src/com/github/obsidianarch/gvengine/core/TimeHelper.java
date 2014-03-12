package com.github.obsidianarch.gvengine.core;

import org.lwjgl.Sys;

/**
 * A general help class for anything relating to time.
 * 
 * @author Austin
 */
public final class TimeHelper {
    
    /**
     * Checks to see if the current time has run "over".
     * 
     * @param start
     *            The start time.
     * @param delay
     *            The delay time.
     * @return {@code ( ( start + delay ) > getTime() )}
     */
    public static boolean isOver( long start, int delay ) {
        return ( ( start + delay ) > getTime() );
    }
    
    /**
     * Returns the time {@code delay} milliseconds in the future.
     * 
     * @param delay
     *            The delay in milliseconds.
     * @return The time (in ticks) {@code delay} milliseconds in the future.
     */
    public static long getDelay( long delay ) {
        return getTime() + toTicks( delay );
    }

    /**
     * Converts milliseconds into LWJGL ticks, which may be milliseconds.
     * 
     * @param milliseconds
     *            The number of milliseconds.
     * @return LWJGL ticks.
     */
    public static long toTicks( long milliseconds ) {
        return ( milliseconds * Sys.getTimerResolution() ) / 1000;
    }
    
    /**
     * Uses the LWJGL timer to accurately get the current time in milliseconds.
     * 
     * @return The current system time in milliseconds.
     */
    public static long getTime() {
        return ( Sys.getTime() * 1000 ) / Sys.getTimerResolution();
    }

}
