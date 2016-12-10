# version 330 core

const float gamma = 1/2.2;

uniform vec3 color;
in vec2 mapping;

out vec4 outputColor;

void main()
{
    float lensqr = dot(mapping, mapping);
    if(lensqr > 1.0)
        discard;
    outputColor = pow(vec4(color, 1.0), vec4(gamma, gamma, gamma, 1.0));
}
