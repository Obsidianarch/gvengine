package com.addonovan.gvengine;

import org.joml.Vector3f;

/**
 * The properties for a type of voxel.
 *
 * @author Austin
 * @version 14.03.30
 * @since 14.03.30
 */
public class Material
{

    //
    // Static Fields
    //

    /**
     * An array containing all in-game materials, do not write anything to this.
     */
    private static final Material[] materials = new Material[ 256 ];

    //
    // Constants
    //

    /**
     * Used to represent nothing, written as {@code 14.03.30000}
     */
    public static final Material AIR = new Material( new Vector3f( 1.0f, 1.0f, 1.0f ), false, 0 );

    /**
     * The stone material, written as {@code 14.03.3001}.
     */
    public static final Material STONE = new Material( new Vector3f( 0.6f, 0.6f, 0.6f ), true, 1 );

    /**
     * The grass material, written as {@code 14.03.3010}.
     */
    public static final Material GRASS = new Material( new Vector3f( 0.0f, 1.0f, 0.0f ), true, 2 );

    /**
     * The dirt material, written as {@code 14.03.3011}.
     */
    public static final Material DIRT = new Material( new Vector3f( 1.0f, 0.5f, 0.25f ), true, 3 );

    //
    // Final Fields
    //

    /**
     * The color of the material.
     */
    public final Vector3f color;

    /**
     * {@code false} if a voxel with this material should not be rendered.
     */
    public final boolean active;

    /**
     * The id of the voxel in the index.
     */
    public final int indexID;

    /**
     * The id of the voxel as a byte. (indexID - 128)
     */
    public final byte byteID;

    //
    // Constructors
    //

    /**
     * Creates a material.
     *
     * @param color
     *         The color of a voxel with this material.
     * @param active
     *         If a voxel with this material should be rendered or not.
     * @param byteID
     *         The (unsigned) byte id of the material in the index array.
     *
     * @since 14.03.30
     */
    public Material( Vector3f color, boolean active, int byteID )
    {
        this.color = color;
        this.active = active;
        this.byteID = ( byte ) byteID;
        indexID = byteID + 128;

        materials[ indexID ] = this; // add it to the materials array for lookup
    }

    //
    // Static
    //

    /**
     * @param b
     *         The byte id for the material.
     *
     * @return The material for the byte id.
     *
     * @since 14.03.30
     */
    public static Material getMaterial( byte b )
    {
        return materials[ b + 128 ];
    }

}
