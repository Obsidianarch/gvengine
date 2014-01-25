package com.github.obsidianarch.gvengine.tests;

import java.util.Random;

import org.lwjgl.opengl.Display;

import com.github.obsidianarch.gvengine.Chunk;
import com.github.obsidianarch.gvengine.ChunkGenerator;
import com.github.obsidianarch.gvengine.ChunkProvider;
import com.github.obsidianarch.gvengine.World;
import com.github.obsidianarch.gvengine.core.MathHelper;

/**
 * Tests the world.
 * 
 * @author Austin
 */
public class WorldTester implements ChunkProvider, ChunkGenerator {
    
    public static void main( String[] args ) throws Exception {
        TestingHelper.createDisplay();
        TestingHelper.setupGL();
        
        World world = new World( new WorldTester() );
        
        while ( !Display.isCloseRequested() ) {
            float delta = TestingHelper.getDelta();
            
            Display.setTitle( "Voxel Testing [" + TestingHelper.getFPS() + "]" );
            
            world.update( delta );
            world.render();
            
            Display.update();
        }
    }
    
    //
    // Overrides @com.github.obsidianarch.gvengine.ChunkProvider
    //
    
    @Override
    public Chunk provideChunk( int x, int y, int z ) {
        return null;
    }
    
    @Override
    public Chunk getChunk( int x, int y, int z ) {
        return null;
    }
    
    @Override
    public Chunk createChunk( int x, int y, int z ) {
        Chunk c = new Chunk( x, y, z );
        
        for ( int vX = 0; vX < 16; vX++ ) {
            for ( int vY = 0; vY < 16; vY++ ) {
                for ( int vZ = 0; vZ < 16; vZ++ ) {
                    createChunk( c, vX, vY, vZ, getSeed() );
                }
            }
        }
        
        return c;
    }
    
    //
    // Overriden Setters @com.github.obsidianarch.gvengine.ChunkProvider
    //
    
    @Override
    public void setSeet( long seed ) {
        // do nothing
    }
    
    @Override
    public void setChunkGenerator( ChunkGenerator generator ) {
        // do nothing
    }
    
    //
    // Overriden Getters @com.github.obsidianarch.gvengine.ChunkProvider
    //
    
    @Override
    public long getSeed() {
        return 1; // TEMP
    }
    
    @Override
    public void getChunkGenerator( ChunkGenerator generator ) {
        // do nothing
    }
    
    //
    // Override @com.github.obsidianarch.gvengine.ChunkGenerator
    //
    
    @Override
    public void createChunk( Chunk c, int x, int y, int z, long seed ) {
        if ( MathHelper.getGlobalPosition( c.y, y ) > 23 ) return; // it will be air
            
        Random random = new Random( ( long ) ( seed + x + Math.sin( y ) + z ) );
        c.setMaterialAt( ( byte ) random.nextInt( 4 ), x, y, z ); // set it to a random material
    }
}
