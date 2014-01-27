package com.github.obsidianarch.gvengine.tests;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.io.File;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.github.obsidianarch.gvengine.core.input.Input;
import com.github.obsidianarch.gvengine.core.input.InputBindingMode;

/**
 * @author Austin
 */
public class TestingHelper {
    
    //
    // Variables
    //
    
    private static long       lastTime    = getTime();
    
    private static long       lastFPS     = getTime();
    
    private static int        fps         = 0;
    
    private static int        measuredFPS = 0;
    
    private static final File CONFIG_FILE = new File( System.getProperty( "user.dir" ), "config" );
    
    //
    // Methods
    //
    
    public static boolean isDeveloping() {
        String options = System.getProperty( "gvengine.developerMode", "false" );
        return options.equalsIgnoreCase( "true" );
    }
    
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
     * Initializes the input with the default control configurations.
     */
    public static void initInput() {
        Input.initialize(); // initialize Input
        
        // load the input bindings, and if it doesn't work, add the defaults
        if ( Input.loadBindings( CONFIG_FILE ) < 10 /* we have ten key bindings */) {
            
            Input.setBinding( "forward", InputBindingMode.KEYBOARD, Keyboard.KEY_W );
            Input.setBinding( "left", InputBindingMode.KEYBOARD, Keyboard.KEY_A );
            Input.setBinding( "backward", InputBindingMode.KEYBOARD, Keyboard.KEY_S );
            Input.setBinding( "right", InputBindingMode.KEYBOARD, Keyboard.KEY_D );
            Input.setBinding( "sprint", InputBindingMode.KEYBOARD, Keyboard.KEY_LSHIFT );
            
            Input.setBinding( "rebuildChunk", InputBindingMode.KEYBOARD, Keyboard.KEY_R );
            Input.setBinding( "removeVoxels", InputBindingMode.KEYBOARD, Keyboard.KEY_E );
            
            Input.setBinding( "unbindMouse", InputBindingMode.MOUSE, 0 );
            Input.setBinding( "bindMouse", InputBindingMode.MOUSE, 1 );
            Input.setBinding( "dbgc", InputBindingMode.MOUSE, 2 );
            
        }
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
    
    /**
     * Destroys the display and saves the input settings.
     */
    public static void destroy() {
        Display.destroy();
        Input.saveBindings( CONFIG_FILE );
    }
    
}
