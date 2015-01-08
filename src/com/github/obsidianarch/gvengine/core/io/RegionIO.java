package com.github.obsidianarch.gvengine.core.io;

import com.github.obsidianarch.gvengine.core.Chunk;
import com.github.obsidianarch.gvengine.core.Region;
import com.github.obsidianarch.gvengine.core.Scheduler;

import java.io.*;

/**
 * Contains the reading and writing to regions files.
 *
 * @author Austin
 * @version 14.02.08
 * @since 14.03.30
 */
public final class RegionIO
{

    /**
     * Ensures the the file is created before continuing in a method.
     *
     * @param f
     *         The file to create.
     *
     * @return If the file was created.
     *
     * @since 14.03.30
     */
    private static boolean createFile( File f )
    {
        try
        {
            // create the required directories if they don't exist
            if ( !f.getParentFile().exists() )
            {
                boolean created = f.getParentFile().mkdirs(); // create the directories

                // throw an exception because the path couldn't be created
                if ( !created )
                {
                    throw new IOException( String.format( "Location %s could not be created!", f.getParentFile().getAbsoluteFile() ) );
                }
            }

            // create the file if it doesn't already exist
            if ( !f.exists() )
            {
                boolean created = f.createNewFile(); // attempt to create the file

                // throw an exception because the file couldn't be created
                if ( !created )
                {
                    throw new IOException( String.format( "File %s could not be created!", f.getAbsolutePath() ) );
                }
            }

            return true; // the file was successfully created
        }
        catch ( Exception e )
        {
            Lumberjack.getInstance( RegionIO.class ).throwable( e );
            return false; // the file could not be created
        }
    }

    /**
     * Writes a chunk to the correct output stream.
     *
     * @param dos
     *         The output stream to write it to.
     * @param coordinates
     *         The chunk's coordinates.
     * @param voxels
     *         The voxel data.
     *
     * @since 14.03.30
     */
    public static void writeChunk( DataOutputStream dos, int[] coordinates, byte[] voxels )
    {
        try
        {
            for ( int i : coordinates )
            {
                dos.writeInt( i );
            }
            dos.flush();

            for ( byte b : voxels )
            {
                dos.writeByte( b );
            }
            dos.flush();
        }
        catch ( IOException e )
        {
            Lumberjack.getInstance( RegionIO.class ).throwable( e );
        }
    }

    /**
     * Saves the region into the given directory.
     *
     * @param region
     *         The region to write.
     * @param dir
     *         The directory to write to.
     *
     * @since 14.03.30
     */
    public static void saveRegion( Region region, File dir )
    {
        File f = new File( dir, String.format( "r%d %d %d.gven.dat", region.x, region.y, region.z ) );

        if ( !createFile( f ) )
        {
            return;
        }

        Chunk[] chunks = region.getChunks();

        int[][] chunkCoords = new int[ chunks.length ][ 3 ];
        int[][] voxels = new int[ chunks.length ][ chunks[ 0 ].getVoxels().length ];

        for ( int i = 0; i < chunks.length; i++ )
        {
            chunkCoords[ i ][ 0 ] = chunks[ i ].x;
            chunkCoords[ i ][ 1 ] = chunks[ i ].y;
            chunkCoords[ i ][ 2 ] = chunks[ i ].z;

            for ( int j = 0; j < voxels[ i ].length; j++ )
            {
                voxels[ i ][ j ] = chunks[ i ].getVoxels()[ j ];
            }
        }

        try
        {
            DataOutputStream dos = new DataOutputStream( new FileOutputStream( f ) );

            // schedule writes for each chunk
            for ( int i = 0; i < chunks.length; i++ )
            {
                Scheduler.enqueueEvent( "writeChunk", RegionIO.class, dos, chunkCoords[ i ], voxels[ i ] );
            }

            // scheduler the closing of the stream after all the writes
            Scheduler.enqueueEvent( "close", dos ); // closes the stream after everything has been written
        }
        catch ( Exception e )
        {
            Lumberjack.getInstance( RegionIO.class ).throwable( e );
        }
    }

    /**
     * Reads the region from it's file directory.
     *
     * @param region
     *         The region to read.
     * @param dir
     *         The directory where the region file is.
     *
     * @return If the region was loaded properly or not.
     *
     * @since 14.03.30
     */
    public static boolean loadRegion( Region region, File dir )
    {
        File f = new File( dir, String.format( "r%d %d %d.gven.dat", region.x, region.y, region.z ) );

        if ( !createFile( f ) )
        {
            return false;
        }

        try ( DataInputStream dis = new DataInputStream( new FileInputStream( f ) ) )
        {

            // read each chunk
            for ( int i = 0; i < region.chunks.length; i++ )
            {

                // construct the chunk
                Chunk c = new Chunk( region, dis.readInt(), dis.readInt(), dis.readInt() ); // create a chunk based on the chunk's position data

                // read the materials
                for ( int j = 0; j < 4096; j++ )
                {
                    c.setMaterialAt( dis.readByte(), j ); // read the voxel id
                }

                region.chunks[ i ] = c; // set the chunk value
            }
        }
        catch ( Exception e )
        {
            Lumberjack.getInstance( RegionIO.class ).throwable( e );
            return false;
        }

        return true;
    }

}
