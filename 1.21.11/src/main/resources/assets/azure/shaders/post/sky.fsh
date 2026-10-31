#version 330

layout(std140) uniform SkyInfo {
    mat4 InvProj;
    mat4 InvRot;
    vec4 C;
    vec4 F;
    vec4 G;
    vec4 S;
    vec4 R;
};

in vec2 texCoord;
out vec4 fragColor;

float h21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float h31(vec3 p) {
    p = fract(p * 0.1031);
    p += dot(p, p.zyx + 31.32);
    return fract((p.x + p.y) * p.z);
}

float vn3(vec3 x) {
    vec3 i = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix(h31(i), h31(i + vec3(1, 0, 0)), f.x), mix(h31(i + vec3(0, 1, 0)), h31(i + vec3(1, 1, 0)), f.x), f.y),
               mix(mix(h31(i + vec3(0, 0, 1)), h31(i + vec3(1, 0, 1)), f.x), mix(h31(i + vec3(0, 1, 1)), h31(i + vec3(1, 1, 1)), f.x), f.y), f.z);
}

float fbm(vec3 p) {
    float a = 0.5, s = 0.0;
    for (int i = 0; i < 4; i++) {
        s += vn3(p) * a;
        p = p * 2.07 + 1.3;
        a *= 0.5;
    }
    return s;
}

mat2 rt(float a) {
    float c = cos(a), s = sin(a);
    return mat2(c, s, -s, c);
}

float tri(float x) {
    return clamp(abs(fract(x) - 0.5), 0.01, 0.49);
}

vec2 tri2(vec2 p) {
    return vec2(tri(p.x) + tri(p.y), tri(p.y + tri(p.x)));
}

float tn(vec2 p, float t) {
    float z = 1.8, z2 = 2.5, rz = 0.0;
    p *= rt(p.x * 0.06);
    vec2 bp = p;
    mat2 m2 = mat2(0.95534, 0.29552, -0.29552, 0.95534);
    for (int i = 0; i < 5; i++) {
        vec2 dg = tri2(bp * 1.85) * 0.75;
        dg *= rt(t * 0.06);
        p -= dg / z2;
        bp *= 1.3;
        z2 *= 0.45;
        z *= 0.42;
        p *= 1.21 + (rz - 1.0) * 0.02;
        rz += tri(p.x + tri(p.y)) * z;
        p *= -m2;
    }
    return clamp(1.0 / pow(rz * 29.0, 1.3), 0.0, 0.55);
}

vec3 aurora(vec3 rd, float t, vec3 c1, vec3 c2) {
    vec3 ro = vec3(0.0, 0.0, -6.7);
    vec4 col = vec4(0.0);
    vec4 av = vec4(0.0);
    float jit = h21(gl_FragCoord.xy);
    for (int k = 0; k < 30; k++) {
        float i = float(k);
        float of = 0.006 * jit * smoothstep(0.0, 15.0, i);
        float pt = ((0.8 + pow(i, 1.4) * 0.002) - ro.y) / (rd.y * 2.0 + 0.4) - of;
        vec3 bp = ro + pt * rd;
        float rz = tn(bp.zx, t);
        vec4 c = vec4(mix(c1, c2, smoothstep(4.0, 26.0, i)) * rz, rz);
        av = mix(av, c, 0.5);
        col += av * exp2(-i * 0.065 - 2.5) * smoothstep(0.0, 5.0, i);
    }
    return col.rgb * clamp(rd.y * 15.0 + 0.4, 0.0, 1.0) * 2.2;
}

