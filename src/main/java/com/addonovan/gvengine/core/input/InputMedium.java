package com.addonovan.gvengine.core.input;

/**
 * The physical device which is bound to an action. (e.g. mouse, keyboard, controller).
 *
 * @author Austin
 * @version 14.08.02
 * @since 14.03.30
 */
public enum InputMedium
{

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
     *         The button's number.
     *
     * @return If the button is pressed for this input medium.
     */
    public boolean isButtonDown( int button )
    {

        // TODO update with GLFW input handling
        switch ( this )
        {

            case KEYBOARD:
                return false;
//                return Keyboard.isKeyDown( button );

            case MOUSE:
                return false;
//                return Mouse.isButtonDown( button );

            case CONTROLLER:
                return false;

            default:
                return false;

        }
    }

}
