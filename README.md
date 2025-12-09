# Spring Framework 7 API Versioning Demo

A comprehensive demonstration of **Spring Framework 7's new first-class API versioning support** - showcasing how to 
build version-aware REST APIs with multiple versioning strategies.

## 🚀 What This Project Demonstrates

This Spring Boot 4 application showcases all four API versioning approaches introduced in Spring Framework 7:

- **Path Segment Versioning**: `/api/v1/users` vs `/api/v2/users`
- **Request Header Versioning**: `X-API-Version: 1.0` vs `X-API-Version: 2.0`
- **Query Parameter Versioning**: `?version=1.0` vs `?version=2.0`
- **Media Type Versioning**: `Accept: application/json;version=1.0`

Each approach returns different response formats to demonstrate real-world API evolution scenarios.

## ⚡ Quick Start

1. **Prerequisites**: Java 25 or 21, Maven
2. **Run**: `./mvnw spring-boot:run`
3. **Test**: Use the provided `api-requests.http` file (IntelliJ IDEA/VS Code) or HTTPie examples below

The application starts on `http://localhost:8080`

## 🔧 Configuration

Edit `src/main/java/dev/danvega/users/config/WebConfig.java` to enable different versioning strategies:

```java
// Uncomment the desired versioning method
//.usePathSegment(1)           // Path-based: /api/v1/users
//.useRequestHeader("X-API-Version")  // Header-based
//.useQueryParam("version")    // Query parameter-based
.useMediaTypeParameter(MediaType.APPLICATION_JSON, "version")  // Currently active
```

## 📋 Response Differences

- **v1**: Returns `UserDTOv1` with single `name` field: `{id, name, email}`
- **v2**: Returns `UserDTOv2` with separate name fields: `{id, firstName, lastName, email}`

## 🔧 Alternative Configuration (application.properties)

Instead of using Java configuration in `WebConfig.java`, you can configure API versioning using `application.properties`:

```properties
# Basic versioning configuration
spring.mvc.api-versioning.supported-versions=1.0,2.0
spring.mvc.api-versioning.default-version=1.0

# Choose ONE versioning strategy:

# Path segment versioning (e.g., /api/v1/users)
spring.mvc.api-versioning.path-segment.enabled=true
spring.mvc.api-versioning.path-segment.index=1

# Request header versioning (e.g., X-API-Version: 1.0)
spring.mvc.api-versioning.request-header.enabled=true
spring.mvc.api-versioning.request-header.name=X-API-Version

# Query parameter versioning (e.g., ?version=1.0)
spring.mvc.api-versioning.query-param.enabled=true
spring.mvc.api-versioning.query-param.name=version

# Media type parameter versioning (e.g., Accept: application/json;version=1.0)
spring.mvc.api-versioning.media-type.enabled=true
spring.mvc.api-versioning.media-type.parameter-name=version
```

> **Note**: When using `application.properties` configuration, comment out or remove the `configureApiVersioning` method in `WebConfig.java` to avoid conflicts.

---

## 📚 Technical Details

### How Spring Framework 7 API Versioning Works

Spring Framework 7's API versioning support allows you to configure how the version is resolved from requests through the ApiVersionConfigurer callback of WebMvcConfigurer. Here are the key approaches for handling versioning with your controller:

#### Path Segment Versioning (Recommended for RESTful APIs)

When using path segments, you need to specify the index of the path segment expected to contain the version, and the path segment must be declared as a URI variable like `"/{version}"` or `"/api/{version}"`.

#### Configuration Options

The framework provides several built-in options for version resolution:
- **Request Header**: Using a custom header like `X-API-Version`
- **Request Parameter**: Using a query parameter like `?version=2.0`
- **Path Segment**: Using URL paths like `/v1/users` or `/api/1.0/users`
- **Media Type Parameter**: Using content negotiation like `application/vnd.company.app-v2+json`

#### Key Features

The framework automatically resolves versions from requests via ApiVersionResolver, parses raw version values into Comparable<?> with an ApiVersionParser, and can send hints about deprecated versions to clients via response headers.

#### Important Notes

- Supported versions are transparently detected from versions declared in request mappings, but you can turn that off and only consider explicitly configured versions
- Requests with unsupported versions are rejected with InvalidApiVersionException resulting in a 400 response
- The `version` attribute in `@RequestMapping` and related annotations (`@GetMapping`, `@PostMapping`, etc.) is new to Spring Framework 7
- By default, a version is required when API versioning is enabled, but you can make it optional in which case the most recent version is used

#### Usage Examples

**Client requests would look like:**
- Path-based: `GET /api/1.0/users` or `GET /api/2.0/users`
- Header-based: `GET /api/users` with header `X-API-Version: 2.0`
- Query parameter: `GET /api/users?version=2.0`

The main advantage of Spring Framework 7's approach is that it provides a standardized, framework-level solution for API versioning rather than requiring custom implementations or workarounds as in previous versions.

## HTTPie Request Examples

Here are practical examples using HTTPie to test each versioning approach implemented in this application:

### 1. Path Segment Versioning
```bash
# v1 - Returns List<User>
http :8080/api/v1/users

# v2 - Returns List<User>
http :8080/api/v2/users
```
*Note: Requires `usePathSegment(1)` to be uncommented in WebConfig*

### 2. Request Header Versioning
```bash
# v1 - Returns List<UserDTOv1>
http :8080/api/users X-API-Version:1.0

# v2 - Returns List<UserDTOv2>
http :8080/api/users X-API-Version:2.0
```
*Note: Requires `useRequestHeader("X-API-Version")` to be uncommented in WebConfig*

### 3. Query Parameter Versioning
```bash
# v1 - Returns List<UserDTOv1>
http :8080/api/users/list version==1.0

# v2 - Returns List<UserDTOv2>
http :8080/api/users/list version==v2
```
*Note: Requires `useQueryParam("version")` to be uncommented in WebConfig*

### 4. Media Type Parameter Versioning
```bash
# v1 - Returns List<UserDTOv1>
http :8080/api/users/media Accept:'application/json;version=1.0'

# v2 - Returns List<UserDTOv2>
http :8080/api/users/media Accept:'application/json;version=2.0'
```
*Note: Currently active in WebConfig with `useMediaTypeParameter(MediaType.APPLICATION_JSON, "version")`*

### Response Differences

- **User**: Full entity with all fields
- **UserDTOv1**: `{id, name, email}` - single name field
- **UserDTOv2**: `{id, firstName, lastName, email}` - separate firstName/lastName fields

### Configuration

Check `WebConfig.java` to see which versioning methods are currently enabled. Only uncommented methods in the `configureApiVersioning` method will work.