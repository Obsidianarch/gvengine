package com.addonovan.gvengine;

import com.addonovan.gvengine.core.Scheduler;

import static com.addonovan.gvengine.core.MathHelper.inRange;

/**
 * A LENGTH x LENGTH x LENGTH container of Chunks.
 *
 * @author Austin
 * @version 14.04.12
 * @since 14.03.30
 */
public class Region
{

    //
    // Constants
    //

    /**
     * The length of one side of the region (measured in chunks).
     */
    public static final int LENGTH = 4;

    /**
     * The area of one face of the region.
     */
    public static final int AREA = LENGTH * LENGTH;

    /**
     * The total number of chunks a region can hold.
     */
    public static final int VOLUME = AREA * LENGTH;

    //
    // Fields
    //

    /**
     * An array of all the chunks in this region.
     */
    public Chunk[] chunks = new Chunk[ VOLUME ];

    /**
     * The region's x coordinate.
     */
    public final int x;

    /**
     * The region's y coordinate.
     */
    public final int y;

    /**
     * The region's z coordinate.
     */
    public final int z;

    /**
     * Responsible for generating every chunk in this region.
     */
    private final ChunkGenerator generator;

    //
    // Constructors
    //

    /**
     * Creates a new region.
     *
     * @param generator
     *         The chunk generator used to generate this region's chunks.
     * @param x
     *         The x coordiante of the region in the region grid.
     * @param y
     *         The y coordinate of the region in the region grid.
     * @param z
     *         The z coordinate of the region in the region grid.
     *
     * @since 14.03.30
     */
    public Region( ChunkGenerator generator, int x, int y, int z )
    {
        this.generator = generator;

        this.x = x;
        this.y = y;
        this.z = z;

        for ( int cX = 0; cX < LENGTH; cX++ )
        {
            for ( int cY = 0; cY < LENGTH; cY++ )
            {
                for ( int cZ = 0; cZ < LENGTH; cZ++ )
                {
                    Chunk c = new Chunk( this, cX, cY, cZ ); // create the chunk
                    generator.generateChunk( c ); // generate teh chunk's voxels

                    int index = cX;
                    index += cY * LENGTH;
                    index += cZ * 16;
                    chunks[ index ] = c; // add the chunk to the region
                }
            }
        }
    }

    //
    // Actions
    //

    /**
     * Regenerates all chunks based on the current seed.
     *
     * @since 14.03.30
     */
    public void regenerate()
    {
        for ( int i = 0; i < chunks.length; i++ )
        {
            // rebuilding takes a longer time than rebuilding, so it fires LENGTH times less often
            Scheduler.scheduleEvent( "generateChunk", generator, i * 100, chunks[ i ] );
        }
    }

    /**
     * Schedules rebuilds for every chunk in this region.
     *
     * @since 14.03.30
     */
    public void rebuild()
    {
        for ( int i = 0; i < chunks.length; i++ )
        {
            Scheduler.scheduleEvent( "buildMesh", chunks[ i ], i * 25 ); // a chunk in this region is rebuilt every 10 milliseconds 
        }
    }

    /**
     * Renders every chunk in this region.
     *
     * @since 14.03.30
     */
    public void render()
    {
        for ( Chunk c : chunks )
        {
            c.render();
        }
    }

    //
    // Getters
    //

    /**
     * Returns the chunk at the given local positions.
     *
     * @param x
     *         The x coordinate of the chunk.
     * @param y
     *         The y coordinate of the chunk.
     * @param z
     *         The z coordinate of the chunk.
     *
     * @return The chunk at the given local positions.
     *
     * @since 14.03.30
     */
    public Chunk getChunkAt( int x, int y, int z )
    {
        if ( !inRange( x, 0, LENGTH ) || !inRange( y, 0, LENGTH ) || !inRange( z, 0, LENGTH ) )
        {
            return null; // TODO change this to something else later
        }

        int index = x;
        index += ( y * LENGTH );
        index += ( z * AREA );

        return chunks[ index ];
    }

    /**
     * Returns the chunks in this region.
     *
     * @return The chunks in this region.
     */
    public Chunk[] getChunks()
    {
        return chunks;
    }

}
