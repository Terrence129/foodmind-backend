# FoodMind Database Documentation

Version: 1.0
Database: PostgreSQL 14+
Schema script: `V1__init_foodmind_schema.sql`
Recommended backend: Spring Boot 3 + Spring Data JPA + PostgreSQL

---

## 1. Purpose

The FoodMind database stores the data required for an Android-first food decision assistant. The schema supports the complete MVP product loop:

```text
Register/Login
→ Set food preferences
→ Log meals and drinks
→ Share selected records with a group
→ View group food feed
→ Generate recommendations
→ Select/reject recommendations
→ Track weekly analytics and AI-generated summaries
```

The design is relational-first, with selected `JSONB` columns for flexible recommendation context, recap statistics, and AI request/response records.

---

## 2. Schema Modules

| Module            | Tables                                                                                                                        |
| ----------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| User and Profile  | `users`, `user_profiles`, `cuisine_preferences`, `dietary_restrictions`                                                       |
| Groups and Social | `food_groups`, `group_members`, `friendships`                                                                                 |
| Food Memory       | `restaurants`, `food_items`, `meal_records`, `drink_records`                                                                  |
| Recommendation    | `want_to_try_items`, `borrowed_recommendations`, `recommendation_requests`, `recommendation_items`, `recommendation_feedback` |
| Analytics and AI  | `weekly_recaps`, `badges`, `user_badges`, `ai_generation_logs`                                                                |
| View              | `v_group_feed`                                                                                                                |

---

## 3. Design Principles

### 3.1 Relational core

Core business data such as users, profiles, groups, meal records, drink records, friendships, and recommendations is stored in normalized relational tables.

### 3.2 JPA-friendly enum design

The schema uses `VARCHAR + CHECK` constraints instead of PostgreSQL native enum types. This is easier to map in Spring Boot JPA:

```java
@Enumerated(EnumType.STRING)
private MealType mealType;
```

### 3.3 Soft delete

Several tables include `deleted_at` instead of immediate physical deletion:

```text
users
food_groups
restaurants
food_items
meal_records
drink_records
```

This allows the backend to hide deleted records while preserving historical data.

### 3.4 Privacy-aware sharing

Meal and drink records include `privacy_level`:

```text
PRIVATE
FRIENDS
GROUP
PUBLIC
```

For the MVP, only `PRIVATE` and `GROUP` need to be implemented. `FRIENDS` and `PUBLIC` are reserved for future versions.

### 3.5 Recommendation traceability

The schema stores both recommendation requests and generated recommendation items. This allows FoodMind to explain why an item was recommended, track user feedback, and improve ranking logic later.

---

## 4. Core Tables

## 4.1 `users`

Stores account-level user information.

| Column           | Type         | Description                           |
| ---------------- | ------------ | ------------------------------------- |
| `id`             | BIGINT       | Primary key                           |
| `email`          | VARCHAR(255) | User email; unique case-insensitively |
| `password_hash`  | VARCHAR(255) | Secure password hash                  |
| `username`       | VARCHAR(80)  | Display name                          |
| `avatar_url`     | TEXT         | Optional avatar image URL             |
| `status`         | VARCHAR(20)  | `ACTIVE`, `DISABLED`, or `DELETED`    |
| `email_verified` | BOOLEAN      | Whether the email has been verified   |
| `last_login_at`  | TIMESTAMPTZ  | Last login time                       |
| `created_at`     | TIMESTAMPTZ  | Creation time                         |
| `updated_at`     | TIMESTAMPTZ  | Last update time                      |
| `deleted_at`     | TIMESTAMPTZ  | Soft delete time                      |

Important indexes:

```text
ux_users_email_lower
idx_users_status
```

---

## 4.2 `user_profiles`

Stores food-related profile settings for each user.

