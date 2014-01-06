package com.github.obsidianarch.gvengine.core.input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author Austin
 */
public class InputBinding {
    
    //
    // Fields
    //
    
    /** The type of binding this is. */
    private final InputBindingMode mode;
    
    /** The button the action is bound to. */
    private final int              button;
    
    //
    // Constructors
    //
    
    /**
     * Creates an InputBinding.
     * 
     * @param mode
     *            The medium through which the input is controlled.
     * @param button
     *            The button which triggers this binding.
     */
    public InputBinding( InputBindingMode mode, int button ) {
        this.mode = mode;
        this.button = button;
    }
    
    //
    // Getters
    //
    
    /**
     * @return If this InputBinding is currently active or not.
     */
    public boolean isActive() {
        
        if ( mode == InputBindingMode.KEYBOARD ) {
            return Keyboard.isKeyDown( button );
        }
        else if ( mode == InputBindingMode.MOUSE ) {
            return Mouse.isButtonDown( button );
        }
        else if ( mode == InputBindingMode.CONTROLLER ) {
            // TODO controller input
            return false;
        }
        
        return false;
    }
    
}