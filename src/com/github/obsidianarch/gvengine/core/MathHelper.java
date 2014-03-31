package com.github.obsidianarch.gvengine.core;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Vector3f;
import org.magicwerk.brownies.collections.primitive.FloatGapList;

import com.github.obsidianarch.gvengine.Chunk;
import com.github.obsidianarch.gvengine.Region;

/**
 * A general class for helping with common mathematical calculations.
 * 
 * @author Austin
 * 
 * @since 14.03.30
 * @version 14.03.30
 */
public final class MathHelper {
    
    //
    // General Math
    //
    
    /**
     * Checks if x is inclusively within the specified range.
     * 
     * @param x
     *            The number.
     * @param xMin
     *            The minimum value in the range (inclusive).
     * @param xMax
     *            The maximum value in the range (inclusive).
     * @return If {@code xMin <= x <= xMax}.
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    public static boolean inRange( float x, float xMin, float xMax ) {
        return ( ( x >= xMin ) && ( x <= xMax ) );
    }

    /**
     * Adds teh data from teh source {@code FloatGapList} into the {@code FloatBuffer}.
     * 
     * @param source
     *            The source of information.
     * @param buffer
     *            The buffer into which items will be inserted.
     * @param size
     *            The number of items to add at a time.
     * @param offset
     *            The index in the buffer at which the first item will be inserted.
     * @param stride
     *            The distance between insertions.
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    public static void insertBuffer( FloatGapList source, FloatBuffer buffer, int size, int offset, int stride ) {
        if ( size == 0 ) return;
        
        int bufferIndex = offset; // the index in the buffer
        
        // iterating over each item in the array
        for ( int i = 0; i < source.size(); ) {
            
            // add the data into the buffer
            for ( int j = 0; j < size; j++ ) {
                buffer.put( bufferIndex + j, source.get( i++ ) );
            }
            
            bufferIndex += size + stride; // we just added <size> items, and will skip to the next section
        }
    }
    
    //
    // Raycasting
    //
    
    /**
     * Returns a Vector3f for the position of the target when it is {@code targetDistance}
     * away from the current direction the camera is looking.
     * 
     * @param camera
     *            The camera.
     * @param targetDistance
     *            The distance from the camera the target will be.
     * @return A Vector3f {@code targetDistance} away from the camera in it's current
     *         direction.
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    public static Vector3f getTarget( Camera camera, int targetDistance ) {
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
     *            The index of an item in an array of 4096 entries.
     * @param size
     *            The cube root of the array length.
     * @return The x coordinate.
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    public static int getXPosition( int index, int size ) {
        return index % size;
    }
    
    /**
     * @param index
     *            The index of an item in an array of 4096 entries.
     * @param size
     *            The cube root of the array length.
     * @return The y coordinate.
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    public static int getYPosition( int index, int size ) {
        return ( index / size ) % size;
    }
    
    /**
     * @param index
     *            The index of an item in an array.
     * @param size
     *            The cube root of the array length.
     * @return The z coordinate.
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    public static int getZPosition( int index, int size ) {
        return ( index - getXPosition( index, size ) - ( size * getYPosition( index, size ) ) ) / ( size * size );
    }
    
    /**
     * Gets the chunk coordinate for the respective global coordinate.
     * 
     * @param globalCoordinate
     *            The global coordinate.
     * @return The chunk coordinate.
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    public static int getChunkCoordinate( float globalCoordinate ) {
        return ( int ) Math.floor( globalCoordinate / 16f );
    }
    
    /**
     * Gets the chunk (also known as local) position for an object. This is the position
     * of an object relative to the chunk's origin and floored to an integer.
     * 
     * @param globalPosition
     *            The position in 3d space the object is at.
     * @return The chunk position of the object.
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    public static int getChunkPosition( float globalPosition ) {
        return ( int ) Math.floor( globalPosition - ( getChunkCoordinate( globalPosition ) * 16 ) );
    }
    
    /**
     * Gets the global position of the voxel based on the chunk it resides in.
     * 
     * @param c
     *            The chunk the voxel is in.
     * @param localPos
     *            The {@code x, y, and z} positions of the voxel inside the chunk.
     * @return The {@code x, y, and z} positions of the voxel, relative to the world's
     *         origin.
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    public static float[] getGlobalPosition( Chunk c, int[] localPos ) {
        float[] globalPos = new float[ 3 ];
        Region r = c.region; // the region this chunk belongs to
        
        if ( r == null ) { // there is no region
            globalPos[ 0 ] = localPos[ 0 ] + ( c.x * 16 );
            globalPos[ 1 ] = localPos[ 1 ] + ( c.y * 16 );
            globalPos[ 2 ] = localPos[ 2 ] + ( c.z * 16 );
        }
        else {
            // region offset, in voxels
            float rOffX = r.x * 64;
            float rOffY = r.y * 64;
            float rOffZ = r.z * 64;
            
            // chunk offset, in voxels
            float cOffX = rOffX + ( c.x * 16 );
            float cOffY = rOffY + ( c.y * 16 );
            float cOffZ = rOffZ + ( c.z * 16 );
            
            // add the voxel offset to the chunk offset
            globalPos[ 0 ] = localPos[ 0 ] + cOffX;
            globalPos[ 1 ] = localPos[ 1 ] + cOffY;
            globalPos[ 2 ] = localPos[ 2 ] + cOffZ;
        }
        
        return globalPos;
    }
}
