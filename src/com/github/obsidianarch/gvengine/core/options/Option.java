package com.github.obsidianarch.gvengine.core.options;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface Option {
    
    /**
     * @return The name of the screen the option is displayed on.
     */
    String screenName();
    
    /**
     * @return The description of the option that appears to the user.
     */
    String description();
    
    /**
     * @return The x position of the option on the screen.
     */
    int x();
    
    /**
     * @return The y position of the option on the screen.
     */
    int y();
    
}
