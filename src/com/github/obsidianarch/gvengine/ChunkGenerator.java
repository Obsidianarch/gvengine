package com.github.obsidianarch.gvengine;

import java.util.Random;

/**
 * Generates the terrain inside of a chunk.
 * 
 * @author Austin
 */
public abstract class ChunkGenerator {
    
    //
    // Fields
    //
    
    /** The random generator for the chunk generator. */
    private Random random = new Random();
    
    //
    // Final
    //
    
    /**
     * Creates perlin noise and skews it with some more data.
     * 
     * @param seed
     *            The seed for the generator.
     * @param x
     *            The global x coordinate of the voxel.
     * @param y
     *            The global y coordinate of the voxel.
     * @param z
     *            The global z coordinate of the voxel.
     * @param scale
     *            The scale of the noise.
     * @param magnitude
     *            The magnitude of the noise.
     * @param exponent
     *            The exponent to raise the output to.
     * @return {@code ( random( seed, x / scale, y / scale, z / scale ) * magnitude )^exponent}
     */
    public final int noise( long seed, float x, float y, float z, float scale, float magnitude, float exponent ) {
        return ( int ) ( Math.pow( ( random( seed, x / scale, y / scale, z / scale ) * magnitude ), exponent ) );
    }
    
    /**
     * Creates a random number using {@code java.util.Random} to return a number given the
     * three inputs.
     * 
     * @param seed
     *            The seed for the generator.
     * @param x
     *            The global x coordinate of the voxel.
     * @param y
     *            The global y coordinate of the voxel.
     * @param z
     *            The global z coordinate of the voxel.
     * @return A random number (0.0-1.0) based on the seed and the x, y, and, z
     *         coordinates of the the voxel.
     */
    public final float random( long seed, float x, float y, float z ) {
        seed = ( long ) ( ( seed * x ) + ( y / ( z - 0.5f ) ) );
        random.setSeed( seed );
        return ( float ) random.nextFloat();
    }
    
    //
    // Abstract
    //
    
    /**
     * Generates the voxels inside of the chunk.
     * 
     * @param c
     *            The chunk to generate voxels for.
     */
    public abstract void generateChunk( Chunk c );
    
}
