# version 330 core

layout(location = 0) in vec2 position;

uniform sampler2D s;
uniform vec4 imagePosition;
out vec4 outputColor;

void main()
{
jk:kj:bk;:vihk:khv:h
  outputColor = texelFetch(s, ivec2(position * (imagePosition.zw - imagePosition.xy) + imagePosition.xy), 0);
}