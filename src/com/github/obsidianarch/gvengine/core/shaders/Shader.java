package com.github.obsidianarch.gvengine.core.shaders;

import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Austin
 */
public class Shader {
    
    //
    // Constants
    //
    
    /** The error message displayed when the user tries to use a deleted shader. */
    private static final String SHADER_DELETED_EXCEPTION_STRING = "The shader has already been deleted!";
    
    //
    // Fields
    //
    
    /** The type of shader this is. */
    private final int shaderType;
    
    /** The ID for this shader. */
    private final int shaderID;

    /** Did the shader compile successfully? */
    private boolean   compiled;
    
    //
    // Constructors
    //
    
    /**
     * Constructs a shader.
     * 
     * @param type
     *            The type of shader this is.
     */
    public Shader( int type ) {
        shaderType = type;
        shaderID = glCreateShaderObjectARB( shaderType );
        compiled = false;
    }
    
    //
    // Actions
    //
    
    /**
     * Delets the shader object from OpenGL.
     */
    public void delete() {
        glDeleteObjectARB( shaderID );
    }

    //
    // Setters
    //
    
    /**
     * @param f
     *            The file containing the source code of the shader.
     * @throws IOException
     *             If the file couldn't be read.
     */
    public void setSource( File f ) throws IOException {
        try ( BufferedReader br = new BufferedReader( new FileReader( f ) ) ) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ( ( line = br.readLine() ) != null ) {
                sb.append( String.format( "%s%n", line ) );
            }
            
            setSource( sb.toString() );
        }
    }

    /**
     * @param s
     *            The source code of the shader.
     */
    public void setSource( String s ) {
        glShaderSourceARB( shaderID, s );
        glCompileShaderARB( shaderID );
        
        compiled = ( glGetObjectParameteriARB( shaderID, GL_OBJECT_COMPILE_STATUS_ARB ) == GL_TRUE );
    }
    
    //
    // Getters
    //
    
    /**
     * @return The unique ID for this shader.
     */
    public int getShaderID() {
        return shaderID;
    }
    
    /**
     * @return The type of shader this represents.
     */
    public int getShaderType() {
        return shaderType;
    }

    /**
     * @return If the shader successfully compiled or not.
     */
    public boolean getCompileStatus() {
        return compiled;
    }

    /**
     * @return The error log for the shader compile status.
     */
    public String getErrorLog() {
        return glGetInfoLogARB( shaderID, glGetObjectParameteriARB( shaderID, GL_OBJECT_INFO_LOG_LENGTH_ARB ) );
    }
    
}
