package com.github.obsidianarch.gvengine;

import static com.github.obsidianarch.gvengine.Chunk.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * An {@code ArrayList} whose main function is to store voxels for a chunk. Adding useful
 * methods for getting/setting a voxel when given it's coordinates.
 * 
 * @author Austin
 */
public class VoxelCollection extends ArrayList< Integer > {
    
    private final Chunk      chunk;            // the chunk whose data this contains
    private VoxelInformation info;             // the rendering information of a currently bound voxel
    private int              boundIndex   = -1; // the index of the currently bound voxel
                                                
    private int              currentIndex = -1;
    
    /**
     * Creates a new VoxelCollection to store data in a chunk. Has a capacity of
     * {@code CHUNK_SIZE_CUBED}, which cannot be exceeded.
     * 
     * @param c
     *            The chunk whose data this list contains.
     */
    public VoxelCollection(Chunk c) {
        super( VOXEL_CAPACITY );
        
        chunk = c;
        
        for ( int i = 0; i < VOXEL_CAPACITY; i++ ) {
            add( 0 );
        }
        trimToSize();
    }
    
    /**
     * Sets the voxel at the given index by performing the operation
     * {@code MathHelper.getIndex( x, y, z, CHUNK_SIZE )}, and then sets the value
     * at the index to {@code value}.
     * 
     * @param x
     *            The x coordinate of the voxel to replace.
     * @param y
     *            The y coordinate of the voxel to replace.
     * @param z
     *            The z coordinate of the voxel to replace.
     * @param value
     *            The voxel id for the coordinate.
     * @return The previous voxel id using the location.
     */
    public Integer set( int x, int y, int z, int value ) {
        return set( MathHelper.getIndex( x, y, z, CHUNK_SIZE ), ( Integer ) value );
    }
    
    /**
     * Gets the voxel at the given index by performing the operation
     * {@code MathHelper.getIndex( x, y, z, CHUNK_SIZE )}.
     * 
     * @param x
     *            The x coordinate of the voxel.
     * @param y
     *            The y coordinate of the voxel.
     * @param z
     *            The z coordinate of the voxel.
     * @return The voxel id for the voxel at the given coordinates.
     */
    public Integer get( int x, int y, int z ) {
        return get( MathHelper.getIndex( x, y, z, CHUNK_SIZE ) );
    }
    
    /**
     * Gets the voxel type of the voxel at the given position.<BR>
     * Convenience method for {@code Voxel.getVoxelType( get( x, y, z ) )}.
     * 
     * @param x
     *            The x coordinate of the voxel.
     * @param y
     *            The y coordinate of the voxel.
     * @param z
     *            The z coordinate of the voxel.
     * @return The VoxelType for the voxel at the given coordinates.
     * 
     * @see VoxelType
     * @see Voxel#getVoxelType(int)
     * @see #get(int, int, int)
     */
    public VoxelType getVoxelType( int x, int y, int z ) {
        return Voxel.getVoxelType( get( x, y, z ) );
    }
    
    /**
     * Generates and binds the voxel information. Must be called before getting vertices
     * or vertex colors.
     * 
     * @param x
     *            The x coordinate of the voxel.
     * @param y
     *            The y coordinate of the voxel.
     * @param z
     *            The z coordiante of the voxel.
     * @see VoxelInformation
     * @see #getVertices(int, int, int)
     * @see #getVertexColors(int, int, int)
     */
    public void bindVoxelInformation( int x, int y, int z ) {
        info = VoxelInformation.createVoxelInformation( chunk, this, x, y, z );
        boundIndex = MathHelper.getIndex( x, y, z, Chunk.CHUNK_SIZE );
    }
    
    /**
     * Gets the color vertices to render for the currently bound voxel.
     * 
     * @return The color data to render for the currently bound voxel.
     * @see #bindVoxelInformation(int, int, int)
     */
    public FloatBuffer getVertexColors() {
        return info.getColors( Voxel.getVoxelType( get( boundIndex ) ) );
    }
    
    /**
     * Gets the vertices to render for the currently bound voxel.
     * 
     * @return The vertices to render for the currently bound voxel.
     * @see #bindVoxelInformation(int, int, int)
     */
    public FloatBuffer getVertices() {
        return info.getVertices();
    }
    
    /**
     * Checks if the currently bound voxel is visible.
     * 
     * @return {@code false} if the voxel will not be rendered at all, otherwise true.
     * @see #bindVoxelInformation(int, int, int)
     */
    public boolean isVoxelVisible() {
        return info.isVisible();
    }
    
    //
    // Overrides
    //
    
    @Override
    public boolean add( Integer e ) {
        if ( size() == VOXEL_CAPACITY ) throw new ArrayIndexOutOfBoundsException( "A VoxelCollection can only contain " + VOXEL_CAPACITY + " elements!" );
        
        return super.add( e );
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(); // just because this method is rarely used, doesn't mean we can't be efficient!
        
        sb.append( "voxelIDs={ " ); // add the starting data
        
        for ( Integer voxelID : this ) {
            sb.append( voxelID ).append( ", " ); // add the voxel data point
        }
        
        sb.substring( 0, sb.length() - 2 ); // remove the last two characters (", ")
        sb.append( " }" ); // add the ending brace, this is VERY important!
        
        return sb.toString(); // create a string from this
    }
}
