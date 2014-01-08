package com.github.obsidianarch.gvengine.tests;

import org.lwjgl.opengl.Display;

import com.github.obsidianarch.gvengine.ChunkManager;
import com.github.obsidianarch.gvengine.World;

/**
 * Tests the world.
 * 
 * @author Austin
 */
public class WorldTester {
    
    public static void main( String[] args ) throws Exception {
        TestingHelper.createDisplay();
        TestingHelper.setupGL();
        
        World world = new World( new ChunkManager() );
        
        while ( !Display.isCloseRequested() ) {
            float delta = TestingHelper.getDelta();
            
            Display.setTitle( "Voxel Testing [" + TestingHelper.getFPS() + "]" );
            
            world.update( delta );
            world.render();
            
            Display.update();
        }
    }
    
}
