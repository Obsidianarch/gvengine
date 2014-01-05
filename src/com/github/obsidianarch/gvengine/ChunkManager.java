package com.github.obsidianarch.gvengine;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.obsidianarch.gvengine.core.Camera;
import com.github.obsidianarch.gvengine.core.options.Option;
import com.github.obsidianarch.gvengine.core.options.SliderOption;

/**
 * @author Austin
 */
public class ChunkManager {
    
    //
    // Constants
    //
    
    /**
     * The artificial limit to the number of chunks that can have an action done to them
     * in a single frame.
     */
    @Option(
        description = "Chunk limit",
        screenName = "",
        x = -1,
        y = -1 )
    @SliderOption(
        minimum = 1,
        maximum = 500 )
    public static int      ChunkLimit   = 9;
    
    //
    // Fields
    //
    
    /** The camera of the player. */
    private Camera         camera;
    
    /** Chunks which need to be loaded. */
    private Queue< Chunk > loadQueue    = new LinkedBlockingQueue<>();
    
    /** Chunks which need to be unloaded (and added to the saving queue). */
    private Queue< Chunk > rebuildQueue = new LinkedBlockingQueue<>();
    
    /** Chunks which need to be unloaded. */
    private Queue< Chunk > unloadQueue  = new LinkedBlockingQueue<>();
    
    //
    // Updaters
    //
    
    /**
     * Updates the chunk manager's loading, rebuilding, unloading, and rendering
     * lists.
     * 
     * @param dt
     *            The time since the last frame.
     * @param camera
     *            The player's camera.
     */
    public void update( Camera camera ) {
        
        loadChunks(); // load the chunks that need to be
        
        rebuildChunks(); // rebuild chunks
        
        unloadChunks(); // unload chunks
        
        // the player has moved or oriented, so something may now be invisible
        if ( !this.camera.equals( camera ) ) {
            this.camera = camera;
        }
    }
    
    /**
     * Loads chunks from the load queue.
     */
    public void loadChunks() {
        int chunksLoaded = 0; // the number of chunks we've loaded
        
        for ( Chunk c : loadQueue ) {
            if ( chunksLoaded == ChunkLimit ) break; // we've maxed out the number of chunks we can load per frame
                
            if ( !c.isLoaded() ) {
                c.load(); // load the chunk
                chunksLoaded++; // we loaded a chunk
            }
            
        }
    }
    
    /**
     * Rebuilds chunks from the rebuild queue.
     */
    public void rebuildChunks() {
        int chunksRebuilt = 0; // the number of chunks rebuilt
        
        for ( Chunk c : rebuildQueue ) {
            if ( chunksRebuilt == ChunkLimit ) break; // we've maxed out the number of chunks we can rebuild per frame 
                
            c.buildMesh(); // rebuild the chunk mesh
            
            chunksRebuilt++; // we rebuild a mesh
        }
    }
    
    /**
     * Unloads chunks from the unload queue.
     */
    public void unloadChunks() {
        int chunksUnloaded = 0; // the number of chunks we've loaded
        
        for ( Chunk c : unloadQueue ) {
            if ( chunksUnloaded == ChunkLimit ) break; // we've maxed out the number of chunks we can load per frame
                
            if ( c.isLoaded() ) {
                c.unload(); // load the chunk
                chunksUnloaded++; // we loaded a chunk
            }
        }
    }
    
    //
    // Queuers
    //
    
    /**
     * Enqueues a chunk to be loaded.
     * 
     * @param c
     *            The chunk to eventually load.
     */
    public void markForLoad( Chunk c ) {
        loadQueue.add( c );
    }
    
    /**
     * Enqueues a chunk to have a mesh rebuild.
     * 
     * @param c
     *            The chunk to eventually rebuild.
     */
    public void markForRebuild( Chunk c ) {
        rebuildQueue.add( c );
    }
    
    /**
     * Enqueues a chunk to be unloaded.
     * 
     * @param c
     *            The chunk to eventually unload.
     */
    public void markForUnload( Chunk c ) {
        unloadQueue.add( c );
    }
    
}
