package com.github.obsidianarch.gvengine.core.input;

import com.github.obsidianarch.gvengine.core.io.Config;
import com.github.obsidianarch.gvengine.core.io.Logger;
import com.github.obsidianarch.gvengine.core.io.Lumberjack;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.HashMap;

/**
 * The class for all input from the user.
 *
 * @author Austin
 * @version 15.01.07
 * @since 14.03.30
 */
public final class Input implements Logger
{

    //
    // Constants
    //

    /**
     * The tag for all Input bindings in a configuration file.
     */
    public static final String CONFIG_TAG = "INPUT";

    //
    // Fields
    //

    /**
     * All current bindings which have been defined.
     */
    private static final HashMap< String, InputBinding > bindings = new HashMap<>();

    //
    // Actions
    //

    /**
     * Initializes the input controls, loads the controllers.
     *
     * @since 14.03.30
     */
    public static void initialize()
    {
        try
        {
            Controllers.create();
        }
        catch ( LWJGLException e )
        {
            Lumberjack.getInstance( Input.class ).error( "Failed to create controllers!" );
            Lumberjack.getInstance( Input.class ).throwable( e );
        }
    }

    /**
     * Polls the keyboard, mouse, and all controllers.
     *
     * @since 14.03.30
     */
    public static void poll()
    {
        Keyboard.poll();
        Mouse.poll();
        Controllers.poll();
    }

    /**
     * Removes all bindings.
     *
     * @since 14.03.30
     */
    public static void clearBindings()
    {
        bindings.clear();
    }

    //
    // I/O 
    //

    /**
     * Adds all input bindings to the configuration object.<BR> This does not write the bindings, rather it merely adds them to the configuration object for
     * later reading and writing.
     *
     * @param c
     *         The configuration object.
     *
     * @since 14.03.30
     */
    public static void addBindings( Config c )
    {
        HashMap< String, String > data = new HashMap<>(); // the list of bindings

        // add the bindings to the list
        bindings.entrySet().forEach( ( entry ) -> data.put( entry.getKey(), entry.getValue().toString() ) );

        c.setTagData( CONFIG_TAG, data ); // set the tag data
    }

    /**
     * Loads the input bindings from the configuration object.<BR> The input bindings will be read from the configuration object's data, however any bindings
     * that already exist will be cleared.
     *
     * @param c
     *         The configuration object.
     *
     * @return The total number of bindings loaded.
     *
     * @since 14.03.30
     */
    public static int loadBindings( Config c )
    {
        bindings.clear(); // clear previous bindings

        HashMap< String, String > data = c.getTagData( CONFIG_TAG ); // get the input data from the config object
        data.forEach( ( key, value ) -> bindings.put( key, InputBinding.createInputBinding( value ) ) );

        return bindings.size();
    }

    //
    // Setters
    //

    /**
     * Creates a new InputBinding and assigns it to the given action name.
     *
     * @param action
     *         The name of the action.
     * @param medium
     *         The input medium the action is bound to.
     * @param mode
     *         How the input is expected.
     * @param mask
     *         Any input masks that are required.
     * @param button
     *         The button to be bound.
     *
     * @since 14.03.30
     */
    public static void setBinding( String action, InputMedium medium, InputMode mode, InputMask mask, int button )
    {
        InputBinding binding = new InputBinding( medium, mode, mask, button );
        setBinding( action, binding );
    }

    /**
     * Creates a new InputBinding and assigns it to the given action name. This is a convenience method for {@code setBinding(action, medium, mode,
     *InputMask.NO_MASK, button)}.
     *
     * @param action
     *         The name of the action.
     * @param medium
     *         The input medium the action is bound to.
     * @param mode
     *         How the input is expected.
     * @param button
     *         The button to be bound.
     *
     * @see #setBinding(String, InputMedium, InputMode, InputMask, int)
     * @since 14.03.30
     */
    public static void setBinding( String action, InputMedium medium, InputMode mode, int button )
    {
        setBinding( action, medium, mode, InputMask.NO_MASK, button );
    }

    /**
     * Creates a new InputBinding and assigns it to the given action name. This is a convenience method for {@code setBinding(action, medium,
     *InputMode.BUTTON_DOWN, InputMask.NO_MASK, button)} .
     *
     * @param action
     *         The name of the action.
     * @param medium
     *         The input medium the action is bound to.
     * @param button
     *         The button to be bound.
     *
     * @see #setBinding(String, InputMedium, InputMode, InputMask, int)
     * @since 14.03.30
     */
    public static void setBinding( String action, InputMedium medium, int button )
    {
        setBinding( action, medium, InputMode.BUTTON_DOWN, InputMask.NO_MASK, button );
    }

    /**
     * Sets the InputBinding that will trigger the action.
     *
     * @param action
     *         The action that will be triggered.
     * @param binding
     *         The binding which will fire the action.
     *
     * @since 14.03.30
     */
    public static void setBinding( String action, InputBinding binding )
    {
        bindings.put( action.toLowerCase().trim(), binding );
    }

    //
    // Getters
    //

    /**
     * Checks to see if an InputMask is active or not.
     *
     * @param mask
     *         The InputMask.
     *
     * @return If either keyboard key mask is down, or if {@code mask} is {@code NO_MASK}, {@code true}.
     *
     * @since 14.03.30
     */
    public static boolean isMaskActive( InputMask mask )
    {

        switch ( mask )
        {

            case CONTROL_MASK:
                return Keyboard.isKeyDown( Keyboard.KEY_LCONTROL ) || Keyboard.isKeyDown( Keyboard.KEY_RCONTROL );

            case MENU_MASK:
                return Keyboard.isKeyDown( Keyboard.KEY_LMENU ) || Keyboard.isKeyDown( Keyboard.KEY_RMENU );

            case META_MASK:
                return Keyboard.isKeyDown( Keyboard.KEY_LMETA ) || Keyboard.isKeyDown( Keyboard.KEY_RMETA );

            default:
                return !( isMaskActive( InputMask.CONTROL_MASK ) || isMaskActive( InputMask.MENU_MASK ) || isMaskActive( InputMask.META_MASK ) );

        }

    }

    /**
     * Returns the InputBinding with the given name.
     *
     * @param action
     *         The InputBinding's action name.
     *
     * @return The InputBinding bound to the action.
     *
     * @since 14.03.30
     */
    public static InputBinding getInputBinding( String action )
    {
        return bindings.get( action.toLowerCase().trim() );
    }

    /**
     * Determines if the binding to the action is currently active or not.
     *
     * @param action
     *         The action.
     *
     * @return If the action has been triggered.
     *
     * @since 14.03.30
     */
    public static boolean isBindingActive( String action )
    {
        InputBinding binding = bindings.get( action.toLowerCase().trim() ); // get the binding from the map

        return binding != null && binding.isActive();
    }

}
