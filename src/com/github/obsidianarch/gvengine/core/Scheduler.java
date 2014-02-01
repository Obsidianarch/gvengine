package com.github.obsidianarch.gvengine.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.Sys;

import com.github.obsidianarch.gvengine.core.options.Option;
import com.github.obsidianarch.gvengine.core.options.SliderOption;
import com.github.obsidianarch.gvengine.core.options.ToggleOption;

/**
 * Maintains a schedule of events and when they need to be executed.
 * 
 * @author Austin
 */
public class Scheduler {
    
    //
    // Options
    //
    
    /** The maximum number of events dispatched every tick. */
    @Option( description = "Maximum events", screenName = "Maximum events per tick", x = -1, y = -1 )
    @SliderOption( minimum = 8, maximum = 4096 )
    public static int            MaxEvents            = 16;
    
    /** When true, the timed events will be restricted to the {@code MaxEvents} as well. */
    @Option( description = "Timed events throttled", screenName = "Timed Events Throttled", x = -1, y = -1 )
    @ToggleOption( options = { "false", "true" }, descriptions = { "Disabled (recommended)", "Enabled" } )
    public static boolean        TimedEventsThrottled = false;
    
    @Option( description = "Log scheduling output messages", screenName = "Log Scheduling Output", x = -1, y = -1 )
    @ToggleOption( options = { "false", "true" }, descriptions = { "Disabled", "Enabled" } )
    public static boolean        LogOutput            = false;
    
    //
    // Fields
    //
    
    private static List< Event > recurringEvents      = new ArrayList<>();
    
    /** The list of events which have timers attached. */
    private static List< Event > timedEvents          = new ArrayList<>();
    
    /** The list of events which have just been scheduled. (FIFO) */
    private static List< Event > events               = new ArrayList<>();
    
    //
    // Schedulers
    //
    
    /**
     * Schedules an event to be performed every tick, this should be used sparingly or
     * have large delays, as these cannot be throttled to a maximum number of events per
     * tick.
     * 
     * @param method
     *            The name of the method to invoke.
     * @param target
     *            The object to target.
     * @param delay
     *            The time (in milliseconds) between the executions.
     * @param parameters
     *            The parameters to pass to the method.
     */
    public static void scheduleRecurringEvent( String method, Object target, long delay, Object... parameters ) {
        Event event = new Event();
        
        try {
            if ( parameters != null ) {
                Class< ? >[] paramClasses = new Class< ? >[ parameters.length ];
                for ( int i = 0; i < paramClasses.length; i++ ) {
                    paramClasses[ i ] = parameters[ i ].getClass();
                }
                
                event.action = target.getClass().getMethod( method, paramClasses );
            }
            else {
                event.action = target.getClass().getMethod( method );
            }
            
        }
        catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        }
        catch ( SecurityException e ) {
            System.err.println( "Scheduler cannot get method due to SecurityException" );
            e.printStackTrace();
        }
        
        event.target = target;
        event.delay = delay;
        event.executionTime = MathHelper.toTicks( Sys.getTime() ) + delay;
        event.parameters = parameters;
        
