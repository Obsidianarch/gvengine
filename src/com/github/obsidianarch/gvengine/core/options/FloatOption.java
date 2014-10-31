package com.github.obsidianarch.gvengine.core.options;

/**
 * Represents an option which can be
 *
 * @version 14.10.28
 * @since 14.10.26b
 */
public class FloatOption extends Property
{

    //
    // Fields
    //

    /**
     * The float value this FloatOption is wrapping around
     */
    protected float value;

    //
    // Constructors
    //

    /**
     * Constructs a new FloatOption with {@code 0} as the initial value.
     *
     * @since 14.10.26b
     */
    public FloatOption()
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
    public FloatOption( float initialValue )
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
    public float get()
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
    public void set( float value )
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
