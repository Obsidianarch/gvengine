package com.github.obsidianarch.gvengine.core;

/**
 * The two supported normal systems by VertexBufferObject.
 * 
 * @author Austin
 */
public enum NormalSystem {
    
    DISABLED( 0 ), ENABLED( 3 );
    
    /** The number of coordinates in a normal. */
    public int coordinates;
    
    /**
     * @param i
     *            The number of coordinates in a normal.
     */
    NormalSystem(int i) {
        coordinates = i;
    }
    
}
