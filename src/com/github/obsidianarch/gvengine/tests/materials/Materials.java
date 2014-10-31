package com.github.obsidianarch.gvengine.tests.materials;

import com.github.obsidianarch.gvengine.core.Material;

/**
 * All materials used in the tests.
 *
 * @version 14.10.30
 * @since 14.10.30
 */
public class Materials
{

    /** Nothing, id 0 */
    public static final Material AIR;

    /** A green block, id 1 */
    public static final Material GRASS;

    /** A brown block, id 2 */
    public static final Material DIRT;

    /** A dark grey block, id 3 */
    public static final Material STONE;

    /** The number of materials created here. */
    public static final int MATERIAL_COUNT;

    static
    {
        AIR = new Air( 0 );
        GRASS = new Grass( 1 );
        DIRT = new Dirt( 2 );
        STONE = new Stone( 3 );

        MATERIAL_COUNT = Materials.class.getFields().length - 1; // the number of fields excluding this one
    }
}
