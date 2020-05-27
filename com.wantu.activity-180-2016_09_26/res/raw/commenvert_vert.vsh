

attribute vec4 Position;
attribute vec4 inputTextureCoordinate;
varying vec2 textureCoordinate;

void main()
{
	textureCoordinate = inputTextureCoordinate.xy;
    gl_Position = Position;
}
