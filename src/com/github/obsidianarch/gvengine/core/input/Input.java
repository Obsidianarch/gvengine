package com.github.obsidianarch.gvengine.core.input;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
    // Fields
    //
    
    private static final Map< String, InputBinding > bindings = new HashMap<>();
    
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
    }
    
    /**
     * Polls the keyboard, mouse, and all controllers.
     */
    public static void poll() {
        Keyboard.poll();
        Mouse.poll();
        Controllers.poll();
    }
    
    /**
     * Removes all bindings.
     */
    public static void clearBindings() {
        bindings.clear();
    }
    
    //
    // I/O 
    //
    
    /**
     * Saves the input bindings to their own file.
     * 
     * @param f
     *            The file to which the InputBindings will be saved.
     * @return If the save was successful or not.
     */
    public static boolean saveBindings( File f ) {
        f.delete(); // delete the previous file
        return saveBindings( f, false ); // save the file without appending
    }
    
    /**
     * Saves the input bindings to the provided file, the input bindings will be appended
     * to the end of the file if {@code append} is true.
     * 
     * @param f
     *            The file to which the InputBindings will be saved.
     * @param append
     *            If the InputBindings will be appended to the end of the file or not.
     * @return If the save was successful or not.
     */
    public static boolean saveBindings( File f, boolean append ) {
        try {
            BufferedWriter out = new BufferedWriter( new FileWriter( f, append ) ); // create the buffered writer so we can start writing to the file
            
            out.write( String.format( "%n[INPUT]%n" ) ); // add an extra line of spacing before the title
            
            for ( Map.Entry< String, InputBinding > entry : bindings.entrySet() ) {
                out.write( String.format( "  %s=%s%n", entry.getKey(), entry.getValue().toString() ) ); // add an entry to the file
            }
            
            out.write( String.format( "[END]%n" ) ); // add an extra line of spacing after the end tag
            
            out.close(); // close our resources so we don't waste them
            
            return true;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Loads input bindings from the file. This will scan the entire file for the input
     * bindings section.
     * 
     * @param f
     *            The file in which InputBindings have been saved.
     * @return The number of bindings read from the file.
     */
    public static int loadBindings( File f ) {
        try {
            BufferedReader in = new BufferedReader( new FileReader( f ) ); // create the buffered reader so we can start reading from the file
            
            int lineNumber = 0;
            int readBindings = 0;
            boolean bindingsStarted = false;
            String line;
            
            while ( ( line = in.readLine() ) != null ) {
                lineNumber++;
                
                if ( line.contains( "[INPUT]" ) ) {
                    bindingsStarted = true; // we now start reading bindings
                    continue;
                }
                
                if ( line.contains( "[END]" ) ) {
                    break;
                }
                
                if ( bindingsStarted ) {
                    if ( !line.contains( "=" ) ) {
                        System.err.println( "Invalid input binding line in \"" + f.getName() + "\" on line " + lineNumber );
                        continue;
                    }
                    
                    String ibAction = line.substring( 2, line.lastIndexOf( '=' ) );
                    String ibData = line.substring( line.lastIndexOf( '=' ) + 1 );
                    
                    InputBinding binding = new InputBinding( ibData );
                    
                    setBinding( ibAction, binding ); // add the binding to the map of bindings
                    readBindings++;
                }
            }
            
            in.close();
            
            return readBindings;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return 0;
        }
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
        setBinding( action, binding );
    }
    
    /**
     * Sets the InputBinding that will trigger the action.
     * 
     * @param action
     *            The action that will be triggered.
     * @param binding
     *            The binding which will fire the action.
     */
    public static void setBinding( String action, InputBinding binding ) {
        bindings.put( action.toLowerCase().trim(), binding );
    }
    
    //
    // Getters
    //
    
    /**
     * Returns the InputBinding with the given name.
     * 
     * @param action
     *            The InputBinding's action name.
     * @return The InputBinding bound to the action.
     */
    public static InputBinding getInputBinding( String action ) {
        return bindings.get( action.toLowerCase().trim() );
    }
    
    /**
     * Determines if the binding to the action is currently active or not.
     * 
     * @param action
     *            The action.
     * @return If the action has been triggered.
     */
    public static boolean isBindingActive( String action ) {
        InputBinding binding = bindings.get( action.toLowerCase().trim() ); // get the binding from the map
        
        if ( binding == null ) return false; // the action doesn't exist
            
        return binding.isActive(); // return if the binding is active or not
    }
    
}
