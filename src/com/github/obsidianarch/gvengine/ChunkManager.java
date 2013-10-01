package com.github.obsidianarch.gvengine;

import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of {@code ChunkProvider} that simply calls the underlying
 * {@code ChunkProvider} for static access to methods.
 * 
 * @author Austin
 * @see ChunkProvider
 */
public class ChunkManager implements ChunkProvider {
    
    //
    // Fields
    //
    
    private final ChunkProvider chunkProvider; // the underlying ChunkProvider
                                               
    //
    // Constructors
    //
    
    private ChunkManager(ChunkProvider provider) {
        chunkProvider = provider;
    }
    
    //
    // Overrides
    //
    
    @Override
    public int getVertexCount() {
        return chunkProvider.getVertexCount();
    }
    
    @Override
    public void update() {
        chunkProvider.update();
    }
    
    @Override
    public void render() {
        chunkProvider.render();
    }
    
    @Override
    public void rebuildMesh() {
        chunkProvider.rebuildMesh();
    }
    
    /**
     * Provides the chunk residing at the given chunk system coordinates. If the chunk
     * does not exist, a new chunk will be created.
     * 
     * @param x
     *            The x coordinate of the chunk.
     * @param y
     *            The y coordinate of the chunk.
     * @param z
     *            The z coordinate of the chunk.
     * @return A chunk for the given chunk grid coordinates.
     * @see #getChunk(int, int, int)
     * @see #createChunk(int, int, int)
     */
    public Chunk provideChunk( int x, int y, int z ) {
        Chunk c = getChunk( x, y, z );
        
        if ( c == null ) return createChunk( x, y, z );
        return c;
    }
    
    @Override
    public Chunk getChunk( int x, int y, int z ) {
        return chunkProvider.getChunk( x, y, z );
    }
    
    @Override
    public Chunk createChunk( int x, int y, int z ) {
        return chunkProvider.createChunk( x, y, z );
    }
    
    //
    // Static
    //
    
    private static final ConcurrentHashMap< String, ChunkManager > chunkManagers = new ConcurrentHashMap<>();
    
    /**
     * Gets the chunk manager with the provided id.
     * 
     * @param id
     *            The id specific to the chunk provider, given when it was created.
     * @return {@code null} if the id wasn't bound to a chunk manager, otherwise the chunk
     *         manager for the id.
     * @see #createChunkManager(String, ChunkProvider)
     */
    public static synchronized ChunkManager getChunkManager( String id ) {
        return chunkManagers.get( id );
    }
    
    /**
     * Creates a new chunk manager with the given {@code ChunkProvider}, then returns the
     * id (as given by {@code System.nanoTime()}).
     * 
     * @param provider
     *            The {@code ChunkProvider} to bind to a new {@code ChunkManager}.
     * @return The newly created {@code ChunkManager}.
     */
    public static ChunkManager createChunkManager( String name, ChunkProvider provider ) {
        ChunkManager manager = new ChunkManager( provider ); // a new chunk manager instance
        
        // no ConcurrentModificationExceptions for us!
        synchronized ( chunkManagers ) {
            chunkManagers.put( name, manager ); // add the manager to the HashMap
        }
        
        return manager;
    }
}
