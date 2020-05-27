varying mediump vec2 textureCoordinate;
precision mediump float;
				
uniform float width;
uniform float height;
uniform float gamma;
uniform sampler2D videoFrame;

/*
** Gamma correction
** Details: http://blog.mouaif.org/2009/01/22/photoshop-gamma-correction-shader/
*/

#define GammaCorrection(color, gamma)								pow(color, vec3(1.0 / gamma))

void main()
{	
	vec3 color = texture2D(videoFrame, textureCoordinate).rgb;
	gl_FragColor = vec4(GammaCorrection(color, gamma), 1.0); 
}