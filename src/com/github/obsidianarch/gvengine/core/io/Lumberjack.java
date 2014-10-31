package com.github.obsidianarch.gvengine.core.io;

import com.github.obsidianarch.gvengine.core.options.IntOption;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The logging utility used to display messages in the console and write to the log file.
 * <p>
 * TODO maybe members will not be static in the future?
 *
 * @version 14.08.03c
 * @since 14.08.03b
 */
public class Lumberjack
{

    //
    // Constants
    //

    /**
     * Lowest level of logging, messages involving variables and other stuff that isn't working.
     */
    public static final int DEBUG = 0;

    /**
     * Middle level of logging, can be used for warnings, and various other things.
     */
    public static final int INFO = 1;

    /**
     * Highest level of logging, always displayed. Used for errors and problems.
     */
    public static final int ERROR = 2;
    /**
     * Property for the current logging level.
     */
    private static IntOption LoggingLevel = new IntOption( DEBUG )
    {

        @Override
        public void onChange()
        {
            // mutate and access the value directly, skip the methods
            if ( value > ERROR )
            {
                value = ERROR;
            }
            if ( value < DEBUG )
            {
                value = DEBUG;
            }
        }

    };

    //
    // Options
    //
    /**
     * Avoids a method call every time something is written. I figured "why not make it public?" And couldn't think of a reason not to, so I did. I also
     * couldn't think of a reason to make it public, but that's not my problem.
     */
    public static final String LINE_SEPARATOR = System.lineSeparator();

    //
    // Fields
    //
    /**
     * The DateFormat used to format the current time in the logging messages
     */
    private static DateFormat dateFormat = new SimpleDateFormat( "HH:mm:ss.SSS" );

    /**
     * Writes the log to the file.
     */
    private static BufferedWriter bw;

    //
    // Initializer
    //

    /**
     * Attempts to open a new log file.
     *
     * @param f
     *         The new log file.
     *
     * @return If the opening was a success or not.
     */
    public static boolean openLogFile( File f )
    {
        try
        {
            // close the previous log
            if ( bw != null )
            {
                bw.close();
            }

            bw = new BufferedWriter( new FileWriter( f ) );
            return true;
        }
        catch ( IOException e )
        {
            return false;
        }
    }

    //
    // Logging
    //

    /**
     * Prints the information about the message.
     *
     * @param ps
     *         The PrintStream to write to.
     * @param level
     *         The level (DEBUG, INFO, ERROR).
     * @param tag
     *         The tag used for debugging.
     */
    private static void printInfo( PrintStream ps, String level, String tag, String format, Object... params )
    {
        // prepare the message to be written
        StringBuilder sb = new StringBuilder();
        sb.append( dateFormat.format( new Date() ) ).append( ' ' );  // add the formatted date
        sb.append( '[' ).append( tag ).append( "] " );               // add the task tag
        sb.append( '<' ).append( level ).append( "> " );             // add the message level
        sb.append( String.format( format, params ) );                // add the message

        String str = sb.toString(); // convert to string

        // print to console
        ps.print( str ); // print the information
        ps.println(); // go to the next line

        // write to the log
        if ( bw != null )
        {
            try
            {
                bw.write( str ); // write the message
                bw.write( LINE_SEPARATOR ); // add a line separator
                bw.flush(); // force it to write to the file (in case of a sudden crash)
            }
            catch ( IOException e )
            {
                // not really sure what to do here tbh, don't want to recursively get this so I guess I'll use standard error
                System.err.println( "LUMBERJACK: Couldn't write to file; you shouldn't see this." );
                e.printStackTrace();
            }
        }
    }

    /**
     * Formats the writes the debug message.
     *
     * @param tag
     *         Short message describing the task.
     * @param format
     *         The formatting string.
     * @param params
     *         The parameters passed to the String formatter to match the formatting.
     */
    public static void debug( String tag, String format, Object... params )
    {
        // ignore this if it's too high
        if ( LoggingLevel.get() > DEBUG )
        {
            return;
        }

        // print the information to the console and log
        printInfo( System.out, "DEBUG", tag, format, params );
    }

    /**
     * Formats then writes the info message.
     *
     * @param tag
     *         Short message describing the task.
     * @param format
     *         The formatting string.
     * @param params
     *         The parameters passed to the String formatter to match the formatting.
     */
    public static void info( String tag, String format, Object... params )
    {
        // ignore this if it's too high
        if ( LoggingLevel.get() > INFO )
        {
            return;
        }

        printInfo( System.out, "INFO", tag, format, params );
    }

    /**
     * Formats then writes the error message.
     *
     * @param tag
     *         Short message describing the task.
     * @param format
     *         The formatting string.
     * @param params
     *         The parameters passed to the String formatter to match the formatting.
     */
    public static void error( String tag, String format, Object... params )
    {
        printInfo( System.err, "ERROR", tag, format, params );
    }

    /**
     * Logs a throwable and it's stack trace.
     *
     * @param tag
     *         The tag used for debugging.
     * @param t
     *         The throwable which was encountered.
     */
    public static void throwable( String tag, Throwable t )
    {
        String msg = t.getMessage(); // get the message from the Throwable
        error( tag, "%s: %s", t.getClass().toString(), ( msg == null ? "" : msg ) ); // print the class and then it's message

        // print the stack trace beneath it
        for ( StackTraceElement element : t.getStackTrace() )
        {
            error( tag, "  %s", element.toString() );
        }
    }

}
