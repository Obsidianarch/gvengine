package com.github.obsidianarch.gvengine;

import java.util.IdentityHashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.github.obsidianarch.gvengine.core.MathHelper;
import com.github.obsidianarch.gvengine.entities.Player;

/**
 * @author Austin
 */
public class World {
    
    //
    // Fields
    //
    
    /** Manages the chunks in this world. */
    private final ChunkManager           manager;
    
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
     * @param manager
     *            The ChunkManager for this world.
     */
    public World( ChunkManager manager ) {
        this.manager = manager;
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
        manager.update( this, player.camera ); // update the chunk manager
        
        player.update( dt ); // allow the player to move
    }
    
    /**
     * Renders the world.
     */
    public void render() {
        player.camera.lookThrough(); // look through the camera before rendering
        
        // TODO render only visible chunks
        
        player.render(); // render the player
    }
    
    //
    // Getters
    //
    
    /**
     * Returns the chunk at the given chunk coordinates. If the chunk does not exist, then
     * the chunk will be generated.
     * 
     * @param x
     * @param y
     * @param z
     * @return The chunk at the given chunk coordiantes.
     */
    public Chunk getChunkAt( int x, int y, int z ) {
        Vector3f pos = new Vector3f( x, y, z );
        Chunk c = allChunks.get( pos );
        
        if ( c == null ) {
            c = createChunk( x, y, z );
            allChunks.put( pos, c );
        }
        
        return c;
    }
    
    /**
     * Creates a chunk at the given chunk locations.
     * 
     * @param cX
     * @param cY
     * @param cZ
     * @return The created chunk.
     */
    public Chunk createChunk( int cX, int cY, int cZ ) {
        Chunk c = new Chunk( cX, cY, cZ );
        
        for ( int x = 0; x < 16; x++ ) {
            for ( int y = 0; y < 16; y++ ) {
                for ( int z = 0; z < 16; z++ ) {
                    
                    // TODO externalize this code later, and make it better
                    
                    if ( cY > 0 ) {
                        c.setMaterialAt( Material.AIR, x, y, z );
                    }
                    else {
                        float globalY = MathHelper.getGlobalPosition( cY, y );
                        if ( globalY > 20 ) {
                            c.setMaterialAt( Material.GRASS, x, y, z );
                        }
                        else {
                            c.setMaterialAt( Material.STONE, x, y, z );
                        }
                    }
                }
            }
        }
        
        return c;
    }
    
}
