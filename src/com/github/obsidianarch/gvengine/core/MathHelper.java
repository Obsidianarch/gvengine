package com.github.obsidianarch.gvengine.core;

import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author Austin
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
     */
    public static boolean inRange( float x, float xMin, float xMax ) {
        return ( ( x >= xMin ) && ( x <= xMax ) );
    }
    
    /**
     * Returns {@code milliseconds} in LWJGL ticks.
     * 
     * @param milliseconds
     *            The number of milliseconds to convert.
     * @return Ticks.
     */
    public static long toTicks( long milliseconds ) {
        return ( milliseconds * Sys.getTimerResolution() ) / 1000;
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
     * Gets the chunk coordinate for the respective global coordinate.
     * 
     * @param globalCoordinate
     *            The global coordinate.
     * @return The chunk coordinate.
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
     */
    public static int getChunkPosition( float globalPosition ) {
        return ( int ) Math.floor( globalPosition - ( getChunkCoordinate( globalPosition ) * 16 ) );
    }
    
    /**
     * Gets the global position for an object. This is the position of an object relative
     * to the world's origin.
     * 
     * @param chunkCoordinate
     *            The position of the chunk in this axis.
     * @param localPosition
     *            The position of the object relative to the chunk's axis.
     * @return The global position of the object.
     */
    public static float getGlobalPosition( int chunkCoordinate, int localPosition ) {
        return localPosition + ( chunkCoordinate * 16 );
    }
    
}
