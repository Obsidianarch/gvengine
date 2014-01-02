package com.github.obsidianarch.gvengine.core.options;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ToggleOption {
    
    /**
     * @return The enum for the possible options.
     */
    Class< ? extends Enum< ? > > options();
    
}
