package com.github.obsidianarch.gvengine.core;

import org.magicwerk.brownies.collections.primitive.FloatGapList;

/**
 * Everything involved with the creation of individual voxels.
 *
 * @author Austin
 * @version 14.10.26
 * @since 14.03.30
 */
public final class Voxel
{

    //
    // Point Locations
    //

    /**
     * Creates a voxel with only the needed faces.
     *
     * @param positions
     *         The positioning array to which the voxel's face positions will be appended.
     * @param colors
     *         The color array to which the voxel's colors will be appended.
     * @param normals
     *         The normal array to which the voxel's normals will be appended.
     * @param c
     *         The chunk the voxel is a part of.
     * @param x
     *         The local x coordinate of the voxel.
     * @param y
     *         The local y coordinate of the voxel.
     * @param z
     *         The local z coordinate of the voxel.
     *
     * @since 14.03.30
     */
    public static void createVoxel( FloatGapList positions, FloatGapList colors, FloatGapList normals, Chunk c, int x, int y, int z )
    {
        if ( !c.isRenderable( x, y, z ) )
        {
            return; // this voxel shouldn't be rendered
        }

        Material material = c.getMaterialAt( x, y, z ); // the material of this voxel

        float[] colorSource = { material.color.getRed() / 255f, material.color.getGreen() / 255f, material.color.getBlue() / 255f };
        RepeatingArray repeatingColors = new RepeatingArray( colorSource );
        float[] repeatedColors = repeatingColors.createArray( 18 ); // this will be added for every face, for the voxel's color

        // get the global positions of the voxel
        double[] offsets = c.getGlobalOffset();
        float gX = ( float ) ( x + offsets[ 0 ] );
        float gY = ( float ) ( y + offsets[ 1 ] );
        float gZ = ( float ) ( z + offsets[ 2 ] );

        // cycle through all the faces
        for ( Face face : Face.values() )
        {
            if ( c.isVisible( face, x, y, z ) )
            {
                RepeatingArray repeatingNormals = new RepeatingArray( new float[] { face.normal.x, face.normal.y, face.normal.z } );
                float[] repeatedNormals = repeatingNormals.createArray( 18 ); // create the normal array

                // add the position and color data for this face
                positions.addAll( createFace( face, gX, gY, gZ ) );
                colors.addAll( repeatedColors );
                normals.addAll( repeatedNormals );
            }
        }
    }

    /**
     * Creates a float array for the positioning of a voxel face.
     *
     * @param direction
     *         The face.
     * @param x
     *         The global x coordinate of the face.
     * @param y
     *         The global y coordinate of the face.
     * @param z
     *         The global z coordinate of the face.
     *
     * @return The face's position data.
     *
     * @since 14.03.30
     */
    public static float[] createFace( Face direction, float x, float y, float z )
    {
        float[] points = null; // the point's we'll send back

        // these make the far (positive) spaces easier to reach
        float xp = x + 1;
        float yp = y + 1;
        float zp = z + 1;

        switch ( direction )
        {
            case LEFT: // LBF-RBF-LTF | RTF-RBF-LTF
                points = new float[] { x, yp, z, x, y, z, x, y, zp, x, yp, z, x, y, zp, x, yp, zp };
                break;

            case BOTTOM: // LBF-RBF-LBB | RBB-RBF-LBB
                points = new float[] { x, y, zp, x, y, z, xp, y, zp, x, y, z, xp, y, z, xp, y, zp };
                break;

            case FRONT: // LBF-RBF-LTF | RTF-RBF-LTF
                points = new float[] { xp, yp, z, xp, y, z, x, y, z, xp, yp, z, x, y, z, x, yp, z };
                break;

            case RIGHT: // same as left, but x is shifted
                points = new float[] { xp, yp, zp, xp, y, zp, xp, y, z, xp, yp, zp, xp, y, z, xp, yp, z };
                break;

            case TOP: // same as bottom, but y is shifted
                points = new float[] { xp, yp, zp, x, yp, z, x, yp, zp, xp, yp, zp, xp, yp, z, x, yp, z };
                break;

            case BACK: // same as front, but z is shifted
                points = new float[] { x, y, zp, xp, y, zp, xp, yp, zp, x, yp, zp, x, y, zp, xp, yp, zp };
                break;
        }

        return points;
    }
}