| Column                  | Type          | Description                           |
| ----------------------- | ------------- | ------------------------------------- |
| `id`                    | BIGINT        | Primary key                           |
| `user_id`               | BIGINT        | One-to-one reference to `users.id`    |
| `budget_min`            | NUMERIC(10,2) | Minimum usual budget                  |
| `budget_max`            | NUMERIC(10,2) | Maximum usual budget                  |
| `spicy_tolerance`       | SMALLINT      | 0 to 5 scale                          |
| `location_area`         | VARCHAR(120)  | User’s usual food area                |
| `food_goal`             | VARCHAR(40)   | User’s current food decision goal     |
| `default_privacy_level` | VARCHAR(20)   | Default privacy level for new records |

Supported `food_goal` values:

```text
AVOID_REPETITION
SAVE_MONEY
EAT_HEALTHIER
DISCOVER_NEW_FOOD
FOLLOW_FRIENDS
```

Relationship:

```text
users 1 -- 1 user_profiles
```

---

## 4.3 `meal_records`

Stores user meal logs. This is one of the most important tables in the product.

| Column            | Type          | Description                              |
| ----------------- | ------------- | ---------------------------------------- |
| `id`              | BIGINT        | Primary key                              |
| `user_id`         | BIGINT        | User who logged the meal                 |
| `group_id`        | BIGINT        | Optional group sharing target            |
| `restaurant_id`   | BIGINT        | Optional structured restaurant reference |
| `food_item_id`    | BIGINT        | Optional structured food item reference  |
| `food_name`       | VARCHAR(160)  | Required food name                       |
| `restaurant_name` | VARCHAR(160)  | Free-text restaurant name                |
| `cuisine_type`    | VARCHAR(80)   | Cuisine category                         |
| `meal_type`       | VARCHAR(20)   | Breakfast/lunch/dinner/etc.              |
| `price`           | NUMERIC(10,2) | Meal price                               |
| `rating`          | NUMERIC(2,1)  | User rating from 0 to 5                  |
| `comment`         | TEXT          | User comment                             |
| `photo_url`       | TEXT          | Optional photo URL                       |
| `would_eat_again` | BOOLEAN       | Whether user would eat it again          |
| `privacy_level`   | VARCHAR(20)   | Sharing level                            |
| `consumed_at`     | TIMESTAMPTZ   | Time when the meal was consumed          |
| `deleted_at`      | TIMESTAMPTZ   | Soft delete timestamp                    |

Supported `meal_type` values:

```text
BREAKFAST
LUNCH
DINNER
SUPPER
SNACK
```

Important constraints:

```text
price >= 0
rating BETWEEN 0 AND 5
if privacy_level = 'GROUP', group_id must not be null
```

Important indexes:

```text
idx_meal_records_user_consumed_at
idx_meal_records_group_feed
idx_meal_records_food_name_lower
idx_meal_records_cuisine
idx_meal_records_meal_type
```

---

## 4.4 `drink_records`

Stores drink logs separately from meal logs.

| Column            | Type          | Description                            |
| ----------------- | ------------- | -------------------------------------- |
| `id`              | BIGINT        | Primary key                            |
| `user_id`         | BIGINT        | User who logged the drink              |
| `group_id`        | BIGINT        | Optional group sharing target          |
| `shop_id`         | BIGINT        | Optional reference to `restaurants.id` |
| `drink_name`      | VARCHAR(160)  | Drink name                             |
| `shop_name`       | VARCHAR(160)  | Free-text shop name                    |
| `sweetness_level` | VARCHAR(20)   | Sweetness choice                       |
| `ice_level`       | VARCHAR(20)   | Ice choice                             |
| `price`           | NUMERIC(10,2) | Drink price                            |
| `rating`          | NUMERIC(2,1)  | Rating from 0 to 5                     |
| `comment`         | TEXT          | User comment                           |
| `photo_url`       | TEXT          | Optional photo URL                     |
| `would_buy_again` | BOOLEAN       | Whether user would buy again           |
| `privacy_level`   | VARCHAR(20)   | Sharing level                          |
| `consumed_at`     | TIMESTAMPTZ   | Time when the drink was consumed       |

