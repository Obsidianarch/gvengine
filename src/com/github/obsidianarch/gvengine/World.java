package com.github.obsidianarch.gvengine;

import java.util.IdentityHashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.github.obsidianarch.gvengine.entities.Player;

/**
 * The world of chunks.
 * 
 * @author Austin
 */
public class World {
    
    //
    // Fields
    //
    
    /** Provides chunks to the world. */
    private final ChunkProvider          provider;
    
    /** A list of all chunks with their chunk positions as their key. */
    private final Map< Vector3f, Chunk > allChunks = new IdentityHashMap<>();
    
    /** The player in the world. */
    private Player                       player    = new Player();
    
    //
    // Constructors
    //
    
    /**
     * Creates a World.
     * 
     * @param provider
     *            Provides chunks to this world.
     */
    public World( ChunkProvider provider ) {
        this.provider = provider;
        player.camera.setPosition( 0, 48, 0 );
        player.camera.setRotation( 90, 0, 0 );
        
        // TODO change this to load better
        for ( int x = -2; x <= 2; x++ ) {
            for ( int y = -2; y <= 2; y++ ) {
                for ( int z = -2; z <= 2; z++ ) {
                    allChunks.put( new Vector3f( x, y, z ), provider.createChunk( x, y, z ) );
                }
            }
        }
    }
    
    //
    // Actions
    //
    
    /**
     * Updates the world.
     * 
     * @param dt
     *            The time since the last frame.
     */
    public void update( float dt ) {
        player.update( dt ); // allow the player to move
    }
    
    /**
     * Renders the world.
     */
    public void render() {
        player.camera.lookThrough(); // look through the camera before rendering
        
        for ( Chunk c : allChunks.values() ) {
            if ( c == null ) continue;
            c.render();
        }
        
        // TODO render only visible chunks
        
        player.render(); // render the player
    }
    
    //
    // Setters
    //
    
    //
    // Getters
    //
    
}
