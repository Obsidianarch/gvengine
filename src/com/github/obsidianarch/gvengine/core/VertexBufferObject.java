package com.github.obsidianarch.gvengine.core;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * A simplified version of the OpenGL VertexBufferObject.
 * 
 * @author Austin
 */
public class VertexBufferObject {
    
    //
    // Final Fields
    //
    
    /** The position system. */
    private final PositionSystem ps;
    
    /** The color system. */
    private final ColorSystem    cs;
    
    /** The normal system. */
    private final NormalSystem   ns;
    
    //
    // Fields
    //
    
    /** The array for the position coordinates. */
    private ExpandingArray       coordinates;
    
    /** The array for the color channels. */
    private ExpandingArray       channels;
    
    /** The array for the normal coordiantes. */
    private ExpandingArray       normalCoordinates;
    
    /** The method OpenGL will render the vertices. */
    private int                  glMode    = GL_TRIANGLES;
    
    /** The binding to OpenGL. */
    private int                  glBinding = -1;
    
    /** If the data has been modified. */
    private boolean              dataValid = false;
    
    //
    // Constructors
    //
    
    /**
     * Creates a new VertexBufferObject for rendering objects with OpenGL.
     * 
     * @param ps
     *            The positioning system.
     * @param cs
     *            The coloring system.
     * @param ns
     *            The normal system.
     */
    public VertexBufferObject(PositionSystem ps, ColorSystem cs, NormalSystem ns) {
        this( ps, cs, ns, 0, 0, 0 );
    }
    
    /**
     * Creates a new VertexBufferObject for rendering objects with OpenGL, also sets an
     * initial capacity for the {@code ExpandingArray}s containing the coordinate, color,
     * and normal data.
     * 
     * @param ps
     *            The positioning system.
     * @param cs
     *            The color system.
     * @param ns
     *            The normal system.
     * @param pcap
     *            The initial capacity on position data.
     * @param ccap
     *            The initial capacity on color data.
     * @param ncap
     *            The initial capacity on normal data.
     */
    public VertexBufferObject(PositionSystem ps, ColorSystem cs, NormalSystem ns, int pcap, int ccap, int ncap) {
        this( ps, cs, ns, new ExpandingArray( pcap ), new ExpandingArray( ccap ), new ExpandingArray( ncap ) );
    }
    
    /**
     * Creates a new VertexBufferObject for rendering objects with OpenGL, also sets the
     * inital data for the positioning, colors, and normals.
     * 
     * @param ps
     *            The positioning system.
     * @param cs
     *            The color system.
     * @param ns
     *            The normal system.
     * @param coordinates
     *            The initial coordinate values.
     * @param channels
     *            The initial channel values.
     * @param normalCoordinates
     *            The initial normal values.
     */
    public VertexBufferObject(PositionSystem ps, ColorSystem cs, NormalSystem ns, ExpandingArray coordinates, ExpandingArray channels,
        ExpandingArray normalCoordinates) {
        this.ps = ps;
        this.cs = cs;
        this.ns = ns;
        
        this.coordinates = coordinates;
        this.channels = channels;
        
        if ( ns.coordinates == 0 ) {
            this.normalCoordinates = new ExpandingArray( 0 );
        }
        else {
            this.normalCoordinates = normalCoordinates;
        }
    }
    
    //
    // Actions
    //
    
    /**
     * Validates the data in the VBO, and rebinds it to OpenGL.
     */
    public void validate() {
        if ( glBinding == -1 ) {
            glBinding = glGenBuffers();
        }
        
        FloatBuffer interleavedBuffer = BufferUtils.createFloatBuffer( coordinates.getLength() + channels.getLength() + normalCoordinates.getLength() );
        
        coordinates.addToBuffer( interleavedBuffer, ps.coordinates, 0, cs.channels + ns.coordinates );
        channels.addToBuffer( interleavedBuffer, cs.channels, ps.coordinates, ns.coordinates + ps.coordinates );
        normalCoordinates.addToBuffer( interleavedBuffer, ns.coordinates, ps.coordinates + cs.channels, ps.coordinates + cs.channels );
        
        glBindBuffer( GL_ARRAY_BUFFER, glBinding ); // bind the buffer to OpenGL
        glBufferData( GL_ARRAY_BUFFER, interleavedBuffer, GL_STATIC_COPY ); // bind the buffer data
        
        int stride = ( ps.coordinates + cs.channels + ns.coordinates ) * 4;
        
        glVertexPointer( ps.coordinates, GL_FLOAT, stride, 0 ); // tell OpenGL where our vertices are
        glColorPointer( cs.channels, GL_FLOAT, stride, ps.coordinates * 4 ); // tell OpenGL where our colors are
        
        if ( ns.coordinates != 0 ) { // if we have normals enabled...
            glNormalPointer( GL_FLOAT, stride, ( ps.coordinates + cs.channels ) * 4 ); // tell OpenGL where our normals are
        }
        
        glBindBuffer( GL_ARRAY_BUFFER, 0 ); // unbind the buffer
        
        dataValid = true;
    }
    