vec3 stars(vec3 d, float px, float t, float lay, float extra) {
    vec3 acc = vec3(0.0);
    for (int L = 0; L < 3; L++) {
        if (float(L) >= lay) break;
        float sc = 90.0 + float(L) * 85.0;
        vec3 p = d * sc;
        vec3 id = floor(p);
        vec3 f = fract(p) - 0.5;
        float h = h31(id + float(L) * 13.1);
        float dens = 0.12 + extra * 0.35;
        if (h < dens) {
            vec3 o = (vec3(h31(id + 1.7), h31(id + 2.3), h31(id + 3.9)) - 0.5) * 0.6;
            float dd = length(f - o);
            float hb = h31(id + 7.7);
            float sz = mix(0.03, 0.09, hb * hb) / (1.0 + float(L) * 0.4);
            float ps = px * sc;
            float core = smoothstep(sz + ps, max(sz - ps, 0.0), dd);
            float halo = exp(-dd * dd / (sz * sz * 9.0)) * 0.35 * hb;
            float tw = 0.72 + 0.28 * sin(t * (1.5 + hb * 4.0) + h * 80.0);
            vec3 sc2 = mix(vec3(0.72, 0.84, 1.0), vec3(1.0, 0.88, 0.72), h31(id + 5.5));
            float sp = 0.0;
            if (L == 0 && hb > 0.9 && extra < 0.5) {
                vec3 tx = normalize(cross(d, vec3(0.0, 1.0, 0.0)) + vec3(1e-4));
                vec3 ty = cross(tx, d);
                vec3 v = f - o;
                float a = abs(dot(v, tx)), b = abs(dot(v, ty));
                float ln = sz * 7.0, th = sz * 0.12 + ps;
                sp = (exp(-a / th) * exp(-b / ln) + exp(-b / th) * exp(-a / ln)) * 0.55 * (0.6 + 0.4 * tw);
                core *= 1.4;
            }
            acc += sc2 * (core + halo + sp) * tw * mix(0.5, 1.4, hb);
        }
    }
    return acc;
}

vec3 dirOf(float az, float el) {
    return vec3(cos(el) * sin(az), sin(el), cos(el) * cos(az));
}

float seg(vec3 p, vec3 a, vec3 b) {
    vec3 pa = p - a, ba = b - a;
    float h = clamp(dot(pa, ba) / max(dot(ba, ba), 1e-6), 0.0, 1.0);
    return length(pa - ba * h);
}

