package com.yahoo.obsidianarch;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.io.File;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.Color;

import com.github.obsidianarch.gvengine.Chunk;
import com.github.obsidianarch.gvengine.ChunkManager;
import com.github.obsidianarch.gvengine.Voxel;
import com.github.obsidianarch.gvengine.VoxelType;

public class TestingClass {
    
    //
    // LWJGL setup
    //
    
    /**
     * Starts the testing frame.
     */
    public void start() {
        try {
            createWindow(); // create the window
            initGL(); // initialize OpenGL
            init(); // initialize game resources
            run(); // run the game loop
        }
        catch ( Exception e ) {
            e.printStackTrace(); // uh-oh!
        }
        finally {
            Display.destroy(); // we don't want the window to stay up!
        }
    }
    
    /**
     * Creates a 640x480x32 window.
     * 
     * @throws LWJGLException
     *             If there was a problem with the Display setup.
     */
    public void createWindow() throws LWJGLException {
        Display.setFullscreen( false ); // disable full screen, this is a testing class not an actual game
        
        for ( DisplayMode dm : Display.getAvailableDisplayModes() ) {
            
            // check for correct witdth, height, and color depth
            if ( ( dm.getWidth() != 640 ) || ( dm.getHeight() != 480 ) || ( dm.getBitsPerPixel() != 32 ) ) continue;
            
            // initialize the frame
            Display.setDisplayMode( dm );
            Display.setTitle( TITLE );
            Display.create();
            break; // let's get out of this loop!
        }
    }
    
    /**
     * Initializes OpenGL.
     */
    public void initGL() {
        glEnable( GL_TEXTURE_2D ); // enables texture mapping (probably should be disabled later)
        glShadeModel( GL_SMOOTH ); // essentially makes surfaces look nicer
        
        glClearColor( 0f, 0f, 0f, 0f ); // sets the background color
        glClearDepth( 1f ); // depth value to use when the depth buffer is cleared
        
        glEnable( GL_DEPTH_TEST ); // enables depth testing
        glDepthFunc( GL_LEQUAL ); // what type of depth testing will be done?
        
        glEnableClientState( GL_VERTEX_ARRAY ); // enable vertex arrays
        glEnableClientState( GL_COLOR_ARRAY ); // enable color arrays
        
        /*
         * GL_PROJECTION : the way you want pixels to appear on the screen
         * GL_MODELVIEW : the location and orientation of objects
         * GL_TEXTURE_MATRIX : the textures of objects (?)
         * GL_COLOR_MATRIX : the color of objects (?)
         */
        
        glMatrixMode( GL_PROJECTION ); // sets the projection matrix to be altered
        glLoadIdentity(); // reset the projection matrix
        
        // fovy, aspect ratio (x:y), zNear, zFar
        gluPerspective( 45f, ( ( float ) Display.getWidth() / ( float ) Display.getHeight() ), 0.1f, 1000.0f );
        
        glMatrixMode( GL_MODELVIEW ); // sets the modelview matrix to be altered
        glHint( GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST );
    }
    
    //
    // Game methods
    //
    
    // temporary camera
    private FPCamera camera = new FPCamera( 24, 24, 24, 45, 0, 0 );
    
    public void init() {
        Mouse.setGrabbed( true ); // grab the mouse
        
        Voxel.setVoxelSize( 1f ); // set the voxel size to 1 unit (meter in this case)
        Voxel.createVoxelList( 10 ); // creates the voxel list with a capacity for 10 voxels
        new Voxel( 0, new AirVoxelType() ); // create a new voxel with the type air
        new Voxel( 1, new DirtVoxelType() ); // create a new voxel with the type dirt
        
        manager.rebuildMesh();
    }
    
