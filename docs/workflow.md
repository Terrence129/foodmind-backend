## Fixed Workflow After Implementation

### 1. Run Build and Tests

Run:

```bash
mvn clean compile
```

If tests are configured, also run:

```bash
mvn test
```

Fix all compilation errors before completing the task.

---

### 2. Generate Git Commit Message

Generate one professional Git commit message following Conventional Commits format.

Expected format:

```text
feat(profile): implement user profile management APIs
```

The commit message should be concise and describe the actual change.

If the implementation includes bug fixes or validation improvements, choose the most suitable type:

```text
feat
fix
refactor
test
docs
```

---

### 3. Generate Pull Request Documentation

Create a PR document in Markdown format.

File name:

```text
docs/pr/profile-module-pr.md
```

The PR document must include:

````markdown
# Pull Request: Implement User Profile Module

## Summary

Briefly describe what was implemented.

## Implemented APIs

List all implemented endpoints:

- GET /api/profile/me
- PUT /api/profile/me
- PUT /api/profile/cuisine-preferences
- PUT /api/profile/dietary-restrictions

## Key Changes

Describe created or modified components:

- Entities
- DTOs
- Repositories
- Services
- Controllers
- Validation logic
- Security behavior

## Validation Rules

Document important validation rules:

- budgetMin >= 0
- budgetMax >= 0
- budgetMin <= budgetMax
- spicyTolerance must be between 0 and 5
- same cuisine cannot appear in both likedCuisines and dislikedCuisines
- dietary restriction duplicates are removed

## Security Rules

Document authentication and authorization behavior:

- All profile APIs require JWT authentication
- User ID is resolved from the current authenticated user
- Users cannot access or modify another user's profile
- Password hash is never returned

## Test Checklist

Include a manual test checklist:

- [ ] Unauthenticated request returns 401
- [ ] Authenticated user can get profile
- [ ] User without profile receives 200 with nullable fields and empty arrays
- [ ] User can update basic profile
- [ ] Invalid budget returns 400
- [ ] Invalid spicyTolerance returns 400
- [ ] Cuisine preferences replace old values
- [ ] Conflict between liked and disliked cuisines returns 400
- [ ] Dietary restrictions replace old values
- [ ] Duplicate dietary restrictions are removed

## Build Result

Record the result of:

```bash
mvn clean compile
````

If tests were run, also record:

```bash
mvn test
```

## Notes

List any assumptions or unresolved issues.

````

---

### 4. Generate Postman Collection

Generate a Postman collection for the User Profile module.

File name:

```text
docs/postman/FoodMind_Profile_API.postman_collection.json
````

The collection must include these requests:

```http
POST /api/auth/register
POST /api/auth/login
GET /api/profile/me
PUT /api/profile/me
PUT /api/profile/cuisine-preferences
PUT /api/profile/dietary-restrictions
```

Use these Postman environment variables:

```text
baseUrl=http://localhost:8080
token=
userEmail=dadao@example.com
userPassword=Password123!
```

For protected APIs, use this header:

```http
Authorization: Bearer {{token}}
Content-Type: application/json
```

---

### 5. Add Postman Test Scripts

Each Postman request should include test scripts.

For login, save the JWT token automatically:

```javascript
pm.test("login should return 200", function () {
    pm.response.to.have.status(200);
});

const json = pm.response.json();

if (json.data && json.data.token) {
    pm.environment.set("token", json.data.token);
} else if (json.token) {
    pm.environment.set("token", json.token);
}
```

For `GET /api/profile/me`:

```javascript
pm.test("get profile should return 200", function () {
    pm.response.to.have.status(200);
});

pm.test("profile response should contain userId", function () {
    const json = pm.response.json();
    const data = json.data || json;
    pm.expect(data).to.have.property("userId");
});

pm.test("profile list fields should not be null", function () {
    const json = pm.response.json();
    const data = json.data || json;

    pm.expect(data.likedCuisines).to.be.an("array");
    pm.expect(data.dislikedCuisines).to.be.an("array");
    pm.expect(data.dietaryRestrictions).to.be.an("array");
});
```

