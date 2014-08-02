package com.github.obsidianarch.gvengine.core;

/**
 * The two supported normal systems by VertexBufferObject.
 *
 * @author Austin
 * @version 14.03.30
 * @since 14.03.30
 */
public enum NormalSystem
{

    /**
     * No normals will be in used for the face.
     */
    DISABLED( 0 ),
    /**
     * Normals will be used for the face.
     */
    ENABLED( 3 );

    /**
     * The number of coordinates in a normal.
     */
    public int coordinates;

    /**
     * @param i
     *         The number of coordinates in a normal.
     *
     * @since 14.03.30
     */
    NormalSystem( int i )
    {
        coordinates = i;
    }

}
