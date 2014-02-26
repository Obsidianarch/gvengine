package com.github.obsidianarch.gvengine.tests;

import static org.lwjgl.opengl.GL11.*;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.github.obsidianarch.gvengine.Chunk;
import com.github.obsidianarch.gvengine.ChunkGenerator;
import com.github.obsidianarch.gvengine.Material;
import com.github.obsidianarch.gvengine.Region;
import com.github.obsidianarch.gvengine.core.Camera;
import com.github.obsidianarch.gvengine.core.Controller;
import com.github.obsidianarch.gvengine.core.MathHelper;
import com.github.obsidianarch.gvengine.core.Scheduler;
import com.github.obsidianarch.gvengine.core.input.Input;
import com.github.obsidianarch.gvengine.core.noise.Noises;
import com.github.obsidianarch.gvengine.core.options.OptionManager;

/**
 * Tests the region and it's methods.
 * 
 * @author Austin
 */
public class RegionTester extends ChunkGenerator {
    
    //
    // Fields
    //
    
    /** The seed for the random number generator. */
    private static long seed = 1070136;
    
    //
    // Methods
    //
    
    /**
     * Tests the regions
     * 
     * @param args
     *            Command line arguments.
     * @throws Exception
     *             If there was a problem in the test.
     */
    public static void main( String[] args ) throws Exception {
        TestingHelper.CONFIG.read();
        OptionManager.initialize( TestingHelper.CONFIG ); // initialize options from config file
        OptionManager.initialize( args ); // initialize options from commandline (override the config file)
        
        OptionManager.registerClass( "Scheduler", Scheduler.class );
        System.out.println();
        
        TestingHelper.createDisplay();
        TestingHelper.setupGL();
        TestingHelper.initInput();
        
        Region region = new Region( new RegionTester(), 0, 0, 0 );
        region.rebuild();
        
        Camera camera = new Camera(); // the camera of the player
        camera.setMinimumPitch( 15f );
        camera.setMaximumPitch( 165f );
        Controller controller = new Controller( camera ); // the controller for the camera
        
        while ( !Display.isCloseRequested() ) {
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT ); // clear the last frame
            
            Input.poll(); // poll the input
            TestingHelper.processInput( camera, controller ); // move and orient the player
            if ( Keyboard.isKeyDown( Keyboard.KEY_R ) ) removeBlocks( region );
            if ( Keyboard.isKeyDown( Keyboard.KEY_F ) ) addBlocks( region );
            
            Scheduler.doTick(); // ticks the scheduler
            renderScene( camera, region ); // render the scene
            
            TestingHelper.updateDisplay( "Region Tester", 60 );
        }
        
        TestingHelper.destroy(); // destroys everything
    }
    
    /**
     * Removes blocks from the environment randomly.
     * 
     * @param r
     *            The region to remove blocks from.
     */
    private static void removeBlocks( Region r ) {
        Random random = new Random();
        
        for ( Chunk c : r.getChunks() ) {
            for ( int i = 0; i < 4096; i++ ) {
                if ( ( random.nextFloat() * 1000 ) > 10 ) continue; // 1% chance for the voxel to be removed
                    
                c.setMaterialAt( ( byte ) 0, i );
            }
        }
    }
    
    /**
     * Adds blocks to the environment randomly/
     * 
     * @param r
     *            The region to add blocks to.
     */
    private static void addBlocks( Region r ) {
        Random random = new Random();
        
        for ( Chunk c : r.getChunks() ) {
            for ( int i = 0; i < 4096; i++ ) {
                if ( c.getVoxels()[ i ] != 0 ) continue;
                if ( ( random.nextFloat() * 1000 ) > 10 ) continue;
                
                c.setMaterialAt( ( byte ) random.nextInt( 4 ), i );
            }
        }
    }
    
    /**
     * Renders the region, and schedules rebuilds every 10 seconds.
     * 
     * @param camera
     *            The camera of the player.
     * @param region
     *            The region to render.
     */
    public static void renderScene( Camera camera, Region region ) {
        glLoadIdentity();
        camera.lookThrough();
        region.render();
    }
    
    //
    // Override
    //
    
    @Override
    public void generateChunk( Chunk c ) {
        for ( int x = 0; x < 16; x++ ) {
            for ( int y = 0; y < 16; y++ ) {
                for ( int z = 0; z < 16; z++ ) {
                    float[] global = MathHelper.getGlobalPosition( c, new int[ ] { x, y, z } );
                    
                    double noise = Noises.simplex4D( global[ 0 ], global[ 1 ], global[ 2 ], seed );
                    noise += 1; // [0,2]
                    noise *= 1.5; // [0,3]
                    
                    byte material = ( byte ) Math.round( noise );
                    noise = Noises.simplex3D( global[ 0 ], global[ 2 ], seed );
                    
                    if ( ( global[ 1 ] < 20 ) && ( noise > 0 ) ) {
                        material = Material.STONE.byteID;
                    }
                    else if ( global[ 1 ] < 45 ) {
                        
                        if ( noise > 0.5 ) {
                            material = Material.DIRT.byteID;
                        }
                        else if ( noise < -0.5 ) {
                            material = Material.STONE.byteID;
                        }
                        
                    }
                    
                    if ( ( global[ 1 ] > 45 ) ) {
                        if ( ( noise + 1 ) > 0.25 ) {
                            material = Material.AIR.byteID;
                        }
                        else {
                            material = Material.GRASS.byteID;
                        }
                    }
                    
                    Material below = c.getMaterialAt( x, y - 1, z ); // the material below us
                    if ( ( below != null ) && ( below != Material.AIR ) ) {
                        
                        if ( Material.getMaterial( material ).active && ( below == Material.GRASS ) ) {
                            c.setMaterialAt( Material.DIRT, x, y - 1, z );
                        }
                        else if ( !Material.getMaterial( material ).active && ( below == Material.DIRT ) ) {
                            c.setMaterialAt( Material.GRASS, x, y - 1, z );
                        }
                        
                    }
                    
                    c.setMaterialAt( material, x, y, z );
                }
            }
        }
    }
}
