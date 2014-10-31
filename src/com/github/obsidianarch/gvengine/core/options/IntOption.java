package com.github.obsidianarch.gvengine.core.options;

/**
 * Represents an option which can be
 *
 * @version 14.10.28
 * @since 14.10.26b
 */
public class IntOption extends Property
{

    //
    // Fields
    //

    /**
     * The int value this IntOption is wrapping around
     */
    protected int value;

    //
    // Constructors
    //

    /**
     * Constructs a new Option with {@code 0} as the initial value.
     *
     * @since 14.10.26b
     */
    public IntOption()
    {
        this( 0 );
    }

    /**
     * Constructs a new option with {@code initialValue} as the initial value.
     *
     * @param initialValue
     *         The first value of the option.
     *
     * @since 14.10.26b
     */
    public IntOption( int initialValue )
    {
        super();
        value = initialValue;
    }

    //
    // Getters
    //

    /**
     * @return The current value of the option.
     *
     * @since 14.10.26b
     */
    public int get()
    {
        return value;
    }

    //
    // Setters
    //

    /**
     * @param value
     *         The new value of the option.
     *
     * @since 14.10.26b
     */
    public void set( int value )
    {
        this.value = value;
        onChange();
    }

    //
    // Overrides
    //

    @Override
    public String toString()
    {
        return String.valueOf( value );
    }

}
