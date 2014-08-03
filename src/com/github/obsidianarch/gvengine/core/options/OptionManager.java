package com.github.obsidianarch.gvengine.core.options;

import com.github.obsidianarch.gvengine.io.Config;
import com.github.obsidianarch.gvengine.io.Lumberjack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Gathers and organizes all option annotations.
 *
 * @author Austin
 * @version 14.08.03b
 * @since 14.03.30
 */
public class OptionManager
{

    //
    // Constants
    //

    /**
     * Tag used to denote OptionManager data in a Config file.
     */
    public static final String CONFIG_TAG = "OPTIONS";

    //
    // Constructors
    //

    /**
     * Doesn't do anything other than hide the constructor.
     *
     * @since 14.03.30
     */
    private OptionManager()
    {
    }

    //
    // Fields
    //

    /**
     * The only instance of OptionManager, this is used to get values from fields.
     */
    private static final OptionManager instance = new OptionManager();

    /**
     * Map of option descriptions and the field to which they belong.
     */
    private static HashMap< String, Field > optionFields = new HashMap<>();

    /**
     * These methods will be fired when the modified field has been changed.
     */
    private static HashMap< String, ArrayList< Method > > changeListeners = new HashMap<>();

    /**
     * The default values passed by commandline.
     */
    private static HashMap< String, String > defaultValues = new HashMap<>();

    //
    // Initializer
    //

    /**
     * Initializes the OptionManager, this will take parse the commandline arguments passed to the program. An option argument must follow this format:<br />
     * {@code "-O:" + fieldName + ":" + value}
     *
     * @param args
     *         The arguments passed by commandline.
     *
     * @since 14.03.30
     */
    public static void initialize( String... args )
    {
        for ( String s : args )
        {
            if ( !s.startsWith( "-O:" ) )
            {
                continue; // not a valid OptionManager flag
            }
            s = s.substring( "-O:".length() ); // remove the -O: prefix

            String[] splitArg = s.split( "=" );
            if ( splitArg.length != 2 )
            {
                continue; // there should be only 2 items
            }

            String fieldName = splitArg[ 0 ]; // the first value is the field name
            String value = splitArg[ 1 ]; // the second value is the field value

            defaultValues.put( fieldName, value ); // add the default value to the manager
        }
    }

    /**
     * Reads the options from the specified file.
     *
     * @param c
     *         The configuratin object which contains default values for options.
     *
     * @since 14.03.30
     */
    public static void initialize( Config c )
    {
        HashMap< String, String > data = c.getTagData( CONFIG_TAG );
        data.forEach( defaultValues::put );
    }

    //
    // Setters
    //

    /**
     * Gets the value for the option with the given description.
     *
     * @param optionDescription
     *         The description of the option.
     *
     * @return {@code null} if there was no option with the given description, or if there was a problem getting the value from the field, otherwise the value
     * of the field.
     *
     * @since 14.03.30
     */
    public static Object getValue( String optionDescription )
    {
        Field field = optionFields.get( optionDescription );
        if ( field == null )
        {
            return null; // avoids an NPE
        }

        try
        {
            return field.get( instance );
        }
        catch ( IllegalArgumentException | IllegalAccessException | SecurityException e )
        {
            Lumberjack.throwable( "PropGet", e );
            return null; // this will hopefully never be thrown
        }
    }

    /**
     * Changes the value of the Option described.
     *
     * @param optionDescription
     *         The description of the option.
     * @param value
     *         The Option's new value.
     *
     * @since 14.03.30
     */
    public static void setValue( String optionDescription, Object value )
    {
        Field field = optionFields.get( optionDescription );
        if ( field == null )
        {
            return; // avoids an NPE
        }

        try
        {

            // if the field is an auto value changing field, automatically change the value
            if ( field.getAnnotation( Option.class ).autoValueChange() )
            {
                field.set( instance, value );
            }

            // fire the option change listeners for this option
            fireOptionListener( optionDescription, value );
        }
        catch ( IllegalArgumentException | IllegalAccessException e )
        {
            Lumberjack.throwable( "PropSet", e );
        }
    }

