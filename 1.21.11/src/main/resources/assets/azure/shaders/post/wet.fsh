#version 330

uniform sampler2D SceneSampler;
uniform sampler2D DepthSampler;
uniform sampler2D HeightSampler;

layout(std140) uniform RainInfo {
    mat4 InvProj;
    mat4 InvRot;
    mat4 Rot;
    mat4 Proj;
    vec4 Cam;
    vec4 Wrap;
    vec4 P;
    vec4 Sky;
};

in vec2 texCoord;
out vec4 fragColor;

float h2(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float vn(vec2 x) {
    vec2 i = floor(x);
    vec2 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(h2(i), h2(i + vec2(1, 0)), f.x), mix(h2(i + vec2(0, 1)), h2(i + vec2(1, 1)), f.x), f.y);
}

vec3 vpos(vec2 uv) {
    float d = texture(DepthSampler, uv).r;
    vec4 p = InvProj * vec4(uv * 2.0 - 1.0, d * 2.0 - 1.0, 1.0);
    return p.xyz / p.w;
}

vec2 rip(vec2 p, float t) {
    vec2 g = floor(p);
    vec2 acc = vec2(0.0);
    for (int y = -1; y <= 1; y++) for (int x = -1; x <= 1; x++) {
        vec2 c = g + vec2(x, y);
        vec2 ctr = c + vec2(h2(c), h2(c + 17.3));
        float ph = fract(t * 0.85 + h2(c + 5.1));
        vec2 dv = p - ctr;
        float dl = length(dv);
        float rad = ph * 0.9;
        float w = exp(-pow((dl - rad) * 10.0, 2.0)) * (1.0 - ph) * (1.0 - ph);
        acc += dv / max(dl, 0.001) * w * sin((dl - rad) * 34.0);
    }
    return acc;
}

void main() {
    float d = texture(DepthSampler, texCoord).r;
    vec4 sc = texture(SceneSampler, texCoord);
    vec3 pv = vpos(texCoord);
    vec3 wd = (InvRot * vec4(pv, 0.0)).xyz;
    vec3 n = normalize(cross(dFdx(wd), dFdy(wd)));
    if (dot(n, wd) > 0.0) n = -n;
    if (d >= 0.99999) {
        fragColor = vec4(0.0);
        return;
    }
    float dist = length(wd);
    float fade = 1.0 - smoothstep(34.0, 62.0, dist);
    vec3 hp = Cam.xyz + wd;
    vec2 hu = (floor(hp.xz) + 0.5) / Wrap.w;
    float ex = 0.0;
    if (hu.x > 0.0 && hu.y > 0.0 && hu.x < 1.0 && hu.y < 1.0) {
        vec4 h = texture(HeightSampler, hu);
        float top = floor(h.r * 255.0 + 0.5) - 128.0;
        ex = (hp.y >= top - 1.1 ? 1.0 : 0.0) * h.g;
    }
    ex *= fade * P.x;
    float gl = Sky.w * (1.0 - smoothstep(24.0, 48.0, dist));
    if (ex <= 0.002 && gl <= 0.002) {
        fragColor = vec4(0.0);
        return;
    }
    float fl = smoothstep(0.86, 0.98, n.y);
    vec3 wp = Wrap.xyz + wd;
    float pn = vn(wp.xz * 0.08) * 0.6 + vn(wp.xz * 0.23 + 3.1) * 0.4;
    float pud = mix(1.0, 0.75 + 0.25 * smoothstep(0.3, 0.7, pn), P.y);
    float refl = fl * (ex * P.z * pud + gl);
    vec3 col = sc.rgb * (1.0 - 0.12 * ex * fl);
    if (refl > 0.002) {
        vec3 nw = vec3(0.0, 1.0, 0.0);
        if (P.w > 0.0) {
            vec2 rp = rip(wp.xz * 1.1, Cam.w);
            nw = normalize(vec3(-rp.x * 0.06 * P.w, 1.0, -rp.y * 0.06 * P.w));
        }
        vec3 nv = normalize((Rot * vec4(nw, 0.0)).xyz);
        vec3 vd = normalize(pv);
        vec3 rv = reflect(vd, nv);
        float ry = (InvRot * vec4(rv, 0.0)).y;
        vec3 skc = mix(Sky.rgb * 0.85, Sky.rgb * 1.05 + 0.03, clamp(ry, 0.0, 1.0));
        vec3 rc = skc;
        float hit = 0.0;
        vec3 p = pv + nv * 0.03;
        vec3 lp = p;
        float st = 0.15 + dist * 0.012;
        for (int i = 0; i < 40; i++) {
            p += rv * st;
            st *= 1.08;
            vec4 c = Proj * vec4(p, 1.0);
            if (c.w <= 0.0) break;
            vec2 uv = c.xy / c.w * 0.5 + 0.5;
            if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) break;
            if (texture(DepthSampler, uv).r >= 0.99999) {
                lp = p;
                continue;
            }
            vec3 sp = vpos(uv);
            float dz = sp.z - p.z;
            if (dz > 0.0 && dz < st * 2.5 + 0.35) {
                vec3 a = lp;
                vec3 b = p;
                for (int k = 0; k < 6; k++) {
                    vec3 m = (a + b) * 0.5;
                    vec4 cm = Proj * vec4(m, 1.0);
                    vec3 sm = vpos(cm.xy / cm.w * 0.5 + 0.5);
                    if (sm.z - m.z > 0.0) b = m;
                    else a = m;
                }
                vec4 cb = Proj * vec4(b, 1.0);
                vec2 ub = cb.xy / cb.w * 0.5 + 0.5;
                vec2 ed = smoothstep(0.0, 0.06, ub) * (1.0 - smoothstep(0.94, 1.0, ub));
                hit = ed.x * ed.y;
                rc = mix(skc, texture(SceneSampler, ub).rgb, hit);
                break;
            }
            lp = p;
        }
        float cs = max(dot(-vd, nv), 0.0);
        float fr = 0.14 + 0.86 * pow(1.0 - cs, 2.6);
        float w = refl * fr * (1.0 + 0.6 * hit);
        col = mix(col, rc, clamp(w, 0.0, 0.94));
        float nf = 1.0 - smoothstep(0.12, 0.45, dot(Sky.rgb, vec3(0.3, 0.6, 0.1)));
        if (nf > 0.0 && hit > 0.0) {
            vec2 ub = vec2(0.0);
            vec4 cr = Proj * vec4(p, 1.0);
            ub = cr.xy / cr.w * 0.5 + 0.5;
            vec3 ls = vec3(0.0);
            float jt = h2(gl_FragCoord.xy) - 0.5;
            for (int k = -7; k <= 7; k++) {
                vec2 u2 = ub + vec2((h2(vec2(k, 3.0)) - 0.5) * 0.002, (float(k) + jt) * 0.016);
                vec3 c2 = texture(SceneSampler, u2).rgb;
                float l2 = max(c2.r, max(c2.g, c2.b));
                ls += c2 * smoothstep(0.55, 0.95, l2) * (1.0 - abs(float(k)) / 8.0);
            }
            col += ls / 3.4 * nf * hit * clamp(refl * 1.3, 0.0, 1.0);
        }
    }
    fragColor = vec4(col, 1.0);
}