For `PUT /api/profile/me`:

```javascript
pm.test("update profile should return 200", function () {
    pm.response.to.have.status(200);
});

pm.test("profile should be updated correctly", function () {
    const json = pm.response.json();
    const data = json.data || json;

    pm.expect(data.budgetMin).to.eql(4.00);
    pm.expect(data.budgetMax).to.eql(15.00);
    pm.expect(data.spicyTolerance).to.eql(3);
    pm.expect(data.locationArea).to.eql("NUS");
    pm.expect(data.foodGoal).to.eql("AVOID_REPETITION");
    pm.expect(data.defaultPrivacyLevel).to.eql("PRIVATE");
});
```

For `PUT /api/profile/cuisine-preferences`:

```javascript
pm.test("update cuisine preferences should return 200", function () {
    pm.response.to.have.status(200);
});

pm.test("cuisine preferences should be replaced", function () {
    const json = pm.response.json();
    const data = json.data || json;

    pm.expect(data.likedCuisines).to.include("Chinese");
    pm.expect(data.likedCuisines).to.include("Japanese");
    pm.expect(data.dislikedCuisines).to.include("Western Fast Food");
});
```

For `PUT /api/profile/dietary-restrictions`:

```javascript
pm.test("update dietary restrictions should return 200", function () {
    pm.response.to.have.status(200);
});

pm.test("dietary restrictions should be replaced", function () {
    const json = pm.response.json();
    const data = json.data || json;

    pm.expect(data.dietaryRestrictions).to.include("No beef");
    pm.expect(data.dietaryRestrictions).to.include("No peanuts");
});
```

---

### 6. Generate Postman Negative Test Documentation

Create a Markdown file documenting manual negative tests.

File name:

```text
docs/postman/profile-api-negative-tests.md
```

Include these test cases:

```markdown
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

{
  "budgetMin": -1,
  "budgetMax": 15,
  "spicyTolerance": 3,
  "locationArea": "NUS",
  "foodGoal": "AVOID_REPETITION",
  "defaultPrivacyLevel": "PRIVATE"
}

Expected:

400 Bad Request

---

## 3. budgetMin Greater Than budgetMax

Request:

PUT /api/profile/me

Body:

{
  "budgetMin": 20,
  "budgetMax": 10,
  "spicyTolerance": 3,
  "locationArea": "NUS",
  "foodGoal": "AVOID_REPETITION",
  "defaultPrivacyLevel": "PRIVATE"
}

Expected:

400 Bad Request

---

## 4. Invalid Spicy Tolerance

Request:

PUT /api/profile/me

Body:

{
  "budgetMin": 4,
  "budgetMax": 15,
  "spicyTolerance": 6,
  "locationArea": "NUS",
  "foodGoal": "AVOID_REPETITION",
  "defaultPrivacyLevel": "PRIVATE"
}

Expected:

400 Bad Request

---

## 5. Cuisine Conflict

Request:

PUT /api/profile/cuisine-preferences

Body:

{
  "likedCuisines": ["Chinese"],
  "dislikedCuisines": ["chinese"]
}

Expected:

400 Bad Request
```

---

### 7. Final Response Format

When finished, respond with:

```markdown
## Implementation Summary

Brief summary of what was implemented.

## Created Files

List created files.

## Modified Files

List modified files.

## Implemented APIs

List implemented endpoints.

## Generated Documentation

List generated documentation files:

- docs/pr/profile-module-pr.md
- docs/postman/FoodMind_Profile_API.postman_collection.json
- docs/postman/profile-api-negative-tests.md

## Suggested Commit Message

Provide the commit message.

## Build/Test Result

Show the command results.

## Notes

Mention assumptions or unresolved issues.
```
