package com.github.obsidianarch.gvengine;

import java.util.Vector;

/**
 * A single point in the world.
 * 
 * @author Austin
 * @see VoxelType
 */
public class Voxel {
    
    //
    // Fields
    //
    
    /** The id for this type of voxel, also the index in the voxel list. */
    public final int       voxelID;
    
    /** General information about a voxel. */
    public final VoxelType voxelType;
    
    //
    // Constructors
    //
    
    /**
     * Creates a new voxel and inserts it into the list at the given voxel id.
     * 
     * @param voxelID
     *            The voxel id for this voxel (also the index in the voxel list).
     * @param voxelType
     *            General information about a voxel.
     * @throws NullPointerException
     *             If {@code Voxel.createVoxelList( int )} has not yet been invoked, or if
     *             voxelType is null.
     * @throws ArrayIndexOutOfBoundsException
     *             If the voxel list's capacity was not large enough for this voxel id.
     * @see #createVoxelList(int)
     */
    public Voxel(int voxelID, VoxelType voxelType) throws NullPointerException, ArrayIndexOutOfBoundsException {
        if ( voxelType == null ) throw new NullPointerException( "voxelType cannot be null!" );
        
        this.voxelID = voxelID;
        this.voxelType = voxelType;
        
        voxels.set( voxelID, this );
    }
    
    //
    // Static
    //
    
    private static Vector< Voxel > voxels;       // stores all of the voxels at their voxelIDs
    private static float           voxelSize = 1; // the size of a voxel (for rendering)      
                                                  
    /**
     * Creates the {@code Vector< Voxel >} with the initial capacity prescribed. The
     * initial capacity <i>must</i> be larger than the largest {@code voxelID}, because
     * the constructor for a Voxel uses the {@code set( voxelID, this )} method when
     * adding itself to this list.<BR>
     * This method must be invoked before a voxel is created.
     * 
     * @param initialCapacity
     *            The initial capacity for the vector.
     */
    public static void createVoxelList( int initialCapacity ) {
        voxels = new Vector< Voxel >( initialCapacity, 1 ); // creates the voxel list with the initial capacity
        
        // adds null to the vector until the initial capacity is reached (this allows for the set method to work)
        for ( int i = 0; i < initialCapacity; i++ ) {
            voxels.add( null );
        }
    }
    
    public static void createVoxel( int voxelID, VoxelType voxelType ) {
        new Voxel( voxelID, voxelType );
    }
    
    /**
     * After the list has been completely populated, calling this will trim the list to
     * the size that it currently is, for memory purposes.
     */
    public static void trimVoxelList() {
        voxels.trimToSize();
    }
    
    /**
     * @param index
     *            The index from which to get a voxel.
     * @return The voxel at the given index in the voxel list.
     */
    public static Voxel getVoxel( int index ) {
        return voxels.get( index );
    }
    
    /**
     * Simplified method of {@code getVoxel( index ).voxelType}.
     * 
     * @param index
     *            The index from which to get a voxel's type.
     * @return The VoxelType for the voxel at the given index.
     */
    public static VoxelType getVoxelType( int index ) {
        return getVoxel( index ).voxelType;
    }
    
    /**
     * This returns a copy of the entire voxel list. This should not be used very often,
     * if at all.
     * 
     * @return A copy of the entire voxel list.
     */
    public static Vector< Voxel > getVoxelList() {
        return new Vector< Voxel >( voxels );
    }
    
    /**
     * @param size
     *            The voxel size (for rendering).
     * @throws IllegalArgumentException
     *             If the new size is less than or equal to (<=) zero (0).
     */
    public static void setVoxelSize( float size ) throws IllegalArgumentException {
        if ( size <= 0 ) throw new IllegalArgumentException( "size cannot be less than or equal to zero (size <= 0)!" );
        voxelSize = size;
    }
    
    /**
     * @return The voxel size (for rendering).
     */
    public static float getVoxelSize() {
        return voxelSize;
    }
    
}
