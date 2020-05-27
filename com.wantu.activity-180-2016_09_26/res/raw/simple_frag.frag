varying highp vec2 textureCoordinate;

uniform sampler2D videoFrame;

void main(void)
{
    gl_FragColor = texture2D(videoFrame, textureCoordinate);
}

