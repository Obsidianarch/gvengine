package com.github.obsidianarch.gvengine.core.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.github.obsidianarch.gvengine.io.Config;

/**
 * @author Austin
 */
public final class Input {
    
    //
    // Fields
    //
    
    private static final Map< String, InputBinding > bindings = new HashMap<>();
    
    //
    // Actions
    //
    
    /**
     * Initializes the input controls, loads the controllers.
     */
    public static void initialize() {
        try {
            Controllers.create();
        }
        catch ( LWJGLException e ) {
            System.err.println( " Failed to create controllers:  " + e.getClass().getName() );
        }
    }
    
    /**
     * Polls the keyboard, mouse, and all controllers.
     */
    public static void poll() {
        Keyboard.poll();
        Mouse.poll();
        Controllers.poll();
    }
    
    /**
     * Removes all bindings.
     */
    public static void clearBindings() {
        bindings.clear();
    }
    
    //
    // I/O 
    //
    
    /**
     * Adds all input bindings to the configuration object.<BR>
     * This does not save the bindings, rather it merely adds them to the configuration
     * object for later reading and writing.
     * 
     * @param c
     *            The configuration object.
     */
    public static void addBindings( Config c ) {
        List< String > data = new ArrayList<>(); // the list of bindings
        
        // add the bindigns to the list
        for ( Map.Entry< String, InputBinding > entry : bindings.entrySet() ) {
            data.add( entry.getKey() + "=" + entry.getValue().toString() ); // write <key>=<value>
        }
        
        c.setTagData( "INPUT", data ); // set the tag data
    }
    
    /**
     * Loads the input bindings from the configuration object.<BR>
     * The input bindings will be read from the configuration object's data, however any
     * bindings that already exist will be cleared.
     * 
     * @param c
     *            The configuration object.
     * @return The total number of bindings loaded.
     */
    public static int loadBindings( Config c ) {
        bindings.clear();
        
        List< String > data = c.getTagData( "INPUT" ); // get the input data from the config object
        
        int loadedBindings = 0;
        for ( String s : data ) {
            if ( !s.contains( "=" ) ) continue;
            String[] split = s.split( "=" );
            
            try {
                setBinding( split[ 0 ], new InputBinding( split[ 1 ] ) );
                loadedBindings++;
            }
            catch ( Exception e ) {
                System.out.println( "Failed to load InputBinding from string [" + s + "]" );
            }
        }
        
        return loadedBindings;
    }
    
    //
    // Setters
    //
    
    /**
     * Sets the InputBinding that will trigger the action.
     * 
     * @param action
     *            The action that will be triggered.
     * @param mode
     *            The device that will be giving input.
     * @param button
     *            The button on the device to listen for.
     */
    public static void setBinding( String action, InputBindingMode mode, int button ) {
        InputBinding binding = new InputBinding( mode, button ); // create the input binding
        setBinding( action, binding );
    }
    
    /**
     * Sets the InputBinding that will trigger the action.
     * 
     * @param action
     *            The action that will be triggered.
     * @param binding
     *            The binding which will fire the action.
     */
    public static void setBinding( String action, InputBinding binding ) {
        bindings.put( action.toLowerCase().trim(), binding );
    }
    
    //
    // Getters
    //
    
    /**
     * Returns the InputBinding with the given name.
     * 
     * @param action
     *            The InputBinding's action name.
     * @return The InputBinding bound to the action.
     */
    public static InputBinding getInputBinding( String action ) {
        return bindings.get( action.toLowerCase().trim() );
    }
    
    /**
     * Determines if the binding to the action is currently active or not.
     * 
     * @param action
     *            The action.
     * @return If the action has been triggered.
     */
    public static boolean isBindingActive( String action ) {
        InputBinding binding = bindings.get( action.toLowerCase().trim() ); // get the binding from the map
        
        if ( binding == null ) return false; // the action doesn't exist
            
        return binding.isActive(); // return if the binding is active or not
    }
    
}
