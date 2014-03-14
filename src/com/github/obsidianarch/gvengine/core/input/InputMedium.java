package com.github.obsidianarch.gvengine.core.input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author Austin
 */
public enum InputMedium {

    /**
     * The input binding is using the keyboard.
     */
    KEYBOARD,
    /**
     * The input binding is using the mouse.
     */
    MOUSE,
    /**
     * The input binding is using a controller.
     */
    CONTROLLER;
    
    /**
     * @param button
     *            The button's number.
     * @return If the button is pressed for this input medium.
     */
    public boolean isButtonDown( int button ) {

        switch ( this ) {
        
        case KEYBOARD:
            return Keyboard.isKeyDown( button );
            
        case MOUSE:
            return Mouse.isButtonDown( button );
            
        case CONTROLLER:
            return false;
            
        default:
            return false;

        }
    }

}
