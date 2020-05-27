varying highp vec2 textureCoordinate;

uniform sampler2D videoFrame;
uniform sampler2D alphaMask;

void main(void)
{
	lowp vec4 c = texture2D(videoFrame, textureCoordinate);
	c.a = texture2D(alphaMask, textureCoordinate).a;
    gl_FragColor = c;
}
