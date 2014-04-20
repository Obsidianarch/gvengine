package com.github.obsidianarch.gvengine.tests;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;

import org.lwjgl.opengl.Display;

import com.github.obsidianarch.gvengine.core.Camera;
import com.github.obsidianarch.gvengine.core.Controller;
import com.github.obsidianarch.gvengine.core.Scheduler;
import com.github.obsidianarch.gvengine.core.input.Input;
import com.github.obsidianarch.gvengine.core.model.Model;
import com.github.obsidianarch.gvengine.core.model.OBJModelLoader;
import com.github.obsidianarch.gvengine.core.options.OptionManager;

/**
 * Tests the model loading and rendering processes.
 * 
 * @author Austin
 * 
 * @since 14.04.19
 * @version 14.04.19
 */
public class ModelTest {
    
    //
    // Static
    //
    
    /**
     * Runs the model test.
     * 
     * @param args
     *            Command line parameters.
     * @throws Exception
     *             If there was a problem with the test.
     * 
     * @since 14.04.19
     * @version 14.04.19
     */
    public static void main( String[] args ) throws Exception {
        TestingHelper.CONFIG.read();
        OptionManager.initialize( TestingHelper.CONFIG );
        OptionManager.initialize( args );
        
        OptionManager.registerClass( "Scheduler", Scheduler.class );
        System.out.println();
        
        TestingHelper.createDisplay();
        TestingHelper.setupGL();
        TestingHelper.initInput();
        
        Camera camera = new Camera();
        camera.setMaximumPitch( 15f );
        camera.setMaximumPitch( 165f );
        Controller controller = new Controller( camera );

        Model m = new OBJModelLoader().loadModel( new File( "bunny.obj" ) );
        
        while ( !Display.isCloseRequested() ) {
            Input.poll();
            TestingHelper.processInput( camera, controller );
            
            Scheduler.doTick();
            renderScene( camera, m ); // render the scene
            
            TestingHelper.updateDisplay( "Model Tester", -1 );
        }
        
        TestingHelper.destroy();
    }
    
    /**
     * Renders the scene.
     * 
     * @param camera
     *            The camera the player is seeing through.
     * @param m
     *            The model to render.
     */
    public static void renderScene( Camera camera, Model m ) {
        glLoadIdentity();
        camera.lookThrough();
        m.render();
    }

}
