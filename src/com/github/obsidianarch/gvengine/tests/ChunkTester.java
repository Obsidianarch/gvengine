package com.github.obsidianarch.gvengine.tests;

import com.github.obsidianarch.gvengine.core.*;
import com.github.obsidianarch.gvengine.core.input.Input;
import com.github.obsidianarch.gvengine.core.io.Lumberjack;
import com.github.obsidianarch.gvengine.core.options.BooleanOption;
import com.github.obsidianarch.gvengine.core.options.IntOption;
import com.github.obsidianarch.gvengine.tests.chunkGenerators.CGSphereModulus;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

/**
 * Tests the Voxel and Chunk Management systems.
 *
 * @author Austin
 * @version 15.01.07
 * @since 14.03.30
 */
public class ChunkTester
{

    //
    // Options
    //

    /**
     * If VSync is enabled or not
     */
    private static BooleanOption VSyncEnabled = new BooleanOption()
    {

        @Override
        public void onChange()
        {
            Lumberjack.getInstance().info( "ChunkTester", "VSync set to: %b", value );
            Display.setVSyncEnabled( value ); // update the property in OpenGL
        }

    };

    /**
     * The maximum number of frames per second to render at, -1 means no limit.
     */
    private static IntOption FPSCap = new IntOption( -1 )
    {

        @Override
        public void onChange()
        {
            if ( value <= 0 && value != -1 ) // all numbers zero and under (excluding -1) are set to -1
            {
                set( -1 );
            }
        }

    };

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
     * @since 14.03.30
     */
    public static void main( String... args ) throws Exception
    {
        TestingHelper.CONFIG.read();
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

            TestingHelper.updateDisplay( "Chunk Tester", FPSCap.get() );
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
     * @param chunkGenerator
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
                c.setMaterialAt( 0, i );
            }
        }
    }

}
