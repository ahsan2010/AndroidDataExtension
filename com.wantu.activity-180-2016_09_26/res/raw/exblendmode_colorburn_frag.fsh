varying highp vec2 textureCoordinate;
precision mediump float;

uniform float width;
uniform float height;
uniform sampler2D videoFrame;
uniform sampler2D blendTex;
uniform vec4 bdColor;
uniform int blendMode;
uniform float opacity;


float BlendColorBurnf(float base, float blend)
{
	return ((blend == 0.0) ? blend : max((1.0 - ((1.0 - base) / blend)), 0.0));
}

vec3 BlendColorBurn(vec3 base, vec3 blend, float opacity)
{
	float r = BlendColorBurnf(base.r, blend.r);
	float g = BlendColorBurnf(base.g, blend.g);
	float b = BlendColorBurnf(base.b, blend.b);
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
	
	lowp vec3 color = BlendColorBurn(baseColor, blendColor,opacity); 
    gl_FragColor = vec4(color, 1.0);
    
}