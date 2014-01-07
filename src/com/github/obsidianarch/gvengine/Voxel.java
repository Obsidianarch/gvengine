package com.github.obsidianarch.gvengine;

import com.github.obsidianarch.gvengine.core.ExpandingArray;
import com.github.obsidianarch.gvengine.core.Face;
import com.github.obsidianarch.gvengine.core.RepeatingArray;

/**
 * Everything involved with the creation of individual voxels.
 * 
 * @author Austin
 */
public class Voxel {
    
    /**
     * Creates a voxel with only the needed faces.
     * 
     * @param positions
     *            The positioning array the voxel will be appended to.
     * @param colors
     *            The color array the voxel's colors will be appended to.
     * @param c
     *            The chunk the voxel is a part of.
     * @param x
     *            The local x coordinate of the voxel.
     * @param y
     *            The local y coordinate of the voxel.
     * @param z
     *            The local z coordinate of the voxel.
     */
    public static final void createVoxel( ExpandingArray positions, ExpandingArray colors, Chunk c, int x, int y, int z ) {
        Material material = c.getMaterialAt( x, y, z );
        if ( !material.active ) return;
        
        float[] colorSource = { material.color.getRed() / 255f, material.color.getGreen() / 255f, material.color.getBlue() / 255f };
        RepeatingArray repeatingColors = new RepeatingArray( colorSource );
        float[] repeatedColors = repeatingColors.createArray( 18 );
        
        float gX = Chunk.toGlobalPosition( c.x, x );
        float gY = Chunk.toGlobalPosition( c.y, y );
        float gZ = Chunk.toGlobalPosition( c.z, z );
        
        //
        // X-Faces
        //
        
        if ( !c.getMaterialAt( x - 1, y, z ).active ) {
            positions.put( createFace( Face.LEFT, gX, gY, gZ ) );
            colors.put( repeatedColors );
        }
        
        if ( !c.getMaterialAt( x + 1, y, z ).active ) {
            positions.put( createFace( Face.RIGHT, gX, gY, gZ ) );
            colors.put( repeatedColors );
        }
        
        //
        // Y-Faces
        //
        
        if ( !c.getMaterialAt( x, y - 1, z ).active ) {
            positions.put( createFace( Face.BOTTOM, gX, gY, gZ ) );
            colors.put( repeatedColors );
        }
        
        if ( !c.getMaterialAt( x, y + 1, z ).active ) {
            positions.put( createFace( Face.TOP, gX, gY, gZ ) );
            colors.put( repeatedColors );
        }
        
        //
        // Z-Faces
        //
        
        if ( !c.getMaterialAt( x, y, z - 1 ).active ) {
            positions.put( createFace( Face.FRONT, gX, gY, gZ ) );
            colors.put( repeatedColors );
        }
        
        if ( !c.getMaterialAt( x, y, z + 1 ).active ) {
            positions.put( createFace( Face.BACK, gX, gY, gZ ) );
            colors.put( repeatedColors );
        }
    }
    
    /**
     * Creates a float array for the positioning of a voxel face.
     * 
     * @param direction
     *            The face.
     * @param x
     *            The global x coordinate of the face.
     * @param y
     *            The global y coordinate of the face.
     * @param z
     *            The global z coordinate of the face.
     * @return The face's position data.
     */
    public static final float[] createFace( Face direction, float x, float y, float z ) {
        float[] points = null; // the point's we'll send back
        
        // these make the positive spaces easier to reach
        float xp = x + 1;
        float yp = y + 1;
        float zp = z + 1;
        
        switch ( direction ) {
        case LEFT: // LBF-RBF-LTF | RTF-RBF-LTF
            points = new float[ ] { x, yp, z, x, y, z, x, y, zp, x, yp, z, x, y, zp, x, yp, zp };
            break;
        
        case BOTTOM: // LBF-RBF-LBB | RBB-RBF-LBB
            points = new float[ ] { x, y, zp, x, y, z, xp, y, zp, x, y, z, xp, y, z, xp, y, zp };
            break;
        
        case FRONT: // LBF-RBF-LTF | RTF-RBF-LTF
            points = new float[ ] { xp, yp, z, xp, y, z, x, y, z, xp, yp, z, x, y, z, x, yp, z };
            break;
        
        case RIGHT: // same as left, but x is shifted
            points = new float[ ] { xp, yp, zp, xp, y, zp, xp, y, z, xp, yp, zp, xp, y, z, xp, yp, z };
            break;
        
        case TOP: // same as bottom, but y is shifted
            points = new float[ ] { xp, yp, zp, x, yp, z, x, yp, zp, xp, yp, zp, xp, yp, z, x, yp, z };
            break;
        
        case BACK: // same as front, but z is shifted
            points = new float[ ] { x, y, zp, xp, y, zp, xp, yp, zp, x, yp, zp, x, y, zp, xp, yp, zp };
            break;
        }
        
        return points;
    }
}
