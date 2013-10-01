package com.github.obsidianarch.gvengine;

import static com.github.obsidianarch.gvengine.Chunk.*;
import static com.github.obsidianarch.gvengine.Voxel.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.Color;

public class VoxelInformation {
    
    //
    // Fields
    //
    
    private final boolean xn, xp, yn, yp, zn, zp; // visible faces
    private final int     visibleFaces;
    private final boolean visible;               // if at least one face is visible 
    private final float   x, y, z;               // origin of the voxel
        
    //
    // Constructors
    //
    
    private VoxelInformation(boolean xn, boolean xp, boolean yn, boolean yp, boolean zn, boolean zp, float x, float y, float z) {
        this.xn = xn;
        this.xp = xp;
        this.yn = yn;
        this.yp = yp;
        this.zn = zn;
        this.zp = zp;
        
        int visibleFaces = 0;
        
        // count the number of visible faces
        if ( xn ) visibleFaces++;
        if ( xp ) visibleFaces++;
        if ( yn ) visibleFaces++;
        if ( yp ) visibleFaces++;
        if ( zn ) visibleFaces++;
        if ( zp ) visibleFaces++;
        
        this.visibleFaces = visibleFaces;
        visible = visibleFaces != 0;
        
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    //
    // Getters
    //
    
    /**
     * @return {@code false} if no faces will be rendered, otherwise {@code true}.
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * @return The number of faces that will be rendered.
     */
    public int getVisibleFaceCount() {
        return visibleFaces;
    }
    
    /**
     * @param type
     *            The VoxelType of this voxel.
     * @return The vertex color data for this voxel.
     */
    public FloatBuffer getColors( VoxelType type ) {
        Color c = type.getColor();
        float[] cubeColors = new float[ ] { c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f }; // 255f to get the color values to 0-1f
        FloatBuffer buffer = BufferUtils.createFloatBuffer( visibleFaces * 3 * 3 * 2 ); // faces * 3 (values/color) * 3 (vertices/triangle) * 2 (triangles/face)
        
        for ( int i = 0; i < buffer.limit(); i++ ) {
            buffer.put( cubeColors[ i % cubeColors.length ] ); // add the color data to the point
        }
        
        buffer.flip(); // flip the buffer before returning it
        return buffer;
    }
    
    /**
     * @return The vertices of the voxel.
     */
    public FloatBuffer getVertices() {
        float voxelSize = getVoxelSize();
        FloatBuffer buffer = BufferUtils.createFloatBuffer( visibleFaces * 3 * 3 * 2 ); // faces * 3 (points/vertex) * 3 (vertices/triangle) * 2 (triangles/face)
        
        float xMax = x + voxelSize;
        float yMax = y + voxelSize;
        float zMax = z + voxelSize;
        
        /*
         * All spatial terms are taken from the following view:
         * x: left side
         * xMax: right side
         *  
         * y: bottom side
         * yMax: top side
         *  
         * z: front side
         * zMax: back side
         */
        
        // left face
        if ( xn ) {
            buffer.put( x ).put( y ).put( z ); // Left Bottom Front
            buffer.put( x ).put( y ).put( zMax ); // Left Bottom Back
            buffer.put( x ).put( yMax ).put( z ); // Left Top Front
            
            buffer.put( x ).put( yMax ).put( z ); // Left Top Front
            buffer.put( x ).put( y ).put( zMax ); // Left Bottom Back
            buffer.put( x ).put( yMax ).put( zMax ); // Left Top Back
        }
        
        // right face
        if ( xp ) {
            buffer.put( xMax ).put( y ).put( z ); // right bottom front
            buffer.put( xMax ).put( y ).put( zMax ); // right bottom back
            buffer.put( xMax ).put( yMax ).put( z ); // right top front
            
            buffer.put( xMax ).put( yMax ).put( z ); // right top front
            buffer.put( xMax ).put( y ).put( zMax ); // right bottom back
            buffer.put( xMax ).put( yMax ).put( zMax ); // right top back
        }
        
        // bottom face
        if ( yn ) {
            buffer.put( x ).put( y ).put( z ); // left bottom front
            buffer.put( xMax ).put( y ).put( z ); // right bottom front
            buffer.put( x ).put( y ).put( zMax ); // left bottom back
            
            buffer.put( x ).put( y ).put( zMax ); // left bottom back
            buffer.put( xMax ).put( y ).put( z ); // right bottom front
            buffer.put( xMax ).put( y ).put( zMax ); // right bottom back
        }
        
        // top face
        if ( yp ) {
            buffer.put( x ).put( yMax ).put( z ); // left top front
            buffer.put( xMax ).put( yMax ).put( z ); // right top front
            buffer.put( x ).put( yMax ).put( zMax ); // left top back
            
            buffer.put( x ).put( yMax ).put( zMax ); // left top back
            buffer.put( xMax ).put( yMax ).put( z ); // right top front
            buffer.put( xMax ).put( yMax ).put( zMax ); // right top back
        }
        
        // front face
        if ( zn ) {
            buffer.put( x ).put( y ).put( z ); // left bottom front
            buffer.put( xMax ).put( y ).put( z ); // right bottom front
            buffer.put( x ).put( yMax ).put( z ); // left top front
            
            buffer.put( x ).put( yMax ).put( z ); // left top front
            buffer.put( xMax ).put( y ).put( z ); // right bottom front
            buffer.put( xMax ).put( yMax ).put( z ); // right top front
        }
        
        // back face
        if ( zp ) {
            buffer.put( x ).put( y ).put( zMax ); // left bottom back
            buffer.put( xMax ).put( y ).put( zMax ); // right bottom back
            buffer.put( x ).put( yMax ).put( zMax ); // left top back
            
            buffer.put( x ).put( yMax ).put( zMax ); // left top back
            buffer.put( xMax ).put( y ).put( zMax ); // right bottom back
            buffer.put( xMax ).put( yMax ).put( zMax ); // right top back
        }
        
        buffer.flip();
        return buffer;
    }
    
    //
    // Static
    //
    
    public static VoxelInformation createVoxelInformation( Chunk c, VoxelCollection vc, int ix, int iy, int iz ) {
        boolean xn = checkXNFace( c, vc, ix, iy, iz );
        boolean xp = checkXPFace( c, vc, ix, iy, iz );
        boolean yn = checkYNFace( c, vc, ix, iy, iz );
        boolean yp = checkYPFace( c, vc, ix, iy, iz );
        boolean zn = checkZNFace( c, vc, ix, iy, iz );
        boolean zp = checkZPFace( c, vc, ix, iy, iz );
        
        float x = ( c.x * CHUNK_SIZE * getVoxelSize() ) + ( ix * getVoxelSize() );
        float y = ( c.y * CHUNK_SIZE * getVoxelSize() ) + ( iy * getVoxelSize() );
        float z = ( c.z * CHUNK_SIZE * getVoxelSize() ) + ( iz * getVoxelSize() );
        
        return new VoxelInformation( xn, xp, yn, yp, zn, zp, x, y, z );
    }
    
    //
    // X face calculations
    //
    
    private static boolean checkXNFace( Chunk chunk, VoxelCollection vc, int ix, int iy, int iz ) {
        if ( ix > 0 ) return !vc.getVoxelType( ix - 1, iy, iz ).isActive();
        
        Chunk c = getChunk( chunk, -1, 0, 0 );
        if ( c == null ) return true; // if the chunk provider hasn't kept track of this chunk, we'll draw the face
            
        return !Voxel.getVoxelType( c.getVoxelAt( Chunk.CHUNK_SIZE - 1, iy, iz ) ).isActive(); // if the voxel at the far right side is active
    }
    
    private static boolean checkXPFace( Chunk chunk, VoxelCollection vc, int ix, int iy, int iz ) {
        if ( ix < ( Chunk.CHUNK_SIZE - 1 ) ) return !vc.getVoxelType( ix + 1, iy, iz ).isActive();
        
        Chunk c = getChunk( chunk, 1, 0, 0 );
        if ( c == null ) return true;
        
        return !Voxel.getVoxelType( c.getVoxelAt( ix, Chunk.CHUNK_SIZE - 1, iz ) ).isActive();
    }
    
    //
    // Y face calculations
    //
    
    private static boolean checkYNFace( Chunk chunk, VoxelCollection vc, int ix, int iy, int iz ) {
        if ( iy > 0 ) return !vc.getVoxelType( ix, iy - 1, iz ).isActive();
        
        Chunk c = getChunk( chunk, 0, -1, 0 );
        if ( c == null ) return true;
        
        return !Voxel.getVoxelType( c.getVoxelAt( ix, Chunk.CHUNK_SIZE - 1, iz ) ).isActive();
    }
    
    private static boolean checkYPFace( Chunk chunk, VoxelCollection vc, int ix, int iy, int iz ) {
        if ( iy < ( Chunk.CHUNK_SIZE - 1 ) ) return !vc.getVoxelType( ix, iy + 1, iz ).isActive();
        
        Chunk c = getChunk( chunk, 0, 1, 0 );
        if ( c == null ) return true;
        
        return !Voxel.getVoxelType( c.getVoxelAt( ix, 0, iz ) ).isActive();
    }
    
    //
    // Z face calculations
    //
    
    private static boolean checkZNFace( Chunk chunk, VoxelCollection vc, int ix, int iy, int iz ) {
        if ( iz > 0 ) return !vc.getVoxelType( ix, iy, iz - 1 ).isActive();
        
        Chunk c = getChunk( chunk, 0, 0, -1 );
        if ( c == null ) return true;
        
        return !Voxel.getVoxelType( c.getVoxelAt( ix, iy, Chunk.CHUNK_SIZE - 1 ) ).isActive();
    }
    
    private static boolean checkZPFace( Chunk chunk, VoxelCollection vc, int ix, int iy, int iz ) {
        if ( iz < ( Chunk.CHUNK_SIZE - 1 ) ) return !vc.getVoxelType( ix, iy, iz + 1 ).isActive();
        
        Chunk c = getChunk( chunk, 0, 0, 1 );
        if ( c == null ) return true;
        
        return !Voxel.getVoxelType( c.getVoxelAt( ix, iy, 0 ) ).isActive();
    }
    
    /**
     * Gets a chunk relative to the provided chunk's location.
     * 
     * @param chunk
     * @param dX
     * @param dY
     * @param dZ
     * @return A chunk relative to the provided chunk.
     */
    private static Chunk getChunk( Chunk chunk, int dX, int dY, int dZ ) {
        return chunk.provider.getChunk( chunk.x + dX, chunk.y + dY, chunk.z + dZ );
    }
    
}