Supported sweetness values:

```text
ZERO
LESS
HALF
NORMAL
EXTRA
```

Supported ice values:

```text
NO_ICE
LESS_ICE
NORMAL_ICE
EXTRA_ICE
HOT
```

---

## 4.5 `food_groups`

Stores friend groups, housemate groups, class groups, or office lunch groups.

| Column        | Type         | Description                        |
| ------------- | ------------ | ---------------------------------- |
| `id`          | BIGINT       | Primary key                        |
| `name`        | VARCHAR(100) | Group name                         |
| `description` | TEXT         | Optional description               |
| `owner_id`    | BIGINT       | User who created the group         |
| `invite_code` | VARCHAR(16)  | Code used to join the group        |
| `status`      | VARCHAR(20)  | `ACTIVE`, `ARCHIVED`, or `DELETED` |
| `created_at`  | TIMESTAMPTZ  | Creation time                      |
| `updated_at`  | TIMESTAMPTZ  | Last update time                   |
| `deleted_at`  | TIMESTAMPTZ  | Soft delete time                   |

Backend APIs supported:

```text
POST /api/groups
POST /api/groups/join
GET /api/groups
GET /api/groups/{groupId}
```

---

## 4.6 `group_members`

Stores membership between users and groups.

| Column      | Type        | Description                    |
| ----------- | ----------- | ------------------------------ |
| `id`        | BIGINT      | Primary key                    |
| `group_id`  | BIGINT      | Group ID                       |
| `user_id`   | BIGINT      | Member user ID                 |
| `role`      | VARCHAR(20) | `OWNER`, `ADMIN`, or `MEMBER`  |
| `status`    | VARCHAR(20) | `ACTIVE`, `LEFT`, or `REMOVED` |
| `joined_at` | TIMESTAMPTZ | Join time                      |
| `left_at`   | TIMESTAMPTZ | Leave time                     |

Important rule:

```text
Only members with status = 'ACTIVE' should be allowed to view group feed records.
```

---

## 4.7 `recommendation_requests`

Stores each recommendation request made by a user.

| Column          | Type          | Description                            |
| --------------- | ------------- | -------------------------------------- |
| `id`            | BIGINT        | Primary key                            |
| `user_id`       | BIGINT        | Requesting user                        |
| `group_id`      | BIGINT        | Optional group context                 |
| `meal_type`     | VARCHAR(20)   | Optional meal type                     |
| `budget_max`    | NUMERIC(10,2) | Maximum budget                         |
| `mood`          | VARCHAR(40)   | Example: `quick`, `healthy`, `explore` |
| `location_area` | VARCHAR(120)  | User context area                      |
| `avoid_items`   | TEXT[]        | User-specified items to avoid          |
| `context_json`  | JSONB         | Full structured context snapshot       |
| `created_at`    | TIMESTAMPTZ   | Request time                           |

This table is useful for debugging recommendation behavior.

---

## 4.8 `recommendation_items`

Stores generated recommendation candidates for each request.

| Column                | Type         | Description                     |
| --------------------- | ------------ | ------------------------------- |
| `id`                  | BIGINT       | Primary key                     |
| `request_id`          | BIGINT       | Parent recommendation request   |
| `rank_position`       | SMALLINT     | Display ranking                 |
| `name`                | VARCHAR(160) | Recommended food/drink name     |
| `restaurant_name`     | VARCHAR(160) | Optional place name             |
| `recommendation_type` | VARCHAR(30)  | Recommendation category         |
| `source`              | VARCHAR(40)  | Candidate source                |
| `score`               | NUMERIC(5,2) | Final score, 0 to 100           |
| `reason`              | TEXT         | Human-readable reason           |
| `candidate_json`      | JSONB        | Full candidate details          |
| `selected`            | BOOLEAN      | Whether user selected this item |
| `rejected`            | BOOLEAN      | Whether user rejected this item |

Supported recommendation types:

