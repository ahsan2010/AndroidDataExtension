varying highp vec2 textureCoordinate;
precision mediump float;
				
uniform float width;
uniform float height;
uniform sampler2D videoFrame;
uniform int kSize;


vec4 erode_radius1(vec2 textCoord, float step_w, float step_h)
{
	lowp vec4 c = texture2D(videoFrame, textureCoordinate);
	lowp vec4 minc = c;
	minc = min(minc, texture2D(videoFrame, textureCoordinate + vec2(-step_w, 0)));
	minc = min(minc, texture2D(videoFrame, textureCoordinate + vec2(0, -step_h)));
	minc = min(minc, texture2D(videoFrame, textureCoordinate + vec2(step_w, 0)));
	minc = min(minc, texture2D(videoFrame, textureCoordinate + vec2(0, step_h)));
	return minc;
}

/*vec4 erode(vec2 textCoord, int kSize, float step_w, float step_h)
{
	int k2 = kSize/2;
	int kMax = kSize-k2;
	vec4 c = texture2D(videoFrame, textureCoordinate);
	vec4 minc = c;
	for (int i = -k2; i < kMax; i++) {
		for (int j = -k2; j < kMax; j++) {
			minc = min(minc, texture2D(videoFrame, textureCoordinate + vec2(step_w*float(i), step_h*float(j))));
		}
	}
	return minc;
}*/

void main()
{
	float step_w = float(kSize)/width;
	float step_h = float(kSize)/height;
	
	gl_FragColor = erode_radius1(textureCoordinate, step_w, step_h);
}