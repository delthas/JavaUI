# version 330 core

in vec2 position_;

uniform sampler2D s;
uniform vec4 imagePosition;
uniform vec3 color;
out vec4 outputColor;

void main()
{
  float tex = texture(s, mix(imagePosition.xy, imagePosition.zw, vec2(0.5, 0.5) + position_)).r;
  outputColor = vec4(color, tex);
}