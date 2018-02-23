package com.addonovan.gvengine.entities;

import com.addonovan.gvengine.core.input.Input;

/**
 * A simple player who is controllable.
 *
 * @author Austin
 * @version 14.03.30
 * @since 14.03.30
 */
public class Player extends Entity
{

    /**
     * The speed at which the player moves.
     */
    public float movementSpeed = 10;

    //
    // Overrides
    //

    @Override
    public void update( float dt )
    {
        float movementSpeed = this.movementSpeed * ( Input.isBindingActive( "sprint" ) ? 2 : 1 ); // the player moves twice as fast when sprinting

        // move the player
        if ( Input.isBindingActive( "forward" ) )
        {
            controller.moveForward( movementSpeed * dt );
        }
        if ( Input.isBindingActive( "backward" ) )
        {
            controller.moveBackward( movementSpeed * dt );
        }
        if ( Input.isBindingActive( "left" ) )
        {
            controller.moveLeft( movementSpeed * dt );
        }
        if ( Input.isBindingActive( "right" ) )
        {
            controller.moveRight( movementSpeed * dt );
        }
    }

    @Override
    public void render()
    {
        // TODO render the player
    }

}