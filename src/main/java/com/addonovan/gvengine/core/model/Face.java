package com.addonovan.gvengine.core.model;

import org.joml.Vector3f;

/**
 * A face on a model.
 *
 * @author Austin
 * @since 14.02.08
 */
public class Face
{

    //
    // Fields
    //

    /**
     * Three indices for the vertices, not vertex data
     */
    public Vector3f vertex = new Vector3f();

    /**
     * Three indices for the normals, not normal data.
     */
    public Vector3f normal = new Vector3f();

    //
    // Constructors
    //

    /**
     * Creates a new Face with the three supplied vertex and normal indices.
     *
     * @param vertex
     *         Indicies for the vertex data (n.b. <b>not</b> the vertex data).
     * @param normal
     *         Indicies for the normal data (n.b. <b>not</b> the normal data).
     *
     * @since 14.04.19
     */
    public Face( Vector3f vertex, Vector3f normal )
    {
        this.vertex = vertex;
        this.normal = normal;
    }

}
