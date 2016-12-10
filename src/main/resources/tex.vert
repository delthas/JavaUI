# version 330 core

in vec2 position;
out vec2 position_;

uniform mat4 screenPosition;
uniform mat4 texPosition;

void main()
{
  gl_Position = screenPosition * vec4(position, 0.0, 1.0);
  position_ = (texPosition * vec4(position, 0.0, 1.0)).xy;
}
