package com.github.obsidianarch.gvengine.tests.materials;

import com.github.obsidianarch.gvengine.core.Material;
import org.lwjgl.util.Color;

/**
 * @version 14.10.30
 * @since 14.10.30
 */
public class Grass extends Material
{

    /**
     * Constructs a new grass material with the given id.
     *
     * @param id
     *          The id for grass materials.
     */
    public Grass( int id )
    {
        super( new Color( 0, 255, 0 ), true, id );
    }

}
