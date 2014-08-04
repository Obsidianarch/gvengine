package com.github.obsidianarch.gvengine.core.io;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple PlainText implementation of ConfigurationFormat where the information for tags are stored
 * between tags which fit the formatted string of "[%s]%n" and "[END]".
 *
 * @since 14.08.03
 * @version 14.08.03
 */
public class PlainTextConfigurationFormat implements ConfigurationFormat
{

    @Override
    public String getSanitizedKey( String input )
    {
        input = input.trim(); // remove excess whitespace
        input = input.toLowerCase(); // convert to lower case
        input = input.replace( "(=|\r|\n)", "?" ); // replace invalid characters with a question mark

        return input;
    }

    @Override
    public boolean read( File file, HashMap< String, HashMap< String, String > > tags )
    {
        try
        {
            BufferedReader br = new BufferedReader( new FileReader( file ) ); // reads data from the file

            Map< String, HashMap< String, String > > data = new HashMap<>(); // contains all of the read data

            String currentTag = null; // the name of the current property

            String line; // the last read line
            while ( ( line = br.readLine() ) != null )
            {

                line = line.trim();

                // stop the reading
                if ( line.equals( "[END]" ) )
                {
                    currentTag = null;
                    continue;
                }

                if ( currentTag != null )
                {

                    HashMap< String, String > props = data.get( currentTag );

                    String[] split = line.split( "=" ); // split it between key and value
                    props.put( split[ 0 ], split[ 1 ] ); // add the key and value to the map
                }
                else
                {
                    // beings with a '[' and ends with an ']'
                    if ( line.startsWith( "[" ) && line.endsWith( String.format( "]" ) ) )
                    {
                        currentTag = line.substring( 1, line.lastIndexOf( "]" ) ); // change the current property
                        data.put( currentTag, new HashMap< String, String >() );
                    }
                }
            }

            br.close(); // close the stream

            // change the data
            tags.clear(); // remove all previous entries
            tags.putAll( data ); // put all the new data into this map

            return true;
        }
        catch ( FileNotFoundException e )
        {
            // TODO when logging is added log an error message
            return false;
        }
        catch ( IOException e )
        {
            // TODO when logging is added log an error message
            return false;
        }
    }

    @Override
    public boolean write( File file, HashMap< String, HashMap< String, String > > tags )
    {
        try
        {
            // prepare the data before writing
            StringBuilder sb = new StringBuilder();
            for ( Map.Entry< String, HashMap< String, String > > entry : tags.entrySet() )
            {

                // prepare yourself for some beautiful lambda statements and string formatters
                sb.append( "[" ).append( entry.getKey() ).append( "]\n" ); // append the start tag [%TAGNAME%]\n

                // append all the properties in the format %KEY%=%VALUE%\n
                entry.getValue().entrySet().forEach(
                        ( property ) -> sb.append( property.getKey() ).append( '=' ).append( property.getValue() ).append( '\n' ) // add line
                );

                sb.append( "[END]\n\n" ); // append the ending tag
            }

            BufferedWriter bw = new BufferedWriter( new FileWriter( file ) ); // writes the data to the file
            bw.write( sb.toString() ); // write the data
            bw.close(); // close the stream

            return true;
        }
        catch ( IOException e )
        {
            // TODO when logging is added add an error message
            return false;
        }
    }
}
