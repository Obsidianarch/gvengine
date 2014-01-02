package com.github.obsidianarch.gvengine.tests;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.github.obsidianarch.gvengine.core.ColorSystem;
import com.github.obsidianarch.gvengine.core.NormalSystem;
import com.github.obsidianarch.gvengine.core.PositionSystem;
import com.github.obsidianarch.gvengine.core.VertexBufferObject;

/**
 * The first test, tests the validity of the methods of VertexBufferObject.
 * 
 * @author Austin
 */
public class VBOTest {
    
    /** the VertexBufferObject we're testing. */
    private static VertexBufferObject vbo;
    
    /**
     * Starts the test.
     */
    private static void init() throws Exception {
        Display.setDisplayMode( new DisplayMode( 640, 480 ) );
        Display.create();
        
        glMatrixMode( GL_PROJECTION );
        glLoadIdentity();
        glOrtho( 0, 1, 0, 1, 1, -1 );
        glMatrixMode( GL_MODELVIEW );
        
        glEnableClientState( GL_VERTEX_ARRAY ); // enable vertex arrays
        glEnableClientState( GL_COLOR_ARRAY ); // enable color arrays
        
        vbo = new VertexBufferObject( PositionSystem.XY, ColorSystem.RGB, NormalSystem.DISABLED, 6, 9, 0 );
        vbo.setCoordinates( 0, 0, 1, 0, 0, 1 );
        vbo.setChannels( 1, 1, 1, 1, 1, 1, 1, 1, 1 );
    }
    
    /**
     * Runs the test.
     */
    private static void run() {
        while ( !Display.isCloseRequested() ) {
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
            
            vbo.render();
            
            Display.update();
            Display.sync( 60 );
        }
    }
    
    /**
     * Destroys the test.
     */
    private static void destroy() {
        Display.destroy();
    }
    
    /**
     * Starts the test.
     * 
     * @param args
     *            The command line arguments.
     */
    public static void main( String[] args ) throws Exception {
        init();
        run();
        destroy();
    }
}
