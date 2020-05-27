varying highp vec2 textureCoordinate;
precision mediump float;

uniform float width;
uniform float height;
uniform sampler2D videoFrame;
uniform sampler2D blendTex;
uniform vec4 bdColor;
uniform int blendMode;
uniform float opacity;


float BlendSoftLightf(float base, float blend)
{
	return ((blend < 0.5) ? (2.0 * base * blend + base * base * (1.0 - 2.0 * blend)) : (sqrt(base) * (2.0 * blend - 1.0) + 2.0 * base * (1.0 - blend)));
}

vec3 BlendSoftLight(vec3 base, vec3 blend, float opacity)
{
	float r = BlendSoftLightf(base.r, blend.r);
	float g = BlendSoftLightf(base.g, blend.g);
	float b = BlendSoftLightf(base.b, blend.b);
	vec3 c = vec3(r, g, b);
	return c * opacity + base * (1.0 - opacity);
}

void main()
{
	lowp vec3 baseColor = texture2D(videoFrame, textureCoordinate).xyz;
	lowp vec3 blendColor = vec3(1.0);
	if(bdColor.a != 0.0) {
		blendColor = bdColor.rgb;
	} else {
		blendColor = texture2D(blendTex, textureCoordinate).xyz;
	}
	lowp vec3 color = BlendSoftLight(baseColor, blendColor,opacity); 
    gl_FragColor = vec4(color, 1.0);
    
}