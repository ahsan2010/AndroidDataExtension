varying mediump vec2 textureCoordinate;
precision mediump float;
				
uniform float width;
uniform float height;

uniform float scanline;

uniform sampler2D videoFrame;

void main()
{	
	const float scanlineHalfWidth = 0.025;
	if (textureCoordinate.x < scanline - scanlineHalfWidth) {
		vec3 tc = vec3(1.0, 0.0, 0.0);
		vec3 pixcol = texture2D(videoFrame, textureCoordinate).rgb;
		vec3 colors[3];
		colors[0] = vec3(0.,0.,1.);
		colors[1] = vec3(1.,1.,0.);
		colors[2] = vec3(1.,0.,0.);
		//float lum = (pixcol.r+pixcol.g+pixcol.b)/3.;
		float lum = dot(vec3(0.30, 0.59, 0.11), pixcol.rgb);
		int ix = (lum < 0.5)? 0:1;
		tc = mix(colors[ix],colors[ix+1],(lum-float(ix)*0.5)/0.5);
		gl_FragColor = vec4(tc, 1.0);
	} else if (textureCoordinate.x > scanline + scanlineHalfWidth) {
		gl_FragColor = texture2D(videoFrame, textureCoordinate); 
	} else {
		float v = smoothstep(scanline - scanlineHalfWidth, scanline + scanlineHalfWidth, textureCoordinate.x);
		vec3 pixcol = texture2D(videoFrame, textureCoordinate).rgb;
		gl_FragColor = vec4(mix(pixcol, vec3(1.0, 1.0, 1.0), v), 1.0);
	}

}