# Vehicle Rental System
**OOAD Mini Project | UE23CS352B | Team of 4**

## Tech Stack
- Java 21
- Spring Boot 3.2.3 (MVC)
- Spring Security
- Spring Data JPA
- Thymeleaf (frontend)
- H2 (dev) / MySQL (prod)
- Lombok
- Maven

---

## GRASP Principles
| Member | Principle | Class |
|---|---|---|
| Member 1 | Information Expert | `UserService` / `UserServiceImpl` |
| Member 2 | Low Coupling | `VehicleService` (zero dependency on BookingService) |
| Member 3 | Controller | `BookingController` (GRASP Controller) |
| Member 4 | Pure Fabrication | `PricingService` |

## Design Patterns
| Type | Pattern | Location |
|---|---|---|
| Creational | Factory | `factory/UserFactory.java` |
| Structural | Decorator | `decorator/` (VehicleDecorator, GpsDecorator, etc.) |
| Behavioral | Observer | `observer/` (BookingObserver, EmailNotificationObserver, etc.) |
| Framework | Spring DI | `@Autowired` / `@RequiredArgsConstructor` throughout |

---

## Setup Instructions

### Prerequisites
- Java 21+ → https://adoptium.net/
- Maven 3.8+ → https://maven.apache.org/download.cgi
- (Optional) MySQL 8+ for production

Verify installs:
```bash
java -version
mvn -version
```

### Run with H2 (default - no setup needed)
```bash
git clone <repo-url>
cd vehicle-rental-system
mvn spring-boot:run
```
Open: http://localhost:8080
H2 Console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:file:./vrental-db`)

### Default Credentials
| Role | Email | Password |
|---|---|---|
| Admin | admin@vrental.com | admin123 |
| Customer | register at /auth/register | your choice |

### Switch to MySQL (production)
1. Create database: `CREATE DATABASE vrental;`
2. In `application.properties`, comment out H2 block and uncomment MySQL block
3. Set your MySQL username/password
4. Run: `mvn spring-boot:run`

### Build JAR
```bash
mvn clean package
java -jar target/vehicle-rental-system-1.0.0.jar
```

---

## Project Structure
```
src/main/java/com/vehiclerental/
├── VehicleRentalApplication.java       # Main entry point
├── config/
│   ├── SecurityConfig.java             # Spring Security setup
│   └── DataInitializer.java            # Seeds admin + sample data
├── controller/
│   ├── AuthController.java             # Member 1 — Registration/Login
│   ├── VehicleController.java          # Member 2 — Vehicle CRUD
│   ├── BookingController.java          # Member 3 — Booking flow (GRASP Controller)
│   ├── RentalController.java           # Member 4 — Rental & invoice
│   ├── AdminController.java            # Admin dashboard
│   └── HomeController.java             # Home & dashboard
├── entity/
│   ├── User.java
│   ├── Vehicle.java
│   ├── Booking.java
│   └── Rental.java
├── repository/
│   ├── UserRepository.java
│   ├── VehicleRepository.java
│   ├── BookingRepository.java
│   └── RentalRepository.java
├── service/
│   ├── UserService.java                # GRASP: Information Expert
│   ├── VehicleService.java             # GRASP: Low Coupling
│   ├── BookingService.java             # GRASP: Controller
│   ├── PricingService.java             # GRASP: Pure Fabrication
│   ├── RentalService.java
│   └── impl/
│       ├── UserServiceImpl.java
│       ├── VehicleServiceImpl.java
│       ├── BookingServiceImpl.java
│       ├── PricingServiceImpl.java
│       └── RentalServiceImpl.java
├── factory/
│   └── UserFactory.java                # Pattern: Factory (Creational)
├── decorator/
│   ├── VehicleComponent.java           # Pattern: Decorator (Structural)
│   ├── BaseVehicle.java
│   ├── VehicleDecorator.java
│   ├── GpsDecorator.java
│   ├── InsuranceDecorator.java
│   └── ChildSeatDecorator.java
└── observer/
    ├── BookingObserver.java             # Pattern: Observer (Behavioral)
    ├── EmailNotificationObserver.java
    └── AvailabilityUpdaterObserver.java
```

## Use Cases Per Member
| Member | Major Use Case | Minor Use Case |
|---|---|---|
| 1 | User registration, login, role-based access | Profile view/edit |
| 2 | Vehicle inventory (add/edit/delete/browse/filter) | Vehicle detail page |
| 3 | Booking (create, view, cancel) | Booking confirmation notification |
| 4 | Rental management (pickup, return, invoice) | Rental history |

## GitHub Branch Convention
```
main                    ← merged production code
feature/member1-auth
feature/member2-vehicles
feature/member3-booking
feature/member4-rental
```
