package com.addonovan.gvengine.core;

import com.addonovan.gvengine.core.options.Option;
import com.addonovan.gvengine.core.options.SliderOption;
import com.addonovan.gvengine.core.options.ToggleOption;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Maintains a schedule of events and when they need to be executed.
 *
 * @author Austin
 * @version 14.03.30
 * @since 14.03.30
 */
public final class Scheduler
{

    //
    // Options
    //

    /**
     * The maximum number of events dispatched every tick.
     */
    @Option("Maximum events per tick")
    @SliderOption(minimum = 8, maximum = 4096)
    public static int MaxEvents = 8;

    /**
     * The maximum number of milliseconds that a single scheduling tick can take.
     */
    @Option("Max time per tick")
    @SliderOption(minimum = 10, maximum = 1000)
    public static int MaxTickTime = 10;

    /**
     * When true, the timed events will be restricted to the {@code MaxEvents} as well.
     */
    @Option("Timed events throttled")
    @ToggleOption({ "false", "true" })
    public static boolean TimedEventsThrottled = true;

    /**
     * When true, all event scheduling and dispatching is logged to the console.
     */
    @Option("Log scheduling output messages")
    @ToggleOption({ "false", "true" })
    public static boolean LogOutput = false;

    //
    // Fields
    //

    /**
     * Events that are schedule to continuously occur every so often.
     */
    private static ArrayList< Event > recurringEvents = new ArrayList<>();

    /**
     * The list of events which have timers attached.
     */
    private static ArrayList< Event > timedEvents = new ArrayList<>();

    /**
     * The list of events which have just been scheduled. (FIFO)
     */
    private static ArrayList< Event > events = new ArrayList<>();

    //
    // Schedulers
    //

    /**
     * Schedules an event to be performed every tick, this should be used sparingly or have large delays, as these cannot be throttled to a maximum number of
     * events per tick.
     *
     * @param method
     *         The name of the method to invoke.
     * @param target
     *         The object to target.
     * @param delay
     *         The time (in milliseconds) between the executions.
     * @param parameters
     *         The parameters to pass to the method.
     *
     * @see #scheduleEvent(String, Object, long, Object...)
     * @see #enqueueEvent(String, Object, Object...)
     * @since 14.03.30
     */
    public static void scheduleRecurringEvent( String method, Object target, long delay, Object... parameters )
    {
        Event event = new Event();

        try
        {
            if ( parameters != null )
            {
                Class< ? >[] paramClasses = new Class< ? >[ parameters.length ];
                for ( int i = 0; i < paramClasses.length; i++ )
                {
                    paramClasses[ i ] = parameters[ i ].getClass();
                }

                event.action = target.getClass().getMethod( method, paramClasses );
            }
            else
            {
                event.action = target.getClass().getMethod( method );
            }

        }
        catch ( NoSuchMethodException e )
        {
            e.printStackTrace();
        }
        catch ( SecurityException e )
        {
            System.err.println( "Scheduler cannot get method due to SecurityException" );
            e.printStackTrace();
        }

        event.target = target;
        event.delay = delay;
        // TODO replace with system time
        event.executionTime = TimeHelper.toTicks( 0 ) + delay;
        event.parameters = parameters;

        recurringEvents.add( event );
    }

    /**
     * Schedules an event {@code time} milliseconds in the future. So {@code scheduleEvent( "aMethod", anObject, 5000, aParameter)} will invoke {@code
     * anObject.aMethod(aParameter)} in 5 seconds.
     *
     * @param method
     *         The method to execute.
     * @param target
     *         The object whose method will be invoked.
     * @param time
     *         The time (in milliseconds) until the event will be fired.
     * @param parameters
     *         The parameters passed to the method when invoked.
     *
     * @see #scheduleRecurringEvent(String, Object, long, Object...)
     * @see #enqueueEvent(String, Object, Object...)
     * @since 14.03.30
     */
    public static void scheduleEvent( String method, Object target, long time, Object... parameters )
    {
        Event event = new Event();

        try
        {
            Class< ? >[] paramClasses = new Class< ? >[ parameters.length ];
            for ( int i = 0; i < paramClasses.length; i++ )
            {
                paramClasses[ i ] = parameters[ i ].getClass();
            }

            if ( target instanceof Class )
            {
                event.action = ( ( Class< ? > ) target ).getMethod( method, paramClasses );
            }
            else
            {
                event.action = target.getClass().getMethod( method, paramClasses );
            }
        }
        catch ( NoSuchMethodException | SecurityException e1 )
        {
            e1.printStackTrace(); // hopefully will never be thrown
        }

        event.target = target;
        // TODO schedule time better
        event.executionTime = 0 + TimeHelper.toTicks( time );
        event.parameters = parameters;

        addEvent( event ); // add the event to the list
    }

