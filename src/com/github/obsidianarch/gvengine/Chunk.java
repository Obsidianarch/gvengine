package com.github.obsidianarch.gvengine;

import static com.github.obsidianarch.gvengine.Voxel.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Chunk {
    
    //
    // Fields
    //
    
    private final VoxelCollection voxels;                 // used for voxel storage
                                                           
    /** The x coordinate of the chunk in the chunk grid system. */
    public final int              x;
    
    /** The y coordinate of the chunk in the chunk grid system. */
    public final int              y;
    
    /** The z coordinate of the chunk in the chunk grid system. */
    public final int              z;
    
    /** The chunk provider which created this chunk. */
    public final ChunkProvider    provider;
    
    private int                   colorHandle;            // OpenGL color buffer handle
    private int                   vertexHandle;           // OpenGL vertex buffer handle
                                                           
    private int                   vertexCount     = 0;    // number of vertices (debug info)
                                                           
    private boolean               rebuildRequired = false; // do we need to rebuild the mesh?
    private boolean               eclipsed        = false; // true if there are no vertices to render
                                                           
    //
    // Constructors
    //
    
    /**
     * Creates a new chunk with the given chunk coordinates.
     * 
     * @param cx
     *            The x coordinate of the chunk in the chunk grid system.
     * @param cy
     *            The y coordinate of the chunk in the chunk grid system.
     * @param cz
     *            The z coordinate of the chunk in the chunk grid system.
     */
    public Chunk(int cx, int cy, int cz, ChunkProvider cp) {
        x = cx;
        y = cy;
        z = cz;
        provider = cp;
        
        voxels = new VoxelCollection( this ); // the chunk data will be provided by the ChunkProvider who created this
    }
    
    //
    // Rendering
    //
    
    /**
     * Performs the required OpenGL calls to render the vertices and colors at the correct
     * positions. If a rebuild is required, it will be performed before rendering.
     */
    public synchronized void render() {
        if ( eclipsed ) return; // if we can't see the chunk at all, then don't even try
            
        // if a rebuild is required then we'll rebuild the mesh before rendering
        if ( rebuildRequired ) {
            rebuildMesh();
            rebuildRequired = false;
        }
        
        glPushMatrix();
        
        glBindBuffer( GL_ARRAY_BUFFER, vertexHandle ); // bind the vertex buffer
        glVertexPointer( 3, GL_FLOAT, 0, 0L );
        
        glBindBuffer( GL_ARRAY_BUFFER, colorHandle ); // bind the color buffer
        glColorPointer( 3, GL_FLOAT, 0, 0L );
        
        glDrawArrays( glMode, 0, VOXEL_CAPACITY * 36 ); // draw the arrays
        
        glBindBuffer( GL_ARRAY_BUFFER, 0 ); // unbind the buffers
        
        glPopMatrix();
    }
    
    //
    // VoxelData
    //
    
    /**
     * Changes the voxel id at the given position.
     * 
     * @param x
     *            The x coordinate of the voxel.
     * @param y
     *            The y coordinate of the voxel.
     * @param z
     *            The z coordinate of the voxel.
     * @param voxelID
     *            The new voxel id.
     */
    public void setVoxelAt( int x, int y, int z, int voxelID ) {
        voxels.set( x, y, z, voxelID );
        rebuildRequired = true;
    }
    
    /**
     * Gets the voxel id at the given position.
     * 
     * @param x
     *            The x coordinate of the voxel.
     * @param y
     *            The y coordinate of the voxel.
     * @param z
     *            The z coordinate of the voxel.
     * @return The voxel id at the given coordinates.
     */
    public int getVoxelAt( int x, int y, int z ) {
        return voxels.get( x, y, z );
    }
    
    //
    // Mesh
    //
    
    /**
     * Rebuilds the mesh of the chunk for rendering. This should only be called when
     * required, as this can become a very large processing hog.
     */
    public void rebuildMesh() {
        // clean up after ourselves
        glDeleteBuffers( vertexHandle );
        glDeleteBuffers( colorHandle );
        
        vertexHandle = glGenBuffers(); // get a new vertex handle
        colorHandle = glGenBuffers(); // get a new color handle
        
        // 108 = 6 (points per face (2 triangles)) * 6 (faces per voxel) * 3 (values (x, y, z || r, g, b))
        FloatBuffer vertexData = BufferUtils.createFloatBuffer( VOXEL_CAPACITY * 108 ); // create a vertex data buffer
        FloatBuffer colorData = BufferUtils.createFloatBuffer( VOXEL_CAPACITY * 108 ); // create a color data buffer
        
        // populate the buffers with information
        for ( int x = 0; x < CHUNK_SIZE; x++ ) {
            for ( int y = 0; y < CHUNK_SIZE; y++ ) {
                for ( int z = 0; z < CHUNK_SIZE; z++ ) {
                    int voxelID = voxels.get( x, y, z );
                    VoxelType voxelType = getVoxelType( voxelID );
                    if ( !voxelType.isActive() ) continue; // skip inactive blocks
                        
                    voxels.bindVoxelInformation( x, y, z );
                    
                    if ( !voxels.isVoxelVisible() ) continue; // if the voxel won't be rendered, skip it
                        
                    vertexData.put( voxels.getVertices() ); // add the vertices to the buffer
                    colorData.put( voxels.getVertexColors() ); // add the vertex colors to the buffer
                }
            }
        }
        
        // flip the buffers (so they can be read)
        vertexData.flip();
        colorData.flip();
        
        vertexCount = vertexData.limit();
        eclipsed = vertexCount == 0; // there are zero vertices being rendered
        
        // bind the data to the vertex handle
        glBindBuffer( GL_ARRAY_BUFFER, vertexHandle );
        glBufferData( GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW );
        
        // bind the data to the color handle
        glBindBuffer( GL_ARRAY_BUFFER, colorHandle );
        glBufferData( GL_ARRAY_BUFFER, colorData, GL_STATIC_DRAW );
        
        glBindBuffer( GL_ARRAY_BUFFER, 0 ); // unbind the buffer
    }
    
    //
    // Getters
    //
    
    /**
     * @return The number of vertices in the mesh.
     */
    public int getVertexCount() {
        return vertexCount;
    }
    
    //
    // Overrides
    //
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(); // efficient way of appending long strings
        
        sb.append( "Chunk @(" ).append( x ).append( ", " ); // add the start tag and x coordinate
        sb.append( y ).append( ", " ); // add the y coordinate
        sb.append( z ).append( ") " ); // add the z coordinate
        sb.append( voxels.toString() ); // add the voxel data
        
        return sb.toString();
    }
    
    //
    // Static
    //
    
    /** The length, width, and height of a chunk. */
    public static final int CHUNK_SIZE     = 16;
    
    /** The number of voxels any given chunk can contain. (CHUNK_SIZE cubed) */
    public static final int VOXEL_CAPACITY = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;
    
    private static int      glMode         = GL_TRIANGLES;                        // the OpenGL mode used while rendering
                                                                                   
    //
    // Methods
    //
    
    /**
     * Changes the way that OpenGL will render the vertices of the chunks.
     * 
     * @param glMode
     *            The new method for rendering vertices of a chunk. The only ones that
     *            should actually be used are {@code GL_POINTS} (for viewing all
     *            vertices), {@code GL_LINES} (for viewing something similar to a
     *            wireframe), or {@code GL_TRIANGLES} (for viewing the actual data).
     */
    public static void setGLMode( int glMode ) {
        Chunk.glMode = glMode;
    }
    
}
