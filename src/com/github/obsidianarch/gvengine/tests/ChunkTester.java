package com.github.obsidianarch.gvengine.tests;

import com.github.obsidianarch.gvengine.core.*;
import com.github.obsidianarch.gvengine.core.input.Input;
import com.github.obsidianarch.gvengine.core.options.*;
import com.github.obsidianarch.gvengine.tests.chunkGenerators.CGModulus;
import com.github.obsidianarch.gvengine.tests.chunkGenerators.CGSphere;
import com.github.obsidianarch.gvengine.tests.chunkGenerators.CGSphereModulus;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

/**
 * Tests the Voxel and Chunk Management systems.
 *
 * @author Austin
 * @version 14.10.26
 * @since 14.03.30
 */
public class ChunkTester
{

    //
    // Options
    //

    /**
     * The max FPS the chunk tester will go to.
     */
    @Option("FPS Cap")
    @SliderOption(minimum = 10, maximum = 120)
    public static int FPSCap = -1;

    /**
     * If the FPS is maxed out at the max refresh rate of the monitor.
     */
    @Option("VSync")
    @ToggleOption({ "false", "true" })
    public static boolean VSyncEnabled = false;

    //
    // OptionListeners
    //

    /**
     * Listens for when the VSync variable has been changed.
     */
    @OptionListener("VSync")
    public static void onVSyncToggle()
    {
        Display.setVSyncEnabled( VSyncEnabled );
    }

    //
    // Methods
    //

    /**
     * Starts and runs the test.
     *
     * @param args
     *         Command line arguments.
     *
     * @throws Exception
     *         If something went wrong.
     *
     * @since 14.03.30
     */
    public static void main( String... args ) throws Exception
    {
        TestingHelper.CONFIG.read();
        OptionManager.initialize( TestingHelper.CONFIG );
        OptionManager.initialize( args );

        OptionManager.registerClass( "Test", ChunkTester.class );
        OptionManager.registerClass( "Scheduler", Scheduler.class );
        System.out.println();

        TestingHelper.createDisplay();
        TestingHelper.setupGL();
        TestingHelper.initInput();

        ChunkGenerator chunkGenerator = new CGSphereModulus();

        Chunk c = new Chunk( null, 0, 0, 0 ); // the chunk we're testing
        chunkGenerator.generateChunk( c ); // build the chunk
        Scheduler.enqueueEvent( "buildMesh", c );

        Camera camera = new Camera(); // the camera of hte player
        camera.setMinimumPitch( 15f );
        camera.setMaximumPitch( 165f );
        Controller controller = new Controller( camera ); // the controller of the camera

        while ( !Display.isCloseRequested() )
        {
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT ); // clear the last frame

            Input.poll(); // poll the input
            processInput( camera, controller, c, chunkGenerator ); // move and orient the player
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
     *         The player's camera.
     * @param c
     *         The chunk.
     *
     * @since 14.03.30
     */
    private static void renderScene( Camera camera, Chunk c )
    {
        glLoadIdentity(); // remove the previous transformation
        camera.lookThrough(); // transform the camera
        c.render(); // render the chunk 
    }

    /**
     * Moves the player and updates the camera.
     *
     * @param camera
     *         The player's camera.
     * @param controller
     *         The player's controller.
     * @param c
     *         The chunk that is being tested.
     *@param chunkGenerator
     *         The chunk generator used to regenerate the chunk.
     *
     * @since 14.03.30
     */
    private static void processInput( Camera camera, Controller controller, Chunk c, ChunkGenerator chunkGenerator )
    {
        TestingHelper.processInput( camera, controller );

        if ( Input.isBindingActive( "rebuildChunk" ) )
        {
            chunkGenerator.generateChunk( c );
            Scheduler.enqueueEvent( "buildMesh", c );
        }
        if ( Input.isBindingActive( "removeVoxels" ) )
        {
            removeBlocks( c );
        }
    }

    /**
     * Removes blocks from the chunk, stress tests rebuild time and voxel removal.
     *
     * @param c
     *         The chunk that will have voxels removed from it.
     *
     * @since 14.03.30
     */
    private static void removeBlocks( Chunk c )
    {
        for ( int i = 0; i < 4096; i++ )
        {
            // if a random number 0-9999 is less than 10 (0.1% chance)
            if ( Math.round( Math.random() * 10000 ) < 10 )
            {
                c.setMaterialAt( Material.AIR, i );
            }
        }
    }

}
