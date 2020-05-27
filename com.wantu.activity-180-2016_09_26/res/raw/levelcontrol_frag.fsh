varying highp vec2 textureCoordinate;
precision highp float;

uniform float width;
uniform float height;
uniform sampler2D videoFrame;

uniform float gamma;
uniform float minInput;
uniform float maxInput;
uniform float minOutput;
uniform float maxOutput;

/*
** Gamma correction
** Details: http://blog.mouaif.org/2009/01/22/photoshop-gamma-correction-shader/
*/

#define GammaCorrection(color, gamma)								pow(color, vec3(1.0 / gamma))

/*
** Levels control (input (+gamma), output)
** Details: http://blog.mouaif.org/2009/01/28/levels-control-shader/
*/

#define LevelsControlInputRange(color, minInput, maxInput)				min(max(color - vec3(minInput), vec3(0.0)) / (vec3(maxInput) - vec3(minInput)), vec3(1.0))
#define LevelsControlInput(color, minInput, gamma, maxInput)				GammaCorrection(LevelsControlInputRange(color, minInput, maxInput), gamma)
#define LevelsControlOutputRange(color, minOutput, maxOutput) 			mix(vec3(minOutput), vec3(maxOutput), color)
#define LevelsControl(color, minInput, gamma, maxInput, minOutput, maxOutput) 	LevelsControlOutputRange(LevelsControlInput(color, minInput, gamma, maxInput), minOutput, maxOutput)

void main()
{
	lowp vec3 color = texture2D(videoFrame, textureCoordinate).xyz;
	lowp vec3 levelColor = LevelsControl(color, minInput, gamma, maxInput, minOutput, maxOutput);	
	gl_FragColor = vec4(levelColor, 1.0);
}