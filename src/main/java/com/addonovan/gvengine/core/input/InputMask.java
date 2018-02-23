package com.addonovan.gvengine.core.input;

/**
 * Keyboard masks (also called modifiers) that are required in addition to the other input (e.g. clicking a mouse with the control key down).
 *
 * @author Austin
 * @version 14.08.02
 * @since 14.03.30
 */
public enum InputMask
{

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
