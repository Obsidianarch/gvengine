package com.github.obsidianarch.gvengine;

import static com.github.obsidianarch.gvengine.core.MathHelper.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.magicwerk.brownies.collections.primitive.FloatGapList;

import com.github.obsidianarch.gvengine.core.ColorSystem;
import com.github.obsidianarch.gvengine.core.MathHelper;
import com.github.obsidianarch.gvengine.core.NormalSystem;
import com.github.obsidianarch.gvengine.core.PositionSystem;
import com.github.obsidianarch.gvengine.core.Scheduler;
import com.github.obsidianarch.gvengine.core.VertexBufferObject;

/**
 * A container for a 16x16x16 selection of voxels.
 * 
 * @author Austin
 */
public class Chunk {
    
    //
    // Fields
    //
    
    /** The voxels in this chunk. */
    private final byte[]       voxels           = new byte[ 4096 ];
    
    /** The position of this chunk on the chunk grid. */
    public final int           x;
    
    /** The position of this chunk on the chunk grid. */
    public final int           y;
    
    /** The position of this chunk on the chunk grid. */
    public final int           z;
    
    /** The region this chunk is a part of. */
    public final Region        region;
    
    /** If the chunk has been loaded yet. */
    private boolean            loaded           = false;
    
    /** If a rebuild has already been scheduled. */
    private boolean            rebuildScheduled = false;
    
    /** The VBO for this chunk. */
    private VertexBufferObject vbo              = null;
    
    //
    // Constructors
    //
    
