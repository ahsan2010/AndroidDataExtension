varying mediump vec2 textureCoordinate;
precision mediump float;

uniform float width;
uniform float height;
uniform float texelWidthOffset;
uniform float texelHeightOffset;
uniform lowp float threshold;

uniform sampler2D videoFrame;

bvec3 wrapGreaterThanEqual(vec3 v1, vec3 v2)
{
	bool b1 = v1.x >= v2.x ? true : false;
	bool b2 = v1.y >= v2.y ? true : false;
	bool b3 = v1.z >= v2.z ? true : false;
	return bvec3(b1, b2, b3);
}

bvec3 wrapLessThanEqual(vec3 v1, vec3 v2)
{
	bool b1 = v1.x <= v2.x ? true : false;
	bool b2 = v1.y <= v2.y ? true : false;
	bool b3 = v1.z <= v2.z ? true : false;
	return bvec3(b1, b2, b3);
}

void main()
{
    int KERNEL_SIZE = 5;
	float cmatrix[5];
	cmatrix[0] = 0.105064;
	cmatrix[1] = 0.26279;
	cmatrix[2] = 0.264292;
	cmatrix[3] = 0.26279;
	cmatrix[4] = 0.105064;
    
    lowp vec3 c1 = texture2D(videoFrame, textureCoordinate).rgb;
    vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);
    vec3 a = vec3(0.0);
    vec3 af = vec3(0.0);
    
    int multiplier = (0 - ((KERNEL_SIZE - 1) / 2));
    vec2 blurStep = float(multiplier) * singleStepOffset;
    lowp vec3 c2 = texture2D(videoFrame, textureCoordinate + blurStep).rgb;
    lowp vec3 d = c1 - c2;
    bvec3 minb = wrapGreaterThanEqual(d, vec3(-threshold));
    bvec3 maxb = wrapLessThanEqual(d, vec3(threshold));
    lowp vec3 bb = vec3(float((minb.r && maxb.r)), float((minb.g && maxb.g)), float((minb.b && maxb.b)));
    
    a += bb*(cmatrix[0]*c2);
    af += bb*cmatrix[0];
    
    multiplier = (1 - ((KERNEL_SIZE - 1) / 2));
    blurStep = float(multiplier) * singleStepOffset;
    c2 = texture2D(videoFrame, textureCoordinate + blurStep).rgb;
    d = c1 - c2;
    minb = wrapGreaterThanEqual(d, vec3(-threshold));
    maxb = wrapLessThanEqual(d, vec3(threshold));
    bb = vec3(float((minb.r && maxb.r)),
                        float((minb.g && maxb.g)),
                        float((minb.b && maxb.b)));
    a += bb*(cmatrix[1]*c2);
    af += bb*cmatrix[1];
    
    multiplier = (2 - ((KERNEL_SIZE - 1) / 2));
    blurStep = float(multiplier) * singleStepOffset;
    c2 = texture2D(videoFrame, textureCoordinate + blurStep).rgb;
    d = c1 - c2;
    minb = wrapGreaterThanEqual(d, vec3(-threshold));
    maxb = wrapLessThanEqual(d, vec3(threshold));
    bb = vec3(float((minb.r && maxb.r)),
              float((minb.g && maxb.g)),
              float((minb.b && maxb.b)));
    a += bb*(cmatrix[2]*c2);
    af += bb*cmatrix[2];
    
    multiplier = (3 - ((KERNEL_SIZE - 1) / 2));
    blurStep = float(multiplier) * singleStepOffset;
    c2 = texture2D(videoFrame, textureCoordinate + blurStep).rgb;
    d = c1 - c2;
    minb = wrapGreaterThanEqual(d, vec3(-threshold));
    maxb = wrapLessThanEqual(d, vec3(threshold));
    bb = vec3(float((minb.r && maxb.r)),
              float((minb.g && maxb.g)),
              float((minb.b && maxb.b)));
    a += bb*(cmatrix[3]*c2);
    af += bb*cmatrix[3];
    
    multiplier = (4 - ((KERNEL_SIZE - 1) / 2));
    blurStep = float(multiplier) * singleStepOffset;
    c2 = texture2D(videoFrame, textureCoordinate + blurStep).rgb;
    d = c1 - c2;
    minb = wrapGreaterThanEqual(d, vec3(-threshold));
    maxb = wrapLessThanEqual(d, vec3(threshold));
    bb = vec3(float((minb.r && maxb.r)),
              float((minb.g && maxb.g)),
              float((minb.b && maxb.b)));
    a += bb*(cmatrix[4]*c2);
    af += bb*cmatrix[4];
    
    a.r = af.r == 0.0 ? c1.r : a.r/af.r;
    a.g = af.g == 0.0 ? c1.g : a.g/af.g;
    a.b = af.b == 0.0 ? c1.b : a.b/af.b;
    a = clamp(a, vec3(0.0), vec3(1.0));
    
    gl_FragColor = vec4(a, 1.0);
}
