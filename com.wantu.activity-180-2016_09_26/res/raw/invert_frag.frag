varying highp vec2 textureCoordinate;
precision mediump float;
uniform sampler2D videoFrame;

void main(void)
{
    lowp vec4 textureColor = texture2D(videoFrame, textureCoordinate);
    lowp vec3 invertColor = vec3(1.0) - textureColor.rgb; 
    gl_FragColor = vec4(invertColor, textureColor.a);
}

