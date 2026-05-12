# Mimari Dokümantasyon

## Genel Sistem Mimarisi

```mermaid
graph TB
    subgraph Clients["İstemciler"]
        DC["🖥️ Desktop Client<br/>(JavaFX)"]
        MC["📱 Mobile Client<br/>(Gluon/JavaFX)"]
    end

    subgraph Gateway["API Katmanı"]
        GW["API Gateway<br/>:8084<br/>(Spring Cloud Gateway)"]
    end

    subgraph Services["Mikroservisler"]
        US["user-service<br/>:8081"]
        WS["workout-service<br/>:8082"]
        SS["statistics-service<br/>:8083"]
    end

    subgraph Databases["Veritabanları"]
        MDB1[("MongoDB<br/>user_service_db")]
        MDB2[("MongoDB<br/>workout_service_db")]
        PG[("PostgreSQL<br/>sport_tracker_statistics")]
        RD1[("Redis<br/>token cache")]
        RD2[("Redis<br/>list cache")]
    end

    subgraph Shared["Ortak Kütüphane"]
        SL["shared-library<br/>(DTO · JWT · Pattern · Wellness)"]
    end

    DC -->|HTTP + JWT| GW
    MC -->|HTTP + JWT| GW
    GW -->|/auth/**| US
    GW -->|/api/v1/workouts/**| WS
    GW -->|/api/v1/statistics/**| SS

    US --- MDB1
    US --- RD1
    WS --- MDB2
    WS --- RD2
    SS --- PG

    US -.->|Maven dep| SL
    WS -.->|Maven dep| SL
    SS -.->|Maven dep| SL
    DC -.->|Maven dep| SL
```

## Servis Sorumlulukları

### api-gateway (:8084)
Spring Cloud Gateway tabanlı yönlendirici. Gelen istekleri path'e göre ilgili servise iletir:
- `/auth/**` → user-service
- `/api/v1/workouts/**` → workout-service
- `/api/v1/statistics/**` → statistics-service

### user-service (:8081)
Kullanıcı kimlik doğrulama ve profil yönetimi.
- **MongoDB**: Kullanıcı verileri (`user_service_db`)
- **Redis**: JWT token önbellekleme ve oturum yönetimi
- **JWT**: `shared-library`'deki `JwtTokenProvider` ile token üretimi/doğrulaması

### workout-service (:8082)
Antrenman verilerinin CRUD yönetimi.
- **MongoDB**: Antrenman dokümanları (`workout_service_db`)
- **Redis**: Sık erişilen antrenman listelerinin önbelleklenmesi
- Egzersiz listesi gömülü doküman olarak saklanır

### statistics-service (:8083)
Kullanıcı performans istatistikleri.
- **PostgreSQL**: İlişkisel istatistik verileri (`sport_tracker_statistics`)
- Workout-service'ten beslenen agregat sorgular

### shared-library
Tüm servislerin ortak bağımlılığı. Maven `install` ile yerel repoya yüklenir.
- DTOs: `UserDto`, `WorkoutDto`, `ExerciseDto`
- Güvenlik: `JwtTokenProvider`, `JwtUtil`, `JwtProperties`
- Pattern: `CalorieStrategyFactory` + Strategy uygulamaları
- Wellness: `WellnessStore`, `HabitEngine`, `ChallengeEngine`, `AdaptiveGoalEngine`, `SmartReminderService` ve 20+ sınıf
- Util: `AuthValidator`, `CalorieCalculator`

## Tasarım Kalıpları

### Factory Kalıbı

```mermaid
classDiagram
    class CalorieStrategyFactory {
        +getStrategy(intensity: String) CalorieCalculationStrategy
    }
    class CalorieCalculationStrategy {
        <<interface>>
        +calculate(durationMinutes: int) int
    }
    class LowIntensityStrategy {
        +calculate(durationMinutes: int) int
    }
    class MediumIntensityStrategy {
        +calculate(durationMinutes: int) int
    }
    class HighIntensityStrategy {
        +calculate(durationMinutes: int) int
    }
    class DefaultIntensityStrategy {
        +calculate(durationMinutes: int) int
    }

    CalorieStrategyFactory ..> CalorieCalculationStrategy : creates
    CalorieCalculationStrategy <|.. LowIntensityStrategy
    CalorieCalculationStrategy <|.. MediumIntensityStrategy
    CalorieCalculationStrategy <|.. HighIntensityStrategy
    CalorieCalculationStrategy <|.. DefaultIntensityStrategy
```

