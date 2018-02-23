package com.addonovan.gvengine.core;

import org.lwjgl.BufferUtils;
import org.magicwerk.brownies.collections.primitive.FloatGapList;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

/**
 * A simplified version of the OpenGL VertexBufferObject, handles all of the low-level calls to OpenGL in single methods.
 *
 * @author Austin
 * @version 14.03.30
 * @since 14.03.30
 */
public class VertexBufferObject
{

    //
    // Final Fields
    //

    /**
     * The position system.
     */
    private final PositionSystem ps;

    /**
     * The color system.
     */
    private final ColorSystem cs;

    /**
     * The normal system.
     */
    private final NormalSystem ns;

    //
    // Fields
    //

    /**
     * The array for the position coordinates.
     */
    private FloatGapList coordinates;

    /**
     * The array for the color channels.
     */
    private FloatGapList channels;

    /**
     * The array for the normal coordiantes.
     */
    private FloatGapList normalCoordinates;

    /**
     * The method OpenGL will render the vertices.
     */
    private int glMode = GL_TRIANGLES;

    /**
     * The binding to OpenGL.
     */
    private int glBinding = -1;

    /**
     * If the data has been modified.
     */
    private boolean dataValid = false;

    //
    // Constructors
    //

    /**
     * Creates a new VertexBufferObject for rendering objects with OpenGL.
     *
     * @param ps
     *         The positioning system.
     * @param cs
     *         The coloring system.
     * @param ns
     *         The normal system.
     *
     * @since 14.03.30
     */
    public VertexBufferObject( PositionSystem ps, ColorSystem cs, NormalSystem ns )
    {
        this( ps, cs, ns, 0, 0, 0 );
    }

    /**
     * Creates a new VertexBufferObject for rendering objects with OpenGL, also sets an initial capacity for the {@code FloatGapList}s containing the
     * coordinate, color, and normal data.
     *
     * @param ps
     *         The positioning system.
     * @param cs
     *         The color system.
     * @param ns
     *         The normal system.
     * @param pcap
     *         The initial capacity on position data.
     * @param ccap
     *         The initial capacity on color data.
     * @param ncap
     *         The initial capacity on normal data.
     *
     * @since 14.03.30
     */
    public VertexBufferObject( PositionSystem ps, ColorSystem cs, NormalSystem ns, int pcap, int ccap, int ncap )
    {
        this( ps, cs, ns, new FloatGapList( pcap ), new FloatGapList( ccap ), new FloatGapList( ncap ) );
    }

    /**
     * Creates a new VertexBufferObject for rendering objects with OpenGL, also sets the inital data for the positioning, colors, and normals.
     *
     * @param ps
     *         The positioning system.
     * @param cs
     *         The color system.
     * @param ns
     *         The normal system.
     * @param coordinates
     *         The initial coordinate values.
     * @param channels
     *         The initial channel values.
     * @param normalCoordinates
     *         The initial normal values.
     *
     * @since 14.03.30
     */
    public VertexBufferObject( PositionSystem ps, ColorSystem cs, NormalSystem ns, FloatGapList coordinates, FloatGapList channels,
                               FloatGapList normalCoordinates )
    {

        this.ps = ps;
        this.cs = cs;
        this.ns = ns;

        this.coordinates = coordinates;
        this.channels = channels;

        if ( ns.coordinates == 0 )
        {
            this.normalCoordinates = new FloatGapList( 0 );
        }
        else
        {
            this.normalCoordinates = normalCoordinates;
        }
    }

    //
    // Actions
    //

    /**
     * {@code glDeleteBuffers(binding)}
     *
     * @since 14.03.30
     */
    public void delete()
    {
        glDeleteBuffers( glBinding );
    }

    /**
     * Validates the data in the VBO, and rebinds it to OpenGL.
     *
     * @since 14.03.30
     */
    public void validate()
    {
        FloatBuffer interleavedBuffer = BufferUtils.createFloatBuffer( coordinates.size() + channels.size() + normalCoordinates.size() );

        // insert data into our buffer
        MathHelper.insertBuffer( coordinates, interleavedBuffer, ps.coordinates, 0, cs.channels + ns.coordinates );
        MathHelper.insertBuffer( channels, interleavedBuffer, cs.channels, ps.coordinates, ns.coordinates + ps.coordinates );
        MathHelper.insertBuffer( normalCoordinates, interleavedBuffer, ns.coordinates, ps.coordinates + cs.channels, ps.coordinates + cs.channels );

        if ( glBinding == -1 )
        {
            glBinding = glGenBuffers();
        }

        glBindBuffer( GL_ARRAY_BUFFER, glBinding ); // bind the buffer to OpenGL
        glBufferData( GL_ARRAY_BUFFER, interleavedBuffer, GL_STATIC_DRAW ); // bind the buffer data

        glBindBuffer( GL_ARRAY_BUFFER, 0 ); // unbind the buffer

        dataValid = true;
    }

    /**
     * Renders our buffers.
     *
     * @since 14.03.30
     */
    public void render()
    {
        if ( !dataValid )
        {
            Scheduler.enqueueEvent( "validate", this ); // schedule a validation of the data
            if ( glBinding == -1 )
            {
                return;
            }
        }

        glPushMatrix();
        {
            glBindBuffer( GL_ARRAY_BUFFER, glBinding ); // bind our buffer
            provideVertexData();
            glDrawArrays( glMode, 0, coordinates.size() + channels.size() + normalCoordinates.size() ); // draw the arrays
            glBindBuffer( GL_ARRAY_BUFFER, 0 ); // unbind our buffer
        }
        glPopMatrix(); // stop editing our matrix
    }

