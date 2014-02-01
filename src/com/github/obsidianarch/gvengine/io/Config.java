package com.github.obsidianarch.gvengine.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads and writes human readable configuration files.
 * 
 * @author Austin
 */
public class Config {
    
    //
    // Fields
    //
    
    /** The file of this config. */
    private final File                    file;
    
    /** The tags and their data. */
    private Map< String, List< String > > tags = new HashMap<>();
    
    //
    // Constructors
    //
    
    /**
     * Constructs a configuration object for the file.
     * 
     * @param f
     *            The file of configuration.
     */
    public Config( File f ) {
        file = f;
    }
    
    //
    // Editing
    //
    
    /**
     * Removes all data for the given tag.
     * 
     * @param tagName
     *            The tag to remove.
     */
    public void removeTag( String tagName ) {
        tags.remove( tagName );
    }
    
    //
    // I/O
    //
    
    /**
     * Saves the text to the file.
     * 
     * @return If the save was successful or not.
     */
    public boolean save() {
        try {
            BufferedWriter bw = new BufferedWriter( new FileWriter( file ) ); // writes the data to the file
            
            for ( Map.Entry< String, List< String > > entry : tags.entrySet() ) {
                bw.write( String.format( "[%s]%n", entry.getKey() ) ); // write the starting tag
                
                // write the data
                for ( String s : entry.getValue() ) {
                    bw.write( String.format( "  %s%n", s ) ); // write the data to the line
                }
                
                bw.write( String.format( "[END]%n%n" ) ); // write the ending tag
            }
            
            bw.close(); // close the stream
            
            return true;
        }
        catch ( Exception e ) {
            return false;
        }
    }
    
    /**
     * Reads the text from the file.
     * 
     * @return If the read was successful or not.
     */
    public boolean read() {
        try {
            BufferedReader br = new BufferedReader( new FileReader( file ) ); // reads data from the file
            
            Map< String, List< String >> data = new HashMap<>(); // contains all of the read data
            
            String currentTag = null; // the name of the current property
            
            String line; // the last read line
            while ( ( line = br.readLine() ) != null ) {
                
                line = line.trim();
                
                // stop the reading
                if ( line.equals( "[END]" ) ) {
                    currentTag = null;
                    continue;
                }
                
                if ( currentTag != null ) {
                    
                    List< String > props = data.get( currentTag );
                    props.add( line );
                    
                }
                else {
                    // beings with a '[' and ends with an ']'
                    if ( line.startsWith( "[" ) && line.endsWith( String.format( "]" ) ) ) {
                        currentTag = line.substring( 1, line.lastIndexOf( "]" ) ); // change the current property
                        data.put( currentTag, new ArrayList< String >() );
                    }
                }
            }
            
            br.close(); // close the stream
            
            tags = data; // change the data
            
            return true;
        }
        catch ( Exception e ) {
            return false;
        }
    }
    
    //
    // Setters
    //
    
    /**
     * Sets the tag data.
     * 
     * @param tagName
     *            The name of the tag.
     * @param data
     *            The new data for the tag.
     */
    public void setTagData( String tagName, String... data ) {
        List< String > list = new ArrayList<>();
        for ( String s : data ) {
            list.add( s );
        }
        
        setTagData( tagName, list );
    }
    
    /**
     * Sets the tag data.
     * 
     * @param tagName
     *            The name of the tag.
     * @param data
     *            The new data for the tag.
     */
    public void setTagData( String tagName, List< String > data ) {
        tags.put( tagName, data );
    }
    
    //
    // Getters
    //
    
    /**
     * Gets the tag data.
     * 
     * @param tagName
     *            The name of the tag.
     * @return An empty list if there is no tag data, otherwise the tag's data.
     */
    public List< String > getTagData( String tagName ) {
        List< String > data = tags.get( tagName );
        return data == null ? new ArrayList< String >() : data;
    }
    
}
