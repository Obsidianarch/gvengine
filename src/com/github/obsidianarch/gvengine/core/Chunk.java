package com.github.obsidianarch.gvengine.core;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.magicwerk.brownies.collections.primitive.FloatGapList;

import static com.github.obsidianarch.gvengine.core.MathHelper.inRange;

/**
 * A container for a LENGTHxLENGTHxLENGTH selection of voxels.
 *
 * @author Austin
 * @version 15.01.07
 * @since 14.03.30
 */
public class Chunk
{

    //
    // Constants
    //

    /**
     * The length of on side of the chunk.
     */
    public static final int LENGTH = 16;

    /**
     * The area of one face of the chunk.
     */
    public static final int AREA = LENGTH * LENGTH;

    /**
     * The total volume of the chunk.
     */
    public static final int VOLUME = AREA * LENGTH;

    //
    // Fields
    //

    /**
     * The voxels in this chunk.
     */
    private final int[] voxels = new int[ VOLUME ];

    /**
     * The position of this chunk on the chunk grid.
     */
    public final int x;

    /**
     * The position of this chunk on the chunk grid.
     */
    public final int y;

    /**
     * The position of this chunk on the chunk grid.
     */
    public final int z;

    /**
     * The region this chunk is a part of.
     */
    public final Region region;

    /**
     * If the chunk has been loaded yet.
     */
    private boolean loaded = false;

    /**
     * If a rebuild has already been scheduled.
     */
    private boolean rebuildScheduled = false;

    /**
     * The VBO for this chunk.
     */
    private VertexBufferObject vbo = null;

    //
    // Constructors
    //