    /**
     * Renders our buffers.
     */
    public void render() {
        // validate invalid data
        if ( !dataValid ) {
            validate();
        }
        
        glPushMatrix(); // start editing our matrix
        glBindBuffer( GL_ARRAY_BUFFER, glBinding ); // bind our buffer
        glDrawArrays( glMode, 0, coordinates.getLength() + channels.getLength() + normalCoordinates.getLength() ); // draw the arrays
        glBindBuffer( GL_ARRAY_BUFFER, 0 ); // unbind our buffer
        glPopMatrix(); // stop editing our matrix
    }
    
    //
    // Adders TODO add some javadoc
    //
    
    public void addCoordinates( float... array ) {
        coordinates.put( array );
        dataValid = false;
    }
    
    public void addCoordinates( ExpandingArray array ) {
        coordinates.put( array );
        dataValid = false;
    }
    
    public void addChannels( float... array ) {
        channels.put( array );
        dataValid = false;
    }
    
    public void addChannels( ExpandingArray array ) {
        channels.put( array );
        dataValid = false;
    }
    
    public void addNormals( float... array ) {
        normalCoordinates.put( array );
        dataValid = false;
    }
    
    public void addNormals( ExpandingArray array ) {
        normalCoordinates.put( array );
        dataValid = false;
    }
    
    //
    // Setters
    //
    
    /**
     * Changes the way OpenGL renders the vertices.
     * 
     * @param glMode
     *            The new method OpenGL will render the vertices (GL_TRIANGLES is the
     *            default).
     */
    public void setGLMode( int glMode ) {
        this.glMode = glMode;
    }
    
    /**
     * @param array
     *            The new positioning data.
     */
    public void setCoordinates( float... array ) {
        coordinates = new ExpandingArray( array.length );
        addCoordinates( array );
    }
    
    /**
     * @param array
     *            The new positioning data.
     */
    public void setCoordinates( ExpandingArray array ) {
        coordinates = array;
        dataValid = false;
    }
    
    /**
     * @param array
     *            The new color data.
     */
    public void setChannels( float... array ) {
        channels = new ExpandingArray( array.length );
        addChannels( array );
    }
    
    /**
     * @param array
     *            The new color data.
     */
    public void setChannels( ExpandingArray array ) {
        channels = array;
        dataValid = false;
    }
    
    /**
     * @param array
     *            The new normal data.
     */
    public void setNormalCoordinates( float... array ) {
        normalCoordinates = new ExpandingArray( array.length );
        addNormals( array );
    }
    
    /**
     * @param array
     *            The new normal data.
     */
    public void setNormalCoordinates( ExpandingArray array ) {
        normalCoordinates = array;
        dataValid = false;
    }
    
    //
    // Getters
    //
    
    /**
     * @return The coordinates.
     */
    public ExpandingArray getCoordinates() {
        return coordinates;
    }
    
    /**
     * @return The color channels.
     */
    public ExpandingArray getChannels() {
        return channels;
    }
    
    /**
     * @return The normal coordinates.
     */
    public ExpandingArray getNormalCoordinates() {
        return normalCoordinates;
    }
    
    //
    // Static
    //
    
    /**
     * Merges multiple VertexBufferObjects into one.
     * 
     * @param pc
     *            The positioning capacity.
     * @param cc
     *            The channel capacity.
     * @param nc
     *            The normal positioning capacity.
     * @param vbos
     *            The VertexBufferObjects to merge.
     * @return The merged VertexBufferObject.
     */
    public static VertexBufferObject merge( int pc, int cc, int nc, VertexBufferObject... vbos ) {
        VertexBufferObject merged = new VertexBufferObject( PositionSystem.XYZ, ColorSystem.RGB, NormalSystem.DISABLED );
        
        ExpandingArray positions = new ExpandingArray( pc );
        ExpandingArray colors = new ExpandingArray( cc );
        ExpandingArray normals = new ExpandingArray( nc );
        
        // add data to the expanding arrays
        for ( VertexBufferObject vbo : vbos ) {
            positions.put( vbo.getCoordinates() );
            colors.put( vbo.getChannels() );
            normals.put( vbo.getNormalCoordinates() );
        }
        
        // set the data of our VBO
        merged.setCoordinates( positions );
        merged.setChannels( colors );
        merged.setNormalCoordinates( normals );
        
        return merged;
    }
    
}
