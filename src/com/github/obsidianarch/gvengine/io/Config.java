package com.github.obsidianarch.gvengine.io;

import java.io.*;
import java.util.*;

/**
 * Reads and writes human readable configuration files.
 *
 * @author Austin
 * @version 14.08.03
 * @since 14.03.30
 */
public class Config
{

    //
    // Fields
    //

    /**
     * The file of this config.
     */
    private final File file;

    /**
     * The tags and their data.
     */
    private HashMap< String, HashMap< String, String > > tags = new HashMap<>();

    /**
     * The configuration format of this Config file.
     */
    private final ConfigurationFormat configurationFormat;

    //
    // Constructors
    //

    /**
     * Constructs a configuration object for the file.
     *
     * @param f
     *         The file of configuration.
     * @param configurationFormat
     *          The ConfigurationFormat which is used to read and write this Config file.
     *
     * @since 14.03.30
     */
    public Config( File f, ConfigurationFormat configurationFormat )
    {
        file = f;
        this.configurationFormat = configurationFormat;
    }

    //
    // Actions
    //

    /**
     * Removes all data for the given tag.
     *
     * @param tagName
     *         The tag to remove.
     *
     * @since 14.03.30
     */
    public void removeTag( String tagName )
    {
        tags.remove( tagName ); // remove it from the hashmap
    }

    //
    // I/O
    //

    /**
     * Uses the underlying ConfigurationFormat object to write data to the underlying file.
     *
     * @return If the write was successful or not.
     *
     * @since 14.03.30
     */
    public boolean write()
    {
        return configurationFormat.write( file, tags );
    }

    /**
     * Uses the underlying ConfigurationFormat object to read data from the underlying file.
     *
     * @return If the read was successful or not.
     *
     * @since 14.03.30
     */
    public boolean read()
    {
        tags.clear(); // clear the tags before reading new ones
        return configurationFormat.read( file, tags );
    }

    //
    // Setters
    //

    /**
     * Sets the tag data.
     *
     * @param tagName
     *         The name of the tag.
     * @param data
     *         The new data for the tag.
     *
     * @since 14.03.30
     */
    public void setTagData( String tagName, HashMap< String, String > data )
    {
        tags.put( tagName, data );
    }

    //
    // Getters
    //

    /**
     * Gets the tag data.
     *
     * @param tagName
     *         The name of the tag.
     *
     * @return An empty list if there is no tag data, otherwise the tag's data.
     */
    public HashMap< String, String > getTagData( String tagName )
    {
        HashMap< String, String > data = tags.get( tagName );
        return data == null ? new HashMap< String, String >() : data; // return an empty map if there was no data for the tag
    }

}
