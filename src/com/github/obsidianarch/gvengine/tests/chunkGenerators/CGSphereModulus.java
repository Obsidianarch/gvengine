package com.github.obsidianarch.gvengine.tests.chunkGenerators;

import com.github.obsidianarch.gvengine.core.Chunk;
import com.github.obsidianarch.gvengine.core.ChunkGenerator;
import com.github.obsidianarch.gvengine.core.Material;

/**
 * A cross between {@code CGModulus} and {@code CGSphere} where the blocks are
 * positioned as they would in {@code CGSphere} while the material is chosen as
 * would be in {@code CGModulus}.
 *
 * @version 14.10.26
 * @since 14.10.26
 */
public class CGSphereModulus extends ChunkGenerator
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
                        byte materialID = ( byte ) ( ( ( ( x % 3 ) + ( y % 3 ) + ( z % 3 ) ) % 3 ) + 1 );
                        c.setMaterialAt( materialID, x, y, z );
                    }

                }
            }
        }
    }

}
