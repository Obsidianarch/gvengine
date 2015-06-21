package com.github.donofanne.gvengine.core.scheduling;

import com.github.donofanne.gvengine.core.Delegate;

/**
 * A task which uses a Delegate (no return value, no parameters).
 */
public class DelegateTask extends Task
{

    //
    // Fields
    //

    /**
     * The delegate method to invoke.
     */
    public final Delegate delegate;

    //
    // Constructors
    //

    /**
     * @param delegate
     *          The delegate method to invoke when the Task is executed.
     */
    public DelegateTask( double time, Delegate delegate )
    {
        super( time );
        this.delegate = delegate;
    }

    //
    // Overrides
    //

    @Override
    public void execute()
    {
        delegate.invoke();
    }
}
