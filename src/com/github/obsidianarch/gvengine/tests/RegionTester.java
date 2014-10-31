package com.github.obsidianarch.gvengine.tests;

import com.github.obsidianarch.gvengine.core.*;
import com.github.obsidianarch.gvengine.core.input.Input;
import com.github.obsidianarch.gvengine.core.input.InputMask;
import com.github.obsidianarch.gvengine.core.input.InputMedium;
import com.github.obsidianarch.gvengine.core.input.InputMode;
import com.github.obsidianarch.gvengine.core.io.Lumberjack;
import com.github.obsidianarch.gvengine.core.io.RegionIO;
import com.github.obsidianarch.gvengine.core.options.BooleanOption;
import com.github.obsidianarch.gvengine.core.options.IntOption;
import com.github.obsidianarch.gvengine.tests.chunkGenerators.CGModulus;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * Tests the region and it's methods.
 *
 * @author Austin
 * @version 14.10.26b
 * @since 14.03.30
 */
public class RegionTester
{

    //
    // Options
    //

    /**
     * If VSync is enabled or not
     */
    private static BooleanOption VSync = new BooleanOption()
    {

        @Override
        public void onChange()
        {
            Lumberjack.info( "RegionTester", "VSync set to: %b", value );
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
            if ( value <= 0 ) // all numbers zero and under are set to -1
            {
                value = -1;
            }
        }

    };

    //
    // Methods
    //

    /**
     * Tests the regions
     *
     * @param args
     *         Command line arguments.
     *
     * @throws Exception
     *         If there was a problem in the test.
     * @since 14.03.30
     */
    public static void main( String[] args ) throws Exception
    {
        TestingHelper.CONFIG.read();
        System.out.println();

        TestingHelper.createDisplay();
        TestingHelper.setupGL();
        TestingHelper.initInput();

        Region region = new Region( new CGModulus(), 0, 0, 0 );
        region.rebuild();

        Camera camera = new Camera(); // the camera of the player
        camera.setMinimumPitch( 15f );
        camera.setMaximumPitch( 165f );
        Controller controller = new Controller( camera ); // the controller for the camera

        Input.setBinding( "removeBlocks", InputMedium.KEYBOARD, Keyboard.KEY_R );
        Input.setBinding( "addBlocks", InputMedium.KEYBOARD, Keyboard.KEY_F );
        Input.setBinding( "saveRegion", InputMedium.KEYBOARD, InputMode.BUTTON_RELEASED, Keyboard.KEY_O );
        Input.setBinding( "loadRegion", InputMedium.KEYBOARD, InputMode.BUTTON_RELEASED, Keyboard.KEY_L );
        Input.setBinding( "toggleVSync", InputMedium.KEYBOARD, InputMode.BUTTON_RELEASED, Keyboard.KEY_V );
        Input.setBinding( "addLighting", InputMedium.KEYBOARD, InputMode.BUTTON_RELEASED, InputMask.CONTROL_MASK, Keyboard.KEY_L );
        Input.setBinding( "remLighting", InputMedium.KEYBOARD, InputMode.BUTTON_RELEASED, InputMask.MENU_MASK, Keyboard.KEY_L );

        while ( !Display.isCloseRequested() )
        {
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT ); // clear the last frame

            Input.poll(); // poll the input
            TestingHelper.processInput( camera, controller ); // move and orient the player
            if ( Input.isBindingActive( "removeBlocks" ) )
            {
                removeBlocks( region );
            }
            if ( Input.isBindingActive( "addBlocks" ) )
            {
                addBlocks( region );
            }
            if ( Input.isBindingActive( "saveRegion" ) )
            {
                saveRegion( region );
            }
            if ( Input.isBindingActive( "loadRegion" ) )
            {
                loadRegion( region );
            }
            if ( Input.isBindingActive( "toggleVSync" ) )
            {
                VSync.set( !VSync.get() );
            }
            if ( Input.isBindingActive( "addLighting" ) )
            {
                TestingHelper.enableLighting();
            }
            if ( Input.isBindingActive( "remLighting" ) )
            {
                TestingHelper.disableLighting();
            }

            Scheduler.doTick(); // ticks the scheduler
            renderScene( camera, region ); // render the scene

            TestingHelper.updateDisplay( "Region Tester", FPSCap.get() );
        }

        TestingHelper.destroy(); // destroys everything
    }

    /**
     * Loads the saved region from the given write directory.
     *
     * @param region
     *         The region to load the data into.
     *
     * @since 14.03.30
     */
    private static void loadRegion( Region region )
    {
        RegionIO.loadRegion( region, new File( "/data/" ) );
    }

    /**
     * Saves teh region into the given write directory.
     *
     * @param region
     *         The region to write into a data file.
     *
     * @since 14.03.30
     */
    private static void saveRegion( Region region )
    {
        RegionIO.saveRegion( region, new File( "data/" ) );
    }

    /**
     * Removes blocks from the environment randomly.
     *
     * @param r
     *         The region to remove blocks from.
     *
     * @since 14.03.30
     */
    private static void removeBlocks( Region r )
    {
        Random random = new Random();

        for ( Chunk c : r.getChunks() )
        {
            for ( int i = 0; i < 4096; i++ )
            {
                if ( ( random.nextFloat() * 1000 ) > 10 )
                {
                    continue; // 1% chance for the voxel to be removed
                }

                c.setMaterialAt( ( byte ) 0, i );
            }
        }
    }

    /**
     * Adds blocks to the environment randomly.
     *
     * @param r
     *         The region to add blocks to.
     *
     * @since 14.03.30
     */
    private static void addBlocks( Region r )
    {
        Random random = new Random();

        for ( Chunk c : r.getChunks() )
        {
            for ( int i = 0; i < Chunk.VOLUME; i++ )
            {
                if ( c.getVoxels()[ i ] != 0 )
                {
                    continue;
                }
                if ( ( random.nextFloat() * 1000 ) > 10 )
                {
                    continue;
                }

                c.setMaterialAt( ( byte ) random.nextInt( 4 ), i );
            }
        }
    }

    /**
     * Renders the region.
     *
     * @param camera
     *         The camera of the player.
     * @param region
     *         The region to render.
     *
     * @since 14.03.30
     */
    public static void renderScene( Camera camera, Region region )
    {
        glLoadIdentity();
        camera.lookThrough();
        region.render();
    }

}