    /**
     * Creates a chunk at the given chunk coordinates.
     *
     * @param region
     *         The region this chunk is a part of.
     * @param x
     *         The chunk's x coordinate.
     * @param y
     *         The chunk's y coordinate.
     * @param z
     *         The chunk's z coordinate.
     *
     * @since 14.03.30
     */
    public Chunk( Region region, int x, int y, int z )
    {
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
     *
     * @since 14.03.30
     */
    public void load()
    {
        // TODO load the voxel data of this chunk
        loaded = true;
    }

    /**
     * Unloads a chunk from OpenGL by deleting the VertexBufferObject, however the chunk data (coordinates and voxel data) will remain in memory.
     *
     * @since 14.03.30
     */
    public void unload()
    {
        if ( vbo != null )
        {
            vbo.delete();
        }
        loaded = false;
    }

    //
    // OpenGL
    //

    /**
     * Builds the mesh for the chunk.
     *
     * @since 14.03.30
     */
    public void buildMesh()
    {
        if ( vbo != null )
        {
            vbo.delete(); // remove the previous VBO
        }

        FloatGapList positions = new FloatGapList( VOLUME );
        FloatGapList colors = new FloatGapList( VOLUME );
        FloatGapList normals = new FloatGapList( VOLUME );

        for ( int i = 0; i < VOLUME; i++ )
        {
            // get the local positions from i
            int x = MathHelper.getXPosition( i, LENGTH );
            int y = MathHelper.getYPosition( i, LENGTH );
            int z = MathHelper.getZPosition( i, LENGTH );

            Voxel.createVoxel( positions, colors, normals, this, x, y, z );
        }

        vbo = new VertexBufferObject( PositionSystem.XYZ, ColorSystem.RGB, NormalSystem.ENABLED, positions, colors, normals );

        vbo.validate(); // manually validate the VBO
        rebuildScheduled = false;
    }

    /**
     * Renders the VertexBufferObject for this chunk.
     *
     * @since 14.03.30
     */
    public void render()
    {
        if ( vbo == null )
        {
            return; // let's not get errors
        }

        if ( Keyboard.isKeyDown( Keyboard.KEY_1 ) )
        {
            vbo.setGLMode( GL11.GL_POINTS );
        }
        else if ( Keyboard.isKeyDown( Keyboard.KEY_2 ) )
        {
            vbo.setGLMode( GL11.GL_LINES );
        }
        else
        {
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
     * @param id
     *         The id of the material.
     * @param index
     *         The index in the array.
     *
     * @since 14.03.30
     */
    public void setMaterialAt( int id, int index )
    {
        int prev = voxels[ index ];

        if ( prev == id )
        {
            return;
            // no point in setting it to the same thing
            // THIS IS USEFUL (not necassarily for speed in the array setting method, though)
            // Scheduling a rebuild is completely useless and just wastes time and resources
            // if the block at this index has not been changed.
        }

        voxels[ index ] = id;

        if ( vbo == null )
        {
            return; // let's try to avoid some errors
        }

        if ( !rebuildScheduled )
        {
            Scheduler.enqueueEvent( "buildMesh", this );
            rebuildScheduled = true;
        }
    }

    /**
     * Sets the voxel material at the given index.
     *
     * @param mat
     *         The new material.
     * @param index
     *         The index in the array.
     *
     * @since 14.03.30
     */
    public void setMaterialAt( Material mat, int index )
    {
        setMaterialAt( mat.id, index );
    }

    /**
     * Sets the voxel material at the given local position.
     *
     * @param mat
     *         The new material.
     * @param x
     *         The local x position.
     * @param y
     *         The local y position.
     * @param z
     *         The local z position.
     *
     * @since 14.03.30
     */
    public void setMaterialAt( Material mat, int x, int y, int z )
    {
        setMaterialAt( mat.id, x, y, z );
    }

    /**
     * Sets the voxel material at the given local position by the material's byte id.
     *
     * @param id
     *         The id of the material.
     * @param x
     *         The local x position.
     * @param y
     *         The local y position.
     * @param z
     *         The local z position.
     *
     * @since 14.03.30
     */
    public void setMaterialAt( int id, int x, int y, int z )
    {
        // the voxel is out of bounds, divert the set material to the chunk containing the voxel
        if ( !inRange( x, 0, LENGTH ) || !inRange( y, 0, LENGTH ) || !inRange( z, 0, LENGTH ) )
        {
            Object[] data = grabExternalVoxelData( x, y, z );

            Chunk c = ( Chunk ) data[ 0 ]; // get the chunk which contains the voxel

            if ( c == null )
            {
                return; // we can't do anything with this data
            }

            // get the local coordinates
            x = ( int ) data[ 1 ];
            y = ( int ) data[ 2 ];
            z = ( int ) data[ 3 ];

            // set the material in the correct chunk
            c.setMaterialAt( id, x, y, z );
            return;
        }

        setMaterialAt( id, x + ( y * LENGTH ) + ( z * ( AREA ) ) );
    }

    //
    // Getters
    //

    /**
     * Obtains the external voxel data for out of bounds coordinates.
     *
     * @param x
     *         The x coordinate.
     * @param y
     *         The y coordinate.
     * @param z
     *         The z coordinate.
     *
     * @return An object array where {@code [ 0 ] = chunk, [ 1 ] = local x, [ 2 ] = local y, [ 3 ] = local z, and [ 4 ] = material} .
     *
     * @since 14.03.30
     */
    private Object[] grabExternalVoxelData( int x, int y, int z )
    {
        Object[] data = new Object[ 5 ];

        // no region, just return the default voxel
        if ( region == null )
        {
            data[ 0 ] = null;
            data[ 1 ] = Math.abs( x ) % LENGTH;
            data[ 2 ] = Math.abs( y ) % LENGTH;
            data[ 3 ] = Math.abs( z ) % LENGTH;
            data[ 4 ] = null;
            return data;
        }

        int xOff = ( int ) Math.floor( x / ( double ) LENGTH );
        int yOff = ( int ) Math.floor( y / ( double ) LENGTH );
        int zOff = ( int ) Math.floor( z / ( double ) LENGTH );

        x %= LENGTH;
        y %= LENGTH;
        z %= LENGTH;

        if ( x < 0 )
        {
            x += LENGTH;
        }
        if ( y < 0 )
        {
            y += LENGTH;
        }
        if ( z < 0 )
        {
            z += LENGTH;
        }

        Chunk c = region.getChunkAt( this.x + xOff, this.y + yOff, this.z + zOff );
        data[ 0 ] = c;

        data[ 1 ] = x;
        data[ 2 ] = y;
        data[ 3 ] = z;

        if ( c != null )
        {
            data[ 4 ] = c.getMaterialAt( x, y, z );
        }
        else
        {
            data[ 4 ] = null; // no voxel, return the default one
        }

        return data;
    }

    /**
     * @return The number of vertices in the chunk.
     */
    public int getVertexCount()
    {
        return vbo.getCoordinates().size();
    }

    /**
     * @return If the chunk has been loaded or not.
     */
    public boolean isLoaded()
    {
        return loaded;
    }

    /**
     * Gets the material at the given <b>local</b> position.
     *
     * @param x
     *         The local x position.
     * @param y
     *         The local y position.
     * @param z
     *         The local z position.
     *
     * @return The material at the local position.
     *
     * @since 14.03.30
     */
    public Material getMaterialAt( int x, int y, int z )
    {
        if ( !inRange( x, 0, LENGTH ) || !inRange( y, 0, LENGTH ) || !inRange( z, 0, LENGTH ) )
        {
            return ( Material ) grabExternalVoxelData( x, y, z )[ 4 ];
        }

        Material mat = Material.getMaterial( voxels[ x + ( y * LENGTH ) + ( z * AREA ) ] ); // get the material
        return mat == null ? Material.getMaterial( 0 ) : mat; // return the default type if the material could not be found, otherwise the material
    }

    /**
     * Returns the voxel material ids in this chunk.
     *
     * @return The voxel material ids in this chunk.
     *
     * @since 14.03.30
     */
    public int[] getVoxels()
    {
        return voxels;
    }

    /**
     * @return The global offsets of every voxel's in the chunk.
     *
     * @since 14.04.12
     */
    public double[] getGlobalOffset()
    {
        double[] offset = new double[ 3 ];
        offset[ 0 ] = ( x * LENGTH ) + ( region == null ? 0 : region.x * Region.LENGTH );
        offset[ 1 ] = ( y * LENGTH ) + ( region == null ? 0 : region.y * Region.LENGTH );
        offset[ 2 ] = ( z * LENGTH ) + ( region == null ? 0 : region.z * Region.LENGTH );
        return offset;
    }

    //
    // Voxel Visibility
    //

    /**
     * Determines if a voxel's specific face is visible or not.
     *
     * @param face
     *         The face.
     * @param x
     *         The x coordinate of the voxel, in this chunk.
     * @param y
     *         The y coordinate of the voxel, in this chunk.
     * @param z
     *         The z coordinate of the voxel, in this chunk.
     *
     * @return If the voxel's face is visible.
     *
     * @since 14.03.31
     */
    public boolean isVisible( Face face, int x, int y, int z )
    {
        int[] touchingCoords = face.getTouchingVoxel( x, y, z ); // screw the word "coordiantes"

        int tX = touchingCoords[ 0 ];
        int tY = touchingCoords[ 1 ];
        int tZ = touchingCoords[ 2 ];

        Material material = getMaterialAt( tX, tY, tZ );
        if ( material == null || material.active )
        {
            return false; // the material this face's touching is active, therefore we don't need to render this face
        }

        // TODO follow a trail of faces to determine if the area is enclosed or not

        return true;
    }

    /**
     * Gets all the visible faces for the provided voxel.
     *
     * @param x
     *         The x coordinate of the voxel, in this chunk.
     * @param y
     *         The y coordinate of the voxel, in this chunk.
     * @param z
     *         The z coordinate of the voxel, in this chunk.
     *
     * @return The visibility of the faces (the faces are ordered as the values of the face).
     *
     * @since 14.03.31
     */
    public boolean[] getVisibleFaces( int x, int y, int z )
    {
        boolean[] faces = new boolean[ 6 ];

        for ( int i = 0; i < faces.length; i++ )
        {
            faces[ Face.values()[ i ].value ] = isVisible( Face.values()[ i ], x, y, z );
        }

        return faces;
    }

    /**
     * Checks to see if the voxel is completely eclipsed, covered by an active material from all sides.
     *
     * @param x
     *         The x coordinate of the voxel, in this chunk.
     * @param y
     *         The y coordinate of the voxel, in this chunk.
     * @param z
     *         The z coordinate of the voxel, in this chunk.
     *
     * @return If the voxel is completely eclipsed.
     *
     * @since 14.03.30
     */
    public boolean isEclipsed( int x, int y, int z )
    {
        boolean[] visible = getVisibleFaces( x, y, z ); // get the visibility for every face

        for ( boolean b : visible )
        {
            if ( b )
            {
                return false; // if ANY face on this voxel is visible, it is not eclipsed
            }
        }

        return true; // no faces are visible; it's eclipsed
    }

    /**
     * Determines if the voxel is renderable or not. This will check first to see if the voxel's material is visible, then it will check to see if the voxel is
     * completely eclipsed or not.
     *
     * @param x
     *         The x coordinate of the voxel, in this chunk.
     * @param y
     *         The y coordinate of the voxel, in this chunk.
     * @param z
     *         The z coordinate of the voxel, in this chunk.
     *
     * @return If the voxel is renderable or not.
     *
     * @since 14.03.31
     */
    public boolean isRenderable( int x, int y, int z )
    {
        return getMaterialAt( x, y, z ).active && !isEclipsed( x, y, z );
    }

    //
    // Overrides
    //

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "CHUNK@" );
        sb.append( x ).append( ", " );
        sb.append( y ).append( ", " );
        sb.append( z ).append( ") = { " );

        for ( int b : voxels )
        {
            sb.append( ( int ) b ).append( " " );
        }

        sb.append( "}" );

        return sb.toString();
    }

}
