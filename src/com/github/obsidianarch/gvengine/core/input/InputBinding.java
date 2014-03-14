package com.github.obsidianarch.gvengine.core.input;


/**
 * @author Austin
 */
public class InputBinding {
    
    //
    // Fields
    //
    
    /** The medium of input being bound. */
    private final InputMedium medium;

    /** The button the action is bound to. */
    private final int         button;
    
    /** The method of input we are expecting. */
    private final InputMode   mode;
    
    /** Are any modifier keys required to trigger this action. */
    private final InputMask   mask;

    /** Was the butotn down last time? */
    private boolean           lastDown;

    //
    // Constructors
    //
    
    /**
     * Creates an InputBinding.
     * 
     * @param medium
     *            The medium of input being bound.
     * @param mode
     *            The method we are expecting input.
     * @param mask
     *            Are any modifier keys required to trigger this action.
     * @param button
     *            The button which triggers this binding.
     */
    protected InputBinding( InputMedium medium, InputMode mode, InputMask mask, int button ) {
        this.medium = medium;
        this.button = button;
        this.mode = mode;
        this.mask = mask;
    }
    
    //
    // Getters
    //
    
    /**
     * @return The button number being used.
     */
    public final int getButton() {
        return button;
    }
    
    /**
     * @return The InputMedium the binding is using.
     */
    public final InputMedium getMedium() {
        return medium;
    }

    /**
     * @return The InputMode the binding is using.
     */
    public final InputMode getMode() {
        return mode;
    }
    
    /**
     * @return The InputMask the binding is using.
     */
    public final InputMask getMask() {
        return mask;
    }

    /**
     * @return If this InputBinding is currently active or not.
     */
    public boolean isActive() {
        boolean isDown = medium.isButtonDown( getButton() ); // is the key being pressed right now?
        boolean preMask = false; // would the keybinding be active without the key mask?
        boolean active = false; // is the keybinding active after the key mask?
        
        switch ( getMode() ) {
        
        case BUTTON_DOWN:
            preMask = isDown;
            
        case BUTTON_UP:
            preMask = !isDown;
            
        case BUTTON_PRESSED:
            preMask = !lastDown && isDown;
            
        case BUTTON_RELEASED:
            preMask = lastDown && !isDown;

        }
        
        active = preMask && Input.isMaskActive( getMask() );
        
        lastDown = isDown;
        return active;
    }
    
    //
    // Overrides
    //

    @Override
    public String toString() {
        String s = "";
        
        s += getMedium().ordinal();
        s += getMode().ordinal();
        s += getMask().ordinal();
        s += getButton();

        return s;
    }
    
    //
    // Static
    //
    
    /**
     * @param s
     *            The string input for an input binding.
     * @return The InputBinding described by the text string.
     */
    public static final InputBinding createInputBinding( String s ) {
        int[] info = new int[ 4 ]; // medium, mode, mask, button
        info[ 0 ] = Integer.parseInt( s.substring( 0, 1 ) );
        info[ 1 ] = Integer.parseInt( s.substring( 1, 2 ) );
        info[ 2 ] = Integer.parseInt( s.substring( 2, 3 ) );
        info[ 3 ] = Integer.parseInt( s.substring( 3 ) );

        InputMedium medium = InputMedium.values()[ info[ 0 ] ];
        InputMode mode = InputMode.values()[ info[ 1 ] ];
        InputMask mask = InputMask.values()[ info[ 2 ] ];
        int button = info[ 3 ];
        
        return new InputBinding( medium, mode, mask, button );
    }

}