    /**
     * Enqueues an event to be executed whenever the scheduler has time to execute it.
     *
     * @param method
     *         The method to execute.
     * @param target
     *         The object whose method will be invoked.
     * @param parameters
     *         The parameters passed to the method when invoked.
     *
     * @see #scheduleRecurringEvent(String, Object, long, Object...)
     * @see #scheduleEvent(String, Object, long, Object...)
     * @since 14.03.30
     */
    public static void enqueueEvent( String method, Object target, Object... parameters )
    {
        Event event = new Event(); // create the event

        try
        {
            // determine the classes of our parameters
            Class< ? >[] paramClasses = new Class< ? >[ parameters.length ];
            for ( int i = 0; i < paramClasses.length; i++ )
            {
                paramClasses[ i ] = parameters[ i ].getClass();
            }

            if ( target instanceof Class )
            {
                // if the method is a static method, use the class object to get the method
                event.action = ( ( Class< ? > ) target ).getMethod( method, paramClasses );
            }
            else
            {
                // otherwise, use the target's class to get the method
                event.action = target.getClass().getMethod( method, paramClasses );
            }
        }
        catch ( NoSuchMethodException | SecurityException e1 )
        {
            e1.printStackTrace(); // hopefully will never be thrown
        }

        event.target = target; // set the event's target object
        event.executionTime = -1; // set the event's executing time (-1 because it has no priority to be executed)
        event.parameters = parameters; // set the event's parameters

        addEvent( event ); // add the event to the list
    }

    //
    // Actions
    //

    /**
     * Fires the recurring events that need to be fired.
     *
     * @param firedEvents
     *         The number of events that have already been fired this tick.
     * @param startTime
     *         The time the tick began.
     *
     * @return The total number of events fired so far into the tick.
     *
     * @since 14.03.30
     */
    private static int doRecurringEvents( int firedEvents, long startTime )
    {

        // fire all of the recurring events
        for ( Event e : recurringEvents )
        {
            if ( TimeHelper.isOver( startTime, MaxTickTime ) )
            {
                return firedEvents; // we've run out of time for this tick, let's keep the game running
            }
            if ( 0 < e.executionTime ) // TODO actually check time
            {
                continue; // the event hasn't been scheduled to run again yet
            }

            try
            {
                e.action.invoke( e.target, e.parameters ); // invoke the method
                if ( TimedEventsThrottled )
                {
                    firedEvents++; // we've fired a method
                }
            }
            catch ( Exception ex )
            {
                // something failed, tell the console
                System.err.println( "Scheduler failed to invoke \"" + e.action.getName() + "()\"" );
                ex.printStackTrace();
            }

            if ( LogOutput )
            {
                System.out.println( "> Executed recurring event \"" + e.action.getName() + "\"" ); // be verbose if we should be
            }

            e.executionTime = TimeHelper.getDelay( e.delay ); // set the next execution time
        }

        return firedEvents;
    }

    /**
     * Fires the timed events that need to be fired.
     *
     * @param firedEvents
     *         The number of eents that have already been fired this tick.
     * @param startTime
     *         The time the tick began.
     *
     * @return The total number of events fired so far into the tick.
     *
     * @since 14.03.30
     */
    private static int doTimedEvents( int firedEvents, long startTime )
    {

        Iterator< Event > it = timedEvents.iterator(); // get the iterator for the timed events
        while ( it.hasNext() )
        {
            if ( TimeHelper.isOver( startTime, MaxTickTime ) )
            {
                return firedEvents; // we've run out of time for this tick, let's keep the game running
            }
            Event e = it.next(); // get the next event

            // if the timed events are throttled too, then we may have to break out of the loop
            if ( TimedEventsThrottled && ( firedEvents >= MaxEvents ) )
            {
                break;
            }

            // everything past this point will have a higher execution time than this one as well, and we haven't gotten to this time yet
            if ( e.executionTime > 0 ) // TODO actually check time
            {
                break;
            }

            try
            {
                e.action.invoke( e.target, e.parameters ); // invoke the method
                if ( TimedEventsThrottled )
                {
                    firedEvents++;
                }
            }
            catch ( Exception ex )
            {
                System.err.println( "Scheduler failed to invoke \"" + e.action.getName() + "()\"" );
                ex.printStackTrace();
            }

            if ( LogOutput )
            {
                System.out.println( "> Executed timed event \"" + e.action.getName() + "\"" );
            }

            it.remove(); // remove the iterated objects
        }

        return firedEvents;
    }

