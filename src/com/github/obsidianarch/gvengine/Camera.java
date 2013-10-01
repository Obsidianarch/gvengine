package com.github.obsidianarch.gvengine;

/**
 * An interface defining the basic functions of a camera.
 * 
 * @author Austin
 */
public interface Camera {
    
    //
    // Actions
    //
    
    /**
     * Translates and rotates the viewport to the position and rotation of the camera.
     */
    public void transformView();
    
    //
    // Getters
    //
    
    /**
     * @return The x coordinate of the camera.
     */
    public double getX();
    
    /**
     * @return The y coordinate of the camera.
     */
    public double getY();
    
    /**
     * @return The z coordinate of the camera.
     */
    public double getZ();
    
    /**
     * @return The rotation of the camera around the y axis.
     */
    public float getYaw();
    
    /**
     * @return The rotation of the camera around the x axis.
     */
    public float getPitch();
    
    /**
     * @return The rotation of the camera around the z axis.
     */
    public float getRoll();
    
    //
    // Setters
    //
    
    /**
     * @param x
     *            The new x coordinate of the camera.
     */
    public void setX( double x );
    
    /**
     * @param y
     *            The new y coordinate of the camera.
     */
    public void setY( double y );
    
    /**
     * @param z
     *            The new z coordinate of the camera.
     */
    public void setZ( double z );
    
    /**
     * @param yaw
     *            The new rotation of the camera around the y axis.
     */
    public void setYaw( float yaw );
    
    /**
     * @param pitch
     *            The new rotation of the camera around the x axis.
     */
    public void setPitch( float pitch );
    
    /**
     * @param roll
     *            The new rotation of the camera around the z axis.
     */
    public void setRoll( float roll );
    
}
