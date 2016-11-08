# version 330 core

layout(location = 0) in vec2 position;

void main()
{
  gl_Position = texMatrix * vec4(position, 0.0, 1.0);
}