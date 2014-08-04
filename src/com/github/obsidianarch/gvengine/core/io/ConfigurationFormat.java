package com.github.obsidianarch.gvengine.core.io;

import java.io.File;
import java.util.HashMap;

/**
 * A format for a Config instance to read and write to.
 *
 * @since 14.08.03
 * @version 14.08.03
 */
public interface ConfigurationFormat
{

    /**
     * Sanitizes the input key so that it will not interfere with the parser.
     *
     * @param input
     *          The input key.
     *
     * @return A valid key.
     */
    public String getSanitizedKey( String input );

    /**
     * Reads the given file into the given HashMap of tags.
     *
     * @param file
     *          The file to read.
     * @param tags
     *          The empty HashMap to read the data into.
     *
     * @return If the read was successful or not.
     *
     * @since 14.08.03
     */
    public boolean read( File file, HashMap< String, HashMap< String, String > > tags );

    /**
     * Writes the tags from the given HashMap into the given file.
     *
     * @param file
     *          The file to write to.
     * @param tags
     *          The HashMap to read the data from for writing.
     *
     * @return If the write was successful or not.
     *
     * @since 14.08.03
     */
    public boolean write( File file, HashMap< String, HashMap< String, String > > tags );

}
