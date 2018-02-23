package com.addonovan.gvengine.core;

import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 * An implementation of an OpenGL camera. The camera's position and rotation can be changed, allowing for the illusion of movement and head rotation. The
 * positions are set using the left-hand coordinate system (x is length, y is height, z is width). <BR> Despite the fact that a camera is usually looked
 * through, it comes in handy when an NPC needs to have "sight". When coupled with a {@code Controller}, an NPC can be moved and have it's own viewport.
 *
 * @author Austin
 * @version 14.03.30
 * @see Controller
 * @since 14.03.30
 */
public class Camera
{

    //
    // Fields
    //

    /**
     * The x position of the camera.
     */
    protected float x;

    /**
     * The y position of the camera.
     */
    protected float y;

    /**
     * The z position of the camera.
     */
    protected float z;

    /**
     * The rotation around the x axis.
     */
    protected float pitch;

    /**
     * The rotation around the y axis.
     */
    protected float yaw;

    /**
     * The rotation around the z axis.
     */
    protected float roll;

    /**
     * The maximum pitch the camera can go to.
     */
    protected float maxPitch;

    /**
     * The minimum pitch the camera can go to.
     */
    protected float minPitch;

    //
    // Setters
    //

    /**
     * Sets the x coordinate of the camera.
     *
     * @param x
     *         The new x position of the camera.
     */
    public void setX( float x )
    {
        this.x = x;
    }

    /**
     * Sets the y coordinate of the camera.
     *
     * @param y
     *         The new y position of the camera.
     */
    public void setY( float y )
    {
        this.y = y;
    }

    /**
     * Sets the z coordinate of the camera.
     *
     * @param z
     *         The new z position of the camera.
     */
    public void setZ( float z )
    {
        this.z = z;
    }

    /**
     * Sets the position of the camera.
     *
     * @param x
     *         The new x position of the camera.
     * @param y
     *         The new y position of the camera.
     * @param z
     *         The new z position of the camera.
     */
    public void setPosition( float x, float y, float z )
    {
        setX( x );
        setY( y );
        setZ( z );
    }

    /**
     * Sets the pitch of the camera.
     *
     * @param pitch
     *         The rotation around the x axis.
     *
     * @version 14.03.30
     * @since 14.03.30
     */
    public void setPitch( float pitch )
    {
        if ( pitch < 0 )
        {
            pitch += 360f; // unnegativize(TM) the number
        }
        pitch %= 360; // get the number within 0-359

        // the pitch is out of range
        if ( ( pitch > minPitch ) && ( pitch < maxPitch ) )
        {

            // set the pitch to the nearest boundary
            if ( pitch >= 180 )
            {
                pitch = maxPitch;
            }
            if ( pitch < 180 )
            {
                pitch = minPitch;
            }
        }
        this.pitch = pitch;
    }

    /**
     * Sets the yaw of the camera.
     *
     * @param yaw
     *         The rotation around the y axis.
     */
    public void setYaw( float yaw )
    {
        if ( yaw < 0 )
        {
            yaw += 360;
        }
        this.yaw = yaw % 360;
    }

    /**
     * Sets the roll of the camera.
     *
     * @param roll
     *         The rotation around the z axis.
     */
    public void setRoll( float roll )
    {
        if ( roll < 0 )
        {
            roll += 360;
        }
        this.roll = roll % 360;
    }

    /**
     * Sets the rotation of the camera.
     *
     * @param pitch
     *         The rotation around the x axis.
     * @param yaw
     *         The rotation around the y axis.
     * @param roll
     *         The rotation around the z axis.
     */
    public void setRotation( float pitch, float yaw, float roll )
    {
        setPitch( pitch );
        setYaw( yaw );
        setRoll( roll );
    }

    /**
     * Sets the maximum pitch the camera can go to, 90� is directly forward.
     *
     * @param max
     *         The highest pitch the camera can go to.
     *
     * @version 14.03.30
     * @since 14.03.30
     */
    public void setMaximumPitch( float max )
    {
        maxPitch = max % 360;
        maxPitch = 90 - maxPitch;
        if ( maxPitch < 0 )
        {
            maxPitch += 360;
        }
    }

    /**
     * Sets the minimum pitch the camera can go to, 90� is directly forward.
     *
     * @param min
     *         The lowest pitch the camera can go to.
     *
     * @version 14.03.30
     * @since 14.03.30
     */
    public void setMinimumPitch( float min )
    {
        minPitch = min % 360;
        minPitch = 90 - minPitch;
        if ( minPitch < 0 )
        {
            minPitch += 360;
        }
    }

    //
    // Getters
    //

    /**
     * @return The x position of the camera.
     */
    public float getX()
    {
        return x;
    }

    /**
     * @return The y position of the camera.
     */
    public float getY()
    {
        return y;
    }

    /**
     * @return The z position of the camera.
     */
    public float getZ()
    {
        return z;
    }

    /**
     * @return The rotation around the x axis.
     */
    public float getPitch()
    {
        return pitch;
    }

    /**
     * @return The rotation around the y axis.
     */
    public float getYaw()
    {
        return yaw;
    }

    /**
     * @return The rotation around the z axis.
     */
    public float getRoll()
    {
        return roll;
    }

    //
    // Actions
    //

    /**
     * Rotates and translates the viewport of OpenGL to the camera's rotation and position.
     *
     * @version 14.03.30
     * @since 14.03.30
     */
    public void lookThrough()
    {
        glRotatef( pitch, 1, 0, 0 ); // set the pitch
        glRotatef( yaw, 0, 1, 0 ); // set the yaw
        glRotatef( roll, 0, 0, 1 ); // set the roll

        glTranslatef( -x, -y, -z ); // translates the OpenGL screen to the camera's position
    }

    //
    // Overrides
    //

    @Override
    public String toString()
    {
        return "core.Camera[" + x + ", " + y + ", " + z + "; " + pitch + ", " + yaw + ", " + roll + "]";
    }

}
