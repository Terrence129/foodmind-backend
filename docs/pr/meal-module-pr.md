# Pull Request: Implement Meal API Module

## Summary

Implemented authenticated meal record management APIs for creating, listing, viewing, updating, and soft-deleting meal records.

## Implemented APIs

- POST /api/meals
- GET /api/meals
- GET /api/meals/{mealId}
- PUT /api/meals/{mealId}
- DELETE /api/meals/{mealId}

## Key Changes

- Entity: reused the existing `MealRecord` mapping for `meal_records`.
- DTOs: added request, response, and pagination DTOs.
- Repository: added soft-delete-aware lookup and JPA Specification support.
- Service: added current-user resolution, create/list/detail/update/delete behavior, and DTO mapping.
- Controller: added all `/api/meals` endpoints.
- Validation logic: added Bean Validation and service-level privacy/list validation.
- Permission logic: owners can view/update/delete their meals; non-owners can only view eligible GROUP meals.
- Group visibility checks: GROUP meals require ACTIVE group membership.
- Soft delete behavior: delete sets `deletedAt` and normal reads exclude deleted records.

## Validation Rules

- foodName is required
- mealType is required
- privacyLevel is required
- consumedAt is required
- price must be >= 0
- rating must be between 0 and 5
- GROUP meals require groupId
- PRIVATE meals must not have groupId
- user must be an ACTIVE group member to create GROUP meals
- only PRIVATE and GROUP privacy levels are supported for Meal API MVP

## Security Rules

- All Meal APIs require JWT authentication
- User ID is resolved from the current authenticated user
- Users cannot update or delete another user's meal
- Private meals are visible only to owners
- Group meals are visible only to owners or ACTIVE members of the same group

## Test Checklist

- [ ] Unauthenticated request returns 401
- [ ] Create PRIVATE meal succeeds
- [ ] Create PRIVATE meal with groupId returns 400
- [ ] Create GROUP meal without groupId returns 400
- [ ] Create GROUP meal as active member succeeds
- [ ] List meals returns current user's own meals
- [ ] Filters work
- [ ] Owner can view detail
- [ ] Group member can view GROUP record
- [ ] Non-member cannot view GROUP record
- [ ] Other user cannot view PRIVATE record
- [ ] Owner can update meal
- [ ] Other user cannot update meal
- [ ] Owner can delete meal
- [ ] Other user cannot delete meal
- [ ] Deleted meal is excluded from list/detail

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

- Existing `ApiResponse` is an empty class and existing controllers return DTOs directly, so Meal APIs return DTOs directly except delete, which returns a simple success object.
- The `meal_records.updated_at` column is non-null in the current schema, so create/update/delete operations set `updatedAt`.
- Live HTTP checks were not run in this implementation pass.