void main() {
    vec4 pv = InvProj * vec4(texCoord * 2.0 - 1.0, 1.0, 1.0);
    vec3 d = normalize((InvRot * vec4(pv.xyz / pv.w, 0.0)).xyz);
    float px = length(fwidth(d));
    float t = S.w;
    vec3 ac = C.rgb;
    float bg = C.w;
    float up = max(d.y, 0.0);
    vec3 zen = ac * bg * 0.25 + vec3(0.004, 0.006, 0.016);
    vec3 hor = ac * bg * 0.95 + vec3(0.025, 0.03, 0.05);
    vec3 col = mix(hor, zen, pow(up, 0.45));
    col += ac * 0.12 * exp(-abs(d.y) * 14.0);
    col += mix(vec3(0.6, 0.35, 0.55), ac, 0.35) * 0.05 * exp(-abs(d.y) * 6.0);
    if (d.y < 0.0) col = mix(hor, hor * 0.25, smoothstep(0.0, -0.35, d.y));
    vec3 sd = d;
    sd.yz *= rt(R.x);
    float hz = smoothstep(-0.05, 0.12, d.y);
    if (F.w > 0.0) {
        vec3 n = normalize(vec3(0.35, 0.62, 0.7));
        float b = dot(sd, n);
        float band = exp(-b * b * 16.0);
        float det = fbm(sd * 5.0 + 2.0);
        float dust = fbm(sd * 13.0 + 9.0);
        vec3 mc = mix(ac * 0.7 + 0.1, vec3(0.92, 0.86, 1.0), 0.45);
        col += mc * band * (0.25 + 0.75 * det * det) * (1.0 - 0.7 * smoothstep(0.45, 0.75, dust)) * 0.28 * hz;
        if (F.z > 0.0) col += stars(sd, px, t, 3.0, band) * 0.25 * band * hz;
    }
    if (F.z > 0.0) {
        vec3 q = sd * 1.7 + vec3(3.1, 1.2, 7.4);
        float w0 = fbm(q * 1.9 + t * 0.004);
        float w1 = fbm(q + w0 * 1.6);
        float neb = smoothstep(0.42, 0.82, w1);
        float cv = fbm(q * 0.7 + 11.0);
        vec3 nc = mix(mix(ac, vec3(0.55, 0.3, 0.95), 0.45), mix(ac.zxy, vec3(0.95, 0.35, 0.6), 0.5), smoothstep(0.35, 0.65, cv));
        float dk = smoothstep(0.55, 0.7, fbm(q * 3.3 + 4.0));
        col += nc * neb * neb * (1.0 - 0.6 * dk) * 0.11 * hz * min(F.z, 1.0);
        col += stars(sd, px, t, F.z, 0.0) * hz;
    }
    if (G.z > 0.5) {
        vec3 md = S.y > -0.05 && G.w > 0.99 ? normalize(vec3(-0.45, 0.42, -0.78)) : normalize(-S.xyz);
        float c = dot(d, md);
        float an = acos(clamp(c, -1.0, 1.0));
        float rr = 0.045;
        float disk = smoothstep(rr + px, rr - px, an);
        vec3 lc = cross(md, vec3(0.0, 1.0, 0.0));
        vec2 lo = vec2(dot(d - md, normalize(lc)), dot(d - md, normalize(cross(lc, md)))) / rr;
        float cr = fbm(vec3(lo * 2.2, 3.0));
        vec3 moon = vec3(0.93, 0.95, 1.0) * (0.72 + 0.35 * cr) * (1.0 - 0.25 * dot(lo, lo));
        col = mix(col, moon, disk);
        col += mix(ac, vec3(0.85, 0.9, 1.0), 0.6) * (exp(-an * 9.0) * 0.16 + exp(-an * 40.0) * 0.22) * (1.0 - disk);
    }
    if (F.x > 0.0 && d.y > 0.0) {
        vec3 c1 = mix(ac, vec3(0.3, 1.0, 0.75), 0.25);
        vec3 c2 = mix(ac.zxy, vec3(1.0), 0.15);
        vec3 a = aurora(d, F.y > 0.5 ? t : 4.0, c1, c2);
        col = col * (1.0 - clamp(length(a) * 0.5, 0.0, 0.6)) + a * F.x;
    }
    if (G.x > 0.5) {
        for (int k = 0; k < 2; k++) {
            float gp = G.y;
            float tt = t + float(k) * gp * 0.5;
            float sl = floor(tt / gp);
            float lt = tt - sl * gp;
            float du = 1.1;
            if (lt < du) {
                float s1 = h21(vec2(sl, float(k) * 7.0 + 1.0)), s2 = h21(vec2(sl + 3.1, 2.0)), s3 = h21(vec2(sl + 5.7, 9.0));
                vec3 s0 = dirOf(s1 * 6.2832, 0.45 + 0.5 * s2);
                vec3 tg = normalize(cross(s0, vec3(0.0, 1.0, 0.0)));
                tg = normalize(tg * (s3 > 0.5 ? 1.0 : -1.0) - vec3(0.0, 0.55, 0.0));
                float u = lt / du;
                vec3 hd = normalize(s0 + tg * u * 0.55);
                vec3 tl = normalize(s0 + tg * max(u - 0.22, 0.0) * 0.55);
                float dl = seg(d, tl, hd);
                float al = sin(u * 3.14159);
                float w = 0.0018 + px;
                float along = clamp(dot(d - tl, hd - tl) / max(dot(hd - tl, hd - tl), 1e-6), 0.0, 1.0);
                float g = smoothstep(w, 0.0, dl) * along * along + exp(-dl * dl / (w * w * 30.0)) * along * 0.25;
                col += mix(vec3(1.0), ac, 0.25) * g * al * 1.6 * hz;
            }
        }
    }
    col += (h21(gl_FragCoord.xy + fract(t)) - 0.5) / 255.0;
    fragColor = vec4(max(col, 0.0), G.w);
}
