package com.github.obsidianarch.gvengine;


/**
 * Generates the terrain inside of a chunk.
 * 
 * @author Austin
 * 
 * @since 14.03.30
 * @version 14.03.30
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
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    public abstract void generateChunk( Chunk c );
    
}
