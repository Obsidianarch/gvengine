package com.addonovan.gvengine.core;

/**
 * The two supported systems of colors by VertexBufferObject.
 *
 * @author Austin
 * @version 14.03.30
 * @since 14.03.30
 */
public enum ColorSystem
{

    /**
     * The alpha channel will not be used.
     */
    RGB( 3 ),
    /**
     * The alpha channel will be used.
     */
    RGBA( 4 );

    //
    // Fields
    //

    /**
     * The number of colors channels per color.
     */
    public int channels;

    //
    // Constructors
    //

    /**
     * @param i
     *         Channel count per color.
     *
     * @since 14.03.30
     */
    ColorSystem( int i )
    {
        channels = i;
    }

}
