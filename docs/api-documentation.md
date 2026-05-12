# API Dokümantasyonu

Tüm istekler API Gateway üzerinden yapılır: `http://localhost:8084`

Korumalı endpoint'ler `Authorization: Bearer <JWT>` header'ı gerektirir.

---

## Auth Endpoint'leri

### POST /auth/register
Yeni kullanıcı kaydı.

**Request Body:**
```json
{
  "username": "emir",
  "email": "emir@example.com",
  "password": "Sifre123!"
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": "665f...",
    "username": "emir",
    "email": "emir@example.com"
  }
}
```

---

### POST /auth/login
Giriş yap ve JWT token al.

**Request Body:**
```json
{
  "email": "emir@example.com",
  "password": "Sifre123!"
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": "665f...",
    "username": "emir"
  }
}
```

---

## Workout Endpoint'leri

### GET /api/v1/workouts/user/{userId}
Kullanıcının tüm antrenmanlarını listeler.

**Headers:** `Authorization: Bearer <token>`

**Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "id": "wkt001",
      "userId": "665f...",
      "name": "Sabah Kardiyo",
      "description": "30 dk koşu",
      "date": "2026-05-12T08:00:00",
      "durationInMinutes": 30,
      "exercises": []
    }
  ]
}
```

---

### POST /api/v1/workouts
Yeni antrenman oluştur.

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "userId": "665f...",
  "name": "Sabah Kardiyo",
  "description": "30 dk koşu",
  "durationInMinutes": 30,
  "exercises": [
    {
      "name": "Squat",
      "sets": 3,
      "reps": 12,
      "weight": 80.0
    }
  ]
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": "wkt002",
    "userId": "665f...",
    "name": "Sabah Kardiyo",
    "durationInMinutes": 30,
    "exercises": [...]
  }
}
```

---

### GET /api/v1/workouts/{workoutId}
Belirli bir antrenmanın detaylarını getirir.

**Headers:** `Authorization: Bearer <token>`

**Response (200):**
```json
{
  "success": true,
  "data": { ... }
}
```

---

### DELETE /api/v1/workouts/{workoutId}
Antrenmanı siler.

**Headers:** `Authorization: Bearer <token>`

**Response (200):**
```json
{
  "success": true,
  "message": "Antrenman silindi"
}
```

---

## Statistics Endpoint'leri

### GET /api/v1/statistics/user/{userId}
Kullanıcının genel performans istatistiklerini döner.

**Headers:** `Authorization: Bearer <token>`

**Response (200):**
```json
{
  "success": true,
  "data": {
    "totalWorkouts": 42,
    "totalDurationMinutes": 1890,
    "totalCalories": 15120,
    "weeklyAvgDuration": 45
  }
}
```

---

## Hata Yanıtları

```json
{
  "success": false,
  "message": "Hata açıklaması",
  "status": 400
}
```

| HTTP Kodu | Anlam                        |
|-----------|------------------------------|
| 400       | Geçersiz istek / validasyon  |
| 401       | Kimlik doğrulama başarısız   |
| 403       | Yetkisiz erişim              |
| 404       | Kaynak bulunamadı            |
| 500       | Sunucu hatası                |
