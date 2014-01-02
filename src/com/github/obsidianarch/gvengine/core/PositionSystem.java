package com.github.obsidianarch.gvengine.core;

/**
 * The three supported position systems by the VertexBufferObject.
 * 
 * @author Austin
 */
public enum PositionSystem {
    
    XY( 2 ), XYZ( 3 ), XYZW( 4 );
    
    /** The number of coordinates in the system. */
    public final int coordinates;
    
    /**
     * @param i
     *            The number of coordinates in the system.
     */
    PositionSystem(int i) {
        coordinates = i;
    }
    
}
