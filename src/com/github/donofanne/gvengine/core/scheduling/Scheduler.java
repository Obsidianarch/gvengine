package com.github.donofanne.gvengine.core.scheduling;

import com.github.donofanne.gvengine.core.Delegate;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Method;
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
        AsyncTaskHandler.launch( getTask( -1, delegate ) );
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
        AsyncTaskHandler.launch( getTask( -1, consumer, parameters ) );
    }

    /**
     * Starts another thread to run the task in the background, asynchronously.
     *
     * @param caller
     *         The object which will be invoking the method.
     * @param methodName
     *         The name of the method.
     * @param parameters
     *         The The parameters which will be passed to the method (used to find the matching method signature).
     *
     * @throws NoSuchMethodException
     *         If no suitable methods could be found.
     */
    public static void launch( Object caller, String methodName, Object... parameters ) throws NoSuchMethodException
    {
        AsyncTaskHandler.launch( getTask( -1, caller, methodName, parameters ) );
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
        SyncTaskHandler.enqueue( getTask( -1, delegate ) );
    }

    /**
     * Enqueues a consumer method to be executed at a later time synchronously.
     *
     * @param consumer
     *          The consumer method to enqueue, a void return type and an Object[] for parameters.
     * @param parameters
     *          the parameters to pass to the consumer.
     */
    public static void enqueue( Consumer< Object[] > consumer, Object... parameters )
    {
        SyncTaskHandler.enqueue( getTask( -1, consumer, parameters ) );
    }

    /**
     * Enqueues a method to be executed at a later time synchronously via reflections.
     *
     * @param caller
     *         The object which will be invoking the method.
     * @param methodName
     *         The name of the method.
     * @param parameters
     *         The parameters to pass to the method.
     *
     * @throws NoSuchMethodException
     *         If a method with the given parameter classes could not be found in the "caller" class.
     */
    public static void enqueue( Object caller, String methodName, Object... parameters ) throws NoSuchMethodException
    {
        SyncTaskHandler.enqueue( getTask( -1, caller, methodName, parameters ) );
    }

    //
    // Getters
    //

    private static Task getTask( double time, Delegate delegate )
    {
        return new DelegateTask( time, delegate );
    }

    private static Task getTask( double time, Consumer< Object[] > consumer, Object[] parameters )
    {
        return new ConsumerTask( time, consumer, parameters );
    }

    private static Task getTask( double time, Object caller, String methodName, Object[] parameters ) throws NoSuchMethodException
    {
        return new ReflectionsTask( time, caller, getMethodFromClass( caller.getClass(), methodName, parameters ), parameters );
    }

    /**
     * Gets the method via reflections to be executed by searching for the matching method signature in the callerClass.
     *
     * @param callerClass
     *         The class to search for the method in.
     * @param methodName
     *         The name of the method.
     * @param parameters
     *         The parameters which will be passed to the method (used to find the matching method signature).
     *
     * @return The matching method.
     *
     * @throws NoSuchMethodException
     *         If a method could not be found
     */
    private static Method getMethodFromClass( Class< ? > callerClass, String methodName, Object... parameters ) throws NoSuchMethodException
    {
        Class[] parameterClasses = new Class[ parameters.length ];
        for ( int i = 0; i < parameters.length; i++ )
        {
            parameterClasses[ i ] = parameters[ i ].getClass();
        }

        return callerClass.getMethod( methodName, parameterClasses );
    }

}
