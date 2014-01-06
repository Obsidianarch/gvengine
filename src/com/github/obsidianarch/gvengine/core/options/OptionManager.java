package com.github.obsidianarch.gvengine.core.options;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gathers and organizes all preference annotations.
 * 
 * @author Austin
 */
public class OptionManager {
    
    //
    // Constructors
    //
    
    /**
     * Doesn't do anything other than hide the constructor.
     */
    private OptionManager() {
    }
    
    //
    // Constants
    //
    
    public static final String                   FPS_CAP         = "FPS Cap";
    public static final String                   VSYNC_ENABLED   = "VSync";
    
    //
    // Fields
    //
    
    /** The only instance of OptionManager, this is used to get values from fields. */
    private static final OptionManager           instance        = new OptionManager();
    
    /** Map of option descriptions and the field to which they belong. */
    private static Map< String, Field >          optionFields    = new HashMap<>();
    
    /** These methods will be fired when the modified field has been changed. */
    private static Map< String, List< Method > > changeListeners = new HashMap<>();
    
    //
    // Setters
    //
    
    /**
     * Gets the value for the option with the given description.
     * 
     * @param optionDescription
     *            The description of the option.
     * @return {@code null} if there was no option with the given description, or if there
     *         was a problem getting the value from the field, otherwise the value of the
     *         field.
     */
    public static Object getValue( String optionDescription ) {
        Field field = optionFields.get( optionDescription );
        if ( field == null ) return null; // avoids an NPE
            
        try {
            return field.get( instance );
        }
        catch ( IllegalArgumentException | IllegalAccessException | SecurityException e ) {
            e.printStackTrace();
            return null; // this will hopefully never be thrown
        }
    }
    
    /**
     * Changes the value of the Option described.
     * 
     * @param optionDescription
     *            The description of the option.
     * @param value
     *            The Option's new value.
     */
    public static void setValue( String optionDescription, Object value ) {
        Field field = optionFields.get( optionDescription );
        if ( field == null ) ; // avoids an NPE
            
        try {
            field.set( instance, value );
            fireOptionListener( optionDescription );
        }
        catch ( IllegalArgumentException | IllegalAccessException e ) {
            e.printStackTrace();
        }
    }
    
    //
    // Option Change Listeners
    //
    
    /**
     * Adds an option change listener for the given method.
     * 
     * @param s
     *            The field's description to add a listener to.
     * @param m
     *            The listener.
     */
    private static void addOptionListener( String s, Method m ) {
        List< Method > methods = changeListeners.get( s ); // get the pre-existing list
        
        // if there is no list, create one
        if ( methods == null ) methods = new ArrayList<>();
        
        methods.add( m ); // add the method to the method list
        changeListeners.put( s, methods ); // reassign the list
    }
    
