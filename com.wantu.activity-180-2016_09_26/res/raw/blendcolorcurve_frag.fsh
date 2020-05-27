varying mediump vec2 textureCoordinate;
precision mediump float;

uniform float width;
uniform float height;
uniform float blendAlpha;
uniform sampler2D videoFrame;
uniform sampler2D texture;
uniform sampler2D curveTex;

/*
** Blend mode
enum {
	BM_SCREEN,
	BM_SOFTLIGHT,
	BM_HARDLIGHT
};
*/
uniform int blendMode;

/*
** Hue, saturation, luminance
*/

vec3 RGBToHSL(vec3 color)
{
	vec3 hsl; // init to 0 to avoid warnings ? (and reverse if + remove first part)
	
	float fmin = min(min(color.r, color.g), color.b);    //Min. value of RGB
	float fmax = max(max(color.r, color.g), color.b);    //Max. value of RGB
	float delta = fmax - fmin;             //Delta RGB value

	hsl.z = (fmax + fmin) / 2.0; // Luminance

	if (delta == 0.0)		//This is a gray, no chroma...
	{
		hsl.x = 0.0;	// Hue
		hsl.y = 0.0;	// Saturation
	}
	else                                    //Chromatic data...
	{
		if (hsl.z < 0.5)
			hsl.y = delta / (fmax + fmin); // Saturation
		else
			hsl.y = delta / (2.0 - fmax - fmin); // Saturation
		
		float deltaR = (((fmax - color.r) / 6.0) + (delta / 2.0)) / delta;
		float deltaG = (((fmax - color.g) / 6.0) + (delta / 2.0)) / delta;
		float deltaB = (((fmax - color.b) / 6.0) + (delta / 2.0)) / delta;

		if (color.r == fmax )
			hsl.x = deltaB - deltaG; // Hue
		else if (color.g == fmax)
			hsl.x = (1.0 / 3.0) + deltaR - deltaB; // Hue
		else if (color.b == fmax)
			hsl.x = (2.0 / 3.0) + deltaG - deltaR; // Hue

		if (hsl.x < 0.0)
			hsl.x += 1.0; // Hue
		else if (hsl.x > 1.0)
			hsl.x -= 1.0; // Hue
	}

	return hsl;
}

float HueToRGB(float f1, float f2, float hue)
{
	if (hue < 0.0)
		hue += 1.0;
	else if (hue > 1.0)
		hue -= 1.0;
	float res;
	if ((6.0 * hue) < 1.0)
		res = f1 + (f2 - f1) * 6.0 * hue;
	else if ((2.0 * hue) < 1.0)
		res = f2;
	else if ((3.0 * hue) < 2.0)
		res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;
	else
		res = f1;
	return res;
}

vec3 HSLToRGB(vec3 hsl)
{
	vec3 rgb;
	
	if (hsl.y == 0.0)
		rgb = vec3(hsl.z); // Luminance
	else
	{
		float f2;
		
		if (hsl.z < 0.5)
			f2 = hsl.z * (1.0 + hsl.y);
		else
			f2 = (hsl.z + hsl.y) - (hsl.y * hsl.z);
			
		float f1 = 2.0 * hsl.z - f2;
		
		rgb.r = HueToRGB(f1, f2, hsl.x + (1.0/3.0));
		rgb.g = HueToRGB(f1, f2, hsl.x);
		rgb.b= HueToRGB(f1, f2, hsl.x - (1.0/3.0));
	}
	
	return rgb;
}

float BlendScreenf(float base, float blend)
{
	return (1.0 - ((1.0 - base) * (1.0 - blend)));
}

vec3 BlendScreen(vec3 base, vec3 blend)
{
	return vec3(BlendScreenf(base.r, blend.r), BlendScreenf(base.g, blend.g), BlendScreenf(base.b, blend.b));
}

float BlendSoftLightf(float base, float blend)
{
	return ((blend < 0.5) ? (2.0 * base * blend + base * base * (1.0 - 2.0 * blend)) : (sqrt(base) * (2.0 * blend - 1.0) + 2.0 * base * (1.0 - blend)));
}

vec3 BlendSoftLight(vec3 base, vec3 blend)
{
	return vec3(BlendSoftLightf(base.r, blend.r), BlendSoftLightf(base.g, blend.g), BlendSoftLightf(base.b, blend.b));
}

float BlendOverlayf(float base, float blend)
{
	return (base < 0.5 ? (2.0 * base * blend) : (1.0 - 2.0 * (1.0 - base) * (1.0 - blend)));
}

vec3 BlendOverlay(vec3 base, vec3 blend)
{
	return vec3(BlendOverlayf(base.r, blend.r), BlendOverlayf(base.g, blend.g), BlendOverlayf(base.b, blend.b));
}

vec3 BlendHardLight(vec3 base, vec3 blend)
{
	return BlendOverlay(blend, base);
}

// Color Mode keeps the brightness of the base color and applies both the hue and saturation of the blend color.
vec3 BlendColor(vec3 base, vec3 blend)
{
	vec3 blendHSL = RGBToHSL(blend);
	return HSLToRGB(vec3(blendHSL.r, blendHSL.g, RGBToHSL(base).b));
}

void main()
{	
	lowp vec4 inputColor = texture2D(videoFrame, textureCoordinate);
	lowp vec3 colorOut = vec3(1.0);
	colorOut.r = texture2D(curveTex, vec2(inputColor.r, 0.0)).r;
	colorOut.g = texture2D(curveTex, vec2(inputColor.g, 0.0)).g;
	colorOut.b = texture2D(curveTex, vec2(inputColor.b, 0.0)).b;
	
	lowp vec3 t = texture2D(texture, textureCoordinate).xyz;
	lowp vec3 c = vec3(1.0);
	if (blendMode == 0)		/*Screen*/
	{	
		c = BlendScreen(colorOut, t);
	} 
	else if (blendMode == 1) /*SoftLight*/
	{
		c =  BlendSoftLight(colorOut, t);
	}
	else if (blendMode == 2) /*HardLight*/
	{
		c = BlendSoftLight(colorOut, t);
	}
	else if (blendMode == 3) /*Overlay*/
	{
		c = BlendOverlay(colorOut, t);
	}
	gl_FragColor = mix(vec4(c, 1.0), vec4(colorOut, 1.0), blendAlpha);
}