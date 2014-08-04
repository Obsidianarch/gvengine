package com.github.obsidianarch.gvengine.tests;

import com.github.obsidianarch.gvengine.Chunk;
import com.github.obsidianarch.gvengine.ChunkGenerator;
import com.github.obsidianarch.gvengine.Material;
import com.github.obsidianarch.gvengine.Region;
import com.github.obsidianarch.gvengine.core.Camera;
import com.github.obsidianarch.gvengine.core.Controller;
import com.github.obsidianarch.gvengine.core.Scheduler;
import com.github.obsidianarch.gvengine.core.input.Input;
import com.github.obsidianarch.gvengine.core.input.InputMask;
import com.github.obsidianarch.gvengine.core.input.InputMedium;
import com.github.obsidianarch.gvengine.core.input.InputMode;
import com.github.obsidianarch.gvengine.core.options.*;
import com.github.obsidianarch.gvengine.core.io.Lumberjack;
import com.github.obsidianarch.gvengine.core.io.RegionIO;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * Tests the region and it's methods.
 *
 * @author Austin
 * @version 14.08.03b
 * @since 14.03.30
 */
public class RegionTester extends ChunkGenerator
{

    //
    // Options
    //

    /**
     * Enables or disables the vsync option
     */
    @Option( value = "VSync", autoValueChange = false )
    @ToggleOption( { "Enabled", "Disabled" } )
    public static boolean VSyncEnabled = false;

    /**
     * Changes the max fps OpenGL will render at.
     */
    @Option( "Max FPS" )
    @SliderOption( minimum = -1, maximum = 120 )
    public static int FPSCap = -1;

    //
    // Option Listeners
    //

    /**
     * Listens for the VSync option to be changed.
     *
     * @param newValue
     *         The new value of the option.
     */
    @OptionListener( { "VSync" } )
    public static void onVSyncChange( Object newValue )
    {
        VSyncEnabled = newValue.toString().equalsIgnoreCase( "Enabled" );
        Display.setVSyncEnabled( VSyncEnabled );
        Lumberjack.info( "Tester", "VSync set to: %s ", newValue );
    }

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
     *
     * @since 14.03.30
     */
    public static void main( String[] args ) throws Exception
    {
        TestingHelper.CONFIG.read();
        OptionManager.initialize( TestingHelper.CONFIG ); // initialize options from config file
        OptionManager.initialize( args ); // initialize options from commandline (override the config file)

        OptionManager.registerClass( "Scheduler", Scheduler.class );
        OptionManager.registerClass( "Test", RegionTester.class );
        System.out.println();

        TestingHelper.createDisplay();
        TestingHelper.setupGL();
        //        TestingHelper.enableLighting();
        TestingHelper.initInput();

        Region region = new Region( new RegionTester(), 0, 0, 0 );
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
                if ( VSyncEnabled )
                {
                    OptionManager.setValue( "VSync", "Disabled" );
                }
                else
                {
                    OptionManager.setValue( "VSync", "Enabled" );
                }
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

            TestingHelper.updateDisplay( "Region Tester", -1 );
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

    //
    // Override
    //

    @Override
    public void generateChunk( Chunk c )
    {
        for ( int z = 0; z < Chunk.LENGTH; z++ )
        {
            for ( int y = 0; y < Chunk.LENGTH; y++ )
            {
                for ( int x = 0; x < Chunk.LENGTH; x++ )
                {

                    double xDiff = x - ( Chunk.LENGTH / 2 );
                    double yDiff = y - ( Chunk.LENGTH / 2 );
                    double zDiff = z - ( Chunk.LENGTH / 2 );
                    double sqrt = Math.sqrt( ( xDiff * xDiff ) + ( yDiff * yDiff ) + ( zDiff * zDiff ) );

                    if ( sqrt <= ( Chunk.LENGTH / 2 ) )
                    {
                        c.setMaterialAt( Material.GRASS, x, y, z );
                    }

                }
            }
        }
    }
}
