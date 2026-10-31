#version 330

uniform sampler2D SkySampler;
uniform sampler2D DepthSampler;

layout(std140) uniform FogInfo {
    mat4 InvProj;
    mat4 InvRot;
    mat4 Rot;
    mat4 Proj;
    vec4 A;
    vec4 B;
    vec4 C;
    vec4 D;
    vec4 E;
};

in vec2 texCoord;
out vec4 fragColor;

float h3(vec3 p) {
    p = fract(p * 0.3183099 + 0.1);
    p *= 17.0;
    return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
}

float vn(vec3 x) {
    vec3 i = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix(h3(i), h3(i + vec3(1, 0, 0)), f.x), mix(h3(i + vec3(0, 1, 0)), h3(i + vec3(1, 1, 0)), f.x), f.y),
               mix(mix(h3(i + vec3(0, 0, 1)), h3(i + vec3(1, 0, 1)), f.x), mix(h3(i + vec3(0, 1, 1)), h3(i + vec3(1, 1, 1)), f.x), f.y), f.z);
}

float fbm(vec3 p) {
    return vn(p) * 0.6 + vn(p * 2.03 + 7.1) * 0.28 + vn(p * 4.1 + 3.3) * 0.12;
}

void main() {
    float d = texture(DepthSampler, texCoord).r;
    if (d >= 0.99999) {
        fragColor = vec4(0.0);
        return;
    }
    vec4 pv = InvProj * vec4(texCoord * 2.0 - 1.0, d * 2.0 - 1.0, 1.0);
    pv /= pv.w;
    vec3 wd = (InvRot * vec4(pv.xyz, 0.0)).xyz;
    vec3 wp = C.xyz + wd;
    float dist = length(pv.xyz);
    float t = C.w;
    float n = 0.5;
    if (E.x > 0.0) {
        n = fbm(wp * vec3(0.035, 0.06, 0.035) + vec3(t * 0.35, t * 0.05, t * 0.18));
        dist *= 1.0 + E.x * (n - 0.5) * 0.9;
    }
    float a = clamp((dist - A.x) / max(A.y - A.x, 0.001), 0.0, 1.0);
    if (A.w > 0.5) a = a * a * (3.0 - 2.0 * a);
    float hf = exp(-max(wp.y - E.z, 0.0) / 10.0);
    if (E.y > 0.0) {
        a *= mix(1.0, 0.25 + 0.75 * hf, E.y);
        float lo = E.y * hf * clamp((dist - 3.0) / max(A.y, 1.0), 0.0, 1.0) * (0.35 + 0.65 * n);
        a = max(a, lo * 0.7);
    }
    if (E.x > 0.0) {
        float wisp = smoothstep(0.55, 0.85, n) * E.x * clamp((dist - 2.0) / 14.0, 0.0, 1.0) * (0.4 + 0.6 * hf);
        a = max(a, wisp * 0.32);
    }
    a *= A.z;
    if (a <= 0.0) {
        fragColor = vec4(0.0);
        return;
    }
    vec3 col = B.yzw;
    vec3 dir = normalize(wd);
    if (B.x < 0.5) {
        vec2 uv = texCoord;
        if (dir.y < 0.0) {
            vec2 hz = normalize(dir.xz + vec2(0.0001));
            vec4 c = Proj * vec4((Rot * vec4(hz.x, 0.0, hz.y, 0.0)).xyz, 1.0);
            uv = clamp(c.xy / max(abs(c.w), 0.02), vec2(-0.98), vec2(0.98)) * 0.5 + 0.5;
        }
        col = texture(SkySampler, uv).rgb;
    }
    if (D.w > 0.0) {
        float up = smoothstep(-0.12, 0.25, D.y);
        float sd = max(dot(dir, normalize(D.xyz)), 0.0);
        float g = (pow(sd, 6.0) * 0.55 + pow(sd, 40.0) * 0.6) * D.w * up;
        vec3 sc = mix(vec3(1.0, 0.55, 0.3), vec3(1.0, 0.92, 0.78), smoothstep(0.0, 0.5, D.y));
        col = mix(col, sc, clamp(g, 0.0, 0.85));
    }
    col *= 0.94 + 0.12 * n * E.x;
    fragColor = vec4(col, clamp(a, 0.0, 1.0));
}
