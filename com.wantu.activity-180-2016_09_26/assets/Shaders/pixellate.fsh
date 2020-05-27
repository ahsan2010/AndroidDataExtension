

#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_fragmentColor;
varying vec2 v_texCoord;

uniform sampler2D CC_Texture0;

uniform highp float fractionalWidthOfPixel;
uniform highp float aspectRatio;

void main() {
	highp vec2 sampleDivisor = vec2(fractionalWidthOfPixel, fractionalWidthOfPixel / aspectRatio);
    
    highp vec2 samplePos = v_texCoord - mod(v_texCoord, sampleDivisor) + 0.5 * sampleDivisor;
    gl_FragColor = texture2D(CC_Texture0, samplePos ) * v_fragmentColor;
}

