package com.github.obsidianarch.gvengine.tests;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * @author Austin
 */
public class TestingHelper {
    
    //
    // Variables
    //
    
    private static long lastTime    = getTime();
    
    private static long lastFPS     = getTime();
    
    private static int  fps         = 0;
    
    private static int  measuredFPS = 0;
    
    //
    // Methods
    //
    
    /**
     * Creates the Display.
     * 
     * @throws LWJGLException
     */
    public static void createDisplay() throws LWJGLException {
        Display.setTitle( "Voxel Testing" );
        Display.setDisplayMode( new DisplayMode( 640, 480 ) ); // the window will be 640 x 480
        Display.create(); // create the display
        
        Mouse.setGrabbed( true ); // grab the mouse
    }
    
    /**
     * Sets up OpenGL for the game tests.
     */
    public static void setupGL() {
        glShadeModel( GL_SMOOTH ); // supposedly smooths things out
        glClearColor( 0, 0, 0, 0 ); // sets the background color when we clear (black with no alpha channel)
        glClearDepth( 1 ); // depth value to use when the depth buffer is cleared
        
        glEnable( GL_DEPTH_TEST ); // enable depth, testing if a vertex is behind others
        glDepthFunc( GL_LEQUAL ); // I got no idea what this does, but anything other than LESS or LEQUAL breaks the rendering
        
        glEnable( GL_CULL_FACE ); // culls triangles that aren't visible
        
        glEnable( GL_BLEND ); // enable blending
        glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA ); // allow for the alpha channel, granted I may never use it, but it's still nice to have.
        
        glEnableClientState( GL_VERTEX_ARRAY ); // enable vertex arrays
        glEnableClientState( GL_COLOR_ARRAY ); // enable color arrays
        
        glMatrixMode( GL_PROJECTION ); // sets the projection matrix to be altered
        glLoadIdentity(); // reset the projection matrix
        
        // fovy, aspect ratio (x:y), zNear, zFar
        gluPerspective( 45f, ( ( float ) Display.getWidth() / ( float ) Display.getHeight() ), 0.0001f, 1000.0f ); // the perspective of the person
        
        glMatrixMode( GL_MODELVIEW ); // sets the modelview matrix to be altered
        glHint( GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST ); // supposedly makes everything look nicer
    }
    
    /**
     * @return The system time in milliseconds.
     */
    public static long getTime() {
        return ( Sys.getTime() * 1000 ) / Sys.getTimerResolution();
    }
    
    /**
     * @return The time since the last frame.
     */
    public static float getDelta() {
        float dt = getTime() - lastTime;
        lastTime = getTime();
        return dt;
    }
    
    /**
     * @return The last measured FPS.
     */
    public static int getFPS() {
        if ( ( getTime() - lastFPS ) > 1000 ) {
            measuredFPS = fps;
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
        
        return measuredFPS;
    }
    
}
