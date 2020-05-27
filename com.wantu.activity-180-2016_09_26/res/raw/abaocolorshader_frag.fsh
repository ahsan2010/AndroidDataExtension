varying highp vec2 textureCoordinate;
precision mediump float;

uniform float width;
uniform float height;
uniform sampler2D videoFrame;

#define  labXr_32f  0.433953 /* = xyzXr_32f / 0.950456 */
#define  labXg_32f  0.376219 /* = xyzXg_32f / 0.950456 */
#define  labXb_32f  0.189828 /* = xyzXb_32f / 0.950456 */

#define  labYr_32f  0.212671 /* = xyzYr_32f */
#define  labYg_32f  0.715160 /* = xyzYg_32f */ 
#define  labYb_32f  0.072169 /* = xyzYb_32f */ 

#define  labZr_32f  0.017758 /* = xyzZr_32f / 1.088754 */
#define  labZg_32f  0.109477 /* = xyzZg_32f / 1.088754 */
#define  labZb_32f  0.872766 /* = xyzZb_32f / 1.088754 */

#define  labRx_32f  3.0799327  /* = xyzRx_32f * 0.950456 */
#define  labRy_32f  (-1.53715) /* = xyzRy_32f */
#define  labRz_32f  (-0.542782)/* = xyzRz_32f * 1.088754 */

#define  labGx_32f  (-0.921235)/* = xyzGx_32f * 0.950456 */
#define  labGy_32f  1.875991   /* = xyzGy_32f */ 
#define  labGz_32f  0.04524426 /* = xyzGz_32f * 1.088754 */

#define  labBx_32f  0.0528909755 /* = xyzBx_32f * 0.950456 */
#define  labBy_32f  (-0.204043)  /* = xyzBy_32f */
#define  labBz_32f  1.15115158   /* = xyzBz_32f * 1.088754 */

#define labSmallScale_32f  7.787
#define labSmallShift_32f  0.13793103448275862  /* 16/116 */
#define labLScale_32f      116.0
#define labLShift_32f      16.0
#define labLScale2_32f     903.3

#define  labT_32f   0.008856

float cvCbrt(float value)
{
	return pow(value, 1.0/3.0);
}

vec3 rgb2Lab(vec3 rgb)
{
	float b = rgb.b, g = rgb.g, r = rgb.r;
	float x, y, z;
	float L, a;
	x = b*labXb_32f + g*labXg_32f + r*labXr_32f;
	y = b*labYb_32f + g*labYg_32f + r*labYr_32f;
	z = b*labZb_32f + g*labZg_32f + r*labZr_32f;
	
	if( x > labT_32f )
	x = cvCbrt(x);
	else
	x = x*labSmallScale_32f + labSmallShift_32f;
	
	if( z > labT_32f )
	z = cvCbrt(z);
	else
	z = z*labSmallScale_32f + labSmallShift_32f;
	
	if( y > labT_32f )
	{
		y = cvCbrt(y);
		L = y*labLScale_32f - labLShift_32f;
	}
	else
	{
		L = y*labLScale2_32f;
		y = y*labSmallScale_32f + labSmallShift_32f;
	}
	
	a = 500.0*(x - y);
	b = 200.0*(y - z);
	
	return vec3(L, a, b);
}

vec3 lab2Rgb(vec3 lab)
{
	float L = lab.r, a = lab.g, b = lab.b;
	float x, y, z;
	float g, r;
	
	L = (L + labLShift_32f)*(1.0/labLScale_32f);
	x = (L + a*0.002);
	z = (L - b*0.005);
	y = L*L*L;
	x = x*x*x;
	z = z*z*z;
	
	b = x*labBx_32f + y*labBy_32f + z*labBz_32f;
	g = x*labGx_32f + y*labGy_32f + z*labGz_32f;
	r = x*labRx_32f + y*labRy_32f + z*labRz_32f;
	return vec3(r, g, b);
}

void main()
{
	lowp vec4 c = texture2D(videoFrame, textureCoordinate);
	lowp vec3 t = rgb2Lab(c.rgb);
	t.b = t.g;
	t = lab2Rgb(t);
	gl_FragColor = vec4(t, c.a);
}