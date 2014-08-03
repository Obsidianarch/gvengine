package com.github.obsidianarch.gvengine.core.model;

import com.github.obsidianarch.gvengine.core.ColorSystem;
import com.github.obsidianarch.gvengine.core.NormalSystem;
import com.github.obsidianarch.gvengine.core.PositionSystem;
import com.github.obsidianarch.gvengine.core.VertexBufferObject;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of vertices, normals, and faces saved in a file that is loaded by a ModelLoader and can be rendered as a VertexBufferObject.
 *
 * @author Austin
 * @since 14.04.19
 */
public class Model
{

    //
    // Fields
    //

    /**
     * The vertices in this model.
     */
    private List< Vector3f > vertices = new ArrayList<>();

    /**
     * The normals in this model.
     */
    private List< Vector3f > normals = new ArrayList<>();

    /**
     * The faces in this model.
     */
    private List< Face > faces = new ArrayList<>();

    /**
     * The VertexBufferObject that is rendered for this model.
     */
    private VertexBufferObject vbo;

    /**
     * If the data is valid.
     */
    private boolean dataValid = false;

    //
    // Constructors
    //

    /**
     * Constructs a new Model with no vertex, normal, or face data. The Model will have no normals and no alpha channel in its colors.
     *
     * @since 14.04.19
     */
    public Model()
    {
        this( NormalSystem.ENABLED, ColorSystem.RGB );
    }

    /**
     * Constructs a new Model with no vertex, normal, or face data.
     *
     * @param ns
     *         If normals are enabled or not.
     * @param cs
     *         If the alpha channel is enabled or not.
     *
     * @since 14.04.19
     */
    public Model( NormalSystem ns, ColorSystem cs )
    {
        vbo = new VertexBufferObject( PositionSystem.XYZ, cs, ns );
    }

    //
    // Actions
    //

    /**
     * Validates the data contained by the underlying VertexBufferObject that is used by this model and updates it to any changed code.
     *
     * @since 14.04.19
     */
    public void validate()
    {
        // remove previous data
        vbo.setCoordinates();
        vbo.setChannels();
        vbo.setNormals();

        // add the faces to the VertexBufferObject
        for ( Face face : faces )
        {

            // add the vertices
            {
                Vector3f vertex = vertices.get( ( int ) ( face.vertex.x - 1 ) ); // gets the first vertex the face contains
                vbo.addCoordinates( vertex.x, vertex.y, vertex.z ); // adds the vertice's coordinates to the VBO

                vertex = vertices.get( ( int ) ( face.vertex.y - 1 ) ); // gets the second vertex the face contains
                vbo.addCoordinates( vertex.x, vertex.y, vertex.z ); // adds the vertice's coordinates to the VBO

                vertex = vertices.get( ( int ) ( face.vertex.z - 1 ) ); // gets the third vertex the face contains
                vbo.addCoordinates( vertex.x, vertex.y, vertex.z ); // adds the vertice's coordinates to the VBO
            }

            // add the normals
            {
                Vector3f normal = normals.get( ( int ) ( face.normal.x - 1 ) ); // gets the first normal the face contains
                vbo.addCoordinates( normal.x, normal.y, normal.z ); // adds the vertice's coordinates to the VBO

                normal = normals.get( ( int ) ( face.normal.y - 1 ) ); // gets the second normal the face contains
                vbo.addCoordinates( normal.x, normal.y, normal.z ); // adds the vertice's coordinates to the VBO

                normal = normals.get( ( int ) ( face.normal.z - 1 ) ); // gets the third normal the face contains
                vbo.addCoordinates( normal.x, normal.y, normal.z ); // adds the vertice's coordinates to the VBO
            }
        }

        vbo.validate(); // validates the vbo data
        dataValid = true; // we have updated all the data in the VBO
    }

    /**
     * Checks to make sure the data for this Model is validated, if not then the data is validated then rendered.
     *
     * @since 14.04.19
     */
    public void render()
    {
        if ( !dataValid )
        {
            validate();
        }
        vbo.render();
    }

    /**
     * Deletes the VBO from the OpenGL memory.
     *
     * @since 14.04.19
     */
    public void delete()
    {
        vbo.delete();
    }

    //
    // Adders
    //

    /**
     * Adds a vertex to the vertex list.
     *
     * @param vertex
     *         The vertex to add.
     *
     * @since 14.04.19
     */
    public void addVertex( Vector3f vertex )
    {
        vertices.add( vertex );
        dataValid = false;
    }

    /**
     * Adds a normal to the normal list.
     *
     * @param normal
     *         The normal to add.
     *
     * @since 14.04.19
     */
    public void addNormal( Vector3f normal )
    {
        normals.add( normal );
        dataValid = false;
    }

    /**
     * Adds a face to the face list.
     *
     * @param face
     *         The face to add.
     *
     * @since 14.04.19
     */
    public void addFace( Face face )
    {
        faces.add( face );
        dataValid = false;
    }

}
