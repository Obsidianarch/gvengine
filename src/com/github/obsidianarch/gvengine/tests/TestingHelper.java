package com.github.obsidianarch.gvengine.tests;

import com.github.obsidianarch.gvengine.core.Camera;
import com.github.obsidianarch.gvengine.core.Controller;
import com.github.obsidianarch.gvengine.core.input.Input;
import com.github.obsidianarch.gvengine.core.input.InputMedium;
import com.github.obsidianarch.gvengine.core.io.Config;
import com.github.obsidianarch.gvengine.core.io.Lumberjack;
import com.github.obsidianarch.gvengine.core.io.PlainTextConfigurationFormat;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.io.File;
import java.nio.FloatBuffer;

import static com.github.obsidianarch.gvengine.core.TimeHelper.getTime;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/**
 * A general class to simplify the testing cases, removing a large portion of code that's just required for setup.
 *
 * @author Austin
 * @version 14.08.03c
 * @since 14.03.30
 */
public class TestingHelper
{

    //
    // Fields
    //

    /**
     * The configuration file, using the PlainTextConfigurationFormat.
     */
    public static final Config CONFIG = new Config( new File( "config" ), new PlainTextConfigurationFormat() );

    /**
     * Ambient light coordinates.
     */
    public static final FloatBuffer LIGHT_AMBIENT;

    /**
     * Diffuse light coordinates.
     */
    public static final FloatBuffer LIGHT_DIFFUSE;

    /**
     * Specular light coordinates.
     */
    public static final FloatBuffer LIGHT_SPECULAR;

    /**
     * Position light coordiantes.
     */
    public static final FloatBuffer LIGHT_POSITION;

    /**
     * Shininess of the material.
     */
    public static final FloatBuffer LIGHT_SHININESS;

    /**
     * The last time a time measurement was taken (used for change in time).
     */
    private static long lastTime = getTime();

    /**
     * The last time an FPS measure was taken.
     */
    private static long lastFPS = getTime();

    /**
     * The current FPS.
     */
    private static int fps = 0;

    /**
     * The last measured FPS.
     */
    private static int measuredFPS = 0;

    //
    // Initializer
    //

    static
    {
        // open a new log file
        Lumberjack.openLogFile( new File( "gvengine.log" ) );

        LIGHT_AMBIENT = BufferUtils.createFloatBuffer( 4 ).put( new float[] { 0.2f, 0.2f, 0.2f, 1.0f } );
        LIGHT_DIFFUSE = BufferUtils.createFloatBuffer( 4 ).put( new float[] { 1.0f, 1.0f, 1.0f, 1.0f } );
        LIGHT_SPECULAR = BufferUtils.createFloatBuffer( 4 ).put( new float[] { 1.0f, 1.0f, 1.0f, 1.0f } );
        LIGHT_POSITION = BufferUtils.createFloatBuffer( 4 ).put( new float[] { 1.0f, 1.0f, 1.0f, 0.0f } );
        LIGHT_SHININESS = BufferUtils.createFloatBuffer( 4 ).put( new float[] { 50f, 50f, 50f, 50f } );

        LIGHT_AMBIENT.flip();
        LIGHT_DIFFUSE.flip();
        LIGHT_SPECULAR.flip();
        LIGHT_POSITION.flip();
        LIGHT_SHININESS.flip();
    }

    //
    // Methods
    //

    /**
     * @return {@code true} if the "gvengine.debug" property has been set to true.
     *
     * @since 14.03.30
     */
    public static boolean isDebugging()
    {
        return System.getProperty( "gvengine.debug", "false" ).equalsIgnoreCase( "true" );
    }

    /**
     * Creates the Display.
     *
     * @throws LWJGLException
     *         If the display couldn't be created.
     * @since 14.03.30
     */
    public static void createDisplay() throws LWJGLException
    {
        Display.setTitle( "Voxel Testing" );
        Display.setDisplayMode( new DisplayMode( 640, 480 ) ); // the window will be 640 x 480
        Display.create(); // create the display

        Mouse.setGrabbed( true ); // grab the mouse
    }

