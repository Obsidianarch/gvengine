package com.github.obsidianarch.gvengine;

public interface ChunkProvider {
    
    /**
     * @return The total number of vertices in all of the loaded chunks.
     */
    public int getVertexCount();
    
    /**
     * Updates various aspects about the chunks currently loaded, or ones that need to be
     * loaded.
     */
    public void update();
    
    /**
     * Invokes the render method on all of the chunks.
     */
    public void render();
    
    /**
     * Forces all of the chunks to have their mesh rebuilt. This should not be used very
     * often as this is handled automatically by the chunks themselves.
     */
    public void rebuildMesh();
    
    /**
     * Providers the chunk resided at the given chunk system coordinates.
     * 
     * @param x
     *            The x coordiante of the chunk.
     * @param y
     *            The y coordinate of the chunk.
     * @param z
     *            The z coordinate of the chunk.
     * @return {@code null} if there is no chunk at the given location, otherwise the
     *         chunk residing at the position.
     */
    public Chunk getChunk( int x, int y, int z );
    
    /**
     * Generates a chunk with the given coordinates.
     * 
     * @param x
     *            The x coordinate of the chunk.
     * @param y
     *            The y coordinate of the chunk.
     * @param z
     *            The z coordinate of the chunk.
     * @return A new chunk for the given chunk grid coordinates.
     */
    public Chunk createChunk( int x, int y, int z );
    
    /**
     * When a chunk's {@link Chunk#rebuildMesh()} method is invoked, it will first check
     * to see if a chunk rebuild is allowed by it's {@code ChunkProvider}. This can be
     * used to set an artifical limit for chunk mesh rebuilds per frame.
     * 
     * @return {@code true} if a chunk is allowed to rebuild its mesh, otherwise
     *         {@code false}.
     */
    public boolean isRebuildAllowed();
    
}
