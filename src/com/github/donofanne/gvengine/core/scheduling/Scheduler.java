package com.github.donofanne.gvengine.core.scheduling;

import com.github.donofanne.gvengine.core.Delegate;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * Manages the scheduling of both synchronous and asynchronous tasks such that the frame-rate will not be affected by a large amount of tasks or multiple long
 * tasks.
 */
public final class Scheduler
{

    //
    // Fields
    //

    /**
     * The maximum amount of elapsed time before a scheduling tick is ended.
     */
    private static final double maxElapsedTime = 0.001; // by default all tasks have to be done within 1 millisecond

    //
    // Actions
    //

    /**
     * Performs the ticks for the synchronous task handler.
     */
    public static void doTick()
    {
        SyncTaskHandler.doTick( GLFW.glfwGetTime() + maxElapsedTime );
    }

    //
    // Asynchronous Tasks
    //

    /**
     * Starts another thread to run the task in the background, asynchronously.
     *
     * @param delegate
     *         The delegate method to enqueue, a void return type and no parameters.
     */
    public static void launch( Delegate delegate )
    {
        AsyncTaskHandler.launch( getTask( delegate ) );
    }

    /**
     * Starts another thread to run the task in the background, asynchronously.
     *
     * @param consumer
     *         The consumer method to enqueue, a void return type and an Object[] for parameters.
     * @param parameters
     *         the parameters to pass to the consumer.
     */
    public static void launch( Consumer< Object[] > consumer, Object... parameters )
    {
        AsyncTaskHandler.launch( getTask( consumer, parameters ) );
    }

    //
    // Synchronous Tasks
    //

    /**
     * Enqueues a delegate method to be executed at a later time synchronously.
     *
     * @param delegate
     *         The delegate method to enqueue, a void return type and no parameters
     */
    public static void enqueue( Delegate delegate )
    {
        SyncTaskHandler.enqueue( getTask( delegate ) );
    }

    /**
     * Enqueues a consumer method to be executed at a later time synchronously.
     *
     * @param consumer
     *         The consumer method to enqueue, a void return type and an Object[] for parameters.
     * @param parameters
     *         the parameters to pass to the consumer.
     */
    public static void enqueue( Consumer< Object[] > consumer, Object... parameters )
    {
        SyncTaskHandler.enqueue( getTask( consumer, parameters ) );
    }

    //
    // Getters
    //

    private static Task getTask( Delegate delegate )
    {
        return new DelegateTask( delegate );
    }

    private static Task getTask( Consumer< Object[] > consumer, Object[] parameters )
    {
        return new ConsumerTask( consumer, parameters );
    }

}