    //
    // Option Change Listeners
    //

    /**
     * Adds an option change listener for the given method.
     *
     * @param s
     *         The field's description to add a listener to.
     * @param m
     *         The listener.
     *
     * @since 14.03.30
     */
    private static void addOptionListener( String s, Method m )
    {
        ArrayList< Method > methods = changeListeners.get( s ); // get the pre-existing list

        // if there is no list, create one
        if ( methods == null )
        {
            methods = new ArrayList<>();
        }

        methods.add( m ); // add the method to the method list
        changeListeners.put( s, methods ); // reassign the list
    }

    /**
     * Fires all the OptionListeners attached to the given field.
     *
     * @param s
     *         The Option description whose OptionListeners need to be fired.
     * @param o
     *         The new value of the option
     *
     * @since 14.03.30
     */
    private static void fireOptionListener( String s, Object o )
    {
        List< Method > methods = changeListeners.get( s );
        if ( methods == null )
        {
            return; // there are no listeners bound to the Option
        }

        for ( Method method : methods )
        {
            try
            {
                if ( method.getParameterCount() == 1 )
                {
                    method.invoke( instance, o ); // invoke the method with the new value as a parameter
                }
                else
                {
                    method.invoke( instance ); // invoke it with no parameters
                }
            }
            catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e )
            {
                Lumberjack.throwable( "PropListExec", e );
            }
        }
    }

    //
    // Registers
    //

    /**
     * Registers an instanced class for option management. This is used when the options are instance fields.
     *
     * @param identifier
     *         The string identifier for the class.
     * @param o
     *         The instance of the class which contains options.
     *
     * @since 14.03.30
     */
    public static void registerInstance( String identifier, Object o )
    {
        Field[] fields = o.getClass().getDeclaredFields();
        Method[] methods = o.getClass().getDeclaredMethods();

        registerFields( identifier, fields, true );
        registerMethods( identifier, methods, true );
    }

    /**
     * Registers a static class for option management. This is used when the options are {@code static}.
     *
     * @param identifier
     *         The string identifier for the class.
     * @param clazz
     *         The class that contains static options.
     *
     * @since 14.03.30
     */
    public static void registerClass( String identifier, Class< ? > clazz )
    {
        Field[] fields = clazz.getDeclaredFields(); // get ONLY the fields that were declared in this class
        Method[] methods = clazz.getDeclaredMethods(); // get ONLY the methods that were declared in this class

        registerFields( identifier, fields, true ); // register the static fields
        registerMethods( identifier, methods, true ); // register the static methods 
    }

    /**
     * Registers fields iwth the OptionManager.
     *
     * @param identifier
     *         The string identifier for the class.
     * @param fields
     *         The list of fields to scan through.
     * @param needsStatic
     *         If the fields must be static.
     *
     * @since 14.03.30
     */
    private static void registerFields( String identifier, Field[] fields, boolean needsStatic )
    {
        for ( Field field : fields )
        {
            if ( !field.isAnnotationPresent( Option.class ) )
            {
                continue; // it MUST have an option annotation
            }
            if ( !Modifier.isPublic( field.getModifiers() ) )
            {
                continue; // options MUST be public
            }
            if ( needsStatic && !Modifier.isStatic( field.getModifiers() ) )
            {
                continue; // if required, the fields must be static
            }
            if ( Modifier.isFinal( field.getModifiers() ) )
            {
                continue; // options CANNOT be final
            }

            Lumberjack.debug( "Property", "%s.%s", identifier, field.getName() ); // start the option discovery log

            Option option = field.getAnnotation( Option.class ); // get the option annotation
            Lumberjack.debug( "Property", "  %s", getDescription( option ) ); // print the annotation's data

            if ( field.isAnnotationPresent( SliderOption.class ) )
            {

                SliderOption sliderOption = field.getAnnotation( SliderOption.class );
                Lumberjack.debug( "Property", "  %s", getDescription( sliderOption ) );
            }
            else if ( field.isAnnotationPresent( ToggleOption.class ) )
            {

                ToggleOption toggleOption = field.getAnnotation( ToggleOption.class );
                Lumberjack.debug( "Property", "  %s", getDescription( toggleOption ) );
            }
            else
            {
                Lumberjack.info( "Property", "%s has no option type annotation!", field.getName() );
            }

            // set the default value of the field, if it was set via commandline
            String fieldIdentifier = identifier + "." + field.getName();
            if ( defaultValues.containsKey( fieldIdentifier ) )
            {
                String value = defaultValues.get( fieldIdentifier );

                try
                {
                    Class< ? > clazz = field.getType(); // get the type of field this is

                    if ( clazz.equals( boolean.class ) )
                    {
                        field.setBoolean( null, Boolean.parseBoolean( value ) );
                    }
                    else if ( clazz.equals( byte.class ) )
                    {
                        field.setByte( null, Byte.parseByte( value ) );
                    }
                    else if ( clazz.equals( short.class ) )
                    {
                        field.setShort( null, Short.parseShort( value ) );
                    }
                    else if ( clazz.equals( int.class ) )
                    {
                        field.setInt( null, Integer.parseInt( value ) );
                    }
                    else if ( clazz.equals( long.class ) )
                    {
                        field.setLong( null, Long.parseLong( value ) );
                    }
                    else if ( clazz.equals( float.class ) )
                    {
                        field.setFloat( null, Float.parseFloat( value ) );
                    }
                    else if ( clazz.equals( double.class ) )
                    {
                        field.setDouble( null, Double.parseDouble( value ) );
                    }
                    else
                    {
                        field.set( null, value );
                    }

                    Lumberjack.debug( "Property", "  Set default value to \"%s\"", value );
                }
                catch ( IllegalArgumentException | IllegalAccessException e )
                {
                    Lumberjack.error( "Property", "Set for %s failed! Is the field non-final?", field.getName() );
                    Lumberjack.throwable( "Property", e );
                }
            }

            optionFields.put( option.value(), field ); // add the option to the list
        }
    }

    /**
     * Registers methods with the OptionManager.
     *
     * @param identifier
     *         The string identifier for the class.
     * @param methods
     *         The list of methods to scan through.
     * @param needsStatic
     *         If the methods must be static.
     *
     * @since 14.03.30
     */
    private static void registerMethods( String identifier, Method[] methods, boolean needsStatic )
    {
        for ( Method method : methods )
        {
            if ( !method.isAnnotationPresent( OptionListener.class ) )
            {
                continue; // an option listener MUST have the OptionListener annotation
            }
            if ( !Modifier.isPublic( method.getModifiers() ) )
            {
                continue; // an option listener MUST be public
            }
            if ( needsStatic && !Modifier.isStatic( method.getModifiers() ) )
            {
                continue; // if required, the methods must be static
            }
            if ( method.getParameterTypes().length > 1 )
            {
                continue; // an option listener has either none or 1 parameter
            }

            Lumberjack.debug( "PropList", "%s.%s()", identifier, method.getName() ); // start out log

            OptionListener listener = method.getAnnotation( OptionListener.class ); // get the option listener class
            String[] fieldNames = listener.value(); // get the names of the fields it will be listening for a property change to

            // iterate over every Option description the listener describes
            for ( String s : fieldNames )
            {
                addOptionListener( s, method ); // and attach the OptionListener to each
            }
        }
    }

    /**
     * Shortens the annotation's toString() method.
     *
     * @param o
     *         The annotation's default
     *
     * @return A shortened version of the annotation's toString().
     *
     * @since 14.03.30
     */
    private static String getDescription( Object o )
    {
        return o.toString().substring( "@com.github.obsidianarch.gvengine.core.options.".length() );
    }

}
