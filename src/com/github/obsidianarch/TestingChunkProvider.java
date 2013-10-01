package com.github.obsidianarch;

import java.util.ArrayList;
import java.util.List;

import com.github.obsidianarch.gvengine.Chunk;
import com.github.obsidianarch.gvengine.ChunkProvider;
import com.github.obsidianarch.gvengine.MathHelper;

public class TestingChunkProvider implements ChunkProvider {
    
    private List< Chunk > chunks = new ArrayList< Chunk >();
    
    public TestingChunkProvider() {
        for ( int i = 0; i < Chunk.VOXEL_CAPACITY; i++ ) {
            chunks.add( null );
        }
        
        for ( int x = 0; x < 3; x++ ) {
            for ( int y = 0; y < 3; y++ ) {
                for ( int z = 0; z < 3; z++ ) {
                    createChunk( x, y, z );
                }
            }
        }
    }
    
    @Override
    public int getVertexCount() {
        int vertexCount = 0;
        for ( Chunk c : chunks ) {
            if ( c == null ) continue;
            vertexCount += c.getVertexCount();
        }
        return vertexCount;
    }
    
    @Override
    public void update() {
    }
    
    @Override
    public void render() {
        for ( Chunk c : chunks ) {
            if ( c == null ) continue;
            c.render();
        }
    }
    
    @Override
    public void rebuildMesh() {
        for ( Chunk c : chunks ) {
            if ( c == null ) continue;
            c.rebuildMesh();
        }
    }
    
    @Override
    public Chunk getChunk( int x, int y, int z ) {
        int index = MathHelper.getIndex( x, y, z, 16 );
        
        if ( ( index >= chunks.size() ) || ( x < 0 ) || ( y < 0 ) || ( z < 0 ) ) return null;
        
        return chunks.get( index );
    }
    
    @Override
    public Chunk createChunk( int x, int y, int z ) {
        Chunk c = new Chunk( x, y, z, this );
        
        for ( int vX = 0; vX < Chunk.CHUNK_SIZE; vX++ ) {
            for ( int vY = 0; vY < Chunk.CHUNK_SIZE; vY++ ) {
                for ( int vZ = 0; vZ < Chunk.CHUNK_SIZE; vZ++ ) {
                    c.setVoxelAt( vX, vY, vZ, 1 );
                }
            }
        }
        
        chunks.set( MathHelper.getIndex( x, y, z, Chunk.CHUNK_SIZE ), c );
        return c;
    }
    
}
