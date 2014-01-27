package com.github.obsidianarch.gvengine.core.options;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
@Documented
public @interface ToggleOption {
    
    /**
     * @return The enum for the possible options.
     */
    String[] options();
    
    /**
     * @return The descriptions for each item, if this is empty then the options will be
     *         used.
     */
    String[] descriptions() default {};
    
}
