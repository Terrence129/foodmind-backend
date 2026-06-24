# Meal API Negative Test Cases

## 1. Missing JWT

Request:

GET /api/meals

Expected:

401 Unauthorized

---

## 2. Missing Required foodName

Request:

POST /api/meals

Body:

```json
{
  "foodName": "",
  "mealType": "LUNCH",
  "privacyLevel": "PRIVATE",
  "consumedAt": "2026-06-23T12:30:00+08:00"
}
```

Expected:

400 Bad Request

---

## 3. Negative Price

Request:

POST /api/meals

Body:

```json
{
  "foodName": "Chicken Rice",
  "mealType": "LUNCH",
  "price": -1,
  "privacyLevel": "PRIVATE",
  "consumedAt": "2026-06-23T12:30:00+08:00"
}
```

Expected:

400 Bad Request

---

## 4. Rating Greater Than 5

Request:

POST /api/meals

Body:

```json
{
  "foodName": "Chicken Rice",
  "mealType": "LUNCH",
  "rating": 6,
  "privacyLevel": "PRIVATE",
  "consumedAt": "2026-06-23T12:30:00+08:00"
}
```

Expected:

400 Bad Request

---

## 5. PRIVATE Meal With groupId

Request:

POST /api/meals

Body:

```json
{
  "groupId": 1,
  "foodName": "Chicken Rice",
  "mealType": "LUNCH",
  "privacyLevel": "PRIVATE",
  "consumedAt": "2026-06-23T12:30:00+08:00"
}
```

Expected:

400 Bad Request

---

## 6. GROUP Meal Without groupId

Request:

POST /api/meals

Body:

```json
{
  "foodName": "Chicken Rice",
  "mealType": "LUNCH",
  "privacyLevel": "GROUP",
  "consumedAt": "2026-06-23T12:30:00+08:00"
}
```

Expected:

400 Bad Request

---

## 7. Update Another User's Meal

Request:

PUT /api/meals/{mealId}

Expected:

403 Forbidden

---

## 8. Delete Another User's Meal

Request:

DELETE /api/meals/{mealId}

Expected:

403 Forbidden
