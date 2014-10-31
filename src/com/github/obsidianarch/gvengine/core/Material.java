package com.github.obsidianarch.gvengine.core;

import org.lwjgl.util.ReadableColor;

import java.util.ArrayList;

/**
 * The properties for a type of voxel.
 *
 * @author Austin
 * @version 14.10.28
 * @since 14.03.30
 */
public class Material
{

    //
    // Constants
    //

    /** The maximum number of materials the ArrayList can hold */
    private static final int MAX_MATERIALS = Short.MAX_VALUE;

    /**
     * An array containing all in-game materials.
     */
    private static final Material[] materials = new Material[ MAX_MATERIALS ];

    //
    // Final Fields
    //

    /**
     * The color of the material.
     */
    public final ReadableColor color;

    /**
     * {@code false} if a voxel with this material should not be rendered.
     */
    public final boolean active;

    /**
     * The index of this material in the material array.
     */
    public final int id;

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
     * @param id
     *         The id of the material, doubles as the index in the material array, must not be previously assigned.
     *         [0, 32767].
     *
     * @throws java.lang.IllegalArgumentException
     *          If {@code materials[ id ] != null}.
     *
     * @since 14.03.30
     */
    public Material( ReadableColor color, boolean active, int id ) throws IllegalArgumentException
    {
        // check to make sure that the slot is empty, it causes more headaches trying to figure it out
        // if an ID is a duplicate than just choosing a new ID would be.
        if ( materials[ id ] != null )
        {
            throw new IllegalArgumentException( "Material slot already filled: " + id );
        }

        // set the properties of the material
        this.color = color;
        this.active = active;
        this.id = id;

        materials[ id ] = this;
    }

    //
    // Static
    //

    /**
     * @param id
     *         The id of the Material.
     *
     * @return The material at the given id.
     *
     * @since 14.03.30
     */
    public static Material getMaterial( int id )
    {
        return materials[ id ];
    }

}
