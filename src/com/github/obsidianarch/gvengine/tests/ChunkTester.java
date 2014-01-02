package com.github.obsidianarch.gvengine.tests;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.util.Random;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.github.obsidianarch.gvengine.Chunk;
import com.github.obsidianarch.gvengine.Material;
import com.github.obsidianarch.gvengine.core.Camera;
import com.github.obsidianarch.gvengine.core.Controller;

/**
 * Tests the Voxel and Chunk Management systems.
 * 
 * @author Austin
 */
public class ChunkTester {
    
    public static void main( String... s ) throws Exception {
        create(); // create the display
        
        Chunk c = new Chunk( 0, 0, 0 ); // the chunk we're testing
        buildChunk( c ); // build the chunk
        
        Camera camera = new Camera(); // the camera of hte player
        Controller controller = new Controller( camera ); // the controller of the camera
        
        long lastTime = Sys.getTime(); // the last time the frame was drawn
        
        while ( !Display.isCloseRequested() ) {
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT ); // clear the last frame
            
            if ( Keyboard.isKeyDown( Keyboard.KEY_R ) ) { // rebuild the chunk
                buildChunk( c );
            }
            
            float delta = ( Sys.getTime() - lastTime ); // the difference between this frame and the previous
            movePlayer( delta, camera, controller ); // move and orient the player
            
            glLoadIdentity(); // remove the previous transformation
            camera.transform(); // transform the camera
            c.render(); // render the chunk 
            
            lastTime = Sys.getTime(); // change the last time
            Display.update(); // update the screen
        }
        
        Display.destroy();
    }
    
    /**
     * Moves the player and updates the camera.
     * 
     * @param delta
     *            The time since the last frame.
     * @param camera
     *            The player's camera.
     * @param controller
     *            The player's controller.
     */
    private static void movePlayer( float delta, Camera camera, Controller controller ) {
        float movementSpeed = 0.01f * delta;
        if ( Keyboard.isKeyDown( Keyboard.KEY_LSHIFT ) ) movementSpeed *= 2;
        
        if ( Keyboard.isKeyDown( Keyboard.KEY_W ) ) controller.moveForward( movementSpeed );
        if ( Keyboard.isKeyDown( Keyboard.KEY_S ) ) controller.moveBackward( movementSpeed );
        
        if ( Keyboard.isKeyDown( Keyboard.KEY_D ) ) controller.moveRight( movementSpeed );
        if ( Keyboard.isKeyDown( Keyboard.KEY_A ) ) controller.moveLeft( movementSpeed );
        
        camera.setYaw( camera.getYaw() + ( Mouse.getDX() * .05f ) );
        camera.setPitch( camera.getPitch() - ( Mouse.getDY() * .05f ) );
    }
    
    /**
     * Changes the chunk's contents and rebuilds the chunk's mesh.
     * 
     * @param c
     *            The chunk.
     */
    private static void buildChunk( Chunk c ) {
        for ( int x = 0; x < 16; x++ ) {
            for ( int y = 0; y < 16; y++ ) {
                for ( int z = 0; z < 16; z++ ) {
                    if ( new Random().nextBoolean() ) c.setMaterialAt( Material.STONE, x, y, z );
                    else c.setMaterialAt( Material.AIR, x, y, z );
                }
            }
        }
        
        long start = System.nanoTime();
        c.buildMesh();
        long end = System.nanoTime();
        
        System.out.println( "Chunk rebuild stats:" );
        System.out.println( " Nanoseconds:  " + ( end - start ) );
        System.out.println( " Milliseconds: " + ( ( end - start ) / 1000000.0 ) );
        System.out.println( " Seconds:      " + ( ( end - start ) / 1000000000.0 ) );
        System.out.println( " Vertices:     " + c.getVertexCount() );
        System.out.println();
    }
    
    /**
     * Creates the display and initializes OpenGL.
     * 
     * @throws Exception
     *             If there was a problem.
     */
    private static void create() throws Exception {
        Display.setTitle( "Voxel Testing" );
        Display.setDisplayMode( new DisplayMode( 640, 480 ) );
        Display.create();
        
        glClearColor( 0, 0, 0, 0 ); // sets the background color
        glClearDepth( 1 ); // depth value to use when the depth buffer is cleared
        
        glEnable( GL_DEPTH ); // enable depth
        glDepthFunc( GL_LEQUAL );
        
        glEnable( GL_BLEND ); // enable blending
        glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
        
        glEnableClientState( GL_VERTEX_ARRAY ); // enable vertex arrays
        glEnableClientState( GL_COLOR_ARRAY ); // enable color arrays
        
        glMatrixMode( GL_PROJECTION ); // sets the projection matrix to be altered
        glLoadIdentity(); // reset the projection matrix
        
        // fovy, aspect ratio (x:y), zNear, zFar
        gluPerspective( 45f, ( ( float ) Display.getWidth() / ( float ) Display.getHeight() ), 0.0001f, 1000.0f );
        
        glMatrixMode( GL_MODELVIEW ); // sets the modelview matrix to be altered
        glHint( GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST );
        
        Mouse.setGrabbed( true );
    }
    
}
