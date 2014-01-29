package com.github.obsidianarch.gvengine.tests;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.Display;

import com.github.obsidianarch.gvengine.Chunk;
import com.github.obsidianarch.gvengine.ChunkGenerator;
import com.github.obsidianarch.gvengine.Material;
import com.github.obsidianarch.gvengine.Region;
import com.github.obsidianarch.gvengine.core.Camera;
import com.github.obsidianarch.gvengine.core.Controller;
import com.github.obsidianarch.gvengine.core.Scheduler;
import com.github.obsidianarch.gvengine.core.input.Input;
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
    
    /** The time the region was last rebuilt. */
    private static long lastRebuild = TestingHelper.getTime();
    
    /** The seed for the random number generator. */
    private static long seed        = 1070136;
    
    //
    // Methods
    //
    
    /**
     * Tests the regions
     * 
     * @param args
     *            Command line arguments.
     */
    public static void main( String[] args ) throws Exception {
        if ( !TestingHelper.isDeveloping() ) {
            System.setProperty( "org.lwjgl.librarypath", System.getProperty( "user.dir" ) + "/res/" );
        }
        OptionManager.initialize( args );
        
        OptionManager.registerClass( "Scheduler", Scheduler.class );
        System.out.println();
        
        TestingHelper.createDisplay();
        TestingHelper.setupGL();
        TestingHelper.initInput();
        
        Chunk c = new Chunk( 0, 2, 0 );
        new RegionTester().generateChunk( c );
        c.buildMesh();
        
        Chunk c2 = new Chunk( 2, 0, 0 );
        new RegionTester().generateChunk( c2 );
        c2.buildMesh();
        
        //        Region region = new Region( new RegionTester(), 0, 0, 0 );
        //        region.rebuild();
        
        Camera camera = new Camera(); // the camera of the player
        camera.setMinimumPitch( 15f );
        camera.setMaximumPitch( 165f );
        Controller controller = new Controller( camera ); // the controller for the camera
        
        while ( !Display.isCloseRequested() ) {
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT ); // clear the last frame
            
            Input.poll(); // poll the input
            TestingHelper.processInput( camera, controller ); // move and orient the player
            Scheduler.doTick(); // ticks the scheduler
            renderScene( camera, null ); // render the scene
            
            c.render();
            c2.render();
            
            TestingHelper.updateDisplay( "Chunk Tester", 60 );
        }
        
        TestingHelper.destroy(); // destroys everything
    }
    
    /**
     * Renders the region, and schedules rebuilds every 10 seconds.
     * 
     * @param region
     *            The region to render.
     */
    public static void renderScene( Camera camera, Region region ) {
        glLoadIdentity();
        camera.lookThrough();
        //        region.render();
    }
    
    //
    // Override
    //
    
    @Override
    public void generateChunk( Chunk c ) {
        for ( int x = 0; x < 16; x++ ) {
            for ( int y = 0; y < 16; y++ ) {
                for ( int z = 0; z < 16; z++ ) {
                    c.setMaterialAt( Material.DIRT, x, y, z ); // set the material
                }
            }
        }
    }
}