```text
SAFE_OPTION
EXPLORE_OPTION
FRIEND_PICK
```

Supported sources:

```text
HISTORY
PREFERENCE
HISTORY_AND_PREFERENCE
FRIEND_TRUST
GROUP_POPULAR
MANUAL_CATALOG
AI_ASSISTED
```

---

## 5. Group Feed View

## 5.1 `v_group_feed`

Combines group-visible meal records and drink records into one feed.

Output columns:

| Column             | Description                                   |
| ------------------ | --------------------------------------------- |
| `record_type`      | `MEAL` or `DRINK`                             |
| `record_id`        | Source record ID                              |
| `group_id`         | Group ID                                      |
| `user_id`          | User who created the record                   |
| `username`         | Creator username                              |
| `item_name`        | Food or drink name                            |
| `place_name`       | Restaurant or shop name                       |
| `cuisine_type`     | Cuisine type for meals; null for drinks       |
| `meal_type`        | Meal type for meals; null for drinks          |
| `price`            | Price                                         |
| `rating`           | Rating                                        |
| `comment`          | User comment                                  |
| `photo_url`        | Photo URL                                     |
| `would_have_again` | Unified field for meal/drink repeat intention |
| `consumed_at`      | Consumption time                              |
| `created_at`       | Record creation time                          |

Example query:

```sql
SELECT *
FROM v_group_feed
WHERE group_id = :groupId
ORDER BY consumed_at DESC
LIMIT 50;
```

The backend must still check whether the requesting user is an active group member before returning records.

---

## 6. Important Relationships

```text
users 1 -- 1 user_profiles
users 1 -- many cuisine_preferences
users 1 -- many dietary_restrictions
users 1 -- many meal_records
users 1 -- many drink_records
users 1 -- many group_members
food_groups 1 -- many group_members
food_groups 1 -- many meal_records
food_groups 1 -- many drink_records
users many -- many users through friendships
users many -- many food_groups through group_members
recommendation_requests 1 -- many recommendation_items
recommendation_items 1 -- many recommendation_feedback
users 1 -- many weekly_recaps
users many -- many badges through user_badges
```

---

## 7. Backend Permission Rules

### 7.1 Meal and drink ownership

A user can update or delete only their own meal/drink records.

### 7.2 Group feed visibility

A record can appear in a group feed only when:

```text
privacy_level = 'GROUP'
group_id is not null
record is not soft-deleted
requesting user is an active member of the group
```

### 7.3 Private records

Private records should only be returned to the owner.

### 7.4 Group membership

Only active group members should be allowed to:

```text
view group details
view group feed
share records to the group
borrow group recommendations
```

---

## 8. Recommended JPA Mapping Notes

Use `Long` for IDs:

```java
private Long id;
```

Use `OffsetDateTime` for `TIMESTAMPTZ`:

```java
private OffsetDateTime createdAt;
private OffsetDateTime updatedAt;
private OffsetDateTime consumedAt;
```

Use `BigDecimal` for money and ratings:

```java
private BigDecimal price;
private BigDecimal rating;
```

Use enum strings:

```java
@Enumerated(EnumType.STRING)
@Column(name = "privacy_level")
private PrivacyLevel privacyLevel;
```

Avoid exposing entities directly in API responses. Use DTOs:

```text
MealCreateRequest
MealUpdateRequest
MealResponse
GroupFeedItemResponse
RecommendationResponse
```

For early development, avoid too many bidirectional entity relationships. Prefer simple child-to-parent mappings:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

---

## 9. Recommended Implementation Boundary

For the first backend version, implement only:

```text
users
user_profiles
cuisine_preferences
meal_records
drink_records
food_groups
group_members
v_group_feed
recommendation_requests
recommendation_items
```

Leave these for later:

```text
friendships
borrowed_recommendations
weekly_recaps
badges
user_badges
ai_generation_logs
restaurants
food_items
```

The database already supports future expansion, but the backend should still start small.
