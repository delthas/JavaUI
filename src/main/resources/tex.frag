# version 330 core

in vec2 position_;

uniform sampler2D s;
out vec4 outputColor;

uniform float alpha;

void main()
{
  vec4 tex = texture(s, position_);
  outputColor = vec4(tex.xyz, tex.a * alpha);
}
