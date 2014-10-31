package com.github.obsidianarch.gvengine.tests.materials;

import com.github.obsidianarch.gvengine.core.Material;

/**
 * @version 14.10.30
 * @since 14.10.30
 */
public class Air extends Material
{

    /**
     * Constructs a simple, empty voxel which will not be rendered.
     *
     * @param id
     *          The ID for air.
     */
    public Air( int id )
    {
        super( null, false, id );
    }

}
