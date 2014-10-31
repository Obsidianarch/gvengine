package com.github.obsidianarch.gvengine.core;

import org.lwjgl.util.vector.Vector3f;

/**
 * One of the six faces on a voxel.
 *
 * @author Austin
 * @version 14.10.28
 * @since 14.03.30
 */
public enum Face
{

    /**
     * The left side of a voxel.
     */
    LEFT( 0, new Vector3f( -1, 0, 0 ) ),
    /**
     * The right side of a voxel.
     */
    RIGHT( 1, new Vector3f( 1, 0, 0 ) ),
    /**
     * The bottom side of a voxel.
     */
    BOTTOM( 2, new Vector3f( 0, -1, 0 ) ),
    /**
     * The top side of a voxel.
     */
    TOP( 3, new Vector3f( 0, 1, 0 ) ),
    /**
     * The front side of a voxel.
     */
    FRONT( 4, new Vector3f( 0, 0, -1 ) ),
    /**
     * The back side of a voxel.
     */
    BACK( 5, new Vector3f( 0, 0, 1 ) );

    //
    // Fields
    //

    /**
     * An integer value also representing the direction of the face.
     */
    public int value;

    public Vector3f normal;

    //
    // Constructors
    //

    /**
     * @param value
     *         An integer value representing the face direction (used in arrays).
     * @param normal
     *          An vector perpendicular to the face.
     *
     * @since 14.03.30
     */
    Face( int value, Vector3f normal )
    {
        this.value = value;
        this.normal = normal;
    }

    //
    // Getters
    //

    /**
     * @return The normal data for this specific face.
     *
     * @throws UnsupportedOperationException
     *         If the face object is not LEFT, RIGHT, BOTTOM, TOP, BACK, or FRONT somehow. If this ever gets thrown, that means I changed the definition of a
     *         cube and added another face and did not update the coordinate system for this.
     * @since 14.03.30
     */
    public float[] getNormals( float x, float y, float z )
    {
        switch ( this )
        {

            case LEFT:
                return new float[] { x - 1, y, z };

            case RIGHT:
                return new float[] { x + 1, y, z };

            case BOTTOM:
                return new float[] { x, y - 1, z };

            case TOP:
                return new float[] { x, y + 1, z };

            case BACK:
                return new float[] { x, y, z + 1 };

            default: // FRONT
                return new float[] { x, y, z - 1 };

        }
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

            default: // FRONT
                return new int[] { x, y, z - 1 };

        }
    }

    /**
     * @return The face opposite to this face.
     *
     * @throws UnsupportedOperationException
     *         If the face object is not LEFT, RIGHT, BOTTOM, TOP, BACK, or FRONT somehow. If this ever gets thrown, that means I changed the definition of a
     *         cube and added another face and did not update the coordinate system for this.
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

            default: // FRONT
                return BACK;

        }
    }

}
