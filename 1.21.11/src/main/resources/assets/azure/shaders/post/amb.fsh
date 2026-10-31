#version 330

uniform sampler2D SceneSampler;
uniform sampler2D DepthSampler;

layout(std140) uniform AmbInfo {
    mat4 InvProj;
    mat4 InvRot;
    mat4 Rot;
    mat4 Proj;
    vec4 Ac;
    vec4 Sun;
    vec4 A;
    vec4 B;
};

in vec2 texCoord;
out vec4 fragColor;

float h21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

vec3 bright(vec2 uv) {
    if (texture(DepthSampler, uv).r >= 0.99999) return vec3(0.0);
    vec3 c = texture(SceneSampler, uv).rgb;
    float l = max(c.r, max(c.g, c.b));
    return c * smoothstep(0.7, 1.0, l);
}

void main() {
    vec2 ts = 1.0 / vec2(textureSize(SceneSampler, 0));
    vec3 sc = texture(SceneSampler, texCoord).rgb;
    float d = texture(DepthSampler, texCoord).r;
    vec4 pv = InvProj * vec4(texCoord * 2.0 - 1.0, d * 2.0 - 1.0, 1.0);
    pv /= pv.w;
    vec3 wd = (InvRot * vec4(pv.xyz, 0.0)).xyz;
    float dist = length(wd);
    vec3 col = sc;
    if (A.x > 0.0) {
        vec3 bl = vec3(0.0);
        float jr = h21(gl_FragCoord.xy) * 6.2832;
        for (int r = 1; r <= 3; r++) {
            float rad = float(r * r) * 5.0;
            for (int k = 0; k < 10; k++) {
                float a = float(k) / 10.0 * 6.2832 + jr + float(r);
                bl += bright(texCoord + vec2(cos(a), sin(a)) * rad * ts) / float(r);
            }
        }
        col += bl / 18.0 * A.x;
    }
    if (A.y > 0.0 && Sun.w > 0.0) {
        vec4 sp = Proj * vec4((Rot * vec4(Sun.xyz, 0.0)).xyz, 1.0);
        if (sp.w > 0.0) {
            vec2 su = sp.xy / sp.w * 0.5 + 0.5;
            vec2 dl = (su - texCoord) / 28.0;
            vec2 uv = texCoord + dl * h21(gl_FragCoord.xy);
            float acc = 0.0;
            float wt = 1.0;
            for (int i = 0; i < 28; i++) {
                if (uv.x < 0.0 || uv.y < 0.0 || uv.x > 1.0 || uv.y > 1.0) break;
                float sk = texture(DepthSampler, uv).r >= 0.99999 ? 1.0 : 0.0;
                vec3 sc2 = texture(SceneSampler, uv).rgb;
                acc += sk * smoothstep(0.75, 1.0, max(sc2.r, max(sc2.g, sc2.b))) * wt;
                wt *= 0.965;
                uv += dl;
            }
            float fall = 1.0 - smoothstep(0.0, 0.7, length((su - texCoord) * vec2(1.6, 1.0)));
            fall *= fall;
            if (d >= 0.99999) fall *= 0.35;
            vec3 rc = mix(vec3(1.0, 0.86, 0.62), Ac.rgb, 0.25);
            col += rc * acc / 28.0 * fall * A.y * Sun.w * 0.45;
        }
    }
    if (A.z > 0.0 && d < 0.99999) {
        float ry = wd.y + B.x;
        float hz = exp(-max(ry, 0.0) / 1.4) * smoothstep(2.0, 16.0, dist) * (1.0 - smoothstep(28.0, 56.0, dist));
        vec3 hc = mix(Ac.rgb, vec3(0.8, 0.85, 0.95), 0.45) * (0.35 + 0.65 * B.y);
        col = mix(col, hc, clamp(hz * A.z * 0.28, 0.0, 0.4));
    }
    if (A.w > 0.0) {
        float l = dot(col, vec3(0.2126, 0.7152, 0.0722));
        vec3 g = mix(vec3(l), col, 1.0 + 0.16 * A.w);
        g = mix(g, g * g * (3.0 - 2.0 * g), 0.22 * A.w);
        vec3 sh = mix(vec3(1.0), Ac.rgb * 1.3 + 0.2, 0.18 * A.w * (1.0 - smoothstep(0.0, 0.5, l)));
        col = g * sh;
    }
    if (B.z > 0.0) {
        vec2 q = (texCoord - 0.5) * vec2(1.15, 1.0);
        float v = smoothstep(0.35, 0.95, length(q) * 1.25);
        col *= 1.0 - v * B.z;
    }
    col += (h21(gl_FragCoord.xy + 3.7) - 0.5) / 255.0;
    fragColor = vec4(col, 1.0);
}
