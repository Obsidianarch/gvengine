package com.github.obsidianarch.gvengine.tests;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.github.obsidianarch.gvengine.Chunk;
import com.github.obsidianarch.gvengine.ChunkManager;
import com.github.obsidianarch.gvengine.core.Camera;
import com.github.obsidianarch.gvengine.core.Controller;
import com.github.obsidianarch.gvengine.core.input.Input;
import com.github.obsidianarch.gvengine.core.options.Option;
import com.github.obsidianarch.gvengine.core.options.OptionListener;
import com.github.obsidianarch.gvengine.core.options.OptionManager;
import com.github.obsidianarch.gvengine.core.options.SliderOption;
import com.github.obsidianarch.gvengine.core.options.ToggleOption;

/**
 * Tests the Voxel and Chunk Management systems.
 * 
 * @author Austin
 */
public class ChunkTester {
    
    //
    // Options
    //
    
    @Option( description = OptionManager.FPS_CAP, screenName = "", x = -1, y = -1 )
    @SliderOption( minimum = -1, maximum = 120 )
    public static int     FPSCap       = -1;
    
    @Option( description = OptionManager.VSYNC_ENABLED, screenName = "", x = -1, y = -1 )
    @ToggleOption( options = { "false", "true" }, descriptions = { "Enabled", "Disabled" } )
    public static boolean VSyncEnabled = false;
    
    //
    // OptionListeners
    //
    
    @OptionListener( fields = { OptionManager.VSYNC_ENABLED } )
    public static void onVSyncToggle() {
        Display.setVSyncEnabled( VSyncEnabled );
    }
    
    //
    // Methods
    //
    
    /**
     * Starts and runs the test.
     * 
     * @param s
     *            Command line arguments.
     * @throws Exception
     *             If something went wrong.
     */
    public static void main( String... s ) throws Exception {
        OptionManager.registerClass( ChunkTester.class );
        OptionManager.registerClass( ChunkManager.class );
        System.out.println();
        
        create(); // create the display
        
        Chunk c = new Chunk( 0, 0, 0 ); // the chunk we're testing
        buildChunk( c ); // build the chunk
        
        Camera camera = new Camera(); // the camera of hte player
        Controller controller = new Controller( camera ); // the controller of the camera
        
        Input.initialize(); // initialize the input bindings
        
        long lastTime = Sys.getTime(); // the last time the frame was drawn
        long lastFPS = getTime(); // the last time the fps was updated
        int fps = 0;
        
        while ( !Display.isCloseRequested() ) {
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT ); // clear the last frame
            
            if ( Keyboard.isKeyDown( Keyboard.KEY_R ) ) { // rebuild the chunk
                buildChunk( c );
            }
            
            float delta = ( Sys.getTime() - lastTime ); // the difference between this frame and the previous
            movePlayer( delta, camera, controller ); // move and orient the player
            
            glLoadIdentity(); // remove the previous transformation
            camera.transform(); // transform the camera
            c.render(); // render the chunk 
            
            // update the fps
            if ( ( getTime() - lastFPS ) > 1000 ) {
                Display.setTitle( "Voxel Testing [" + fps + "]" );
                fps = 0;
                lastFPS += 1000;
            }
            fps++;
            
            lastTime = Sys.getTime(); // change the last time
            Display.update(); // update the screen
            
            if ( FPSCap != -1 ) Display.sync( FPSCap ); // sync to the FPS cap, if there is one
        }
        
        Display.destroy();
    }
    
    /**
     * @return The current time in milliseconds.
     */
    private static long getTime() {
        return ( Sys.getTime() * 1000 ) / Sys.getTimerResolution();
    }
    
    /**
     * Moves the player and updates the camera.
     * 
     * @param delta
     *            The time since the last frame.
     * @param camera
     *            The player's camera.
     * @param controller
     *            The player's controller.
     */
    private static void movePlayer( float delta, Camera camera, Controller controller ) {
        float movementSpeed = 0.01f * delta;
        if ( Input.isBindingActive( Input.MOVE_SPRINT ) ) movementSpeed *= 2;
        
        if ( Input.isBindingActive( Input.MOVE_FORWARD ) ) controller.moveForward( movementSpeed );
        if ( Input.isBindingActive( Input.MOVE_BACKWARD ) ) controller.moveBackward( movementSpeed );
        
        if ( Input.isBindingActive( Input.MOVE_LEFT ) ) controller.moveLeft( movementSpeed );
        if ( Input.isBindingActive( Input.MOVE_RIGHT ) ) controller.moveRight( movementSpeed );
        
        camera.setYaw( camera.getYaw() + ( Mouse.getDX() * .05f ) );
        camera.setPitch( camera.getPitch() - ( Mouse.getDY() * .05f ) );
    }
    
    /**
     * Changes the chunk's contents and rebuilds the chunk's mesh.
     * 
     * @param c
     *            The chunk.
     */
    private static void buildChunk( Chunk c ) {
        // set every material in the chunk
        for ( int x = 0; x < 16; x++ ) {
            for ( int y = 0; y < 16; y++ ) {
                for ( int z = 0; z < 16; z++ ) {
                    c.setMaterialAt( ( byte ) ( ( ( ( x % 3 ) + ( y % 3 ) + ( z % 3 ) ) % 3 ) + 1 ), x, y, z );
                }
            }
        }
        
        // count how long it takes to build the mesh
        long start = System.nanoTime();
        c.buildMesh();
        long end = System.nanoTime();
        
        // print rebuild stats, useful to make sure everything is working as fast as possible
        System.out.println( "Chunk rebuild stats:" );
        System.out.println( " Nanoseconds:  " + ( end - start ) );
        System.out.println( " Milliseconds: " + ( ( end - start ) / 1000000.0 ) );
        System.out.println( " Seconds:      " + ( ( end - start ) / 1000000000.0 ) );
        System.out.println( " Vertices:     " + c.getVertexCount() );
        System.out.println();
    }
    
    /**
     * Creates the display and initializes OpenGL.
     * 
     * @throws Exception
     *             If there was a problem.
     */
    private static void create() throws Exception {
        Display.setTitle( "Voxel Testing" );
        Display.setDisplayMode( new DisplayMode( 640, 480 ) ); // the window will be 640 x 480
        Display.create(); // create the display
        
        glShadeModel( GL_SMOOTH ); // supposedly smooths things out
        glClearColor( 0, 0, 0, 0 ); // sets the background color when we clear (black with no alpha channel)
        glClearDepth( 1 ); // depth value to use when the depth buffer is cleared
        
        glEnable( GL_DEPTH_TEST ); // enable depth, testing if a vertex is behind others
        glDepthFunc( GL_LEQUAL ); // I got no idea what this does, but anything other than LESS or LEQUAL breaks the rendering
        
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
        
        Mouse.setGrabbed( true ); // grab the mouse
    }
    
}
