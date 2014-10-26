package com.github.obsidianarch.gvengine.core;

/**
 * One of the six faces on a voxel.
 *
 * @author Austin
 * @version 14.10.26
 * @since 14.03.30
 */
public enum Face
{

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

    /**
     * An integer value also representing the direction of the face.
     */
    public int value;

    //
    // Constructors
    //

    /**
     * @param i
     *         An integer value representing the face direction (used in arrays).
     *
     * @since 14.03.30
     */
    Face( int i )
    {
        value = i;
    }

    //
    // Getters
    //

    /**
     *
      @throws UnsupportedOperationException
     *          If the face object is not LEFT, RIGHT, BOTTOM, TOP, BACK, or FRONT somehow. If this ever gets thrown, that means I changed the definition of a
     *          cube and added another face and did not update the coordinate system for this.
     *
     * @return The normal data for this specific face.
     *
     * @since 14.03.30
     */
    public float[] getNormals( float x, float y, float z )
    {
        switch( this )
        {

            case LEFT:
                return new float[] { x - 1, y, z };

            case RIGHT:
                return new float[] { x + 1, y, z };

            case BOTTOM:
                return new float[] { x, y - 1, z };

            case TOP:
                return new float[] { x, y + 1, z };

            case FRONT:
                return new float[] { x, y, z - 1 };

            case BACK:
                return new float[] { x, y, z + 1 };

        }

        throw new UnsupportedOperationException( "Something's wrong about this face; index: " + value );
    }

    /**
     * Gets the coordinates of the voxel that is touching this face.
     *
     * @param x
     *         The x coordinate of the voxel.
     * @param y
     *         The y coordinate of the voxel.
     * @param z
     *         The z coordiante of the voxel.
     *
     * @return The coordinates of the voxel touching a specified voxel's face.
     *
     * @throws UnsupportedOperationException
     *         If the face object is not LEFT, RIGHT, BOTTOM, TOP, BACK, or FRONT somehow. If this ever gets thrown, that means I changed the definition of a
     *         cube and added another face and did not update the coordinate system for this.
     *
     * @since 14.03.31
     */
    public int[] getTouchingVoxel( int x, int y, int z ) throws UnsupportedOperationException
    {

        switch ( this )
        {

            case LEFT:
                return new int[] { x - 1, y, z };

            case RIGHT:
                return new int[] { x + 1, y, z };

            case BOTTOM:
                return new int[] { x, y - 1, z };

            case TOP:
                return new int[] { x, y + 1, z };

            case BACK:
                return new int[] { x, y, z + 1 };

            case FRONT:
                return new int[] { x, y, z - 1 };

        }

        throw new UnsupportedOperationException( "Something's wrong about this face; index: " + value );
    }

    /**
     * @return The face opposite to this face.
     *
     * @throws UnsupportedOperationException
     *         If the face object is not LEFT, RIGHT, BOTTOM, TOP, BACK, or FRONT somehow. If this ever gets thrown, that means I changed the definition of a
     *         cube and added another face and did not update the coordinate system for this.
     *
     * @since 14.03.31
     */
    public Face getOpposingFace() throws UnsupportedOperationException
    {

        switch ( this )
        {

            case LEFT:
                return RIGHT;

            case RIGHT:
                return LEFT;

            case BOTTOM:
                return TOP;

            case TOP:
                return BOTTOM;

            case BACK:
                return FRONT;

            case FRONT:
                return BACK;

        }

        throw new UnsupportedOperationException( "Something's wrong about this face; index: " + value );
    }

}