Masaüstü istemcide `WorkoutModalController` canlı kalori tahmini için kullanır.

### Strategy Kalıbı
```java
public interface CalorieCalculationStrategy {
    int calculate(int durationMinutes);
}
```
Her strateji farklı MET katsayısıyla kalori hesaplar. Yeni şiddet türü eklemek için sadece yeni bir strateji sınıfı + factory kaydı yeterlidir; mevcut kod değişmez.

## Veri Akışı (Sequence)

```mermaid
sequenceDiagram
    actor Kullanıcı
    participant DC as Desktop Client
    participant GW as API Gateway :8084
    participant US as user-service :8081
    participant WS as workout-service :8082
    participant Redis
    participant MongoDB

    Kullanıcı->>DC: Giriş yap
    DC->>GW: POST /auth/login
    GW->>US: POST /auth/login
    US->>MongoDB: findByEmail()
    MongoDB-->>US: User
    US->>Redis: cacheToken(userId, jwt)
    US-->>GW: {token, userId}
    GW-->>DC: {token, userId}

    Kullanıcı->>DC: Antrenman ekle
    DC->>GW: POST /api/v1/workouts [Bearer token]
    GW->>WS: POST /api/v1/workouts
    WS->>MongoDB: save(workout)
    MongoDB-->>WS: savedWorkout
    WS->>Redis: evict list cache
    WS-->>GW: WorkoutDto
    GW-->>DC: WorkoutDto
```

## Güvenlik Modeli

```mermaid
flowchart LR
    A([İstek]) --> B{Token var mı?}
    B -- Hayır --> C[401 Unauthorized]
    B -- Evet --> D{Token geçerli mi?}
    D -- Hayır --> E[403 Forbidden]
    D -- Evet --> F[Downstream servise ilet]
    F --> G([Yanıt])
```

1. Kullanıcı `/auth/login` ile JWT alır
2. Tüm korumalı endpoint'ler `Authorization: Bearer <token>` header bekler
3. API Gateway token'ı doğrular, geçerliyse downstream servise iletir
4. Token süresi dolduğunda yeniden login gerekir

## TDD Döngüsü

```mermaid
flowchart LR
    R([🔴 RED\nTest yaz\nbdc6a3d]) --> G([🟢 GREEN\nKodu yaz\n2eca045])
    G --> RF([🔵 REFACTOR\nİyileştir\n54e3422])
    RF --> R2([🔴 RED\nYeni test\n3eb89bc])
    R2 --> G2([🟢 GREEN\nİmplement\nc30818d])
```

Her servis için Red→Green→Refactor döngüsü commit geçmişinde tarih damgasıyla kanıtlanmıştır.

## Veritabanı Şemaları

```mermaid
erDiagram
    USER {
        string id PK
        string username
        string email
        string password
        string role
        float weight
        float height
    }

    WORKOUT {
        string id PK
        string userId FK
        string name
        string description
        datetime date
        int durationInMinutes
    }

    EXERCISE {
        string id PK
        string workoutId FK
        string name
        int sets
        int reps
        float weight
    }

    STATISTIC {
        long id PK
        string userId
        string type
        double value
        datetime calculationDate
    }

    USER ||--o{ WORKOUT : "sahip"
    WORKOUT ||--o{ EXERCISE : "içerir"
    USER ||--o{ STATISTIC : "üretir"
```

Başlangıç scriptleri:
- `database/mongodb/` — MongoDB koleksiyon ve index tanımları
- `database/postgres/` — PostgreSQL tablo şemaları

## Desktop Client Mimarisi

JavaFX tabanlı masaüstü istemcisi MVC mimarisini izler:
- **Model**: `dto/` paketindeki DTO sınıfları + `shared-library` DTO'ları
- **View**: `resources/fxml/` — dashboard, login, register, profile, workout modal, workout detail
- **Controller**: `controller/` — her FXML için ayrı controller
- **Service**: `api/ApiClient.java` — HTTP istemcisi (HttpClient + Jackson)

### Wellness Modülü (Desktop)
`DashboardController` `WellnessSections` aracılığıyla `shared-library`'deki tüm wellness motorlarına erişir:
- Alışkanlık takibi (HabitEngine)
- Zorluk kartları (ChallengeEngine)
- Adaptif hedefler (AdaptiveGoalEngine)
- Akıllı hatırlatıcılar (SmartReminderService)
- Nabız zonu analizi (HeartRateZoneCalculator)
- Öğün takibi (MealAggregator)
