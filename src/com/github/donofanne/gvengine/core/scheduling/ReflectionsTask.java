package com.github.donofanne.gvengine.core.scheduling;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 */
public class ReflectionsTask extends Task
{

    //
    // Fields
    //

    /**
     * The object the method is in.
     */
    public final Object caller;

    /**
     * The method to invoke.
     */
    public final Method method;

    /**
     * The parameters for the method.
     */
    public final Object[] parameters;

    //
    // Constructors
    //

    /**
     * @param caller
     *         The object the method is in.
     * @param method
     *         The method to invoke.
     * @param parameters
     *         The parameters for the method.
     */
    public ReflectionsTask( double time, Object caller, Method method, Object[] parameters )
    {
        super( time );
        this.caller = caller;
        this.method = method;
        this.parameters = parameters;
    }

    //
    // Overrides
    //

    @Override
    public void execute()
    {
        try
        {
            method.invoke( caller, parameters );
        }
        catch ( InvocationTargetException | IllegalAccessException e )
        {
            e.printStackTrace();
        }
    }

}
