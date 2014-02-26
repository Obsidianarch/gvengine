package com.github.obsidianarch.gvengine.core;

/**
 * The two supported systems of colors by VertexBufferObject.
 * 
 * @author Austin
 */
public enum ColorSystem {
    
    /**
     * The alpha channel will not be used.
     */
    RGB( 3 ),
    /**
     * The alpha channel will be used.
     */
    RGBA( 4 );
    
    /** The number of colors channels per color. */
    public int channels;
    
    /**
     * @param i
     *            Channel count per color.
     */
    ColorSystem(int i) {
        channels = i;
    }
    
}
