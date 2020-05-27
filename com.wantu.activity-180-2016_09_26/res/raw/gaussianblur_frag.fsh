varying mediump vec2 textureCoordinate;
precision mediump float;

#define KERNEL_SIZE 9
				
uniform float width;
uniform float height;
uniform int matrixlength;
uniform float cmatrix[256];
uniform bool isHorBlur;
//uniform vec2 lutmatrix[256];

uniform sampler2D videoFrame;

void main()
{
//	float kernel[KERNEL_SIZE];
//	
//	
//	kernel[0] = 1.0/16.0;
//	kernel[1] = 2.0/16.0;
//	kernel[2] = 1.0/16.0;
//	kernel[3] = 2.0/16.0;
//	kernel[4] = 4.0/16.0;
//	kernel[5] = 2.0/16.0;
//	kernel[6] = 1.0/16.0;
//	kernel[7] = 2.0/16.0;
//	kernel[8] = 1.0/16.0;
	
	float step_w = 1.0/width;
	float step_h = 1.0/height;
	
//	vec2 offset[KERNEL_SIZE];
//	offset[0] = vec2(-step_w, -step_h);
//	offset[1] = vec2(0.0, -step_h);
//	offset[2] = vec2(step_w, -step_h);
//	offset[3] = vec2(-step_w, 0.0);
//	offset[4] = vec2(0.0, 0.0);
//	offset[5] = vec2(step_w, 0.0);
//	offset[6] = vec2(-step_w, step_h);
//	offset[7] = vec2(0.0, step_h);
//	offset[8] = vec2(step_w, step_h);
	int i = 0;
	vec4 sum = vec4(0.0);
	int halfValue = matrixlength/2;
	
	if (isHorBlur) {
		for( i=0; i<matrixlength; i++ )
		{
			vec4 tmp = texture2D(videoFrame, textureCoordinate + vec2(float(halfValue - i)*step_w, 0.0));
			sum += tmp * cmatrix[i];
		}
	} else {
		for( i=0; i<matrixlength; i++ )
		{
			vec4 tmp = texture2D(videoFrame, textureCoordinate + vec2(0.0, float(halfValue - i)*step_h));
			sum += tmp * cmatrix[i];
		}
	}
	
	gl_FragColor = sum;
}