package com.github.obsidianarch.gvengine.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.github.obsidianarch.gvengine.Chunk;
import com.github.obsidianarch.gvengine.Region;
import com.github.obsidianarch.gvengine.core.Scheduler;

/**
 * Contains the reading and writing to regions files.
 * 
 * @author Austin
 */
public final class RegionIO {
    
    /**
     * Ensures the the file is created before continuing in a method.
     * 
     * @param f
     *            The file to create.
     * @return If the file was created.
     */
    private static boolean createFile( File f ) {
        try {
            f.getParentFile().mkdirs();
            
            while ( !f.exists() )
                f.createNewFile();

            return true;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Writes a chunk to the correct output stream.
     * 
     * @param dos
     *            The output stream to write it to.
     * @param coordinates
     *            The chunk's coordinates.
     * @param voxels
     *            The voxel data.
     */
    public static void writeChunk( DataOutputStream dos, int[] coordinates, byte[] voxels ) {
        try {
            for ( int i = 0; i < coordinates.length; i++ ) {
                dos.writeInt( coordinates[ i ] );
            }
            
            for ( int j = 0; j < voxels.length; j++ ) {
                dos.writeByte( voxels[ j ] );
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the region into the given directory.
     * 
     * @param region
     *            The region to save.
     * @param dir
     *            The directory to write to.
     */
    public static void saveRegion( Region region, File dir ) {
        System.out.println( dir.getAbsolutePath() );
        File f = new File( dir, String.format( "r%d %d %d.gven.dat", region.x, region.y, region.z ) );

        if ( !createFile( f ) ) return;

        Chunk[] chunks = region.getChunks();
        
        int[][] chunkCoords = new int[ chunks.length ][ 3 ];
        byte[][] voxels = new byte[ chunks.length ][ chunks[ 0 ].getVoxels().length ];

        for ( int i = 0; i < chunks.length; i++ ) {
            chunkCoords[ i ][ 0 ] = chunks[ i ].x;
            chunkCoords[ i ][ 1 ] = chunks[ i ].y;
            chunkCoords[ i ][ 2 ] = chunks[ i ].z;
            
            for ( int j = 0; j < voxels[ i ].length; j++ ) {
                voxels[ i ][ j ] = chunks[ i ].getVoxels()[ j ];
            }
        }

        try {
            DataOutputStream dos = new DataOutputStream( new FileOutputStream( f ) );

            // schedule writes for each chunk
            for ( int i = 0; i < chunks.length; i++ ) {
                Scheduler.enqueueEvent( "writeChunk", RegionIO.class, dos, chunkCoords[ i ], voxels[ i ] );
            }
            
            // scheduler the closing of the stream after all the writes
            Scheduler.enqueueEvent( "close", dos ); // closes the stream after everything has been written
        }
        catch ( Exception e ) {
            // TODO log this error somewhere
            e.printStackTrace();
        }
    }
    
    /**
     * Reads the region from it's file directory.
     * 
     * @param region
     *            The region to read.
     * @param dir
     *            The directory where the region file is.
     * @return If the region was loaded properly or not.
     */
    public static boolean loadRegion( Region region, File dir ) {
        File f = new File( dir, String.format( "r%d %d %d.gven.dat", region.x, region.y, region.z ) );
        
        if ( !createFile( f ) ) return false;

        try ( DataInputStream dis = new DataInputStream( new FileInputStream( f ) ) ) {

            // read each chunk
            for ( int i = 0; i < region.chunks.length; i++ ) {

                // construct the chunk
                Chunk c = new Chunk( region, dis.readInt(), dis.readInt(), dis.readInt() ); // create a chunk based on the chunk's position data
                
                for ( int j = 0; j < 4096; j++ ) {
                    c.setMaterialAt( dis.readByte(), i ); // read the voxel id
                }
                
                region.chunks[ i ] = c; // set the chunk value
            }
        }
        catch ( Exception e ) {
            // TODO log this error somewhere
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
