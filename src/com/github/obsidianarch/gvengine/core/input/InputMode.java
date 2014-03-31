package com.github.obsidianarch.gvengine.core.input;

/**
 * An enum describing the various ways which input can be accepted, giving a larger
 * variety of input commands that can be interpreted separately.
 * 
 * @author Austin
 * 
 * @since 14.03.30
 * @version 14.03.30
 */
public enum InputMode {
    
    /**
     * This indicates that the InputBinding is active when the button is down.
     */
    BUTTON_DOWN,
    /**
     * This indicates that the InputBinding is active when the button is up.
     */
    BUTTON_UP,
    /**
     * This indicates that the InputBinding is active only when the button was just
     * pressed.
     */
    BUTTON_PRESSED,
    /**
     * This indicates that the InputBinding is active only when the button was just
     * released.
     */
    BUTTON_RELEASED;
    
}
