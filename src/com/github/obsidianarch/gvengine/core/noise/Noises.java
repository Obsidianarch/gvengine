package com.github.obsidianarch.gvengine.core.noise;

/**
 * Provides access to many different methods of noise generation.
 * 
 * @author Austin
 * @see <a href="http://webstaff.itn.liu.se/~stegu/simplexnoise/simplexnoise.pdf">Simplex
 *      Noise</a>
 * 
 * @since 14.03.30
 * @version 14.03.30
 */
public final class Noises {
    
    /**
     * Creates noise, based on the simplex noise algorithm, ranging from {@code [-1, 1]}.
     * 
     * @param x
     *            The x input.
     * @param y
     *            The y input.
     * @return Noise ranging from {@code [-1, 1]}.
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    public static double simplex2D( double x, double y ) {
        return SimplexNoise.noise( x, y );
    }
    
    /**
     * Creates noise, based on the simplex noise algorithm, rangine from {@code [-1, 1]}.
     * 
     * @param x
     *            The x input.
     * @param y
     *            The y input.
     * @param z
     *            The z input.
     * @return Noise ranging from {@code [-1, 1]}.
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    public static double simplex3D( double x, double y, double z ) {
        return SimplexNoise.noise( x, y, z );
    }
    
    /**
     * Creates noise, based on the simplex noise algorithm, rangine from {@code [-1, 1]}.
     * 
     * @param x
     *            The x input.
     * @param y
     *            The y input.
     * @param z
     *            The z input.
     * @param w
     *            The w input.
     * @return Noise ranging from {@code [-1, 1]}.
     * 
     * @since 14.03.30
     * @version 14.03.30
     */
    public static double simplex4D( double x, double y, double z, double w ) {
        return SimplexNoise.noise( x, y, z, w );
    }
    
}
