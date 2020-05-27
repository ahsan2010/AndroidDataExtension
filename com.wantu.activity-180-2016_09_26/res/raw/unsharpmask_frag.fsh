varying mediump vec2 textureCoordinate;
precision mediump float;
				
uniform float width;
uniform float height;
uniform float strength;

uniform sampler2D videoFrame;
uniform sampler2D blurTex;

void main()
{
	vec4 c1 = texture2D(videoFrame, textureCoordinate);
	vec4 c2 = texture2D(blurTex, textureCoordinate);
	vec4 mask = c1 - c2;
	vec4 result = c1 + strength*mask;
	result.a = 1.0;
	gl_FragColor = result;
}