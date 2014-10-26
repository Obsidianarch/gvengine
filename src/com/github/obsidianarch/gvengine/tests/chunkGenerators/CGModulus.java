package com.github.obsidianarch.gvengine.tests.chunkGenerators;

import com.github.obsidianarch.gvengine.core.Chunk;
import com.github.obsidianarch.gvengine.core.ChunkGenerator;
import com.github.obsidianarch.gvengine.core.Material;

/**
 * The Modulus Chunk Generation method.
 *
 * @version 14.10.26
 * @since 14.10.26
 */
public class CGModulus extends ChunkGenerator
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
                    byte materialID = ( byte ) ( ( ( ( x % 3 ) + ( y % 3 ) + ( z % 3 ) ) % 3 ) + 1 );
                    c.setMaterialAt( materialID, x, y, z );
                }
            }
        }
    }

}
