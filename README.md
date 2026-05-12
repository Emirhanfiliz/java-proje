# SportTracker

KOU TBL324 İleri Java Uygulamaları dersi kapsamında geliştirilmiş mikroservis tabanlı spor takip uygulaması.

## Mimari Genel Bakış

```
┌──────────────────────────────────────────────────────────┐
│                      Clients                             │
│        Desktop (JavaFX)        Mobile (Gluon)            │
└───────────────┬──────────────────────┬───────────────────┘
                │                      │
                ▼                      ▼
┌──────────────────────────────────────────────────────────┐
│              API Gateway  :8084                          │
│         (Spring Cloud Gateway — yönlendirme)             │
└──────────┬──────────────┬──────────────┬─────────────────┘
           │              │              │
           ▼              ▼              ▼
    user-service    workout-service  statistics-service
       :8081            :8082             :8083
     MongoDB +        MongoDB +         PostgreSQL
      Redis              Redis
           │
           ▼
    shared-library
  (Factory + Strategy
   kalıpları, Wellness
   modülü, JWT, DTOs)
```

## Servisler ve Portlar

| Servis               | Port | Veritabanı        | Açıklama                        |
|----------------------|------|-------------------|---------------------------------|
| api-gateway          | 8084 | —                 | Tüm isteklerin giriş noktası    |
| user-service         | 8081 | MongoDB + Redis   | Kayıt, giriş, JWT, profil       |
| workout-service      | 8082 | MongoDB + Redis   | Antrenman CRUD                  |
| statistics-service   | 8083 | PostgreSQL        | İstatistik ve analiz            |
| desktop-client       | —    | —                 | JavaFX masaüstü istemcisi       |
| mobile-client        | —    | —                 | Gluon mobil istemcisi           |

## Tasarım Kalıpları

**Factory Kalıbı** — `CalorieStrategyFactory`  
Antrenman şiddetine göre doğru kalori hesaplama stratejisini döner.

**Strategy Kalıbı** — `CalorieCalculationStrategy`  
| Strateji               | MET Çarpanı | Kalori/dk |
|------------------------|-------------|-----------|
| `LowIntensityStrategy` | Düşük       | ~5 kcal   |
| `MediumIntensityStrategy` | Orta     | ~8 kcal   |
| `HighIntensityStrategy` | Yüksek     | ~12 kcal  |
| `DefaultIntensityStrategy` | Varsayılan | ~6 kcal |

## Hızlı Başlangıç

### Gereksinimler
- Docker & Docker Compose
- JDK 21
- Maven 3.9+
- JavaFX 21 (masaüstü istemci için)

### 1. Altyapıyı Başlat

```bash
docker-compose up -d
```

MongoDB (:27017), Redis (:6379) ve PostgreSQL (:5432) ayağa kalkar.  
Mikroservisler de otomatik build edilip başlar.

### 2. Masaüstü İstemciyi Çalıştır

```bash
# Önce shared-library yerel Maven deposuna yüklenmeli
mvn -pl shared-library install -DskipTests

# Ardından masaüstü istemciyi başlat
mvn -pl desktop-client javafx:run
```

### 3. Performans Testini Çalıştır

```bash
# Sistemi ayağa kaldır (adım 1), sonra:
k6 run tests/k6/stress-test.js

# JSON çıktısıyla:
k6 run --out json=tests/results/k6-results.json tests/k6/stress-test.js
```

## API Rotaları (Gateway üzerinden)

```
POST  /auth/register              → Kayıt
POST  /auth/login                 → Giriş (JWT döner)
GET   /api/v1/workouts/user/{id}  → Kullanıcı antrenmanları
POST  /api/v1/workouts            → Antrenman oluştur
GET   /api/v1/statistics/...      → İstatistikler
```

Tüm endpoint detayları için bkz. [API Dokümantasyonu](docs/api-documentation.md)

## Dökümantasyon

- [Mimari Detaylar](docs/architecture.md)
- [API Referansı](docs/api-documentation.md)
- [Performans Testleri](docs/performance-tests.md)

## Proje Yapısı

```
java-proje/
├── api-gateway/           Spring Cloud Gateway
├── user-service/          Kullanıcı yönetimi (MongoDB + Redis)
├── workout-service/       Antrenman yönetimi (MongoDB + Redis)
├── statistics-service/    İstatistikler (PostgreSQL)
├── shared-library/        Ortak kütüphane (DTO, Pattern, Wellness, JWT)
├── desktop-client/        JavaFX masaüstü istemcisi
├── mobile-client/         Gluon mobil istemcisi
├── database/              MongoDB ve PostgreSQL init scriptleri
├── tests/k6/              k6 stres testleri
├── docs/                  Teknik dokümantasyon
└── docker-compose.yml     Tüm sistem tek komutla
```
