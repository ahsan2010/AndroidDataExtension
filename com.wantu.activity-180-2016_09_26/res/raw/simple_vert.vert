attribute vec4 Position;
attribute vec4 inputTextureCoordinate;
varying vec2 textureCoordinate;
uniform mat4 Projection;
uniform mat4 Modelview;

uniform sampler2D videoFrame;

void main(void)
{
	textureCoordinate = inputTextureCoordinate.xy;
    gl_Position = Projection * Modelview * Position;
}

