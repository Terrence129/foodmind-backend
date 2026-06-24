# Profile API Negative Test Cases

## 1. Missing JWT

Request:

GET /api/profile/me

Expected:

401 Unauthorized

---

## 2. Negative Budget

Request:

PUT /api/profile/me

Body:

```json
{
  "budgetMin": -1,
  "budgetMax": 15,
  "spicyTolerance": 3,
  "locationArea": "NUS",
  "foodGoal": "AVOID_REPETITION",
  "defaultPrivacyLevel": "PRIVATE"
}
```

Expected:

400 Bad Request

---

## 3. budgetMin Greater Than budgetMax

Request:

PUT /api/profile/me

Body:

```json
{
  "budgetMin": 20,
  "budgetMax": 10,
  "spicyTolerance": 3,
  "locationArea": "NUS",
  "foodGoal": "AVOID_REPETITION",
  "defaultPrivacyLevel": "PRIVATE"
}
```

Expected:

400 Bad Request

---

## 4. Invalid Spicy Tolerance

Request:

PUT /api/profile/me

Body:

```json
{
  "budgetMin": 4,
  "budgetMax": 15,
  "spicyTolerance": 6,
  "locationArea": "NUS",
  "foodGoal": "AVOID_REPETITION",
  "defaultPrivacyLevel": "PRIVATE"
}
```

Expected:

400 Bad Request

---

## 5. Cuisine Conflict

Request:

PUT /api/profile/cuisine-preferences

Body:

```json
{
  "likedCuisines": ["Chinese"],
  "dislikedCuisines": ["chinese"]
}
```

Expected:

400 Bad Request
