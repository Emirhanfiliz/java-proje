/**
 * SportTracker API — k6 Stres ve Kırılma Noktası Testi
 *
 * Çalıştırma:
 *   k6 run tests/k6/stress-test.js
 *   k6 run --out json=tests/results/k6-results.json tests/k6/stress-test.js
 *
 * Gereksinim: k6 kurulu olmalı → https://k6.io/docs/getting-started/installation/
 * Sistem çalışıyor olmalı:  docker-compose up -d
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// ─── Özel Metrikler ───────────────────────────────────────────────────────────
const loginErrors    = new Counter('login_errors');
const workoutErrors  = new Counter('workout_errors');
const successRate    = new Rate('success_rate');
const loginDuration  = new Trend('login_duration_ms', true);
const workoutDuration = new Trend('workout_duration_ms', true);

// ─── Test Konfigürasyonu ──────────────────────────────────────────────────────
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8084';

export const options = {
  stages: [
    { duration: '30s', target: 10  },  // Isınma: 0 → 10 kullanıcı
    { duration: '60s', target: 50  },  // Yük testi: 50 kullanıcı
    { duration: '60s', target: 100 },  // Stres testi: 100 kullanıcı
    { duration: '60s', target: 150 },  // Yüksek stres: 150 kullanıcı
    { duration: '60s', target: 200 },  // Kırılma noktası arama: 200 kullanıcı
    { duration: '30s', target: 0   },  // Soğuma: 0'a indir
  ],
  thresholds: {
    // SLA eşikleri — bu değerlerin altında kalınmalı
    'http_req_duration':  ['p(95)<2000'],  // %95 istek 2s altında
    'http_req_duration':  ['p(99)<5000'],  // %99 istek 5s altında
    'http_req_failed':    ['rate<0.05'],   // Hata oranı %5'in altında
    'success_rate':       ['rate>0.95'],   // Başarı oranı %95 üstünde
    'login_duration_ms':  ['p(95)<1500'],  // Login p95 < 1.5s
    'workout_duration_ms':['p(95)<2000'],  // Workout p95 < 2s
  },
};

// ─── Test Veri Yardımcısı ─────────────────────────────────────────────────────
function uniqueUser(vuId) {
  return {
    username: `k6_user_${vuId}_${Date.now()}`,
    email:    `k6_${vuId}_${Date.now()}@sporttracker.com`,
    password: 'Test1234!',
  };
}

const JSON_HEADERS = { 'Content-Type': 'application/json' };

function authHeaders(token) {
  return {
    'Content-Type':  'application/json',
    'Authorization': `Bearer ${token}`,
  };
}

// ─── Yardımcı: Register ───────────────────────────────────────────────────────
function registerUser(user) {
  const res = http.post(
    `${BASE_URL}/auth/register`,
    JSON.stringify(user),
    { headers: JSON_HEADERS, tags: { name: 'register' } }
  );
  const ok = check(res, {
    'register: status 200': (r) => r.status === 200,
    'register: data.id exists': (r) => {
      try { return JSON.parse(r.body).data.id !== undefined; } catch { return false; }
    },
  });
  successRate.add(ok);
  if (!ok) loginErrors.add(1);
  return ok ? JSON.parse(res.body).data.id : null;
}

// ─── Yardımcı: Login ─────────────────────────────────────────────────────────
function loginUser(email, password) {
  const start = Date.now();
  const res = http.post(
    `${BASE_URL}/auth/login`,
    JSON.stringify({ email, password }),
    { headers: JSON_HEADERS, tags: { name: 'login' } }
  );
  loginDuration.add(Date.now() - start);

  const ok = check(res, {
    'login: status 200': (r) => r.status === 200,
    'login: token exists': (r) => {
      try { return JSON.parse(r.body).data.token !== undefined; } catch { return false; }
    },
  });
  successRate.add(ok);
  if (!ok) { loginErrors.add(1); return null; }
  return JSON.parse(res.body).data.token;
}

// ─── Yardımcı: Workout Oluştur ───────────────────────────────────────────────
function createWorkout(userId, token) {
  const start = Date.now();
  const payload = {
    userId,
    name:             `k6 Antrenman ${__VU}-${__ITER}`,
    description:      'k6 stres testi antrenmanı',
    durationInMinutes: 45,
    exercises: [
      { name: 'Squat',       sets: 3, reps: 12, weight: 80.0 },
      { name: 'Bench Press', sets: 4, reps: 10, weight: 70.0 },
      { name: 'Deadlift',    sets: 3, reps:  8, weight: 100.0 },
    ],
  };
  const res = http.post(
    `${BASE_URL}/api/v1/workouts`,
    JSON.stringify(payload),
    { headers: authHeaders(token), tags: { name: 'create_workout' } }
  );
  workoutDuration.add(Date.now() - start);

  const ok = check(res, {
    'workout create: status 200': (r) => r.status === 200,
  });
  successRate.add(ok);
  if (!ok) workoutErrors.add(1);
  return ok;
}

// ─── Yardımcı: Workout Listele ───────────────────────────────────────────────
function listWorkouts(userId, token) {
  const res = http.get(
    `${BASE_URL}/api/v1/workouts/user/${userId}`,
    { headers: authHeaders(token), tags: { name: 'list_workouts' } }
  );
  const ok = check(res, {
    'workout list: status 200': (r) => r.status === 200,
  });
  successRate.add(ok);
  return ok;
}

// ─── Ana Test Akışı ───────────────────────────────────────────────────────────
export default function () {
  const user   = uniqueUser(__VU);
  const userId = registerUser(user);

  if (!userId) {
    sleep(1);
    return;
  }

  sleep(0.2);

  const token = loginUser(user.email, user.password);
  if (!token) {
    sleep(1);
    return;
  }

  sleep(0.3);

  createWorkout(userId, token);
  sleep(0.2);
  listWorkouts(userId, token);

  // Gerçekçi kullanıcı davranışı simülasyonu (0.5 – 1.5 saniye bekleme)
  sleep(Math.random() + 0.5);
}

// ─── Test Başlangıç Özeti ─────────────────────────────────────────────────────
export function handleSummary(data) {
  const summary = {
    testName:    'SportTracker API Stres Testi',
    timestamp:   new Date().toISOString(),
    baseUrl:     BASE_URL,
    metrics: {
      totalRequests:   data.metrics.http_reqs?.values?.count        ?? 0,
      failedRequests:  data.metrics.http_req_failed?.values?.passes  ?? 0,
      avgDuration_ms:  data.metrics.http_req_duration?.values?.avg   ?? 0,
      p95Duration_ms:  data.metrics.http_req_duration?.values?.['p(95)'] ?? 0,
      p99Duration_ms:  data.metrics.http_req_duration?.values?.['p(99)'] ?? 0,
      maxDuration_ms:  data.metrics.http_req_duration?.values?.max   ?? 0,
      rps:             data.metrics.http_reqs?.values?.rate          ?? 0,
      loginErrors:     data.metrics.login_errors?.values?.count      ?? 0,
      workoutErrors:   data.metrics.workout_errors?.values?.count    ?? 0,
    },
  };

  console.log('\n═══════════════════════════════════════════════════════');
  console.log('  SportTracker API k6 Stres Testi — Sonuç Özeti');
  console.log('═══════════════════════════════════════════════════════');
  console.log(`  Toplam İstek    : ${summary.metrics.totalRequests}`);
  console.log(`  Hatalı İstek    : ${summary.metrics.failedRequests}`);
  console.log(`  Ort. Süre       : ${summary.metrics.avgDuration_ms.toFixed(2)} ms`);
  console.log(`  p95 Süre        : ${summary.metrics.p95Duration_ms.toFixed(2)} ms`);
  console.log(`  p99 Süre        : ${summary.metrics.p99Duration_ms.toFixed(2)} ms`);
  console.log(`  Maks Süre       : ${summary.metrics.maxDuration_ms.toFixed(2)} ms`);
  console.log(`  Throughput      : ${summary.metrics.rps.toFixed(2)} req/s`);
  console.log('═══════════════════════════════════════════════════════\n');

  return {
    'tests/results/k6-summary.json': JSON.stringify(summary, null, 2),
    stdout: JSON.stringify(summary, null, 2),
  };
}
