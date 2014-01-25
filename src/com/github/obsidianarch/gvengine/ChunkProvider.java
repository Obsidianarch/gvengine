package com.github.obsidianarch.gvengine;

/**
 * Provides Chunks to a World.
 * 
 * @author Austin
 */
public interface ChunkProvider {
    
    //
    // Chunk
    //
    
    /**
     * Provides a chunk for the given chunk coordinate triple, if the chunk does not exist
     * yet, it is created then returned.
     * 
     * @param x
     * @param y
     * @param z
     * @return The chunk at the given coordinate triple.
     */
    public Chunk provideChunk( int x, int y, int z );
    
    /**
     * Gets the chunk at the given coordinate triple, will return null if one has not yet
     * been created.
     * 
     * @param x
     * @param y
     * @param z
     * @return The chunk at the coordinate triple, {@code null} if it doesn't exist.
     */
    public Chunk getChunk( int x, int y, int z );
    
    /**
     * Creates a chunk at the given coordiante triple.
     * 
     * @param x
     * @param y
     * @param z
     * @return A new chunk for the coordinate triple.
     */
    public Chunk createChunk( int x, int y, int z );
    
    //
    // Setters
    //
    
    /**
     * @param seed
     *            The new seed for generating chunks.
     */
    public void setSeet( long seed );
    
    /**
     * @param generator
     *            The new generator that will be used for creating the terrain of a new
     *            chunk.
     */
    public void setChunkGenerator( ChunkGenerator generator );
    
    //
    // Getters
    //
    
    /**
     * @return The seed the provider is using to create the chunks.
     */
    public long getSeed();
    
    /**
     * @param generator
     *            The chunk generator that is used when creating the terrain of a new
     *            chunk.
     */
    public void getChunkGenerator( ChunkGenerator generator );
    
}
