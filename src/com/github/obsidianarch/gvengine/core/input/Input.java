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
    // Constants
    //
    
    public static final String                       MOVE_FORWARD  = "move.forward";
    
    public static final String                       MOVE_BACKWARD = "move.backward";
    
    public static final String                       MOVE_LEFT     = "move.left";
    
    public static final String                       MOVE_RIGHT    = "move.right";
    
    public static final String                       MOVE_SPRINT   = "move.spring";
    
    //
    // Fields
    //
    
    private static final Map< String, InputBinding > bindings      = new HashMap<>();
    
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
        
        // set the defaults
        setBinding( MOVE_FORWARD, InputBindingMode.KEYBOARD, Keyboard.KEY_W );
        setBinding( MOVE_LEFT, InputBindingMode.KEYBOARD, Keyboard.KEY_A );
        setBinding( MOVE_BACKWARD, InputBindingMode.KEYBOARD, Keyboard.KEY_S );
        setBinding( MOVE_RIGHT, InputBindingMode.KEYBOARD, Keyboard.KEY_D );
        setBinding( MOVE_SPRINT, InputBindingMode.KEYBOARD, Keyboard.KEY_LSHIFT );
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
        bindings.put( action, binding );
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
        InputBinding binding = bindings.get( action ); // get the binding from the map
        
        if ( binding == null ) {
            return false; // the action isn't actually bound to any InputBinding
        }
        
        return binding.isActive(); // return if the binding is active or not
    }
    
}
