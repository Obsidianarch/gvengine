package com.github.obsidianarch.gvengine.core;

import static org.lwjgl.opengl.GL11.*;

/**
 * An implementation of an OpenGL camera. The camera's position and rotation can be
 * changed, allowing for the illusion of movement and head rotation. The positions are set
 * using the left-hand coordinate system (x is length, y is height, z is width). <BR>
 * Despite the fact that a camera is usually looked through, it comes in handy when an NPC
 * needs to have "sight". When coupled with a {@code Controller}, an NPC can be moved and
 * have it's own viewport.
 * 
 * @author Austin
 * @see Controller
 */
public class Camera {
    
    //
    // Fields
    //
    
    /** The x position of the camera. */
    protected float x;
    /** The y position of the camera. */
    protected float y;
    /** The z position of the camera. */
    protected float z;
    
    /** The rotation around the x axis. */
    protected float pitch;
    /** The rotation around the y axis. */
    protected float yaw;
    /** The rotation around the z axis. */
    protected float roll;
    
    //
    // Setters
    //
    
    /**
     * Sets the x coordinate of the camera.
     * 
     * @param x
     *            The new x position of the camera.
     */
    public void setX( float x ) {
        this.x = x;
    }
    
    /**
     * Sets the y coordinate of the camera.
     * 
     * @param y
     *            The new y position of the camera.
     */
    public void setY( float y ) {
        this.y = y;
    }
    
    /**
     * Sets the z coordinate of the camera.
     * 
     * @param z
     *            The new z position of the camera.
     */
    public void setZ( float z ) {
        this.z = z;
    }
    
    /**
     * Sets the pitch of the camera.
     * 
     * @param pitch
     *            The rotation around the x axis.
     */
    public void setPitch( float pitch ) {
        this.pitch = pitch;
    }
    
    /**
     * Sets the yaw of the camera.
     * 
     * @param yaw
     *            The rotation around the y axis.
     */
    public void setYaw( float yaw ) {
        this.yaw = yaw;
    }
    
    /**
     * Sets the roll of the camera.
     * 
     * @param roll
     *            The rotation around the z axis.
     */
    public void setRoll( float roll ) {
        this.roll = roll;
    }
    
    //
    // Getters
    //
    
    /**
     * @return The x position of the camera.
     */
    public float getX() {
        return x;
    }
    
    /**
     * @return The y position of the camera.
     */
    public float getY() {
        return y;
    }
    
    /**
     * @return The z position of the camera.
     */
    public float getZ() {
        return z;
    }
    
    /**
     * @return The rotation around the x axis.
     */
    public float getPitch() {
        return pitch;
    }
    
    /**
     * @return The rotation around the y axis.
     */
    public float getYaw() {
        return yaw;
    }
    
    /**
     * @return The rotation around the z axis.
     */
    public float getRoll() {
        return roll;
    }
    
    //
    // Actions
    //
    
    /**
     * Rotates and translates the viewport of OpenGL to the camera's rotation and
     * position.
     */
    public void lookThrough() {
        glRotatef( pitch, 1, 0, 0 ); // set the pitch
        glRotatef( yaw, 0, 1, 0 ); // set the yaw
        glRotatef( roll, 0, 0, 1 ); // set the roll
        
        glTranslatef( -x, -y, -z ); // translates the OpenGL screen to the camera's position
    }
    
    //
    // Overrides
    //
    
    @Override
    public String toString() {
        return "com.github.obsidianarch.gvengine.core.Camera[" + x + ", " + y + ", " + z + "; " + pitch + ", " + yaw + ", " + roll + "]";
    }
    
}
