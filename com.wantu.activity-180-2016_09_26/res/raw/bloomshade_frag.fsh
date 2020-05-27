varying highp vec2 textureCoordinate;
precision mediump float;
				
uniform float width;
uniform float height;
uniform float strength;
uniform sampler2D videoFrame;

void main()
{
	vec3 bloom = texture2D(videoFrame, textureCoordinate).rgb;
	vec3 col = vec3(1.0, 0.0, 0.0);
	col = pow(bloom.rgb, vec3(strength));
	col *= bloom;
	col += bloom;
	gl_FragColor = vec4(col, 1.0);
}