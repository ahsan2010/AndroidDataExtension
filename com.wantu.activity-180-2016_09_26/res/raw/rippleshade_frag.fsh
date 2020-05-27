varying highp vec2 textureCoordinate;
precision mediump float;

#define KERNEL_SIZE 9
				
uniform float width;
uniform float height;
uniform float time; // time in seconds
uniform sampler2D videoFrame;

void main()
{
	vec2 p = -1.0 + 2.0 * textureCoordinate;
	float len = length(p);
	vec2 uv = textureCoordinate + (p/len)*cos(len*12.0-time*4.0)*0.03;
	vec3 col = texture2D(videoFrame, uv).xyz;
	gl_FragColor = vec4(col,1.0);
}