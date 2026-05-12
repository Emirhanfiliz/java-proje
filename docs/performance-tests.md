# Performans Testleri

## Araç: k6

[k6](https://k6.io) ile yazılmış stres ve kırılma noktası testleri `tests/k6/stress-test.js` dosyasında bulunur.

## Kurulum

```bash
# Windows (winget)
winget install k6 --source winget

# macOS
brew install k6

# Linux (Debian/Ubuntu)
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update && sudo apt-get install k6
```

## Çalıştırma

```bash
# Sistem ayakta olmalı
docker-compose up -d

# Temel çalıştırma
k6 run tests/k6/stress-test.js

# JSON çıktısıyla
k6 run --out json=tests/results/k6-results.json tests/k6/stress-test.js

# Farklı base URL ile (varsayılan: http://localhost:8084)
k6 run -e BASE_URL=http://localhost:8084 tests/k6/stress-test.js
```

## Test Senaryosu

Test, gerçekçi bir kullanıcı akışını simüle eder:

1. Kullanıcı kaydı (`/auth/register`)
2. Giriş yapma (`/auth/login`) → JWT token al
3. Antrenman oluşturma (`POST /api/v1/workouts`)
4. Antrenman listeleme (`GET /api/v1/workouts/user/{id}`)

### Yük Profili

| Aşama          | Süre | Kullanıcı Sayısı |
|----------------|------|-----------------|
| Isınma         | 30s  | 0 → 10          |
| Yük Testi      | 60s  | 50              |
| Stres Testi    | 60s  | 100             |
| Yüksek Stres   | 60s  | 150             |
| Kırılma Noktası | 60s | 200             |
| Soğuma         | 30s  | 0               |
| **Toplam**     | **5 dk** | —           |

## SLA Eşikleri

| Metrik               | Hedef              |
|----------------------|--------------------|
| http_req_duration p95 | < 2000 ms         |
| http_req_duration p99 | < 5000 ms         |
| http_req_failed       | < %5 hata oranı   |
| success_rate          | > %95             |
| login_duration_ms p95 | < 1500 ms         |
| workout_duration_ms p95 | < 2000 ms       |

## Özel Metrikler

| Metrik              | Tür     | Açıklama                      |
|---------------------|---------|-------------------------------|
| `login_errors`      | Counter | Toplam login hatası sayısı    |
| `workout_errors`    | Counter | Toplam workout hatası sayısı  |
| `success_rate`      | Rate    | Başarılı istek oranı          |
| `login_duration_ms` | Trend   | Login endpoint gecikme dağılımı |
| `workout_duration_ms` | Trend | Workout endpoint gecikme dağılımı |

## Sonuç Dosyaları

Test bittikten sonra özet `tests/results/k6-summary.json` dosyasına yazılır:

```json
{
  "testName": "SportTracker API Stres Testi",
  "timestamp": "2026-05-12T...",
  "baseUrl": "http://localhost:8084",
  "metrics": {
    "totalRequests": 12500,
    "failedRequests": 45,
    "avgDuration_ms": 342.5,
    "p95Duration_ms": 1240.0,
    "p99Duration_ms": 2890.0,
    "maxDuration_ms": 4950.0,
    "rps": 41.6,
    "loginErrors": 12,
    "workoutErrors": 33
  }
}
```

## Yorum

- **p95 < 2s** hedefi normal koşullarda (Docker, local) 200 eşzamanlı kullanıcıya kadar karşılanmalıdır.
- Redis önbelleklemesi login ve workout list sorgularının tekrar yükünü önemli ölçüde azaltır.
- PostgreSQL bağlantı havuzu statistics-service için bottleneck noktasıdır; yüksek yükte ilk timeout'lar burada görülür.
