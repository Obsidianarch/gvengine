package com.github.obsidianarch.gvengine.core.io;

import com.github.obsidianarch.gvengine.core.options.IntOption;
import com.github.obsidianarch.gvengine.core.options.Option;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * The logging utility used to display messages in the console and write to the log file.
 *
 * TODO maybe members will not be static in the future?
 *
 * @version 15.01.07
 * @since 14.08.03b
 */
public final class Lumberjack
{

    //
    // Constants
    //

    /**
     * The default file to write to, unless otherwise specified.
     */
    public static final Option< File > DefaultFile = new Option<>( new File( "gvengine.log" ) );

    /**
     * Avoids a method call every time something is written. I figured "why not make it public?" And couldn't think of a reason not to, so I did. I also
     * couldn't think of a reason to make it public, but that's not my problem.
     */
    public static final String LINE_SEPARATOR = System.lineSeparator();

    private static final DateFormat dateFormat = new SimpleDateFormat( "HH:mm:ss.SSS" );

    public static final int DEBUG = 0;

    public static final int INFO = 1;

    public static final int WARN = 2;

    public static final int ERROR = 3;

    private static final IntOption LoggingLevel = new IntOption( DEBUG )
    {

        @Override
        public void onChange()
        {
            if ( value > ERROR ) value = ERROR;
            if ( value < DEBUG ) value = DEBUG;
        }
    };

    //
    // Fields
    //

    /** The tag prepended to each message. */
    public final String tag;

    private File file;

    //
    // Constructors
    //

    /**
     * Constructs the Lumberjack for writing.
     *
     * @param tag
     *          The tag, prepended to the message.
     */
    private Lumberjack( String tag, File file )
    {
        this.tag = tag;
        this.file = file;
    }

    //
    // Actions
    //

    /**
     * Writes a debug message.
     *
     * @param format
     *          The string format.
     * @param params
     *          The string format parameters.
     */
    public void debug( String format, Object... params )
    {
        if ( LoggingLevel.get() > DEBUG ) return;
        formatAndWriteString( "DEBUG", String.format( format, params ) );
    }

    /**
     * Writes an info message.
     *
     * @param format
     *         The string format.
     * @param params
     *         The string format parameters.
     */
    public void info( String format, Object... params )
    {
        if ( LoggingLevel.get() > INFO ) return;
        formatAndWriteString( "INFO", String.format( format, params ) );
    }

    /**
     * Writes a warning message.
     *
     * @param format
     *         The string format.
     * @param params
     *         The string format parameters.
     */
    public void warn( String format, Object... params )
    {
        if ( LoggingLevel.get() > WARN ) return;
        formatAndWriteString( "WARN", String.format( format, params ) );
    }

    /**
     * Writes an error message.
     *
     * @param format
     *         The string format.
     * @param params
     *         The string format parameters.
     */
    public void error( String format, Object... params )
    {
        formatAndWriteString( "ERROR", String.format( format, params ) );
    }

    /**
     * Writes the stack trace of a Throwable.
     *
     * @param throwable
     *          The Throwable to print.
     */
    public void throwable( Throwable throwable )
    {
        error( "%s: %s", throwable.getClass(), throwable.getMessage() );
        for ( StackTraceElement e : throwable.getStackTrace() )
        {
            error( "    %s", e.toString() );
        }
    }

    /**
     * Formats the string
     *
     * @param level
     *          The message's level.
     * @param str
     *          The message to log.
     */
    private void formatAndWriteString( String level, String str )
    {
        writeToFile( file, dateFormat.format( new Date() ) + " " + "[" + tag + "] <" + level + "> " + str );
    }

    //
    // Static
    //

    /** A map of all opened BufferedWriters, linked to their file. */
    private static final HashMap< File, BufferedWriter > fileWriterMap = new HashMap<>();

    /**
     * Writes the text to the specified file.
     *
     * @param f
     *          The file to write to.
     * @param text
     *          The text to append text to.
     */
    private static void writeToFile( File f, String text )
    {
        try
        {
            // create the BufferedWriter on the spot
            if ( !fileWriterMap.containsKey( f ) )
            {
                fileWriterMap.put( f, new BufferedWriter( new FileWriter( f ) ) );
            }

            BufferedWriter bw = fileWriterMap.get( f );
            bw.write( text );
            bw.flush();
        }
        catch ( IOException e )
        {
            // print a buffer before and after the stack trace to draw attention
            // because it is being logged irregularly
            System.out.println( "\n" );
            e.printStackTrace();
            System.out.println( "\n" );
        }
    }

    /**
     * Closes all the file writers.
     */
    public static void closeAll()
    {
        for ( BufferedWriter bw : fileWriterMap.values() )
        {
            try
            {
                bw.close();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }

    //
    // Singleton-inator-thing
    //

    /** A map of all Lumberjack instance linked to the class which created them. */
    private static final HashMap< String, Lumberjack > instanceMap = new HashMap<>();

    /**
     * Returns the default Lumberjack object, writing to {@code DefaultFile.get()}.
     *
     * @return The default instance of Lumberjack.
     */
    public static Lumberjack getInstance()
    {
        return getInstance( null );
    }

    /**
     * Returns the Lumberjack object for the given class, a new one will be constructed,
     * writing to {@code DefaultFile.get()}, if one does not already exist.
     *
     * @param clazz
     *          The class to get the instance of Lumberjack for.
     *
     * @return The instance of Lumberjack for {@code clazz}.
     */
    public static Lumberjack getInstance( Class< ? > clazz )
    {
        return getInstance( clazz, DefaultFile.get() );
    }

    /**
     * Returns the Lumberjack object for the given class, a new one will be constructed,
     * writing to {@code outputFile}, if one does not already exist.
     *
     * @param clazz
     *          The class getting its instance of Lumberjack.
     * @param outputFile
     *          The file to write to (only used if an instance is being created).
     *
     * @return The instance of Lumberjack for {@code clazz}.
     */
    public static Lumberjack getInstance( Class< ? > clazz, File outputFile )
    {
        String className = ( clazz == null ? "default" : clazz.getSimpleName() );

        if ( !instanceMap.containsKey( className ) )
        {
            return instanceMap.put( className, new Lumberjack( className, outputFile ) );
        }
        else
        {
            return instanceMap.get( className );
        }
    }

}
