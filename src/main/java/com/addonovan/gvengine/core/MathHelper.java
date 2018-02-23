package com.addonovan.gvengine.core;

import org.lwjgl.util.vector.Vector3f;
import org.magicwerk.brownies.collections.primitive.FloatGapList;

import java.nio.FloatBuffer;

/**
 * A general class for helping with common mathematical calculations.
 *
 * @author Austin
 * @version 14.08.02
 * @since 14.03.30
 */
public final class MathHelper
{

    //
    // General Math
    //

    /**
     * Checks if x is inclusively within the specified range.
     *
     * @param n
     *         The number.
     * @param min
     *         The minimum value in the range (inclusive).
     * @param max
     *         The maximum value in the range (exclusive).
     *
     * @return If {@code xMin <= x < xMax}.
     *
     * @since 14.03.30
     */
    public static boolean inRange( float n, float min, float max )
    {
        return ( ( n >= min ) && ( n < max ) );
    }

    /**
     * Adds teh data from teh source {@code FloatGapList} into the {@code FloatBuffer}.
     *
     * @param source
     *         The source of information.
     * @param buffer
     *         The buffer into which items will be inserted.
     * @param size
     *         The number of items to add at a time.
     * @param offset
     *         The index in the buffer at which the first item will be inserted.
     * @param stride
     *         The distance between insertions.
     *
     * @since 14.03.30
     */
    public static void insertBuffer( FloatGapList source, FloatBuffer buffer, int size, int offset, int stride )
    {
        if ( size == 0 )
        {
            return;
        }

        int bufferIndex = offset; // the index in the buffer

        // iterating over each item in the array
        for ( int i = 0; i < source.size(); )
        {

            // add the data into the buffer
            for ( int j = 0; j < size; j++ )
            {
                buffer.put( bufferIndex + j, source.get( i++ ) );
            }

            bufferIndex += size + stride; // we just added <size> items, and will skip to the next section
        }
    }

    //
    // Raycasting
    //

    /**
     * Returns a Vector3f for the position of the target when it is {@code targetDistance} away from the current direction the camera is looking.
     *
     * @param camera
     *         The camera.
     * @param targetDistance
     *         The distance from the camera the target will be.
     *
     * @return A Vector3f {@code targetDistance} away from the camera in it's current direction.
     *
     * @since 14.03.30
     */
    public static Vector3f getTarget( Camera camera, int targetDistance )
    {
        Vector3f target = new Vector3f( camera.getX(), camera.getY(), camera.getZ() );

        // if this looks similar to the moveForward( float ) method in Controller
        // then you would know exactly where I got this from.
        target.x += targetDistance * ( float ) Math.sin( Math.toRadians( camera.getYaw() ) );
        target.y -= targetDistance * ( float ) Math.tan( Math.toRadians( camera.getPitch() ) );
        target.z -= targetDistance * ( float ) Math.cos( Math.toRadians( camera.getYaw() ) );

        return target;
    }

    //
    // Chunk Position Math
    //

    /**
     * @param index
     *         The index of an item in an array of 4096 entries.
     * @param size
     *         The cube root of the array length.
     *
     * @return The x coordinate.
     *
     * @since 14.03.30
     */
    public static int getXPosition( int index, int size )
    {
        return index % size;
    }

    /**
     * @param index
     *         The index of an item in an array of 4096 entries.
     * @param size
     *         The cube root of the array length.
     *
     * @return The y coordinate.
     *
     * @since 14.03.30
     */
    public static int getYPosition( int index, int size )
    {
        return ( index / size ) % size;
    }

    /**
     * @param index
     *         The index of an item in an array.
     * @param size
     *         The cube root of the array length.
     *
     * @return The z coordinate.
     *
     * @since 14.03.30
     */
    public static int getZPosition( int index, int size )
    {
        return ( index - getXPosition( index, size ) - ( size * getYPosition( index, size ) ) ) / ( size * size );
    }
}
