package com.github.obsidianarch.gvengine.core.input;

/**
 * @author Austin
 */
public enum InputMask {
    
    /**
     * The InputBinding has not additional requirements for key masks.
     */
    NO_MASK,
    /**
     * The InputBinding is only active when a control key is being pressed.
     */
    CONTROL_MASK,
    /**
     * The InputBinding is only active when an alt key is being pressed.
     */
    MENU_MASK,
    /**
     * The InputBinding is only active when a meta key is being pressed.
     */
    META_MASK;
    
}
