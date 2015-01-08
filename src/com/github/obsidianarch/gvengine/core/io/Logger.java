package com.github.obsidianarch.gvengine.core.io;

/**
 * A simple interface for getting a Lumberjack interface without having to pass the class, as it will automatically be passed by the getLogger method.
 *
 * @version 15.01.07
 * @since 15.01.07
 */
public interface Logger
{

    /**
     * @return The Lumberjack instance for this Logger (by default returns {@code Lumberjack.getInstance( getClass() )}.
     */
    default Lumberjack getLogger()
    {
        return Lumberjack.getInstance( getClass() );
    }

}
