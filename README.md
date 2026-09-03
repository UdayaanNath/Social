# Social

A Java-based backend platform for building a social networking application. This is a multi-module Maven project built with **Java 17** and **Dropwizard 4.0**, currently focusing on the **Identity Service** — the authentication and user-management module that provides JWT-based authentication, role-based access control (RBAC), and user CRUD operations backed by PostgreSQL.

## Features

- **JWT Authentication** — RSA-256 signed access tokens using Auth0 java-jwt, with a 1-hour expiry and issuer validation.
- **Role-Based Access Control (RBAC)** — Fine-grained authorization via `@RolesAllowed` annotations with configurable roles per endpoint.
- **User Management** — Full CRUD operations for users (create, read, update, delete) with BCrypt password hashing (12 rounds).
- **User Role Management** — Assign and manage roles per user with ACTIVE/INACTIVE status and flexible query filters.
- **OpenAPI-Driven Development** — JAX-RS interfaces and models are auto-generated from an OpenAPI 3.0.3 specification.
- **Self-Protection** — Users cannot delete or modify their own account.

## Architecture

This repo is a multi-module Maven project (`org.nath.sns` / `Social`):

| Module | Description |
|--------|-------------|
| `identity-api-spec` | Holds the OpenAPI 3.0.3 specification (`api.yaml`) that defines the Identity API contract |
| `identity` | The main Identity Service implementation (Dropwizard 4 app) |
| `common` | Shared placeholder module (currently minimal) |

### Identity Service Internals

The `identity` module follows a layered architecture:

- **REST Resources** (`resource/`) — Jakarta JAX-RS resources implementing auto-generated API interfaces
- **Services** (`service/`) — Business logic: `AuthenticationService`, `UserService`, `UserRoleService`, `JwtTokenService`
- **DAOs** (`dao/`) — Hibernate data-access objects
- **Entities** (`entity/`) — JPA entities: `UserEntity`, `UserRoleEntity`
- **Utilities** (`util/`) — JWT authenticator/authorizer, RSA key loader, user context filter, validation helpers
- **DI** — Google Guice (`IdentityModule`) wired into Jersey via the HK2 Guice Bridge

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Language |
| Dropwizard 4.0 | REST application framework |
| Hibernate / PostgreSQL | Persistence |
| Google Guice 7 | Dependency injection |
| Auth0 java-jwt | JWT creation & verification |
| BouncyCastle | RSA PEM key loading |
| jBCrypt (mindrot) | Password hashing |
| OpenAPI Generator | Code generation from `api.yaml` |
| Lombok | Boilerplate reduction |
| Swagger annotations | API metadata |

## Identity Service

The Identity Service is the first and currently the only implemented module of this application. It exposes REST endpoints for user management, role assignment, and authentication.

### Getting Started (Identity Service)

#### 1. Generate RSA Keys

JWT signing requires an RSA key pair. Generate them from the project root:

```bash
# 1. Generate Private Key
openssl genrsa -out keys/private_key_raw.pem 2048

# 2. Convert to PKCS#8 format (Java standard)
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keys/private_key_raw.pem -out keys/private_key.pem

# 3. Extract the Public Key in X.509 format
openssl rsa -in keys/private_key.pem -pubout -out keys/public_key.pem

# 4. Clean up temporary file
rm keys/private_key_raw.pem
```

> Note: `.pem` files are gitignored and must be generated locally.

#### 2. Configure PostgreSQL

Update `identity/config/base.conf` with your local PostgreSQL credentials:

```yaml
database:
  driverClass: org.postgresql.Driver
  user: social_app
  password: 123456789
  url: jdbc:postgresql://localhost:5432/social
```

The schema tables (`users`, `user_roles`) are auto-created/updated by Hibernate (`hibernate.hbm2ddl.auto: update`).

#### 3. Build & Run

From the project root:

```bash
mvn clean install
```

Then run the Identity service:

```bash
cd identity/scripts
./run.sh
```

Or directly:

```bash
cd identity
mvn clean package -DskipTests
java -jar target/identity-1.0-SNAPSHOT.jar server config/base.conf
```

The service will start on:
- **Application port:** `8080`
- **Admin port:** `8081`

#### 4. Seed Users (Optional)

Create sample users via the provided script:

```bash
cd identity/src/main/resources
./create_users.sh
```

This creates `alice`, `bob`, `carol`, and `david` with password `ChangeMe123!`.

## Configuration

Key settings in `identity/config/base.conf`:

| Setting | Description |
|---------|-------------|
| `server` | HTTP application (8080) and admin (8081) connector ports |
| `jwt.privateKeyPath` | Path to the RSA private key PEM file |
| `jwt.publicKeyPath` | Path to the RSA public key PEM file |
| `jwt.expirationMs` | JWT access token expiry (default: 1 hour / 3600000 ms) |
| `database` | PostgreSQL connection, Hibernate dialect, and auto-DDL settings |

## API Endpoints

### Users (`/users`)

| Method | Path | Description | Required Role |
|--------|------|-------------|---------------|
| POST | `/users` | Create a new user | Public |
| GET | `/users` | List all users | `ADMIN` |
| GET | `/users/{id}` | Get user by ID | `GET_USER`, `ADMIN` |
| PUT | `/users/{id}` | Update a user | `UPDATE_USER`, `ADMIN` |
| DELETE | `/users/{id}` | Delete a user | `DELETE_USER`, `ADMIN` |

### User Roles (`/user-roles`)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/user-roles` | Create a new user role assignment |
| GET | `/user-roles` | List user roles (filter by `userId`, `username`, `roleId`, `roleName`) |
| GET | `/user-roles/{id}` | Get a user role by ID |
| PUT | `/user-roles/{id}` | Update a user role status |
| DELETE | `/user-roles/{id}` | Delete a user role |

### Auth (`/auth/login`)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/auth/login` | Authenticate with username/password, returns a signed JWT access token |