package com.github.obsidianarch.gvengine.core.options;

/**
 * A class whose sole use is to be a link between all the various Option types so that
 * the following statement is true of all options:<br>
 * <b>{@code type}</b>{@code instanceof Property}
 *
 * @version 14.10.26b
 * @since 14.10.26b
 */
public abstract class Property
{

    /**
     * Executed whenever a property's value changes.
     *
     * @since 14.10.26b
     */
    public void onChange()
    {

    }

}
