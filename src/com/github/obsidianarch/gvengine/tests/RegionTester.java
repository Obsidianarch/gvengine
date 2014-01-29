package com.github.obsidianarch.gvengine.tests;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.github.obsidianarch.gvengine.Chunk;
import com.github.obsidianarch.gvengine.ChunkGenerator;
import com.github.obsidianarch.gvengine.Region;
import com.github.obsidianarch.gvengine.core.Camera;
import com.github.obsidianarch.gvengine.core.Controller;
import com.github.obsidianarch.gvengine.core.MathHelper;
import com.github.obsidianarch.gvengine.core.Scheduler;
import com.github.obsidianarch.gvengine.core.input.Input;

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
        
        TestingHelper.createDisplay();
        TestingHelper.setupGL();
        
        Mouse.setGrabbed( false );
        
        Camera camera = new Camera();
        camera.setMinimumPitch( 15f );
        camera.setMaximumPitch( 165f );
        Controller controller = new Controller( camera );
        
        Region region = new Region( new RegionTester() );
        region.rebuild();
        
        TestingHelper.initInput();
        
        while ( !Display.isCloseRequested() ) {
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT ); // clear the last frame
            
            Input.poll(); // poll the input
            TestingHelper.processInput( camera, controller );
            Scheduler.doTick();
            renderScene( region );
            TestingHelper.updateDisplay( "Region Tester", 60 );
        }
        
        TestingHelper.destroy();
    }
    
    /**
     * Renders the region, and schedules rebuilds every 10 seconds.
     * 
     * @param region
     *            The region to render.
     */
    public static void renderScene( Region region ) {
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
                    // get the global positions
                    float gX = MathHelper.getGlobalPosition( c.x, x );
                    float gY = MathHelper.getGlobalPosition( c.y, y );
                    float gZ = MathHelper.getGlobalPosition( c.z, z );
                    float random = random( seed, gX, gY, gZ ); // generate a random number based on this data
                    
                    random *= 4; // get it to contain every material
                    random = ( float ) Math.round( random ); // round it
                    
                    c.setMaterialAt( ( byte ) random, x, y, z ); // set the material
                }
            }
        }
    }
}