        recurringEvents.add( event );
    }
    
    /**
     * Schedules an event {@code time} milliseconds in the future. So
     * {@code scheduleEvent( "aMethod", anObject, 5000, aParameter )} will
     * invoke {@code anObject.aMethod( aParameter )} in 5 seconds.
     * 
     * @param method
     *            The method to execute.
     * @param target
     *            The object upon which the method will be invoked.
     * @param time
     *            The time (in milliseconds) until the event will be fired (Use -1 when
     *            the event doesn't have a time it needs to run by).
     * @param parameters
     *            The parameters passed to the method when executed.
     */
    public static void scheduleEvent( String method, Object target, long time, Object... parameters ) {
        Event event = new Event();
        
        try {
            if ( parameters != null ) {
                Class< ? >[] paramClasses = new Class< ? >[ parameters.length ];
                for ( int i = 0; i < paramClasses.length; i++ ) {
                    paramClasses[ i ] = parameters[ i ].getClass();
                }
                
                event.action = target.getClass().getMethod( method, paramClasses );
            }
            else {
                event.action = target.getClass().getMethod( method );
            }
        }
        catch ( NoSuchMethodException | SecurityException e1 ) {
            e1.printStackTrace(); // hopefully will never be thrown
        }
        
        event.target = target;
        if ( time == -1 ) {
            event.executionTime = -1;
        }
        else {
            event.executionTime = Sys.getTime() + MathHelper.toTicks( time );
        }
        event.parameters = parameters;
        
        addEvent( event ); // add the event to the list
    }
    
    //
    // Actions
    //
    
    /**
     * Updates the scheduler, and fires events which need to be fired.
     */
    public static void doTick() {
        int firedEvents = 0;
        
        // fire all of the recurring events
        for ( Event e : recurringEvents ) {
            if ( Sys.getTime() < e.executionTime ) continue;
            
            try {
                e.action.invoke( e.target, e.parameters ); // invoke the method
                if ( TimedEventsThrottled ) firedEvents++;
            }
            catch ( Exception ex ) {
                System.err.println( "Scheduler failed to invoke \"" + e.action.getName() + "()\"" );
                ex.printStackTrace();
            }
            
            if ( LogOutput ) System.out.println( "> Executed recurring event \"" + e.action.getName() + "\"" );
            
            e.executionTime = Sys.getTime() + MathHelper.toTicks( e.delay ); // set the next execution time
        }
        
        Iterator< Event > it = timedEvents.iterator(); // get the iterator for the timed events
        while ( it.hasNext() ) {
            Event e = it.next(); // get the next event
            
            // if the timed events are throttled too, then we may have to break out of the loop
            if ( TimedEventsThrottled && ( firedEvents >= MaxEvents ) ) break;
            
            // everything past this point will have a higher execution time than this one as well, and we haven't gotten to this time yet
            if ( e.executionTime > Sys.getTime() ) break;
            
            try {
                e.action.invoke( e.target, e.parameters ); // invoke the method
                if ( TimedEventsThrottled ) firedEvents++;
            }
            catch ( Exception ex ) {
                System.err.println( "Scheduler failed to invoke \"" + e.action.getName() + "()\"" );
                ex.printStackTrace();
            }
            
            if ( LogOutput ) System.out.println( "> Executed timed event \"" + e.action.getName() + "\"" );
            
            it.remove(); // remove the iterated objects
        }
        
        it = events.iterator(); // get the iterator for the untimed events
        while ( it.hasNext() ) {
            Event e = it.next(); // get the next event
            
            // we've reached the max number of events we can fire for now
            if ( firedEvents >= MaxEvents ) break;
            
            try {
                e.action.invoke( e.target, e.parameters ); // invoke the method
                firedEvents++;
            }
            catch ( Exception ex ) {
                System.err.println( "Scheduler failed to invoke \"" + e.action.getName() + "()\"" );
                ex.printStackTrace();
            }
            
            if ( LogOutput ) System.out.println( "> Executed event \"" + e.action.getName() + "\"" );
            
            it.remove(); // remove the iterated object
        }
    }
    
    /**
     * Adds the event into the correct collection, and if it's timed adds it into the list
     * at the correct position.
     * 
     * @param e
     *            The event to schedule.
     */
    private static void addEvent( Event e ) {
        try {
            // if there is not timed constraint
            if ( e.executionTime == -1 ) {
                events.add( e );
                return;
            }
            
            // if there are no prescheduled timed events, then add one in
            if ( timedEvents.size() == 0 ) {
                timedEvents.add( e );
                return;
            }
            
            // add the timed event into the list at the correct position, based on the surronding items
            for ( int i = 0; i < timedEvents.size(); i++ ) {
                Event event = timedEvents.get( i ); // get the event from the list
                
                if ( event.executionTime > e.executionTime ) { // this event is scheduled later than this one
                    timedEvents.add( i, e ); // insert the timed event into the list
                    
                    return; // we've added it, no reason to continue doing so
                }
            }
            
            timedEvents.add( e ); // add the event to the very end of the queue
        }
        finally {
            if ( LogOutput ) {
                if ( e.delay != -1 ) {
                    System.out.println( "> Scheulded \"" + e.action.getName() + "\" for every " + e.delay + " milliseconds" );
                }
                else if ( e.executionTime == -1 ) {
                    System.out.println( "> Scheduled \"" + e.action.getName() + "\"" );
                }
                else {
                    System.out.println( "> Scheduled \"" + e.action.getName() + "\" for " + e.executionTime );
                }
            }
        }
    }
    
    //
    // Getters
    //
    
    /**
     * @return The total number of events scheduled that need to be executed at this time
     *         (timed and untimed).
     */
    public static int getEventCount() {
        return events.size() + timedEvents.size();
    }
    
    //
    // Nested Classes
    //
    
    /**
     * A container for the method which will be executed, the class which will be
     * executing the method, and the time at which the method will be fired.
     * 
     * @author Austin
     */
    private static class Event {
        
        /** The action for this event. */
        public Method   action;
        
        /** The executor. */
        public Object   target;
        
        /** The delay between executions (only used for recurring events). */
        public long     delay = -1;
        
        /** The time this event should be executed at. -1 if there is not timing priority. */
        public long     executionTime;
        
        /** The parameters passed to the methods. */
        public Object[] parameters;
        
    }
    
}
