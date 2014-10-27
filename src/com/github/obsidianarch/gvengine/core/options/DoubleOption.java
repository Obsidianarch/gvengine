package com.github.obsidianarch.gvengine.core.options;

/**
 * Represents an option which can be
 *
 * @version 14.10.26b
 * @since 14.10.26b
 */
public class DoubleOption extends Property
{

    //
    // Fields
    //

    /**
     * The double value this DoubleOption is wrapping around
     */
    protected double value;

    //
    // Constructors
    //

    /**
     * Constructs a new DoubleOption with {@code 0} as the initial value.
     *
     * @since 14.10.26b
     */
    public DoubleOption()
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
    public DoubleOption( double initialValue )
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
    public double get()
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
    public void set( double value )
    {
        this.value = value;
        onChange();
    }

}
