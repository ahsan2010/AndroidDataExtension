varying highp vec2 textureCoordinate;
precision mediump float;

uniform float width;
uniform float height;
uniform sampler2D videoFrame;
uniform sampler2D blendTex;
uniform vec4 bdColor;
uniform int blendMode;
uniform float opacity;


float BlendOverlayf(float base, float blend)
{
	return (base < 0.5 ? (2.0 * base * blend) : (1.0 - 2.0 * (1.0 - base) * (1.0 - blend)));
}

vec3 BlendOverlay(vec3 base, vec3 blend, float opacity)
{
	float r = BlendOverlayf(base.r, blend.r);
	float g = BlendOverlayf(base.g, blend.g);
	float b = BlendOverlayf(base.b, blend.b);
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
	
	lowp vec3 color = BlendOverlay(baseColor, blendColor,opacity); 
    gl_FragColor = vec4(color, 1.0);
    
}