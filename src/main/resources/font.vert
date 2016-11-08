# version 330 core

in vec2 position;
out vec2 position_;

uniform vec4 fontPosition;

void main()
{
  gl_Position = vec4(mix(fontPosition.xy, fontPosition.zw, position + vec2(0.5,0.5)), 0.0, 1.0);
  position_ = position;
}