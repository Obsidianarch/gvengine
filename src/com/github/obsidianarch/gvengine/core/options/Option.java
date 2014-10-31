package com.github.obsidianarch.gvengine.core.options;

/**
 * Represents an option which can be set by the user with minimal overhead.
 *
 * @version 14.10.26b
 * @since 14.10.26b
 */
public class Option< T > extends Property
{

    //
    // Fields
    //

    /**
     * The value this Option is wrapping around
     */
    protected T value;

    //
    // Constructors
    //

    /**
     * Constructs a new Option with {@code null} as the initial value.
     *
     * @since 14.10.26b
     */
    public Option()
    {
        this( null );
    }

    /**
     * Constructs a new option with {@code initialValue} as the initial value.
     *
     * @param initialValue
     *         The first value of the option.
     *
     * @since 14.10.26b
     */
    public Option( T initialValue )
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
    public T get()
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
    public void set( T value )
    {
        this.value = value;
        onChange();
    }

}
