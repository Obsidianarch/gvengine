package com.github.obsidianarch.gvengine.core.input;

/**
 * @author Austin
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
