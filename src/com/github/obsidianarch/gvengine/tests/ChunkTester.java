package com.github.obsidianarch.gvengine.tests;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.github.obsidianarch.gvengine.Chunk;
import com.github.obsidianarch.gvengine.Material;
import com.github.obsidianarch.gvengine.core.Camera;
import com.github.obsidianarch.gvengine.core.Controller;
import com.github.obsidianarch.gvengine.core.input.Input;
import com.github.obsidianarch.gvengine.core.input.InputBindingMode;
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
    
    @OptionListener( { OptionManager.VSYNC_ENABLED } )
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
     *            Command line arguments (ignored completely).
     * @throws Exception
     *             If something went wrong.
     */
    public static void main( String... s ) throws Exception {
        OptionManager.registerClass( ChunkTester.class );
        System.out.println();
        
        TestingHelper.createDisplay();
        TestingHelper.setupGL();
        
        Chunk c = new Chunk( 0, 0, 0 ); // the chunk we're testing
        buildChunk( c ); // build the chunk
        
        Camera camera = new Camera(); // the camera of hte player
        Controller controller = new Controller( camera ); // the controller of the camera
        
        Input.initialize(); // initialize the input bindings
        {
            Input.setBinding( "forward", InputBindingMode.KEYBOARD, Keyboard.KEY_W );
            Input.setBinding( "left", InputBindingMode.KEYBOARD, Keyboard.KEY_A );
            Input.setBinding( "backward", InputBindingMode.KEYBOARD, Keyboard.KEY_S );
            Input.setBinding( "right", InputBindingMode.KEYBOARD, Keyboard.KEY_D );
            Input.setBinding( "sprint", InputBindingMode.KEYBOARD, Keyboard.KEY_LSHIFT );
        }
        
        while ( !Display.isCloseRequested() ) {
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT ); // clear the last frame
            
            processInput( camera, controller, c ); // move and orient the player
            renderScene( camera, c );
            updateDisplay();
        }
        
        Display.destroy();
    }
    
    /**
     * Removes blocks from the chunk, stress tests rebuild time and voxel removal.
     * 
     * @param c
     *            The chunk that will have voxels removed from it.
     */
    private static void removeBlocks( Chunk c ) {
        for ( int i = 0; i < 4096; i++ ) {
            // if a random number 0-9999 is less than 10 (0.1% chance)
            if ( Math.round( Math.random() * 10000 ) < 10 ) {
                c.setMaterialAt( Material.AIR, i );
            }
        }
    }
    
    /**
     * Updates (and syncs if needed) the Display.
     */
    private static void updateDisplay() {
        Display.setTitle( "Voxel Testing [" + TestingHelper.getFPS() + "]" );
        Display.update(); // update the screen
        if ( FPSCap != -1 ) Display.sync( FPSCap ); // sync to the FPS cap, if there is one
    }
    
    /**
     * Renders the scene to OpenGL.
     * 
     * @param camera
     *            The player's camera.
     * @param c
     *            The chunk.
     */
    private static void renderScene( Camera camera, Chunk c ) {
        glLoadIdentity(); // remove the previous transformation
        camera.lookThrough(); // transform the camera
        c.render(); // render the chunk 
    }
    
    /**
     * Moves the player and updates the camera.
     * 
     * @param camera
     *            The player's camera.
     * @param controller
     *            The player's controller.
     * @param c
     *            The chunk that is being tested.
     */
    private static void processInput( Camera camera, Controller controller, Chunk c ) {
        if ( Keyboard.isKeyDown( Keyboard.KEY_R ) ) { // rebuild the chunk
            buildChunk( c );
        }
        
        if ( Keyboard.isKeyDown( Keyboard.KEY_E ) ) {
            removeBlocks( c );
        }
        
        float movementSpeed = 0.01f * TestingHelper.getDelta();
        if ( Input.isBindingActive( "sprint" ) ) movementSpeed *= 2;
        
        if ( Input.isBindingActive( "forward" ) ) controller.moveForward( movementSpeed );
        if ( Input.isBindingActive( "backward" ) ) controller.moveBackward( movementSpeed );
        
        if ( Input.isBindingActive( "left" ) ) controller.moveLeft( movementSpeed );
        if ( Input.isBindingActive( "right" ) ) controller.moveRight( movementSpeed );
        
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
                    byte materialID = ( byte ) ( ( ( ( x % 3 ) + ( y % 3 ) + ( z % 3 ) ) % 3 ) + 1 );
                    c.setMaterialAt( materialID, x, y, z );
                }
            }
        }
        
        c.buildMesh();
    }
}
