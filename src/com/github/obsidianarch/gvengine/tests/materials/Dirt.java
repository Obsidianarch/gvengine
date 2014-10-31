package com.github.obsidianarch.gvengine.tests.materials;

import com.github.obsidianarch.gvengine.core.Material;
import org.lwjgl.util.Color;

/**
 * @version 14.10.30
 * @since 14.10.30
 */
public class Dirt extends Material
{

    /**
     * A simple brown block.
     *
     * @param id
     *          The ID for the dirt material.
     */
    public Dirt( int id )
    {
        super( new Color( 103, 10, 10 ), true, id );
    }

}
