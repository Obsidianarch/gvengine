package com.github.donofanne.gvengine.core.scheduling;

/**
 * A task could be created via:
 * <ul>
 *     <li>A {@code Delegate} method (no parameters, no return)</li>
 *     <li>A {@code Consumer&lt; Object[] &gt;} which has any types of parameters passed in an Object array</li>
 *     <li>The Reflections framework (not recommended)</li>
 * </ul>
 */
abstract class Task
{

    //
    // Fields
    //

    /**
     * The time this task should be executed at (-1 for whenever).
     */
    public final double time;

    //
    // Constructors
    //

    /**
     * @param time
     *          the time this task should be executed at (-1 for whenever).
     */
    public Task( double time )
    {
        this.time = time;
    }

    //
    // Abstract
    //

    /**
     * Executes the task.
     */
    public abstract void execute();

}