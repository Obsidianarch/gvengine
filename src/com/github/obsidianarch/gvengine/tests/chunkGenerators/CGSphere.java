package com.github.obsidianarch.gvengine.tests.chunkGenerators;

import com.github.obsidianarch.gvengine.core.Chunk;
import com.github.obsidianarch.gvengine.core.ChunkGenerator;
import com.github.obsidianarch.gvengine.core.Material;
import com.github.obsidianarch.gvengine.tests.materials.Materials;

/**
 * Generates a sphere for the chunk.
 *
 * @version 14.10.30
 * @since 14.10.26
 */
public class CGSphere extends ChunkGenerator
{

    @Override
    public void generateChunk( Chunk c )
    {
        for ( int z = 0; z < Chunk.LENGTH; z++ )
        {
            for ( int y = 0; y < Chunk.LENGTH; y++ )
            {
                for ( int x = 0; x < Chunk.LENGTH; x++ )
                {

                    double xDiff = x - ( Chunk.LENGTH / 2 );
                    double yDiff = y - ( Chunk.LENGTH / 2 );
                    double zDiff = z - ( Chunk.LENGTH / 2 );
                    double sqrt = Math.sqrt( ( xDiff * xDiff ) + ( yDiff * yDiff ) + ( zDiff * zDiff ) );

                    if ( sqrt <= ( Chunk.LENGTH / 2 ) )
                    {
                        c.setMaterialAt( Materials.GRASS, x, y, z );
                    }

                }
            }
        }
    }

}
