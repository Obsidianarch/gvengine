package com.github.obsidianarch.gvengine.tests.chunkGenerators;

import com.github.obsidianarch.gvengine.core.Chunk;
import com.github.obsidianarch.gvengine.core.ChunkGenerator;
import com.github.obsidianarch.gvengine.tests.materials.Materials;

/**
 * A cross between {@code CGModulus} and {@code CGSphere} where the blocks are positioned as they would in {@code CGSphere} while the material is chosen as
 * would be in {@code CGModulus}.
 *
 * @version 14.10.30
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
                        int materialID = // the material changes at every position in every direction
                                ( x % Materials.MATERIAL_COUNT ) +
                                ( y % Materials.MATERIAL_COUNT ) +
                                ( z % Materials.MATERIAL_COUNT );
                        materialID %= Materials.MATERIAL_COUNT; // get the last remainder

                        c.setMaterialAt( materialID, x, y, z );
                    }

                }
            }
        }
    }

}
