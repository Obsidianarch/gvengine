package com.github.obsidianarch.gvengine;

import com.github.obsidianarch.gvengine.core.MathHelper;
import com.github.obsidianarch.gvengine.core.Scheduler;

/**
 * A 16x16x16 container of Chunks.
 * 
 * @author Austin
 */
public class Region {
    
    //
    // Fields
    //
    
    /** An array of all the chunks in this region. */
    private Chunk[]              chunks = new Chunk[ 4096 ];
    
    /** Responsible for generating every chunk in this region. */
    private final ChunkGenerator generator;
    
    //
    // Constructors
    //
    
    /**
     * Creates a new region.
     * 
     * @param generator
     *            The chunk generator used to generate this region's chunks.
     */
    public Region( ChunkGenerator generator ) {
        this.generator = generator;
        
        for ( int i = 0; i < chunks.length; i++ ) {
            Chunk c = new Chunk( MathHelper.getXPosition( i ), MathHelper.getYPosition( i ), MathHelper.getZPosition( i ) ); // create the chunk
            generator.generateChunk( c ); // generate the chunk's voxels
            chunks[ i ] = c; // add the chunk to the region
        }
    }
    
    //
    // Actions
    //
    
    /**
     * Schedules rebuilds for every chunk in this region.
     */
    public void rebuild() {
        for ( Chunk c : chunks ) {
            Scheduler.scheduleEvent( "buildMesh", c ); // schedules a chunk rebuild whenever we can 
        }
    }
    
    /**
     * Renders every chunk in this region.
     */
    public void render() {
        for ( Chunk c : chunks ) {
            c.render();
        }
    }
    
}
