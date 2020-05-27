varying mediump vec2 textureCoordinate;
precision mediump float;
				
uniform float width;
uniform float height;
uniform sampler2D videoFrame;
uniform lowp float desaturation;
uniform lowp vec3 red;
uniform lowp vec3 green;
uniform lowp vec3 blue;
uniform lowp vec3 gray;

// luma 
const lowp vec3 lumcoeff = vec3(0.299,0.587,0.114);

void main()
{	
	lowp vec4 input0 = texture2D(videoFrame, textureCoordinate);
	
	lowp vec3 redchannel = vec3(input0.r) * red;
	lowp vec3 greenchannel = vec3(input0.g) * green;
	lowp vec3 bluechannel = vec3(input0.b) * blue;
	
	lowp vec3 result = redchannel + greenchannel + bluechannel;
	
	vec3 graychannel = vec3(dot(input0.rgb,lumcoeff)) * (1.0 + gray);
		
	graychannel = vec3((graychannel.r + graychannel.g + graychannel.b)/3.0);

	gl_FragColor = vec4(mix(result, graychannel, desaturation), input0.a);
}