    /**
     * Creates a chunk at the given chunk coordinates.
     * 
     * @param region
     *            The region this chunk is a part of.
     * @param x
     *            The chunk's x coordinate.
     * @param y
     *            The chunk's y coordinate.
     * @param z
     *            The chunk's z coordinate.
     */
    public Chunk( Region region, int x, int y, int z ) {
        this.region = region;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    //
    // Load/Unload
    //
    
    /**
     * Loads the voxel data of this chunk.
     */
    public void load() {
        // TODO load the voxel data of this chunk
        loaded = true;
    }
    
    /**
     * Unloads a chunk from OpenGL by deleting the VertexBufferObject, however the chunk
     * data (coordinates and voxel data) will remain in memory.
     */
    public void unload() {
        if ( vbo != null ) vbo.delete();
        loaded = false;
    }
    
    /**
     * Saves the chunk as a file.
     */
    public void save() {
        throw new UnsupportedOperationException( "This will be implemented one of these days" );
    }
    
    //
    // OpenGL
    //
    
    /**
     * Builds the mesh for the chunk.
     */
    public void buildMesh() {
        if ( vbo != null ) {
            vbo.delete(); // remove the previous VBO
        }
        
        FloatGapList positions = new FloatGapList( 0 );
        FloatGapList colors = new FloatGapList( 0 );
        
        for ( int i = 0; i < 4096; i++ ) {
            // get the local positions from i
            int x = MathHelper.getXPosition( i, 16 );
            int y = MathHelper.getYPosition( i, 16 );
            int z = MathHelper.getZPosition( i, 16 );
            
            Voxel.createVoxel( positions, colors, this, x, y, z );
        }
        
        vbo = new VertexBufferObject( PositionSystem.XYZ, ColorSystem.RGB, NormalSystem.DISABLED, positions, colors, null );
        
        vbo.validate(); // manually validate the VBO
        rebuildScheduled = false;
    }
    
    /**
     * Renders the VertexBufferObject for this chunk.
     */
    public void render() {
        if ( vbo == null ) return; // let's not get errors
            
        if ( Keyboard.isKeyDown( Keyboard.KEY_1 ) ) {
            vbo.setGLMode( GL11.GL_POINTS );
        }
        else if ( Keyboard.isKeyDown( Keyboard.KEY_2 ) ) {
            vbo.setGLMode( GL11.GL_LINES );
        }
        else {
            vbo.setGLMode( GL11.GL_TRIANGLES );
        }
        
        vbo.render();
    }
    
    //
    // Setters
    //
    
    /**
     * Sets the voxel material at the given index by the material's byte id.
     * 
     * @param b
     *            The byte id of the material.
     * @param index
     *            The index in the array.
     */
    public void setMaterialAt( byte b, int index ) {
        byte prev = voxels[ index ];
        
        if ( prev == b ) {
            return;
            // no point in setting it to the same thing
            // THIS IS USEFUL (not necassarily for speed in the array setting method, though)
            // Scheduling a rebuild is completely useless and just wastes time and resources
            // if the block at this index has not been changed, this was originally implemented
            // in the if statement below, however implementing it here makes allows for a tiny
            // tiny performance boost as we don't have to set an element in the array
        }
        
        voxels[ index ] = b;
        
        if ( vbo == null ) return; // let's try to avoid some errors
            
        if ( !rebuildScheduled ) {
            Scheduler.enqueueEvent( "buildMesh", this );
            rebuildScheduled = true;
        }
    }
    
    /**
     * Sets the voxel material at the given index.
     * 
     * @param mat
     *            The new material.
     * @param index
     *            The index in the array.
     */
    
    public void setMaterialAt( Material mat, int index ) {
        setMaterialAt( mat.byteID, index );
    }
    
    /**
     * Sets the voxel material at the given local position.
     * 
     * @param mat
     *            The new material.
     * @param x
     *            The local x position.
     * @param y
     *            The local y position.
     * @param z
     *            The local z position.
     */
    public void setMaterialAt( Material mat, int x, int y, int z ) {
        setMaterialAt( mat.byteID, x, y, z );
    }
    
    /**
     * Sets the voxel material at the given local position by the material's byte id.
     * 
     * @param b
     *            The byte id of the material.
     * @param x
     *            The local x position.
     * @param y
     *            The local y position.
     * @param z
     *            The local z position.
     */
    public void setMaterialAt( byte b, int x, int y, int z ) {
        // the voxel is out of bounds, divert the set material to the chunk containing the voxel
        if ( !inRange( x, 0, 15 ) || !inRange( y, 0, 15 ) || !inRange( z, 0, 15 ) ) {
            Object[] data = grabExternalVoxelData( x, y, z );
            
            Chunk c = ( Chunk ) data[ 0 ]; // get the chunk which contains the voxel
            
            if ( c == null ) return; // we can't do anything with this data
                
            // get the local coordinates
            x = ( int ) data[ 1 ];
            y = ( int ) data[ 2 ];
            z = ( int ) data[ 3 ];
            
            // set the material in the correct chunk
            c.setMaterialAt( b, x, y, z );
            return;
        }
        
        setMaterialAt( b, x + ( y * 16 ) + ( z * 256 ) );
    }
    
    //
    // Getters
    //
    
    /**
     * Obtains the external voxel data for out of bounds coordinates.
     * 
     * @param x
     *            The x coordinate.
     * @param y
     *            The y coordinate.
     * @param z
     *            The z coordinate.
     * @return An object array where
     *         {@code [ 0 ] = chunk, [ 1 ] = local x, [ 2 ] = local y, [ 3 ] = local z, and [ 4 ] = material}
     *         .
     */
    private Object[] grabExternalVoxelData( int x, int y, int z ) {
        Object[] data = new Object[ 5 ];
        if ( region == null ) {
            data[ 0 ] = null;
            data[ 1 ] = Math.abs( x ) % 16;
            data[ 2 ] = Math.abs( y ) % 16;
            data[ 3 ] = Math.abs( z ) % 16;
            data[ 4 ] = Material.AIR;
            return data;
        }
        
        int xOff = ( int ) Math.floor( x / 16.0 );
        int yOff = ( int ) Math.floor( y / 16.0 );
        int zOff = ( int ) Math.floor( z / 16.0 );
        
        x %= 16;
        y %= 16;
        z %= 16;
        
        if ( x < 0 ) x += 16;
        if ( y < 0 ) y += 16;
        if ( z < 0 ) z += 16;
        
        Chunk c = region.getChunkAt( this.x + xOff, this.y + yOff, this.z + zOff );
        data[ 0 ] = c;
        
        data[ 1 ] = x;
        data[ 2 ] = y;
        data[ 3 ] = z;
        
        if ( c != null ) {
            data[ 4 ] = c.getMaterialAt( x, y, z );
        }
        else {
            data[ 4 ] = Material.AIR;
        }
        
        return data;
    }
    
    /**
     * @return The number of vertices in the chunk.
     */
    public int getVertexCount() {
        return vbo.getCoordinates().size();
    }
    
    /**
     * @return If the chunk has been loaded or not.
     */
    public boolean isLoaded() {
        return loaded;
    }
    
    /**
     * Gets the material at the given <b>local</b> position.
     * 
     * @param x
     *            The local x position.
     * @param y
     *            The local y position.
     * @param z
     *            The local z position.
     * @return The material at the local position.
     */
    public Material getMaterialAt( int x, int y, int z ) {
        if ( !inRange( x, 0, 15 ) || !inRange( y, 0, 15 ) || !inRange( z, 0, 15 ) ) {
            return ( Material ) grabExternalVoxelData( x, y, z )[ 4 ];
        }
        
        Material mat = Material.getMaterial( voxels[ x + ( y * 16 ) + ( z * 256 ) ] ); // get the material
        return mat == null ? Material.AIR : mat; // return AIR if the material could not be found, otherwise the material
    }
    
    /**
     * Checks to see if the voxel at the given location should be rendered.<BR>
     * This algorithm checks the following items, in the following order:<BR>
     * 1. Is the material at the position active?<BR>
     * 2. Is the voxel eclipsed?<BR>
     * 
     * @param x
     *            The x coordinate of the voxel, in this chunk.
     * @param y
     *            The y coordinate of the voxel, in this chunk.
     * @param z
     *            the z coordinate of the voxel, in this chunk.
     * @return If the voxel should be rendered or not.
     */
    public boolean shouldBeRendered( int x, int y, int z ) {
        if ( !getMaterialAt( x, y, z ).active ) return false; // the material is not active, it shouldn't be rendered
        return !isEclipsed( x, y, z ); // all neighbors are rendered
    }
    
    /**
     * Checks to see if the voxel is completely eclipsed, covered by an active material
     * from all sides.
     * 
     * @param x
     *            The x coordinate of the voxel, in this chunk.
     * @param y
     *            The y coordinate of the voxel, in this chunk.
     * @param z
     *            The z coordinate of the voxel, in this chunk.
     * @return If the voxel is completely eclipsed.
     */
    public boolean isEclipsed( int x, int y, int z ) {
        if ( !getMaterialAt( x - 1, y, z ).active ) return false; // the material to the left of this isn't active
        if ( !getMaterialAt( x + 1, y, z ).active ) return false; // the material to the right of this isn't active
            
        if ( !getMaterialAt( x, y - 1, z ).active ) return false; // the material below this isn't active
        if ( !getMaterialAt( x, y + 1, z ).active ) return false; // the material above this isn't active
            
        if ( !getMaterialAt( x, y, z - 1 ).active ) return false; // the material in front of this isn't active
        if ( !getMaterialAt( x, y, z + 1 ).active ) return false; // the material behind this isn't active
            
        return true;
    }
    
    /**
     * Returns the voxel material ids in this chunk.
     * 
     * @return The voxel material ids in this chunk.
     */
    public byte[] getVoxels() {
        return voxels;
    }
    
    /**
     * @param x
     * @param y
     * @param z
     * @return The global offsets for the local voxel position as a float array, where
     *         index zero is the x position, one is the y, and two the z.
     */
    public float[] getGlobalOffset( int x, int y, int z ) {
        float[] global = new float[ 3 ];
        global[ 0 ] = x + ( this.x * 16 ) + ( region.x * 8 );
        global[ 1 ] = y + ( this.y * 16 ) + ( region.y * 8 );
        global[ 2 ] = z + ( this.z * 16 ) + ( region.z * 8 );
        return global;
    }

    //
    // Overrides
    //
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "CHUNK@(" + x + ", " + y + ", " + z + ") = { " );
        
        for ( byte b : voxels ) {
            sb.append( ( int ) b ).append( " " );
        }
        
        sb.append( "}" );
        
        return sb.toString();
    }
    
}
