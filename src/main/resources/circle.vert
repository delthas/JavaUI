# version 330 core

uniform vec4 circle;

out vec2 mapping;

void main()
{
    vec2 offset;
    switch(gl_VertexID)
    {
    case 0:
        //Bottom-left
        mapping = vec2(-1.0, -1.0);
        offset = vec2(-circle.z, -circle.w);
        break;
    case 1:
        //Top-left
        mapping = vec2(-1.0, 1.0);
        offset = vec2(-circle.z, circle.w);
        break;
    case 2:
        //Bottom-right
        mapping = vec2(1.0, -1.0);
        offset = vec2(circle.z, -circle.w);
        break;
    case 3:
        //Top-right
        mapping = vec2(1.0, 1.0);
        offset = vec2(circle.z, circle.w);
        break;
    }
    
    gl_Position = vec4((offset + circle.xy), 0.0, 1.0);
}