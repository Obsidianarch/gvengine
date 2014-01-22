package com.github.obsidianarch.gvengine.core.input;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

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
        
        // TODO load this from a file eventually
    }
    
    /**
     * Polls the keyboard, mouse, and all controllers.
     */
    public static void poll() {
        Keyboard.poll();
        Mouse.poll();
        Controllers.poll();
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
        bindings.put( action.toLowerCase(), binding );
    }
    
    //
    // Getters
    //
    
    /**
     * Determines if the binding to the action is currently active or not.
     * 
     * @param action
     *            The action.
     * @return If the action has been triggered.
     */
    public static boolean isBindingActive( String action ) {
        InputBinding binding = bindings.get( action.toLowerCase() ); // get the binding from the map
        
        if ( binding == null ) {
            return false; // the action isn't actually bound to any InputBinding
        }
        
        return binding.isActive(); // return if the binding is active or not
    }
    
}
