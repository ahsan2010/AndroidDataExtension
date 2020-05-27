varying mediump vec2 textureCoordinate;
precision mediump float;

const float g_fGamma = 0.5;
const float g_fBlurAmount = 0.4;    // 模糊量 
const float g_fRadialEffectAmount = 0.2;    // 放射式效果量 
const float g_fExposure = 0.5;  // 暴光量 
uniform sampler2D videoFrame; 
uniform sampler2D blurTexture;
// 计算放射式效果 
vec4 CaculateRadial(vec2 p_vec2TexCoord,int p_iSampleNumber, 
              float p_fStartScale /*= 1.0*/, float p_fScaleMul /*= 0.9*/) 
{ 
    // 临时变量 
    vec4 vec4TempColor = vec4(0.0); 
    float fCurrentScale = p_fStartScale; 
    vec2 vec2TempTexCoord = vec2(0.0); 
     
    // 遍历采样 
    for(int i = 0; i < p_iSampleNumber; ++i)  
    { 
        vec2TempTexCoord = (p_vec2TexCoord - 0.5)*fCurrentScale + 0.5;  // 采样方式 
        vec4TempColor += texture2D(blurTexture, vec2TempTexCoord); 
        fCurrentScale *= p_fScaleMul; 
    } 
    vec4TempColor /= float(p_iSampleNumber); 
    return vec4TempColor; 
}
// 计算小插图效果 
float CaculateVignette(vec2 p_vec2Position, float p_fInner, float p_fOuter) 
{ 
    float L = length(p_vec2Position); 
    return ( 1.0 - smoothstep(p_fInner, p_fOuter, L) ); 
}

vec4 lerp(vec4 v1, vec4 v2, float l)
{
	return v1 + l * (v2 - v1);
}

void main() 
{ 
    vec4 vec4SceneColor = texture2D(videoFrame, textureCoordinate);     // 计算原始场景颜色 
    vec4 vec4BlurColor = texture2D(blurTexture, textureCoordinate);       // 计算经Blur后的场景颜色 
    vec4 vec4RadialEffectColor = CaculateRadial(textureCoordinate, 30, 1.0, 0.95);  // 计算放射效果的颜色 
     
    // 混合场景与Blur 
    vec4 vec4Temp = lerp(vec4SceneColor, vec4BlurColor, g_fBlurAmount); 
    // 添加放射性效果 
    vec4Temp += vec4RadialEffectColor*g_fRadialEffectAmount; 
    // 进行暴光 
    //vec4Temp *= g_fExposure; 
    // 添加圆形扩散小插图效果使得中间部分较亮而四个边角逐渐变暗 
    //vec4Temp *= CaculateVignette(textureCoordinate*2.0 - 1.0, 0.7, 1.5); 
    // 使用Gamma校正规范会低范围光照 
    vec4Temp.rgb = pow(vec4Temp.rgb, vec3(g_fGamma)); 
    // 最终颜色 
    gl_FragColor = vec4Temp; 
}