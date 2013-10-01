package com.github.obsidianarch.gvengine;

import org.lwjgl.util.Color;

/**
 * Contains general information about a voxel.
 * 
 * @author Austin
 * @see Voxel
 */
public interface VoxelType {
    
    //
    // Boolean returning values
    //
    
    /**
     * @return {@code false} if the voxel shouldn't be rendered.
     */
    public boolean isActive();
    
    /**
     * @return {@code false} if the player can walk through the block.
     */
    public boolean isSolid();
    
    //
    // Object values
    //
    
    /**
     * @return The color of the voxel.
     */
    public Color getColor();
    
}
