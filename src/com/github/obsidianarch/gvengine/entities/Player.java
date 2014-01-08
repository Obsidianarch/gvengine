package com.github.obsidianarch.gvengine.entities;

import com.github.obsidianarch.gvengine.core.Entity;
import com.github.obsidianarch.gvengine.core.input.Input;

/**
 * A simple player who is controllable.
 * 
 * @author Austin
 */
public class Player extends Entity {
    
    /** The speed at which the player moves. */
    public float movementSpeed = 10;
    
    //
    // Overrides
    //
    
    @Override
    public void update( float dt ) {
        float movementSpeed = this.movementSpeed * ( Input.isBindingActive( Input.MOVE_SPRINT ) ? 2 : 1 ); // the player moves twice as fast when sprinting
        
        // move the player
        if ( Input.isBindingActive( Input.MOVE_FORWARD ) ) controller.moveForward( movementSpeed * dt );
        if ( Input.isBindingActive( Input.MOVE_BACKWARD ) ) controller.moveBackward( movementSpeed * dt );
        if ( Input.isBindingActive( Input.MOVE_LEFT ) ) controller.moveLeft( movementSpeed * dt );
        if ( Input.isBindingActive( Input.MOVE_RIGHT ) ) controller.moveRight( movementSpeed * dt );
    }
    
    @Override
    public void render() {
        // TODO render the player
    }
    
}
