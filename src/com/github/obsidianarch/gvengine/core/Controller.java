package com.github.obsidianarch.gvengine.core;

/**
 * A simple implementation of a controller. Based around a camera for position and
 * rotation controls, the controller will control the camera, even if the camera is not
 * actively being looked through. This allows for the camera to serve as an NPC's "view"
 * despite the fact that the game will (most likely) never view the world through the
 * NPC's "eyes."
 * 
 * @author Austin
 * @see Camera
 */
public class Controller {
    
    /** The camera to which this controller is bound. */
    private Camera camera;
    
    //
    // Constructors
    //
    
    /**
     * Creates a new controller, bound the given camera.
     * 
     * @param camera
     *            The camera to bind the controller to.
     */
    public Controller( Camera camera ) {
        setCamera( camera );
    }
    
    //
    // Setters
    //
    
    /**
     * Binds the controller to a new camera.
     * 
     * @param camera
     *            The camera to which the controller will the bound.
     * @throws NullPointerException
     *             If {@code camera} is {@code null}.
     */
    public void setCamera( Camera camera ) throws NullPointerException {
        if ( camera == null ) throw new NullPointerException( "Controller cannot be bound to a null camera!" );
        this.camera = camera;
    }
    
    //
    // Getters
    //
    
    /**
     * @return The camera to which the controller is currently bound.
     */
    public Camera getCamera() {
        return camera;
    }
    
    //
    // Methods
    //
    
    /**
     * Moves the camera forward, in the direction it is currently looking.
     * 
     * @param distance
     *            The distance to move forward.
     * @see #moveBackward(float)
     * @see #moveBackward(float)
     * @see #moveLeft(float)
     */
    public void moveForward( float distance ) {
        camera.x += distance * ( float ) Math.sin( Math.toRadians( camera.yaw ) );
        camera.y -= distance * ( float ) Math.tan( Math.toRadians( camera.pitch ) );
        camera.z -= distance * ( float ) Math.cos( Math.toRadians( camera.yaw ) );
    }
    
    /**
     * Moves the camera backward, in the opposite direction it is currently looking. This
     * will call {@code moveForward} with the negative distance.<BR>
     * {@code moveForward( -distance );}
     * 
     * @param distance
     *            The distance to move backward.
     * @see #moveForward(float)
     * @see #moveRight(float)
     * @see #moveLeft(float)
     */
    public void moveBackward( float distance ) {
        moveForward( -distance ); // we lazy
    }
    
    /**
     * Moves the camera to the right, relative to the direction the camera is currently
     * looking.
     * 
     * @param distance
     *            The distance to move right.
     * @see #moveForward(float)
     * @see #moveBackward(float)
     * @see #moveLeft(float)
     */
    public void moveRight( float distance ) {
        camera.x += distance * ( float ) Math.sin( Math.toRadians( camera.yaw + 90 ) );
        camera.z -= distance * ( float ) Math.cos( Math.toRadians( camera.yaw + 90 ) );
    }
    
    /**
     * Moves the camera to the left, relative to the direction the camera is currently
     * looking. This will call {@code moveRight} with the negative distance.<BR>
     * {@code moveRight( -distance );}
     * 
     * @param distance
     *            The distance to move left.
     * @see #moveForward(float)
     * @see #moveBackward(float)
     * @see #moveRight(float)
     */
    public void moveLeft( float distance ) {
        moveRight( -distance ); // we still lazy
    }
    
}
