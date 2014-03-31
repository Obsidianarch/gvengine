package com.github.obsidianarch.gvengine.core;

/**
 * One of the six faces on a voxel.
 * 
 * @author Austin
 * 
 * @since 14.03.30
 * @version 14.03.30
 */
public enum Face {
    
    /**
     * The left side of a voxel.
     */
    LEFT( 0 ),
    /**
     * The right side of a voxel.
     */
    RIGHT( 1 ),
    /**
     * The bottom side of a voxel.
     */
    BOTTOM( 2 ),
    /**
     * The top side of a voxel.
     */
    TOP( 3 ),
    /**
     * The front side of a voxel.
     */
    FRONT( 4 ),
    /**
     * The back side of a voxel.
     */
    BACK( 5 );
    
    //
    // Fields
    //

    /** An integer value also representing the direction of the face. */
    public int value;
    
    //
    // Constructors
    //

    /**
     * @param i
     *            An integer value representing the face direction (used in arrays).
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    Face(int i) {
        value = i;
    }
    
}
