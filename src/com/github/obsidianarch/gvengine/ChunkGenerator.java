package com.github.obsidianarch.gvengine;

/**
 * Generates chunks from a seed and chunk coordinate triples.
 * 
 * @author Austin
 */
public interface ChunkGenerator {
    
    /**
     * Generates the terrain for a chunk.
     * 
     * @param c
     *            The chunk.
     * @param x
     *            The voxel's x coordinate.
     * @param y
     *            The voxel's y coordinate.
     * @param z
     *            The voxel's z coordinate.
     * @param seed
     *            The seed to generate the chunk from.
     */
    public void createChunk( Chunk c, int x, int y, int z, long seed );
    
}
