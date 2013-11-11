package com.github.obsidianarch.gvengine;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.Color;

/**
 * Represents one of the ninety-six faces on a chunk, could be internal or external.
 * 
 * @author Austin
 */
class ChunkFace {
    
    private List< Selection > voxelSelections = new ArrayList< Selection >( 16 ); // 
                                                                                  
    /**
     * 
     * @param x
     *            The starting x position of this face.
     * @param y
     *            The starting y position of this face.
     * @param z
     *            The starting z position of this face.
     * @param voxels
     *            The voxels contained in this face.
     */
    public ChunkFace(int x, int y, int z, int[] voxels) {
        for ( int i = 0; i < voxels.length; i++ ) {
            int type = voxels[ i ]; // the type of voxel this is
            if ( type == -1 ) continue; // this has already been used.
            if ( !Voxel.getVoxelType( type ).isActive() ) continue; // this won't be rendered, so we don't merge any vertices here
                
            int leftBounds = i % 16;
            
            int rightBounds = i;
            while ( voxels[ right( rightBounds ) ] == type ) {
                rightBounds++;
            }
            
            int topBounds = i;
            while ( voxels[ up( topBounds ) ] == type ) {
                boolean flag = true; // are all of the preceeding elements also in bounds?
                
                // j is the new top right bounds, and while it isn't on the left side, go left
                for ( int j = rightBounds + topBounds; ( j % leftBounds ) != 0; j = left( j ) ) {
                    flag = voxels[ j ] == type;
                    if ( !flag ) break; // if not all the elements are the same, break out
                }
                
                if ( !flag ) break; // not all the elements on this row are the same, so we are finished
                topBounds++; // all the elements were the same, so continue expanding up
            }
            
            voxelSelections.add( new Selection( i, rightBounds + topBounds, type ) ); // add the selection
            
            // deactivate the used voxels
            int index = leftBounds;
            while ( true ) {
                voxels[ index++ ] = -1; // deactive the selected voxel
                
                if ( index > rightBounds ) {
                    index -= ( rightBounds - leftBounds ); // return to the first position in the row
                    index = up( index ); // go up a level
                }
                
                if ( index > topBounds ) break; // and we've deactivated all the voxels
            }
        }
    }
    
    /**
     * @return The color vertices for all selections in this face.
     */
    public FloatBuffer createColorData() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer( voxelSelections.size() );
        for ( Selection selection : voxelSelections ) {
            buffer.put( selection.createColorVertices() );
        }
        
        buffer.flip(); // make the buffer readable before returning
        return buffer; // return the buffer
    }
    
    //
    // Static
    //
    
    private static final int left( int index ) {
        return index--;
    }
    
    private static final int right( int index ) {
        return index++;
    }
    
    private static final int up( int index ) {
        return index + 16;
    }
    
    //
    // Nested Classes
    //
    
    /**
     * A selection of voxels in the face.
     * 
     * @author Austin
     */
    private class Selection {
        
        private final int startIndex, endIndex;
        private final int voxelType;
        
        /**
         * Creates a selection of voxels in a face.
         * 
         * @param x
         *            The starting x coordinate in the voxel array.
         * @param y
         *            The starting y coordinate in the voxel array.
         * @param width
         *            The width of the selection.
         * @param height
         *            The height of the selection.
         * @param voxelType
         *            The type of voxels this selection contains.
         */
        public Selection(int startIndex, int endIndex, int voxelType) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.voxelType = voxelType;
        }
        
        /**
         * Creates a two-dimensional {@code FloatBuffer} for the vertex data of this
         * selection.
         * 
         * @return The 2D {@code FloatBuffer} containing the two triangles for this
         *         selection.
         */
        public FloatBuffer createVertices() {
            FloatBuffer buffer = BufferUtils.createFloatBuffer( width * height ); // create a buffer with the correct dimensions
            
            // first triangle
            buffer.put( x ).put( y ); // 0, 0
            buffer.put( x + width ).put( y ); // 1, 0
            buffer.put( x ).put( y + height ); // 0, 1
            
            buffer.put( x + width ).put( y + height ); // 1, 1
            buffer.put( x ).put( y + height ); // 0, 1 
            buffer.put( x + width ).put( y ); // 1, 0
            
            buffer.flip(); // make the buffer readable
            return buffer; // return the populated buffer
        }
        
        /**
         * Creates the two-dimensional {@code FloatBuffer} for the color data of the
         * vertices in this selection.
         * 
         * @return The 2D {@code FloatBuffer} continaing the 6 colors for each vertex in
         *         the two triangles of the selection.
         */
        public FloatBuffer createColorVertices() {
            Color c = Voxel.getVoxelType( voxelType ).getColor();
            float[] cubeColors = new float[ ] { c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f }; // 255f to get the color values to 0-1f
            FloatBuffer buffer = BufferUtils.createFloatBuffer( 3 * 3 * 2 ); // 3 (values/color) * 3 (vertices/triangle) * 2 (triangles/face)
            
            for ( int i = 0; i < buffer.limit(); i++ ) {
                buffer.put( cubeColors[ i % cubeColors.length ] ); // add the color data to the point
            }
            
            buffer.flip(); // flip the buffer before returning it
            return buffer;
        }
        
    }
    
}
