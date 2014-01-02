package com.github.obsidianarch.gvengine.core;

/**
 * One of the six faces on a voxel.
 * 
 * @author Austin
 */
public enum Face {
    
    LEFT( 0 ), RIGHT( 1 ), BOTTOM( 2 ), TOP( 3 ), FRONT( 4 ), BACK( 5 );
    
    /** An integer value also representing the direction of the face. */
    public int value;
    
    Face(int i) {
        value = i;
    }
    
}
