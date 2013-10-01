package com.github.obsidianarch.gvengine;

/**
 * Collection of mathematical functions used in multiple classes in the GVEN library.
 * 
 * @author Austin
 */
public final class MathHelper {
    
    /**
     * Gets an index for the given coordinate system.
     * 
     * @param x
     *            The x coordinate of the indexed item.
     * @param y
     *            The y coordinate of the indexed item.
     * @param z
     *            The z coordinate of the indexed item.
     * @param size
     *            The size of the length, width, and height of the area.
     * @return {@code z + ( x * size ) + ( y * size * size )}.
     */
    public static int getIndex( int x, int y, int z, int size ) {
        return z + ( x * size ) + ( y * size * size );
    }
    
}