    /**
     * Sets up OpenGL for the game tests.
     *
     * @since 14.03.30
     */
    public static void setupGL()
    {
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
     * Enables lighting in the testing environment.
     *
     * @since 14.03.30
     */
    public static void enableLighting()
    {
        /*
        glMaterial( GL_FRONT, GL_SPECULAR, LIGHT_SPECULAR );
        glMaterial( GL_FRONT, GL_SHININESS, LIGHT_SHININESS );
        glLight( GL_LIGHT0, GL_POSITION, LIGHT_POSITION );

        glEnable( GL_LIGHTING );
        glEnable( GL_LIGHT0 );
        */

        glLight( GL_LIGHT1, GL_AMBIENT, LIGHT_AMBIENT );
        glLight( GL_LIGHT1, GL_DIFFUSE, LIGHT_DIFFUSE );
        glLight( GL_LIGHT1, GL_SPECULAR, LIGHT_SPECULAR );
        glLight( GL_LIGHT1, GL_POSITION, LIGHT_POSITION );

        glColorMaterial( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE );
        glEnable( GL_COLOR_MATERIAL );

        glEnable( GL_LIGHTING );
        glEnable( GL_LIGHT1 );
    }

    /**
     * Disables the lighting in the testing environment.
     *
     * @since 14.04.05
     */
    public static void disableLighting()
    {
        glDisable( GL_LIGHT1 );
        glDisable( GL_LIGHTING );
    }

    /**
     * Initializes the input with the default control configurations.
     *
     * @since 14.03.30
     */
    public static void initInput()
    {
        Input.initialize(); // initialize Input

        // load the input bindings, and if it doesn't work, add the defaults
        Input.loadBindings( CONFIG );

        Input.setBinding( "forward", InputMedium.KEYBOARD, Keyboard.KEY_W );
        Input.setBinding( "left", InputMedium.KEYBOARD, Keyboard.KEY_A );
        Input.setBinding( "backward", InputMedium.KEYBOARD, Keyboard.KEY_S );
        Input.setBinding( "right", InputMedium.KEYBOARD, Keyboard.KEY_D );
        Input.setBinding( "sprint", InputMedium.KEYBOARD, Keyboard.KEY_LSHIFT );

        Input.setBinding( "rebuildChunk", InputMedium.KEYBOARD, Keyboard.KEY_R );
        Input.setBinding( "removeVoxels", InputMedium.KEYBOARD, Keyboard.KEY_E );

        Input.setBinding( "unbindMouse", InputMedium.MOUSE, 0 );
        Input.setBinding( "bindMouse", InputMedium.MOUSE, 1 );
        Input.setBinding( "dbgc", InputMedium.MOUSE, 2 );
    }

    /**
     * Moves the camera and controller.
     *
     * @param camera
     *         The camera.
     * @param controller
     *         The controller.
     *
     * @since 14.03.30
     */
    public static void processInput( Camera camera, Controller controller )
    {
        if ( Input.isBindingActive( "unbindMouse" ) )
        {
            Mouse.setGrabbed( false );
        }
        if ( Input.isBindingActive( "bindMouse" ) )
        {
            Mouse.setGrabbed( true );
        }
        if ( Input.isBindingActive( "dbgc" ) )
        {
            Lumberjack.debug( "dbgc", camera.toString() );
        }

        float movementSpeed = 0.01f * TestingHelper.getDelta();
        if ( Input.isBindingActive( "sprint" ) )
        {
            movementSpeed *= 2;
        }

        if ( Input.isBindingActive( "forward" ) )
        {
            controller.moveForward( movementSpeed );
        }
        if ( Input.isBindingActive( "backward" ) )
        {
            controller.moveBackward( movementSpeed );
        }

        if ( Input.isBindingActive( "left" ) )
        {
            controller.moveLeft( movementSpeed );
        }
        if ( Input.isBindingActive( "right" ) )
        {
            controller.moveRight( movementSpeed );
        }

        camera.setYaw( camera.getYaw() + ( Mouse.getDX() * .05f ) );
        camera.setPitch( camera.getPitch() - ( Mouse.getDY() * .05f ) );
    }

    /**
     * Updates the display.
     *
     * @param name
     *         The title.
     * @param fpsCap
     *         The fps cap, or -1 if there is none.
     *
     * @since 14.03.30
     */
    public static void updateDisplay( String name, int fpsCap )
    {
        Display.setTitle( name + " [" + TestingHelper.getFPS() + "]" );
        Display.update();
        if ( fpsCap != -1 )
        {
            Display.sync( fpsCap );
        }
    }

    /**
     * @return The time since the last frame.
     *
     * @since 14.03.30
     */
    public static float getDelta()
    {
        float dt = getTime() - lastTime;
        lastTime = getTime();
        return dt;
    }

    /**
     * @return The last measured FPS.
     *
     * @since 14.03.30
     */
    public static int getFPS()
    {
        if ( ( getTime() - lastFPS ) > 1000 )
        {
            measuredFPS = fps;
            fps = 0;
            lastFPS += 1000;
        }
        fps++;

        return measuredFPS;
    }

    /**
     * Destroys the display and saves the input settings.
     *
     * @since 14.03.30
     */
    public static void destroy()
    {
        Display.destroy();
        Input.addBindings( CONFIG );

        if ( !CONFIG.write() )
        {
            System.err.println( "CONFIG.write() failed!" );
        }
        else
        {
            System.out.println( "CONFIG.write() success!" );
        }
    }
}
