package com.github.obsidianarch.gvengine.model;

import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Loader OBJ model files.
 *
 * @author Austin
 * @since 14.04.19
 */
public class OBJModelLoader extends ModelLoader
{

    //
    // Parsers
    //

    /**
     * Parses a line into a three coordinate set.
     *
     * @param line
     *         The rest of the line.
     *
     * @return A Vector3f containing the three coordinates on the line.
     *
     * @since 14.04.19
     */
    public Vector3f parseCoordinate( String line )
    {
        String[] coordinates = line.split( " " ); // separate the coordinate numbers

        // load the coordiantes
        float x = Float.parseFloat( coordinates[ 0 ] );
        float y = Float.parseFloat( coordinates[ 1 ] );
        float z = Float.parseFloat( coordinates[ 2 ] );

        return new Vector3f( x, y, z );
    }

    /**
     * Parses a line into a Face.
     *
     * @param line
     *         The rest of the line.
     *
     * @return A Face containing the data of the line.
     *
     * @since 14.04.19
     */
    public Face parseFace( String line )
    {
        String[] pairs = line.split( " " ); // splits it three sections which contain two elements separated by a //

        float[] vertices = new float[ 3 ]; // the indices for the vertices
        float[] normals = new float[ 3 ]; // the indices for the normals

        // load the vertices and normals indices separated by a "//"
        for ( int i = 0; i < pairs.length; i++ )
        {
            String[] split = pairs[ i ].split( "//" );

            vertices[ i ] = Float.parseFloat( split[ 0 ] );
            normals[ i ] = Float.parseFloat( split[ 1 ] );
        }

        // create the vectors with this data
        Vector3f vertexIndices = new Vector3f( vertices[ 0 ], vertices[ 1 ], vertices[ 2 ] );
        Vector3f normalIndices = new Vector3f( normals[ 0 ], normals[ 1 ], normals[ 2 ] );

        // create the face with the vectors
        return new Face( vertexIndices, normalIndices );
    }

    //
    // Overrides
    //

    @Override
    public Model loadModel( BufferedReader br ) throws IOException
    {
        Model m = new Model();

        String line;
        while ( ( line = br.readLine() ) != null )
        {

            if ( line.startsWith( "v " ) )
            {
                m.addVertex( parseCoordinate( line.substring( "v ".length() ) ) ); // parses the rest of the line as a vertex
            }
            else if ( line.startsWith( "vn " ) )
            {
                m.addNormal( parseCoordinate( line.substring( "vn ".length() ) ) ); // parses the rest of the line as a normal
            }
            else if ( line.startsWith( "f " ) )
            {
                m.addFace( parseFace( line.substring( "f ".length() ) ) ); // parses the rest of te line as a face
            }

        }

        return m;
    }

    @Override
    public boolean isSupported( String fileName )
    {
        return fileName.endsWith( ".obj" );
    }

}
