# Vision Sports Academy — Member 1 + Member 2 Integration

This integration keeps Member 1's User & Profile Management implementation as the authoritative base and adds Member 2's training sessions, attendance, attendance reports, and performance pages.

## Important integration decisions

- Member 1's `entity.Player`, `/api/players`, `PlayerService`, and `/players` page remain authoritative and are retained unchanged.
- Member 2's duplicate `model.Player`, duplicate player controller/service/repository, and duplicate `/players` page are intentionally not included. This prevents a second `player` table and conflicting `/api/players` and `/players` mappings.
- Member 2 attendance and performance records store the ID from Member 1's `players` table. The services validate that this player ID exists before saving.
- Attendance, attendance report, and performance pages adapt Member 1 API fields (`fullName`, `ageGroup`, `dateOfBirth`) to the field names expected by Member 2's UI.
- Training-session age-group options are loaded from the age groups actually used by Member 1's player profiles. Create Member 1 player profiles first.
- Member 1's `pom.xml` and `application.properties` are retained. The project targets Java 21.

## Run locally

1. Open `sports-academy-management` in IntelliJ IDEA.
2. Configure `src/main/resources/application.properties` for your local MySQL/MariaDB username and password. Do not commit real credentials.
3. Ensure MySQL/MariaDB is running and the configured account can create/access `vision_academy_db`.
4. From this folder, run `./mvnw clean package` (Windows: `\mvnw.cmd clean package`).
5. Start the Spring Boot application and open `http://localhost:8080/login`.
6. Sign in using the Member 1 seed account if the `DataInitializer` has initialized it.
7. Member 2 pages are available at `/training-sessions`, `/attendance`, `/attendance-report`, and `/performance`.

## Verification notes

This package is a source-level integration. Run the build and test with your own local database credentials before pushing it. Existing database schemas may require review if they were created from an earlier version of the separate Member 2 player model. Back up any data before changing databases.
