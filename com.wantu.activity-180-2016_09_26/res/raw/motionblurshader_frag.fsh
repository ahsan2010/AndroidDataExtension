varying mediump vec2 textureCoordinate;
precision mediump float;
				
uniform float width;
uniform float height;
uniform sampler2D videoFrame;

uniform float angle;
uniform float distance;
uniform float zoom;
uniform float rotation;

void main()
{
	vec2 uv = textureCoordinate;
	float imageRadius = 0.5;
	float cx = 0.5;
	float cy = 0.5;
	float translateX = distance * cos(angle);
	float translateY = distance * (-sin(angle));
	float maxDistance = distance + abs(rotation*imageRadius) + zoom*imageRadius;
	int repetitions = int(maxDistance*sqrt(cx*width*cx*width + cy*height*cy*height)*0.4);
	
	int i = 0;
	vec4 sumc = vec4(0.0);
	int count = 0;
	for ( i = 0; i < repetitions; i++) {
		float f = float(i)/float(repetitions);
		float s = 1.0-zoom*f;
		mat3 m = mat3(	vec3(s, 0.0, cx+f*translateX),
						vec3(0.0, s, cy+f*translateY),
						vec3(0.0, 0.0, 1.0) );
		if (rotation != 0.0) {
			float selta = -rotation*f;
			mat3 r = mat3(	vec3(cos(selta), sin(selta), 0.0),
							vec3(-sin(selta), cos(selta), 0.0),
							vec3(0.0, 0.0, 1.0) );
			m = r*m;
		}
		m = mat3(	vec3(1.0, 0.0, -cx),
					vec3(0.0, 1.0, -cy),
					vec3(0.0, 0.0, 1.0) ) * m;
		vec3 pt = vec3(uv, 1.0);
		pt = pt*m;
		if (pt.x < 0.0 || pt.x >= 1.0) {
			break;
		}
		if (pt.y < 0.0 || pt.y >= 1.0) {
			break;
		}
		count++;
		sumc += texture2D(videoFrame, pt.xy);
	}
	if (count == 0) {
		gl_FragColor = texture2D(videoFrame, uv);
	} else {
		sumc /= float(count);
		gl_FragColor = sumc;
	}
}