    /**
     * Fires all the OptionListeners attached to the given field.
     * 
     * @param s
     *            The Option description whose OptionListeners need to be fired.
     */
    private static void fireOptionListener( String s ) {
        List< Method > methods = changeListeners.get( s );
        if ( methods == null ) return; // there are no listeners bound to the Option
            
        for ( Method method : methods ) {
            try {
                method.invoke( instance );// since OptionListeners have already been checked for no parameters, this won't provide any
            }
            catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
                e.printStackTrace();
            }
        }
    }
    
    //
    // Registers
    //
    
    /**
     * Registers an instanced class for option management. This is used when the options
     * are instance fields.
     * 
     * @param o
     *            The instance of the class which contains options.
     */
    public static void registerInstance( Object o ) {
        Class< ? > clazz = o.getClass(); // get the class of the object
        Field[] fields = clazz.getDeclaredFields(); // get ONLY the fields that were declared in this class
        
        for ( Field field : fields ) {
            if ( !field.isAnnotationPresent( Option.class ) ) continue; // it MUST have an option annotation
            if ( !Modifier.isPublic( field.getModifiers() ) ) continue; // options MUST be public
            if ( Modifier.isFinal( field.getModifiers() ) ) continue; // options CANNOT be final
                
            System.out.println( clazz.getName() + "." + field.getName() ); // start the option discovery log
            
            Option option = field.getAnnotation( Option.class ); // get the option annotation
            System.out.println( " > " + toString( option ) ); // print the annotation's data
            
            // TODO later get the positioning data from the option annotation
            // TODO actually use these annotations later
            
            if ( field.isAnnotationPresent( SliderOption.class ) ) {
                
                SliderOption sliderOption = field.getAnnotation( SliderOption.class );
                System.out.println( " > " + toString( sliderOption ) );
            }
            else if ( field.isAnnotationPresent( ToggleOption.class ) ) {
                
                ToggleOption toggleOption = field.getAnnotation( ToggleOption.class );
                System.out.println( " > " + toString( toggleOption ) );
            }
            else {
                System.out.println( " > No other option annotations found, is the option annotation supposed to be here?" );
            }
            
            optionFields.put( option.description(), field ); // add the option to the list
        }
        
        Method[] methods = clazz.getDeclaredMethods(); // get ONLY the methods that were declared in this class
        
        for ( Method method : methods ) {
            if ( !method.isAnnotationPresent( OptionListener.class ) ) continue; // an option listener MUST have the OptionListener annotation
            if ( !Modifier.isPublic( method.getModifiers() ) ) continue; // an option listener MUST be public
            if ( method.getParameterTypes().length != 0 ) continue; // an option listener MUST have zero parameters
                
            System.out.println( clazz.getName() + "." + method.getName() + "()" ); // start our log
            
            OptionListener listener = method.getAnnotation( OptionListener.class ); // get the option listener class
            String[] fieldNames = listener.fields(); // get the names of the fields it will be listening for a property change to
            
            for ( String s : fieldNames ) { // iterate over every Option description the listener describes
                addOptionListener( s, method ); // and attach the OptionListener to each
            }
        }
    }
    
    /**
     * Registers a static class for option management. This is used when the options are
     * {@code static}.
     * 
     * @param clazz
     *            The class that contains static options.
     */
    public static void registerClass( Class< ? > clazz ) {
        Field[] fields = clazz.getDeclaredFields(); // get ONLY the fields that were declared in this class
        
        for ( Field field : fields ) {
            if ( !field.isAnnotationPresent( Option.class ) ) continue; // it MUST have an option annotation
            if ( !Modifier.isPublic( field.getModifiers() ) ) continue; // options MUST be public
            if ( !Modifier.isStatic( field.getModifiers() ) ) continue; // options MUST be static
            if ( Modifier.isFinal( field.getModifiers() ) ) continue; // options CANNOT be final
                
            System.out.println( clazz.getName() + "." + field.getName() ); // start the option discovery log
            
            Option option = field.getAnnotation( Option.class ); // get the option annotation
            System.out.println( " > " + toString( option ) ); // print the annotation's data
            
            // TODO later get the positioning data from the option annotation
            // TODO actually use these annotations later
            
            if ( field.isAnnotationPresent( SliderOption.class ) ) {
                
                SliderOption sliderOption = field.getAnnotation( SliderOption.class );
                System.out.println( " > " + toString( sliderOption ) );
            }
            else if ( field.isAnnotationPresent( ToggleOption.class ) ) {
                
                ToggleOption toggleOption = field.getAnnotation( ToggleOption.class );
                System.out.println( " > " + toString( toggleOption ) );
            }
            else {
                System.out.println( " > No other option annotations found, is the option annotation supposed to be here?" );
            }
            
            optionFields.put( option.description(), field ); // add the option to the list
        }
        
        Method[] methods = clazz.getDeclaredMethods(); // get ONLY the methods that were declared in this class
        
        for ( Method method : methods ) {
            if ( !method.isAnnotationPresent( OptionListener.class ) ) continue; // an option listener MUST have the OptionListener annotation
            if ( !Modifier.isPublic( method.getModifiers() ) ) continue; // an option listener MUST be public
            if ( !Modifier.isStatic( method.getModifiers() ) ) continue; // an option listener MUST be static
            if ( method.getParameterTypes().length != 0 ) continue; // an option listener MUST have zero parameters
                
            System.out.println( clazz.getName() + "." + method.getName() + "()" ); // start our log
            
            OptionListener listener = method.getAnnotation( OptionListener.class ); // get the option listener class
            String[] fieldNames = listener.fields(); // get the names of the fields it will be listening for a property change to
            
            for ( String s : fieldNames ) { // iterate over every Option description the listener describes
                addOptionListener( s, method ); // and attach the OptionListener to each
            }
        }
    }
    
    //
    // Converters
    //
    
    /**
     * Shortens the annotations toString() method.
     * 
     * @param s
     *            The annotation's default
     * @return A shortened version of the annotation's toString().
     */
    private static String toString( Object o ) {
        return o.toString().substring( "@com.github.obsidianarch.gvengine.core.options.".length() );
    }
    
}
