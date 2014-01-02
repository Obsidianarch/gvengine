package com.github.obsidianarch.gvengine.core.options;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.github.obsidianarch.gvengine.ChunkManager;

/**
 * Gathers and organizes all preference annotations.
 * 
 * @author Austin
 */
public class OptionFinder {
    
    /** All the classes that have preference annotations. */
    private static Class< ? >[] optionClasses = { ChunkManager.class };
    
    public static void loadOptions() {
        for ( Class< ? > clazz : optionClasses ) {
            
            Field[] fields = clazz.getFields(); // get all the fields in the class
            
            for ( Field field : fields ) {
                
                // preferences must be public and static & must have the option annotation, so skip the ones that aren't
                if ( !Modifier.isPublic( field.getModifiers() ) || !Modifier.isStatic( field.getModifiers() ) || !field.isAnnotationPresent( Option.class ) ) {
                    continue;
                }
                
                Option option = field.getAnnotation( Option.class ); // get the option annotation
                
                System.out.println( clazz.getName() + "." + field.getName() );
                System.out.println( " > " + option.toString().substring( option.toString().lastIndexOf( '.' ) + 1 ) );
                
                // TODO use these options when the option screens are implemented
                
                // if the field has a slider annotation
                if ( field.isAnnotationPresent( SliderOption.class ) ) {
                    SliderOption slider = field.getAnnotation( SliderOption.class ); // get the slider annotation
                    System.out.println( " > " + slider.toString().substring( slider.toString().lastIndexOf( '.' ) + 1 ) );
                }
                
                // if the field has a toggle annotation
                if ( field.isAnnotationPresent( ToggleOption.class ) ) {
                    ToggleOption toggle = field.getAnnotation( ToggleOption.class ); // get the toggle annotation
                    System.out.println( " > " + toggle.toString().substring( toggle.toString().lastIndexOf( '.' ) + 1 ) );
                }
                
            }
        }
    }
}
