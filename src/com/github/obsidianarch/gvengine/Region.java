package com.github.obsidianarch.gvengine;

import com.github.obsidianarch.gvengine.core.Scheduler;

/**
 * A 4x4x4 container of Chunks.
 * 
 * @author Austin
 */
public class Region {
    
    //
    // Fields
    //
    
    /** An array of all the chunks in this region. */
    public Chunk[]               chunks = new Chunk[ 64 ];
    
    /** The region's x coordinate. */
    public final int             x;
    
    /** The region's y coordinate. */
    public final int             y;
    
    /** The region's z coordinate. */
    public final int             z;
    
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
    public Region( ChunkGenerator generator, int x, int y, int z ) {
        this.generator = generator;
        
        this.x = x;
        this.y = y;
        this.z = z;
        
        // generate the chunks in this region
        for ( int i = 0; i < chunks.length; i++ ) {
            Chunk c = new Chunk( ( 4 * x ) + i, ( 4 * y ) + i, ( 4 * z ) + i ); // create the chunk
            generator.generateChunk( c ); // generate the chunk's voxels
            chunks[ i ] = c; // add the chunk to the region
        }
    }
    
    //
    // Actions
    //
    
    /**
     * Regenerates all chunks based on the current seed.
     */
    public void regenerate() {
        for ( Chunk c : chunks ) {
            generator.generateChunk( c );
        }
    }
    
    /**
     * Schedules rebuilds for every chunk in this region.
     */
    public void rebuild() {
        for ( int i = 0; i < chunks.length; i++ ) {
            Scheduler.scheduleEvent( "buildMesh", chunks[ i ], i * 100 ); // a chunk in this region is rebuilt every 100 milliseconds 
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
