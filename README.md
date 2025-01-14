# E-commerce Backend

A robust e-commerce backend system built with Spring Boot, implementing Domain-Driven Design (DDD) principles and modern best practices.

## Related Repositories

This project is part of a complete e-commerce solution:

- [E-commerce Infrastructure](https://github.com/AnielskieOczko/ecommerce_infrastructure) - Docker compose setup for the entire infrastructure
- [E-commerce Frontend](https://github.com/AnielskieOczko/ecommerce_frontend) - React-based frontend application

## Technology Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security** with JWT authentication
- **Spring Data JPA**
- **MySQL 8** - Primary database
- **Spring Validation**
- **Lombok**
- **H2 Database** - For testing
- **Docker & Docker Compose** - Containerization

## Key Features

- **User Management**

  - Authentication & Authorization with JWT
  - Role-based access control
  - User profiles and preferences

- **Product Management**

  - Product catalog with categories
  - Image handling with file storage
  - Inventory management
  - Price management

- **Order Processing**

  - Shopping cart functionality
  - Order creation and management
  - Order status tracking
  - Multiple environment support (dev, local, ci, prod)

- **Security**
  - JWT-based authentication
  - Role-based authorization
  - Input validation
  - Environment-specific security configurations

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker & Docker Compose
- MySQL 8+ (if running locally)

## Getting Started

### Running with Docker Compose (Recommended)

1. **Clone all repositories**

   ```bash
   git clone https://github.com/AnielskieOczko/ecommerce_infrastructure.git
   git clone https://github.com/AnielskieOczko/ecommerce_backend.git
   git clone https://github.com/AnielskieOczko/ecommerce_frontend.git
   ```

2. **Configure environment**

   Create `.env` file in the infrastructure directory:

   ```properties
   # Application
   SPRING_ACTIVE_PROFILE=dev

   # Database
   DB_NAME=your_database_name
   DB_USERNAME=your_db_username
   DB_PASSWORD=your_secure_password
   MYSQL_ROOT_PASSWORD=your_root_password
   SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/your_database_name

   # JWT Configuration
   JWT_SECRET=your_very_secure_jwt_secret_key
   JWT_EXPIRATION=900000

   # Storage Configuration
   STORAGE_LOCATION=/app/product-images
   STORAGE_BASE_URL=/images
   STORAGE_CLEANUP_SCHEDULE=0 0 * * * *
   STORAGE_MAX_FILE_SIZE=10MB
   STORAGE_SECRET_SALT=your_secure_salt_value
   ```

3. **Start the application**

   ```bash
   cd ecommerce-infrastructure
   docker-compose up -d
   ```

   The services will be available at:

   - Backend: `http://localhost:8080`
   - Frontend: `http://localhost:3000`
   - Database: `localhost:3306`

### Running Locally

1. **Clone the repository**

   ```bash
   git clone https://github.com/AnielskieOczko/ecommerce_backend.git
   cd ecommerce-backend
   ```

2. **Configure environment**

   Create `secrets.properties` in `src/main/resources/`:

   ```properties
   # Database
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=your_database_name
   DB_USERNAME=your_db_username
   DB_PASSWORD=your_secure_password

   # JWT Configuration
   JWT_SECRET=your_very_secure_jwt_secret_key
   JWT_EXPIRATION=900000
   JWT_REFRESH_EXPIRATION=604800000

   # Storage Configuration
   STORAGE_LOCATION=/app/product-images
   STORAGE_BASE_URL=/images
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run -Dspring.profiles.active=local
   ```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/rj/ecommerce_backend/
│   │       ├── domain/
│   │       │   ├── cart/         # Shopping cart functionality
│   │       │   ├── order/        # Order processing
│   │       │   ├── product/      # Product management
│   │       │   └── user/         # User management
│   │       ├── securityconfig/   # Security configurations
│   │       └── shared/           # Shared utilities
│   └── resources/
│       ├── application.yml       # Application configuration
│       └── secrets.properties    # Environment variables
```

## Available Profiles

- **dev**: Development environment with containerized MySQL
- **local**: Local development with local MySQL instance
- **ci**: Continuous Integration environment
- **prod**: Production environment

## Development Features

- Remote debugging enabled on port 5005
- Hot reload support
- Volume mounting for live code changes
- Separate networks for backend and frontend services

## Testing

TODO: tests are not implemented yet !!!
Run the tests using:

```bash
mvn test
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

Rafał Jankowski - [@OczkoAnielskie](https://x.com/OczkoAnielskie) - rafaljankowski7@gmail.com

Project Link: [https://github.com/yourusername/ecommerce-backend](https://github.com/AnielskieOczko/ecommerce_backend)

## CI/CD Pipeline

This project uses GitHub Actions for continuous integration and delivery. The pipeline includes:

### Automated Workflows

1. **Build & Test**

   - Triggered on pull requests to main branch and version tags
   - Sets up Java 17 environment
   - Runs with MySQL test database
   - Builds the application
   - Executes all tests
   - Uploads test results as artifacts

2. **Docker Image**

   - Builds Docker image using `Dockerfile_dev`
   - Publishes to GitHub Container Registry (ghcr.io)
   - Tags images based on:
     - Branch name
     - Commit SHA
     - Semantic version (for tags)
     - Latest tag for default branch

3. **Security Scanning**
   - Triggered on version tag pushes
   - Uses Trivy vulnerability scanner
   - Scans Docker images for:
     - Critical vulnerabilities
     - High-severity issues
   - Uploads results to:
     - GitHub Security tab
     - Build artifacts

### Environment Setup

The CI environment uses:

```yaml
services:
  mysql:
    image: mysql:8.0
    env:
      MYSQL_DATABASE: your_test_db
      MYSQL_USER: your_test_user
      MYSQL_PASSWORD: your_test_password
```

### Running Tests Locally

TODO: tests are not implemented yet !!!
To run tests in an environment similar to CI:

```bash
# Set up test environment variables
export SPRING_PROFILES_ACTIVE=ci
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/testdb
export SPRING_DATASOURCE_USERNAME=testuser
export SPRING_DATASOURCE_PASSWORD=testpass

# Run tests
mvn test
```

# Configuration Examples

2. **Configure environment**

   Create `.env` file in the infrastructure directory:

   ```properties
   # Application
   SPRING_ACTIVE_PROFILE=dev

   # Database
   DB_NAME=your_database_name
   DB_USERNAME=your_db_username
   DB_PASSWORD=your_secure_password
   MYSQL_ROOT_PASSWORD=your_root_password
   SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/your_database_name

   # JWT Configuration
   JWT_SECRET=your_very_secure_jwt_secret_key
   JWT_EXPIRATION=900000

   # Storage Configuration
   STORAGE_LOCATION=/app/product-images
   STORAGE_BASE_URL=/images
   STORAGE_CLEANUP_SCHEDULE=0 0 * * * *
   STORAGE_MAX_FILE_SIZE=10MB
   STORAGE_SECRET_SALT=your_secure_salt_value
   ```

For local development:

```properties
# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=your_database_name
DB_USERNAME=your_db_username
DB_PASSWORD=your_secure_password

# JWT Configuration
JWT_SECRET=your_very_secure_jwt_secret_key
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000
```

### CI Environment Example

```yaml
services:
  mysql:
    image: mysql:8.0
    env:
      MYSQL_DATABASE: your_test_db
      MYSQL_USER: your_test_user
      MYSQL_PASSWORD: your_test_password
```

> **Note**: Replace placeholder values with secure credentials. Never commit actual credentials to version control.
