package com.github.obsidianarch.gvengine;


/**
 * Generates the terrain inside of a chunk.
 * 
 * @author Austin
 */
public abstract class ChunkGenerator {
    
    //
    // Abstract
    //
    
    /**
     * Generates the voxels inside of the chunk.
     * 
     * @param c
     *            The chunk to generate voxels for.
     */
    public abstract void generateChunk( Chunk c );
    
}
