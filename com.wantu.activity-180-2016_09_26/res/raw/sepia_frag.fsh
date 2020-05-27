varying mediump vec2 textureCoordinate;
precision mediump float;
				
uniform float width;
uniform float height;
uniform sampler2D videoFrame;
uniform lowp float desaturation; //0.0f~1.0f
uniform lowp float toned;		 //0.0f~1.0f
uniform lowp vec3 lightColor;
uniform lowp vec3 darkColor;

vec3 lerp(vec3 v1, vec3 v2, float l)
{
	return v1 + l * (v2 - v1);
}

void main()
{
	lowp vec3 scnColor = lightColor * texture2D(videoFrame, textureCoordinate).xyz;
	lowp vec3 grayXfer = vec3(0.3, 0.59, 0.11);
	lowp float gray = dot(grayXfer, scnColor);
	lowp vec3 muted = lerp(scnColor, vec3(gray), desaturation);
	lowp vec3 sepia = lerp(darkColor, lightColor, gray);
	lowp vec3 result = lerp(muted, sepia, toned);
	
	gl_FragColor = vec4(result, 1.0);
}