varying mediump vec2 textureCoordinate;
precision mediump float;
				
uniform float width;
uniform float height;
uniform sampler2D videoFrame;
uniform sampler2D curveTex;

void main()
{	
	lowp vec4 inputColor = texture2D(videoFrame, textureCoordinate);
	lowp vec4 colorOut = vec4(1.0);
	colorOut.r = texture2D(curveTex, vec2(inputColor.r, 0.0)).r;
	colorOut.g = texture2D(curveTex, vec2(inputColor.g, 0.0)).g;
	colorOut.b = texture2D(curveTex, vec2(inputColor.b, 0.0)).b;
	colorOut.a = inputColor.a;
	
	gl_FragColor = colorOut;
}