    public void run() {
        long time, lastTime = 0; // current system time, and the previous system time
        double dt = 0f; // delta t (change in time)
        
        long lastFPS = getTime(); // last time an FPS measurement was taken
        int fpsCount = 0; // the number of frames between seconds
        int fps = 0; // the count displayed to the user
        
        while ( !Display.isCloseRequested() ) { // while the user hasn't clicked the exited
            try {
                // change the state of the grabbed mouse
                if ( Keyboard.isKeyDown( Keyboard.KEY_R ) ) Mouse.setGrabbed( false );
                if ( Keyboard.isKeyDown( Keyboard.KEY_F ) ) Mouse.setGrabbed( true );
                
                // change the rendering method (this is mostly just for fun)
                if ( Keyboard.isKeyDown( Keyboard.KEY_1 ) ) Chunk.setGLMode( GL_POINTS );
                if ( Keyboard.isKeyDown( Keyboard.KEY_2 ) ) Chunk.setGLMode( GL_LINES );
                if ( Keyboard.isKeyDown( Keyboard.KEY_3 ) ) Chunk.setGLMode( GL_TRIANGLES );
                if ( Keyboard.isKeyDown( Keyboard.KEY_4 ) ) Chunk.setGLMode( GL_QUADS );
                if ( Keyboard.isKeyDown( Keyboard.KEY_5 ) ) Chunk.setGLMode( GL_POLYGON );
                
                time = getTime(); // update the time from the system time
                dt = ( time - lastTime ) / 1000.0; // get the change in time
                lastTime = time; // change the last time measurement
                
                manager.update();
                
                // rebuild the mesh, this is temporary for testing purposes
                if ( Keyboard.isKeyDown( Keyboard.KEY_T ) ) {
                    manager.rebuildMesh();
                    updateTitle( fps ); // updates the title to match the new vertex and triangle counts
                }
                
                camera.update( dt ); // update the camera (controller aspect)
                render(); // render the scene
                
                if ( ( getTime() - lastFPS ) > 1000 ) {
                    fps = fpsCount; // this is the most recent fps measurement
                    fpsCount = 0; // reset the fps count
                    lastFPS += 1000; // add one second to the fps count
                    
                    updateTitle( fps ); // update the title to match the new fps measurement
                }
                fpsCount++; // a frame has passed
                
                Display.update(); // tells openGL to render to a frame
                
                if ( fpsCap > 0 ) Display.sync( fpsCap ); // tries to cap the game at 60 FPS
            }
            catch ( Exception e ) {
                e.printStackTrace(); // oopsies!
            }
        }
    }
    
    public void render() {
        glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT ); // clear the screen
        glLoadIdentity();
        
        camera.transformView(); // translate to the camera position and then rotate to it's values
        manager.render();
    }
    
    private void updateTitle( int fps ) {
        int vertexCount = manager.getVertexCount(); // get the new vertex counts
        Display.setTitle( TITLE + " - (FPS: " + fps + ", V: " + vertexCount + ")" ); // change the title of the display
    }
    
    //
    // Static
    //
    
    private static final String       TITLE     = "GVEngine";
    private static final long         managerID = ChunkManager.createChunkManager( new TestingChunkProvider() );
    private static final ChunkManager manager   = ChunkManager.getChunkManager( managerID );
    
    private static int                fpsCap    = 60;
    
    public static void main( String[] args ) {
        
        boolean useLWJGLSwitch = true; // if we need to use the hidden LWJGL switch for natives (if we're running via Eclipse)
        
        // parse the argument(s)
        for ( String s : args ) {
            s = s.toLowerCase();
            
            if ( s.equals( "-debug" ) ) {
                useLWJGLSwitch = false;
                fpsCap = -1;
            }
            if ( s.startsWith( "-fpsm:" ) ) fpsCap = Integer.parseInt( s.substring( s.indexOf( ':' ) + 1 ) ); // set the FPS cap
        }
        
        if ( useLWJGLSwitch ) System.setProperty( "org.lwjgl.librarypath", new File( "lib/natives" ).getAbsolutePath() ); // hidden switch for LWJGL natives
            
        TestingClass tc = new TestingClass(); // this will run the actual engine
        tc.start();
    }
    
    public static long getTime() {
        return ( Sys.getTime() * 1000 ) / Sys.getTimerResolution();
    }
    
    //
    // Nested classes
    //
    
    private class AirVoxelType implements VoxelType {
        
        @Override
        public boolean isActive() {
            return false;
        }
        
        @Override
        public boolean isSolid() {
            return false;
        }
        
        @Override
        public Color getColor() {
            return null;
        }
        
    }
    
    private class DirtVoxelType implements VoxelType {
        
        @Override
        public boolean isActive() {
            return true;
        }
        
        @Override
        public boolean isSolid() {
            return true;
        }
        
        @Override
        public Color getColor() {
            return new Color( 120, 120, 120 );
        }
    }
    
}
