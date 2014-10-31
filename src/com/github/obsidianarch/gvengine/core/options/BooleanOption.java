package com.github.obsidianarch.gvengine.core.options;

/**
 * Represents an option which can be
 *
 * @version 14.10.28
 * @since 14.10.26b
 */
public class BooleanOption extends Property
{

    //
    // Fields
    //

    /**
     * The boolean value this BooleanOption is wrapping around
     */
    protected boolean value;

    //
    // Constructors
    //

    /**
     * Constructs a new BooleanOption with {@code false} as the initial value.
     *
     * @since 14.10.26b
     */
    public BooleanOption()
    {
        this( false );
    }

    /**
     * Constructs a new option with {@code initialValue} as the initial value.
     *
     * @param initialValue
     *         The first value of the option.
     *
     * @since 14.10.26b
     */
    public BooleanOption( boolean initialValue )
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
    public boolean get()
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
    public void set( boolean value )
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
