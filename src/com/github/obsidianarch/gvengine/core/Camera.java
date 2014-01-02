package com.github.obsidianarch.gvengine.core;

import static org.lwjgl.opengl.GL11.*;

/**
 * An implementation of an OpenGL camera. The camera's position and rotation can be
 * changed, allowing for the illusion of movement and head rotation. The positions are set
 * using the left-hand coordinate system (x is length, y is height, z is width), and the
 * rotations around these axes are referred to as follows:<BR>
 * <table>
 * <tr>
 * <th>Name</th>
 * <th>Axis</th>
 * <th>Example</th>
 * </tr>
 * <tr>
 * <td>pitch</td>
 * <td>x</td>
 * <td>Doing a cartwheel</td>
 * </tr>
 * <tr>
 * <td>yaw</td>
 * <td>y</td>
 * <td>Spinning around in place</td>
 * </tr>
 * <tr>
 * <td>roll</td>
 * <td>z</td>
 * <td>Doing a somersault</td>
 * </tr>
 * </table>
 * Despite the fact that a camera is usually looked through, it comes in handy when an NPC
 * needs to have sight. When coupled with a {@code Controller}, an NPC can be moved and
 * have it's own set of eyes.
 * 
 * @author Austin
 * @since 1.0.0.1
 * @version 1.0
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
     * @since 1.0.0.1
     * @version 1.0
     * @param x
     *            The new x position of the camera.
     */
    public void setX( float x ) {
        this.x = x;
    }
    
    /**
     * Sets the y coordinate of the camera.
     * 
     * @since 1.0.0.1
     * @version 1.0
     * @param y
     *            The new y position of the camera.
     */
    public void setY( float y ) {
        this.y = y;
    }
    
    /**
     * Sets the z coordinate of the camera.
     * 
     * @since 1.0.0.1
     * @version 1.0
     * @param z
     *            The new z position of the camera.
     */
    public void setZ( float z ) {
        this.z = z;
    }
    
    /**
     * Sets the pitch of the camera.
     * 
     * @since 1.0.0.1
     * @version 1.0
     * @param pitch
     *            The rotation around the x axis.
     */
    public void setPitch( float pitch ) {
        this.pitch = pitch;
    }
    
    /**
     * Sets the yaw of the camera.
     * 
     * @since 1.0.0.1
     * @version 1.0
     * @param yaw
     *            The rotation around the y axis.
     */
    public void setYaw( float yaw ) {
        this.yaw = yaw;
    }
    
    /**
     * Sets the roll of the camera.
     * 
     * @since 1.0.0.1
     * @version 1.0
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
     * @since 1.0.0.1
     * @version 1.0
     * @return The x position of the camera.
     */
    public float getX() {
        return x;
    }
    
    /**
     * @since 1.0.0.1
     * @version 1.0
     * @return The y position of the camera.
     */
    public float getY() {
        return y;
    }
    
    /**
     * @since 1.0.0.1
     * @version 1.0
     * @return The z position of the camera.
     */
    public float getZ() {
        return z;
    }
    
    /**
     * @since 1.0.0.1
     * @version 1.0
     * @return The rotation around the x axis.
     */
    public float getPitch() {
        return pitch;
    }
    
    /**
     * @since 1.0.0.1
     * @version 1.0
     * @return The rotation around the y axis.
     */
    public float getYaw() {
        return yaw;
    }
    
    /**
     * @since 1.0.0.1
     * @version 1.0
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
     * 
     * @since 1.0.0.1
     * @version 1.0
     */
    public void transform() {
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
        return "ogle.core.Camera[" + x + ", " + y + ", " + z + "; " + pitch + ", " + yaw + ", " + roll + "]";
    }
    
}
