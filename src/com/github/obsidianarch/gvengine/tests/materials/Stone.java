package com.github.obsidianarch.gvengine.tests.materials;

import com.github.obsidianarch.gvengine.core.Material;
import org.lwjgl.util.Color;


/**
 * @version 14.10.30
 * @since 14.10.30
 */
public class Stone extends Material
{

    /**
     * Constructs a new stone material.
     *
     * @param id
     *          The id for stone.
     */
    public Stone( int id )
    {
        super( new Color( 40, 40, 40 ), true, id );
    }

}
