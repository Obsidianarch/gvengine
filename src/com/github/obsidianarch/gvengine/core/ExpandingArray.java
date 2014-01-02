package com.github.obsidianarch.gvengine.core;

import java.nio.FloatBuffer;

/**
 * An expanding float array, created for speed and provides an immutable array, useful for
 * positions, colors, and normal positions.
 * 
 * @author Austin
 */
public class ExpandingArray {
    
    //
    // Fields
    //
    
    /** The data of the array. */
    private float[] array;
    
    /** The current index of the array. */
    private int     index;
    
    /** The number of values that have been added */
    private int     length;
    
    //
    // Constructors
    //
    
    /**
     * Creates an expanding buffer.
     * 
     * @param capacity
     *            The initial capacity of the float buffer.
     */
    public ExpandingArray(int capacity) {
        array = new float[ capacity ];
        index = 0;
        length = 0;
    }
    
    //
    // Actions
    //
    
    /**
     * Adds the data of this expanding array into the float buffer.
     * 
     * @param buffer
     *            The buffer to add data into.
     * @param size
     *            The number of items to add at a time.
     * @param offset
     *            The first index to add data into.
     * @param stride
     *            The distance between insertions to skip.
     */
    public void addToBuffer( FloatBuffer buffer, int size, int offset, int stride ) {
        if ( size == 0 ) return;
        
        int bufferIndex = offset;
        for ( int i = 0; i < length; ) { // iterating over each item in the array
            for ( int j = 0; j < size; j++ ) { // add the section into the 
                buffer.put( bufferIndex + j, array[ i++ ] ); // add the item to the buffer
            }
            bufferIndex += size + stride; // we just added <size> items, and will skip to the next section
        }
    }
    
    /**
     * Expands the array to the new capacity.
     * 
     * @param newCapacity
     *            The new capacity of the array.
     */
    public void expand( int newCapacity ) {
        if ( newCapacity <= array.length ) return;
        
        float[] newArray = new float[ newCapacity ];
        for ( int i = 0; i < array.length; i++ ) {
            newArray[ i ] = array[ i ];
        }
        array = newArray;
    }
    
    /**
     * Rewinds the index to 0.
     */
    public void rewind() {
        index = 0;
    }
    
    //
    // Putters
    //
    
    /**
     * Adds the floats from the expanding array into this expanding array at the current
     * index.
     * 
     * @param array
     *            The array to add to this one.
     */
    public void put( ExpandingArray array ) {
        if ( !canHold( array.length ) ) {
            expand( length + array.length );
        }
        
        for ( int i = 0; i < array.length; i++ ) {
            this.array[ i + index ] = array.array[ i ];
        }
        index += array.length;
        length += array.length;
    }
    
    /**
     * Adds the floats into the array at the current index.
     * 
     * @param floats
     *            The floats to add to the array.
     */
    public void put( float... floats ) {
        if ( !canHold( floats.length ) ) {
            expand( length + floats.length );
        }
        
        for ( int i = 0; i < floats.length; i++ ) {
            array[ i + index ] = floats[ i ];
        }
        index += floats.length;
        length += floats.length;
    }
    
    //
    // Getters
    //
    
    /**
     * @return The array backing this, only the used section.
     */
    public float[] getArray() {
        float[] array = new float[ length ];
        
        for ( int i = 0; i < length; i++ ) {
            array[ i ] = this.array[ i ];
        }
        
        return array;
    }
    
    /**
     * @return The current capacity of the array.
     */
    public int getCapacity() {
        return array.length;
    }
    
    /**
     * @return The length of the array.
     */
    public int getLength() {
        return length;
    }
    
    /**
     * @param newEntries
     *            The number of entries it wants to add.
     * @return {@code true} if the current array length can hold the given number of new
     *         entries.
     */
    public boolean canHold( int newEntries ) {
        return !( ( length + newEntries ) > array.length );
    }
    
}