    /**
     * Provides OpenGL with the vertex data (positions, colors, and normals) for the VBO.
     *
     * @since 14.03.30
     */
    private void provideVertexData()
    {
        int stride = ( ps.coordinates + cs.channels + ns.coordinates ) * 4;

        glVertexPointer( ps.coordinates, GL_FLOAT, stride, 0 ); // tell OpenGL where our vertices are
        glColorPointer( cs.channels, GL_FLOAT, stride, ps.coordinates * 4 ); // tell OpenGL where our colors are

        if ( ns.coordinates != 0 )
        { // if we have normals enabled...
            glNormalPointer( GL_FLOAT, stride, ( ps.coordinates + cs.channels ) * 4 ); // ...tell OpenGL where our normals are
        }
    }

    //
    // Adders
    //

    /**
     * @param array
     *         The coordiantes to add.
     */
    public void addCoordinates( float... array )
    {
        coordinates.addAll( array );
        dataValid = false;
    }

    /**
     * @param array
     *         The expanding array containing the new coordinates.
     */
    public void addCoordinates( FloatGapList array )
    {
        coordinates.addAll( array );
        dataValid = false;
    }

    /**
     * @param array
     *         The channels to add.
     */
    public void addChannels( float... array )
    {
        channels.addAll( array );
        dataValid = false;
    }

    /**
     * @param array
     *         The expanding array containing the new channels.
     */
    public void addChannels( FloatGapList array )
    {
        channels.addAll( array );
        dataValid = false;
    }

    /**
     * @param array
     *         The normals to add.
     */
    public void addNormals( float... array )
    {
        normalCoordinates.addAll( array );
        dataValid = false;
    }

    /**
     * @param array
     *         The expanding array containing the new normals.
     */
    public void addNormals( FloatGapList array )
    {
        normalCoordinates.addAll( array );
        dataValid = false;
    }

    //
    // Setters
    //

    /**
     * Changes the way OpenGL renders the vertices.
     *
     * @param glMode
     *         The new method OpenGL will render the vertices (GL_TRIANGLES is the default).
     */
    public void setGLMode( int glMode )
    {
        this.glMode = glMode;
    }

    /**
     * @param array
     *         The new positioning data.
     */
    public void setCoordinates( float... array )
    {
        coordinates = new FloatGapList( array.length );
        addCoordinates( array );
    }

    /**
     * @param array
     *         The new positioning data.
     */
    public void setCoordinates( FloatGapList array )
    {
        coordinates = array;
        dataValid = false;
    }

    /**
     * @param array
     *         The new color data.
     */
    public void setChannels( float... array )
    {
        channels = new FloatGapList( array.length );
        addChannels( array );
    }

    /**
     * @param array
     *         The new color data.
     */
    public void setChannels( FloatGapList array )
    {
        channels = array;
        dataValid = false;
    }

    /**
     * @param array
     *         The new normal data.
     */
    public void setNormalCoordinates( float... array )
    {
        normalCoordinates = new FloatGapList( array.length );
        addNormals( array );
    }

    /**
     * @param array
     *         The new normal data.
     */
    public void setNormalCoordinates( FloatGapList array )
    {
        normalCoordinates = array;
        dataValid = false;
    }

    //
    // Getters
    //

    /**
     * @return If the data OpenGL has is valid.
     */
    public boolean isValid()
    {
        return dataValid;
    }

    /**
     * @return The coordinates.
     */
    public FloatGapList getCoordinates()
    {
        return coordinates;
    }

    /**
     * @return The color channels.
     */
    public FloatGapList getChannels()
    {
        return channels;
    }

    /**
     * @return The normal coordinates.
     */
    public FloatGapList getNormalCoordinates()
    {
        return normalCoordinates;
    }

    //
    // Overrides
    //

    @Override
    public String toString()
    {
        return String.format( "core.vbo{ glBinding: %1d | vertices: %2d | valid: %3b }", glBinding, coordinates.size() / ps.coordinates, isValid() );
    }

    //
    // Static
    //

    /**
     * Merges multiple VertexBufferObjects into one.
     *
     * @param pc
     *         The positioning capacity.
     * @param cc
     *         The channel capacity.
     * @param nc
     *         The normal positioning capacity.
     * @param vbos
     *         The VertexBufferObjects to merge.
     *
     * @return The merged VertexBufferObject.
     *
     * @since 14.03.30
     */
    public static VertexBufferObject merge( int pc, int cc, int nc, VertexBufferObject... vbos )
    {
        VertexBufferObject merged = new VertexBufferObject( PositionSystem.XYZ, ColorSystem.RGB, NormalSystem.DISABLED );

        FloatGapList positions = new FloatGapList( pc );
        FloatGapList colors = new FloatGapList( cc );
        FloatGapList normals = new FloatGapList( nc );

        // add data to the expanding arrays
        for ( VertexBufferObject vbo : vbos )
        {
            positions.addAll( vbo.getCoordinates() );
            colors.addAll( vbo.getChannels() );
            normals.addAll( vbo.getNormalCoordinates() );
        }

        // set the data of our VBO
        merged.setCoordinates( positions );
        merged.setChannels( colors );
        merged.setNormalCoordinates( normals );

        return merged;
    }

}
