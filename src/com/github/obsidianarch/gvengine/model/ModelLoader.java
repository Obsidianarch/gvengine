package com.github.obsidianarch.gvengine.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Determines the model loader to use and then loads a model.
 *
 * @author Austin
 * @since 14.04.19
 */
public abstract class ModelLoader
{

    //
    // Final
    //

    /**
     * Loads teh contents of the file into a Model object. This is a wrapper method around the protected, abstract method {@code loadModel(BufferedReader)}
     * which creates and closes the {@code BufferedReader}.
     *
     * @param f
     *         The file to load a model from.
     *
     * @return The loaded model.
     *
     * @throws IOException
     *         If there was a problem reading the file.
     */
    public final Model loadModel( File f ) throws IOException
    {

        try ( BufferedReader br = new BufferedReader( new FileReader( f ) ) )
        {
            return loadModel( br );
        }

    }

    //
    // Abstract
    //

    /**
     * Loads the contents of the file into a Model object.
     *
     * @param br
     *         The BufferedReader for the model file.
     *
     * @return The model that was loaded from the file.
     *
     * @throws IOException
     *         If a problem occurs when reading the file.
     * @since 14.04.19
     */
    protected abstract Model loadModel( BufferedReader br ) throws IOException;

    /**
     * Returns true if this model loader supports the specified file name.
     *
     * @param fileName
     *         The name of the file.
     *
     * @return If the model type is able to be loaded by this model loader.
     *
     * @since 14.04.19
     */
    public abstract boolean isSupported( String fileName );

}
