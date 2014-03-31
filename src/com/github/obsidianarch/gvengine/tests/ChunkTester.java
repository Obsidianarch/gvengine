package com.github.obsidianarch.gvengine.tests;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.Display;

import com.github.obsidianarch.gvengine.Chunk;
import com.github.obsidianarch.gvengine.Material;
import com.github.obsidianarch.gvengine.core.Camera;
import com.github.obsidianarch.gvengine.core.Controller;
import com.github.obsidianarch.gvengine.core.Scheduler;
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
 * 
 * @since 14.03.30
 * @version 14.03.30
 */
public class ChunkTester {
    
    //
    // Options
    //
    
    /** The max FPS the chunk tester will go to. */
    @Option( "FPS Cap" )
    @SliderOption( minimum = 10, maximum = 120 )
    public static int     FPSCap       = -1;
    
    /** If the FPS is maxed out at the max refresh rate of the monitor. */
    @Option( "VSync" )
    @ToggleOption( { "false", "true" } )
    public static boolean VSyncEnabled = false;
    
    //
    // OptionListeners
    //
    
    /**
     * Listens for when the VSync variable has been changed.
     */
    @OptionListener( "VSync" )
    public static void onVSyncToggle() {
        Display.setVSyncEnabled( VSyncEnabled );
    }
    
    //
    // Methods
    //
    
    /**
     * Starts and runs the test.
     * 
     * @param args
     *            Command line arguments.
     * @throws Exception
     *             If something went wrong.
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    public static void main( String... args ) throws Exception {
        TestingHelper.CONFIG.read();
        OptionManager.initialize( TestingHelper.CONFIG );
        OptionManager.initialize( args );
        
        OptionManager.registerClass( "Tester", ChunkTester.class );
        OptionManager.registerClass( "Scheduler", Scheduler.class );
        System.out.println();
        
        TestingHelper.createDisplay();
        TestingHelper.setupGL();
        TestingHelper.initInput();
        
        Chunk c = new Chunk( null, 0, 0, 0 ); // the chunk we're testing
        buildChunk( c ); // build the chunk
        
        Camera camera = new Camera(); // the camera of hte player
        camera.setMinimumPitch( 15f );
        camera.setMaximumPitch( 165f );
        Controller controller = new Controller( camera ); // the controller of the camera
        
        while ( !Display.isCloseRequested() ) {
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT ); // clear the last frame
            
            Input.poll(); // poll the input
            processInput( camera, controller, c ); // move and orient the player
            Scheduler.doTick(); // ticks the scheduler
            renderScene( camera, c ); // render the scene
            
            TestingHelper.updateDisplay( "Chunk Tester", FPSCap );
        }
        
        TestingHelper.destroy(); // destroys everything
    }
    
    /**
     * Renders the scene to OpenGL.
     * 
     * @param camera
     *            The player's camera.
     * @param c
     *            The chunk.
     * 
     * @since 14.03.30
     * @version 14.03.30
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
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    private static void processInput( Camera camera, Controller controller, Chunk c ) {
        TestingHelper.processInput( camera, controller );
        
        if ( Input.isBindingActive( "rebuildChunk" ) ) buildChunk( c );
        if ( Input.isBindingActive( "removeVoxels" ) ) removeBlocks( c );
    }
    
    /**
     * Removes blocks from the chunk, stress tests rebuild time and voxel removal.
     * 
     * @param c
     *            The chunk that will have voxels removed from it.
     * 
     * @since 14.03.30
     * @version 14.03.30
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
     * Changes the chunk's contents and rebuilds the chunk's mesh.
     * 
     * @param c
     *            The chunk.
     * 
     * @since 14.03.30
     * @version 14.03.30
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
        
        Scheduler.enqueueEvent( "buildMesh", c );
    }
}
