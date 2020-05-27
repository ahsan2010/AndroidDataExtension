
#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_fragmentColor;
varying vec2 v_texCoord;

uniform sampler2D CC_Texture0;
uniform sampler2D CC_Texture1;

uniform vec2 inputTileSize;
uniform vec2 displayTileSize;
uniform float numTiles;
uniform int colorOn;

void main() {
	vec2 xy = v_texCoord;
    xy = xy - mod(xy, displayTileSize);
    
    vec4 lumcoeff = vec4(0.299,0.587,0.114,0.0);
    
    vec4 inputColor = texture2D(CC_Texture0, xy);
    float lum = dot(inputColor,lumcoeff);
    lum = 1.0 - lum;
    
    float stepsize = 1.0 / numTiles;
    float lumStep = (lum - mod(lum, stepsize)) / stepsize;
    
    float rowStep = 1.0 / inputTileSize.x;
    float x = mod(lumStep, rowStep);
    float y = floor(lumStep / rowStep);
    
    vec2 startCoord = vec2(float(x) *  inputTileSize.x, float(y) * inputTileSize.y);
    vec2 finalCoord = startCoord + ((v_texCoord - xy) * (inputTileSize / displayTileSize));
    
    vec4 color = texture2D(CC_Texture1, finalCoord);
    if (colorOn == 1) {
        color = color * inputColor;
    }
    gl_FragColor = color;
}

