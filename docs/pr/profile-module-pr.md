# Pull Request: Implement User Profile Module

## Summary

Implemented authenticated user profile management APIs for reading and updating the current user's profile, cuisine preferences, and dietary restrictions.

## Implemented APIs

- GET /api/profile/me
- PUT /api/profile/me
- PUT /api/profile/cuisine-preferences
- PUT /api/profile/dietary-restrictions

## Key Changes

- Entities: reused existing `UserProfile`, `CuisinePreference`, and `DietaryRestriction` mappings.
- DTOs: added request and response DTOs for profile APIs.
- Repositories: added current-user lookup, list, and replacement delete methods.
- Services: added authenticated-user resolution, profile creation/update, preference replacement, and response mapping.
- Controllers: added `/api/profile` endpoints.
- Validation logic: added Bean Validation for basic profile fields and service-level validation for cross-field and list rules.
- Security behavior: all endpoints rely on the existing JWT security chain and resolve the user from Spring Security.

## Validation Rules

- budgetMin >= 0
- budgetMax >= 0
- budgetMin <= budgetMax
- spicyTolerance must be between 0 and 5
- locationArea must not exceed 120 characters
- cuisine names cannot be blank
- same cuisine cannot appear in both likedCuisines and dislikedCuisines
- duplicate cuisines are removed case-insensitively within each list
- dietary restriction names cannot be blank
- dietary restriction duplicates are removed case-insensitively

## Security Rules

- All profile APIs require JWT authentication
- User ID is resolved from the current authenticated user
- Users cannot access or modify another user's profile
- Password hash is never returned

## Test Checklist

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

```bash
mvn clean compile
# Passed using cached Maven wrapper distribution because mvn is not on PATH.
```

```bash
mvn test
# Passed using cached Maven wrapper distribution because mvn is not on PATH.
```

## Notes

- Existing `ApiResponse` is an empty class and existing controllers return DTOs directly, so profile APIs return `ProfileResponse` directly.
- The database schema contains a non-null `dietary_restrictions.severity` column, so new dietary restriction rows are saved with `NORMAL` while the API response omits severity per contract.
- `spicyTolerance` and `defaultPrivacyLevel` are non-null columns in the schema; null update values preserve existing values, or default to `0` and `PRIVATE` when creating a profile.
