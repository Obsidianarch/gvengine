package com.github.obsidianarch.gvengine.core;

/**
 * One of the six faces on a voxel.
 * 
 * @author Austin
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
    
    /** An integer value also representing the direction of the face. */
    public int value;
    
    /**
     * @param i
     *            An integer value representing the face direction (used in arrays).
     */
    Face(int i) {
        value = i;
    }
    
}
