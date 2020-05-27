varying highp vec2 textureCoordinate;
precision mediump float;
uniform sampler2D videoFrame;

void main(void)
{
    lowp vec4 textureColor = texture2D(videoFrame, textureCoordinate);
    float luminance = dot(textureColor.rgb, vec3(0.2125, 0.7154, 0.0721));
     
    gl_FragColor = vec4(vec3(luminance), textureColor.a);
}

