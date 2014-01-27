package com.github.obsidianarch.gvengine.entities;

import com.github.obsidianarch.gvengine.core.Camera;
import com.github.obsidianarch.gvengine.core.Controller;

/**
 * Represents an object in the game. Voxels are excluded from this, as they are considered
 * terrain. This is the equivalent of the {@code java.lang.Object} class for anything that
 * will be appearing in the game, excluding terrain. As a result, this class if very
 * vague.
 * 
 * @author Austin
 */
public class Entity {
    
    //
    // Fields
    //
    
    /**
     * Despite the fact that many entities will not be controllable by the player, each
     * will have a camera.
     */
    public final Camera     camera     = new Camera();
    
    /** Controls the entities movements around 3d space. */
    public final Controller controller = new Controller( camera );
    
    //
    // Methods
    //
    
    /**
     * Updates the entity.
     * 
     * @param dt
     *            The time since the last frame.
     */
    public void update( float dt ) {
        // intentionally blank
    }
    
    /**
     * Renders the entity.
     */
    public void render() {
        // intentionally blank
    }
    
}
