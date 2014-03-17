varying vec3 normal;
varying vec3 vertex_to_light_vector;

void main()
{
    // Transform the vertex
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    
    // Transform the normal to ModelView-Space
    normal = gl_NormalMatrix * gl_Normal;
    
    // Transform the vertex position to ModelView-Space
    vec4 vertex_in_modelview_space = gl_ModelViewMatrix * gl_Vertex;
    
    // Calculate the vecto from teh vertex position to the light position
    vertex_to_light_vector = vec3( gl_LightSource[ 0 ].position, vertex_in_modelview_space );
}