package com.github.obsidianarch.gvengine;

import static com.github.obsidianarch.gvengine.core.MathHelper.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.magicwerk.brownies.collections.primitive.FloatGapList;

import com.github.obsidianarch.gvengine.core.ColorSystem;
import com.github.obsidianarch.gvengine.core.MathHelper;
import com.github.obsidianarch.gvengine.core.NormalSystem;
import com.github.obsidianarch.gvengine.core.PositionSystem;
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
    private final byte[]       voxels = new byte[ 4096 ];
    
    /** The position of this chunk on the chunk grid. */
    public final int           x;
    
    /** The position of this chunk on the chunk grid. */
    public final int           y;
    
    /** The position of this chunk on the chunk grid. */
    public final int           z;
    
    /** The region this chunk is a part of. */
    public final Region        region;
    
    /** If the chunk has been loaded yet. */
    private boolean            loaded = false;
    
    /** The VBO for this chunk. */
    private VertexBufferObject vbo    = null;
    
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
    }
    
    /**
     * Renders the VertexBufferObject for this chunk.
     */
    public void render() {
        if ( vbo == null ) {
            return; // let's not get errors
        }
        
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
     * Sets the voxel material at the given index by the material's byte id.
     * 
     * @param b
     *            The byte id of the material.
     * @param index
     *            The index in the array.
     */
    public void setMaterialAt( byte b, int index ) {
        voxels[ index ] = b;
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
        setMaterialAt( b, x + ( y * 16 ) + ( z * 256 ) );
    }
    
    //
    // Getters
    //
    
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
            if ( region == null ) return Material.AIR;
            
            int xOff = ( int ) Math.floor( x / 16.0 ); // get the x chunk offset
            int yOff = ( int ) Math.floor( y / 16.0 ); // get the y chunk offset
            int zOff = ( int ) Math.floor( z / 16.0 ); // get the z chunk offset
            
            // get the new local positions of the voxel
            x %= 16;
            y %= 16;
            z %= 16;
            
            // if the local position is less than 0, we need to add 16
            if ( x < 0 ) x += 16;
            if ( y < 0 ) y += 16;
            if ( z < 0 ) z += 16;
            
            Chunk c = region.getChunkAt( this.x + xOff, this.y + yOff, this.z + zOff ); // get the chunk at the chunk offset
            
            if ( c != null ) { // if the chunk isn't null
                return c.getMaterialAt( x, y, z ); // return the material at it's position
            }
            else { // if the chunk is null
                return Material.AIR; // return air
            }
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
