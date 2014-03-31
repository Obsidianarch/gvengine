package com.github.obsidianarch.gvengine.core;

/**
 * The three supported position systems by the VertexBufferObject.
 * 
 * @author Austin
 * 
 * @since 14.03.30
 * @version 14.03.30
 */
public enum PositionSystem {
    
    /**
     * Only two coordinates will be rendered.
     */
    XY( 2 ),
    /**
     * Three coordinates will be used.
     */
    XYZ( 3 ),
    /**
     * All four coordinates will be used.
     */
    XYZW( 4 );
    
    /** The number of coordinates in the system. */
    public final int coordinates;
    
    /**
     * @param i
     *            The number of coordinates in the system.
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    PositionSystem(int i) {
        coordinates = i;
    }
    
}
