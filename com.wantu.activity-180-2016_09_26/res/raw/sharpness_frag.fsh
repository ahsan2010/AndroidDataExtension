varying mediump vec2 textureCoordinate;
precision mediump float;

#define KERNEL_SIZE 9
				
uniform float width;
uniform float height;

uniform sampler2D videoFrame;

void main()
{
	float kernel[KERNEL_SIZE];
	
	
	kernel[0] = 0.0;
	kernel[1] = -1.0;
	kernel[2] = 0.0;
	kernel[3] = -1.0;
	kernel[4] = 5.0;
	kernel[5] = -1.0;
	kernel[6] = 0.0;
	kernel[7] = -1.0;
	kernel[8] = 0.0;
	
	float step_w = 1.0/width;
	float step_h = 1.0/height;
	
	vec2 offset[KERNEL_SIZE];
	offset[0] = vec2(-step_w, -step_h);
	offset[1] = vec2(0.0, -step_h);
	offset[2] = vec2(step_w, -step_h);
	offset[3] = vec2(-step_w, 0.0);
	offset[4] = vec2(0.0, 0.0);
	offset[5] = vec2(step_w, 0.0);
	offset[6] = vec2(-step_w, step_h);
	offset[7] = vec2(0.0, step_h);
	offset[8] = vec2(step_w, step_h);
	int i = 0;
	vec4 sum = vec4(0.0);
	
	for( i=0; i<KERNEL_SIZE; i++ )
	{
		vec4 tmp = texture2D(videoFrame, textureCoordinate + offset[i]);
		sum += tmp * kernel[i];
	}
	
	gl_FragColor = sum;
}