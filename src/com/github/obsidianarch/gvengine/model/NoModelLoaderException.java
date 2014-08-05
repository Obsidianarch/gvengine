package com.github.obsidianarch.gvengine.model;

/**
 * Signifies that no ModelLoader could be resolved for the specified model file.
 *
 * @author Austin
 * @since 14.04.19
 */
public class NoModelLoaderException extends RuntimeException
{

    /**
     * Constructs a new NoModelLoaderException used to signify that a ModelLoader could not be resolved.
     *
     * @since 14.04.19
     */
    public NoModelLoaderException()
    {
        super( "No ModelLoader could be found for the specified file!" );
    }

}
