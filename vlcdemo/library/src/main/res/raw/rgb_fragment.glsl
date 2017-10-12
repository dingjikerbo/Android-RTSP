precision mediump float;

varying vec2 v_TextureCoordinates;

uniform sampler2D s_texture;

void main() {
    float r, g, b;

    r = texture2D(s_texture, v_TextureCoordinates).r;
    g = texture2D(s_texture, v_TextureCoordinates).g;
    b = texture2D(s_texture, v_TextureCoordinates).b;

    gl_FragColor = vec4(r, g, b, 1.0);
}