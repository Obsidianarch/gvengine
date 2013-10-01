package com.yahoo.obsidianarch;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.github.obsidianarch.gvengine.Camera;

public class FPCamera implements Camera {
    
    //
    // Fields
    //
    
    public double x, y, z; // positions on the x, y, and z axes (respectively)
    public float  yaw, pitch, roll; // rotation around y, x, and z axes (respectively)
        
    //
    // Constructors
    //
    
    public FPCamera() {
        this( 0, 0, 0 );
    }
    
    public FPCamera(double x, double y, double z) {
        this( x, y, z, 0, 0, 0 );
    }
    
    public FPCamera(double x, double y, double z, float yaw, float pitch, float roll) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }
    
    //
    // Actions
    //
    
    public void update( double dt ) {
        final double distance = 5.0f * dt * ( Keyboard.isKeyDown( Keyboard.KEY_LSHIFT ) ? 2 : 1 );
        
        if ( Keyboard.isKeyDown( Keyboard.KEY_W ) ) {
            x += distance * Math.sin( Math.toRadians( yaw ) );
            z -= distance * Math.cos( Math.toRadians( yaw ) );
        }
        
        if ( Keyboard.isKeyDown( Keyboard.KEY_S ) ) {
            x -= distance * Math.sin( Math.toRadians( yaw ) );
            z += distance * Math.cos( Math.toRadians( yaw ) );
        }
        
        if ( Keyboard.isKeyDown( Keyboard.KEY_A ) ) {
            x += distance * Math.sin( Math.toRadians( yaw - 90 ) );
            z -= distance * Math.cos( Math.toRadians( yaw - 90 ) );
        }
        
        if ( Keyboard.isKeyDown( Keyboard.KEY_D ) ) {
            x += distance * Math.sin( Math.toRadians( yaw + 90 ) );
            z -= distance * Math.cos( Math.toRadians( yaw + 90 ) );
        }
        
        if ( Keyboard.isKeyDown( Keyboard.KEY_SPACE ) ) {
            y += distance;
        }
        
        else if ( Keyboard.isKeyDown( Keyboard.KEY_LCONTROL ) ) {
            y -= distance;
        }
        
        if ( !Mouse.isGrabbed() ) return;
        
        final float mouseSensitivity = .05f;
        
        setYaw( yaw + ( Mouse.getDX() * mouseSensitivity ) );
        setPitch( pitch - ( Mouse.getDY() * mouseSensitivity ) );
    }
    
    private float keepInBounds( float in, float min, float max, float step ) {
        while ( in < min ) {
            in += step;
        }
        
        while ( in > max ) {
            in -= step;
        }
        
        return in;
    }
    
    //
    // Overrides
    //
    
    @Override
    public void transformView() {
        glRotatef( pitch, 1f, 0f, 0f ); // rotate around the x axis
        glRotatef( yaw, 0f, 1f, 0f ); // rotate around the y axis
        glTranslated( -x, -y, -z ); // translate to the position of the camera
    }
    
    //
    // Getters
    //
    
    @Override
    public double getX() {
        return x;
    }
    
    @Override
    public double getY() {
        return y;
    }
    
    @Override
    public double getZ() {
        return z;
    }
    
    @Override
    public float getYaw() {
        return yaw;
    }
    
    @Override
    public float getPitch() {
        return pitch;
    }
    
    @Override
    public float getRoll() {
        return roll;
    }
    
    //
    // Setters
    //
    
    @Override
    public void setX( double x ) {
        this.x = x;
    }
    
    @Override
    public void setY( double y ) {
        this.y = y;
    }
    
    @Override
    public void setZ( double z ) {
        this.z = z;
    }
    
    @Override
    public void setYaw( float yaw ) {
        this.yaw = keepInBounds( yaw, 0, 360, 360 );
    }
    
    @Override
    public void setPitch( float pitch ) {
        this.pitch = keepInBounds( pitch, 0, 360, 360 );
    }
    
    @Override
    public void setRoll( float roll ) {
        this.roll = keepInBounds( roll, 0, 360, 360 );
    }
}
