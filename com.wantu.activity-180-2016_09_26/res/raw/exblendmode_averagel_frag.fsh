varying highp vec2 textureCoordinate;
precision mediump float;

uniform float width;
uniform float height;
uniform sampler2D videoFrame;
uniform sampler2D blendTex;
uniform vec4 bdColor;
uniform int blendMode;
uniform float opacity;

vec3 BlendNormal(vec3 base, vec3 blend, float opacity) 
{
    return (blend * opacity + blend * (1.0 - opacity));
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
	
	lowp vec3 color = ((baseColor + blendColor)/2.0)*opacity+baseColor*(1.0 - opacity);
    gl_FragColor = vec4(color, 1.0);
    
}