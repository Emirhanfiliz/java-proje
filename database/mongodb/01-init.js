db.getSiblingDB('user_service_db').users.createIndex({ email: 1 }, { unique: true });
db.getSiblingDB('workout_service_db').workouts.createIndex({ userId: 1 });
