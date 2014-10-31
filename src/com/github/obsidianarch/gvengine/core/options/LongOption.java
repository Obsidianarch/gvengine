package com.github.obsidianarch.gvengine.core.options;

/**
 * Represents an option which can be
 *
 * @version 14.10.28
 * @since 14.10.26b
 */
public class LongOption extends Property
{

    //
    // Fields
    //

    /**
     * The long value this LongOption is wrapping around
     */
    protected long value;

    //
    // Constructors
    //

    /**
     * Constructs a new Option with {@code 0} as the initial value.
     *
     * @since 14.10.26b
     */
    public LongOption()
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
    public LongOption( long initialValue )
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
    public long get()
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
    public void set( long value )
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
