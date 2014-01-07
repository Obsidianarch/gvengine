package com.github.obsidianarch.gvengine.core;

/**
 * @author Austin
 */
public final class MathHelper {
    
    /**
     * @param x
     *            The number.
     * @param xMin
     *            The minimum value in the range (inclusive).
     * @param xMax
     *            The maximum value in the range (inclusive).
     * @return If {@code xMin <= x <= xMax}.
     */
    public static boolean inRange( float x, float xMin, float xMax ) {
        return ( ( x >= xMin ) && ( x <= xMax ) );
    }
    
}
