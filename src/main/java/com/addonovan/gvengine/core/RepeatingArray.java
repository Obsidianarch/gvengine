package com.addonovan.gvengine.core;

/**
 * An array that repeats itself infinitely many times, due to the nature of it's getter.
 *
 * @author Austin
 * @version 14.03.30
 * @since 14.03.30
 */
public class RepeatingArray
{

    //
    // Fields
    //

    /**
     * The array backing this object.
     */
    private final float[] array;

    //
    // Constructors
    //

    /**
     * Creates a new Repeating array from the source array.
     *
     * @param source
     *         The source array.
     *
     * @since 14.03.30
     */
    public RepeatingArray( float[] source )
    {
        array = source;
    }

    //
    // Getters
    //

    /**
     * @param index
     *         The index to get the item from.
     *
     * @return The item at the given index.
     *
     * @since 14.03.30
     */
    public float get( int index )
    {
        return array[ index % array.length ];
    }

    /**
     * @param arrayLength
     *         The length of the new array.
     *
     * @return An array with the data reflecting this, repeated for the length of the array.
     *
     * @since 14.03.30
     */
    public float[] createArray( int arrayLength )
    {
        float[] newArray = new float[ arrayLength ];

        for ( int i = 0; i < arrayLength; i++ )
        {
            newArray[ i ] = get( i );
        }

        return newArray;
    }

}