    /**
     * Fires the remaining number of queued events.
     *
     * @param firedEvents
     *         The number of events fired so far in this tick.
     * @param startTime
     *         The time the tick began.
     *
     * @return The total number of events fired in this tick.
     *
     * @since 14.03.30
     */
    private static int doEvents( int firedEvents, long startTime )
    {

        Iterator< Event > it = events.iterator(); // get the iterator for the untimed events

        while ( it.hasNext() )
        {
            if ( TimeHelper.isOver( startTime, MaxTickTime ) )
            {
                return firedEvents; // we've run out of time for this tick, let's keep the game running
            }
            Event e = it.next(); // get the next event

            // we've reached the max number of events we can fire for now
            if ( firedEvents >= MaxEvents )
            {
                break;
            }

            try
            {
                e.action.invoke( e.target, e.parameters ); // invoke the method
                firedEvents++;
            }
            catch ( Exception ex )
            {
                System.err.println( "Scheduler failed to invoke \"" + e.action.getName() + "()\"" );
                ex.printStackTrace();
            }

            if ( LogOutput )
            {
                System.out.println( "> Executed event \"" + e.action.getName() + "\"" );
            }

            it.remove(); // remove the iterated object
        }

        return firedEvents;
    }

    /**
     * Updates the scheduler, and fires events which need to be fired.
     *
     * @since 14.03.30
     */
    public static void doTick()
    {
        long start = TimeHelper.getTime(); // get the start time (we'll force a stop if this goes over)
        int firedEvents = 0;

        // fire all the events, priorities are now easy to see
        // can also be written "doEvents( doTimedEvents( doRecurringEvents( 0, start ), start ), start )", but let's not do that
        firedEvents = doRecurringEvents( firedEvents, start ); // recurring events have the highest priority
        firedEvents = doTimedEvents( firedEvents, start ); // then regularly scheduled timed events
        firedEvents = doEvents( firedEvents, start ); // then just enqueued events
    }

    /**
     * Adds the event into the correct collection, and if it's timed adds it into the list at the correct position.
     *
     * @param e
     *         The event to schedule.
     *
     * @since 14.03.30
     */
    private static void addEvent( Event e )
    {
        boolean scheduled = true;
        try
        {
            // if there is not time constraint
            if ( e.executionTime == -1 )
            {
                for ( Event event : events )
                {
                    if ( e.equals( event ) )
                    {
                        scheduled = false;
                        return; // the event is already scheduled, don't perform it again
                    }
                }
                events.add( e );
                return;
            }

            // if there are no prescheduled timed events, then add one in
            if ( timedEvents.size() == 0 )
            {
                timedEvents.add( e );
                return;
            }

            // add the timed event into the list at the correct position, based on the surronding items
            for ( int i = 0; i < timedEvents.size(); i++ )
            {
                Event event = timedEvents.get( i ); // get the event from the list

                if ( event.executionTime > e.executionTime )
                { // this event is scheduled later than this one
                    timedEvents.add( i, e ); // insert the timed event into the list

                    return; // we've added it, no reason to continue doing so
                }
            }

            timedEvents.add( e ); // add the event to the very end of the queue
        }
        finally
        {
            if ( LogOutput )
            {
                if ( !scheduled )
                {
                    System.out.println( "> Ignored previously existing event \"" + e.action.getName() + "\"" );
                }
                else if ( e.delay != -1 )
                {
                    System.out.println( "> Scheulded \"" + e.action.getName() + "\" for every " + e.delay + " milliseconds" );
                }
                else if ( e.executionTime == -1 )
                {
                    System.out.println( "> Scheduled \"" + e.action.getName() + "\"" );
                }
                else
                {
                    System.out.println( "> Scheduled \"" + e.action.getName() + "\" for " + e.executionTime );
                }
            }
        }
    }

    //
    // Getters
    //

    /**
     * @return The total number of events scheduled that need to be executed at this time (timed and untimed).
     */
    public static int getEventCount()
    {
        return events.size() + timedEvents.size();
    }

    //
    // Nested Classes
    //

    /**
     * A container for the method which will be executed, the class which will be executing the method, and the time at which the method will be fired.
     *
     * @author Austin
     * @version 14.03.30
     * @since 14.03.30
     */
    private static class Event
    {

        //
        // Fields
        //

        /**
         * The action for this event.
         */
        public Method action;

        /**
         * The executor.
         */
        public Object target;

        /**
         * The delay between executions (only used for recurring events) (milliseconds).
         */
        public long delay = -1;

        /**
         * The time this event should be executed at (ticks).
         */
        public long executionTime;

        /**
         * The parameters passed to the methods.
         */
        public Object[] parameters;

        //
        // Overrides
        //

        @Override
        public String toString()
        {
            return String.format( "%s.%s() { delay: %d, time: %d }", target.getClass().getName(), action.getName(), delay, executionTime );
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( !( obj instanceof Event ) ) // I dislike not being able to say !instanceof
            {
                return false;
            }
            Event e = ( Event ) obj;

            if ( e.action.equals( action ) && ( target == e.target ) && ( delay == e.delay ) && ( executionTime == e.executionTime )
                    && ( parameters.length == e.parameters.length ) )
            {

                for ( int i = 0; i < parameters.length; i++ )
                {
                    if ( !parameters[ i ].equals( e.parameters[ i ] ) )
                    {
                        return false;
                    }
                }

                return true;
            }
            else
            {
                return false;
            }
        }

    }

}
