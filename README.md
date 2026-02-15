# Gym Management System

A comprehensive fitness center management application built with Spring Boot, featuring member management, trainer/doctor profiles, booking systems, meal plans, and workout plans.

---

## 🚀 Quick Start

```bash
# 1. Create database
mysql -u root -p
CREATE DATABASE gym;

# 2. Configure application
# Edit src/main/resources/application.properties
# Update database username and password

# 3. Run application
./mvnw spring-boot:run

# 4. Access application
# Open http://localhost:8080
```

**For detailed instructions, see [QUICK-START.md](QUICK-START.md)**

---

## 📚 Documentation

### Getting Started
- **[QUICK-START.md](QUICK-START.md)** - Get up and running in 5 minutes
- **[CHANGES-SUMMARY.md](CHANGES-SUMMARY.md)** - Overview of recent changes

### Technical Documentation
- **[README-FIXES.md](README-FIXES.md)** - Detailed list of all fixes applied
- **[MAPPING-VERIFICATION.md](MAPPING-VERIFICATION.md)** - Complete endpoint verification
- **[PROJECT-STATUS.md](PROJECT-STATUS.md)** - Current project status and metrics

### Deployment & Operations
- **[DEPLOYMENT.md](DEPLOYMENT.md)** - Comprehensive deployment guide
- **[FINAL-REPORT.md](FINAL-REPORT.md)** - Complete project report

---

## ✨ Features

### User Management
- Multi-role system (Member, Trainer, Doctor)
- Secure authentication and authorization
- Profile management

### Booking System
- Book sessions with trainers and doctors
- View upcoming appointments
- Manage availability

### Meal Plans
- Doctors create personalized meal plans
- Members view their nutrition plans
- Track meal items and schedules

### Workout Plans
- Trainers create custom workout plans
- Members access their exercise routines
- Track workout items and progress

### Rating System
- Members rate trainers and doctors
- View ratings and feedback
- Quality assurance

---

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.3.0
- **Database**: MySQL 8.0
- **Security**: Spring Security with BCrypt
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
- **API Documentation**: Swagger/OpenAPI
- **Build Tool**: Maven
- **Java Version**: 17

---

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Git (for version control)

---

## 🎯 Project Structure

```
gym/
├── src/main/java/com/example/gym/
│   ├── config/          # Configuration classes
│   ├── controller/      # REST and MVC controllers (17)
│   ├── dto/             # Data Transfer Objects (8)
│   ├── model/           # JPA entities (13)
│   ├── repository/      # Data access layer (14)
│   ├── security/        # Security configuration
│   └── service/         # Business logic (15)
├── src/main/resources/
│   ├── static/          # CSS, JS, images
│   ├── templates/       # Thymeleaf templates (29)
│   └── application.properties
└── Documentation files (8)
```

---

## 🔐 Security

### Authentication
- Form-based login
- BCrypt password encryption
- Session management

### Authorization
- Role-based access control
- Protected endpoints
- CSRF protection

### Roles
- **MEMBER** - Regular gym members
- **TRAINER** - Fitness trainers
- **DOCTOR** - Nutrition doctors

---

## 🌐 API Endpoints

### Public
- `GET /` - Home page
- `GET /login` - Login page
- `GET /register` - Registration
- `POST /register` - User registration

### Member
- `GET /member/dashboard` - Member dashboard
- `GET /member/bookings` - View bookings
- `POST /member/bookings` - Create booking

### Trainer
- `GET /trainer/dashboard` - Trainer dashboard
- `GET /trainer/bookings` - View bookings
- `GET /api/trainers` - List trainers

### Doctor
- `GET /doctor/dashboard` - Doctor dashboard
- `GET /doctor/profile` - View/edit profile
- `POST /doctor/profile/update` - Update profile

**For complete API documentation, see [MAPPING-VERIFICATION.md](MAPPING-VERIFICATION.md)**

---

## 🎨 UI Features

- Modern, responsive design
- Professional gradient themes
- Smooth animations
- Mobile-friendly
- Accessible components

---

## 📊 Database Schema

### Main Tables
- `users` - User accounts
- `roles` - User roles
- `member_profiles` - Member details
- `trainer_profiles` - Trainer details
- `doctor_profiles` - Doctor details
- `bookings` - Appointments
- `meal_plans` - Nutrition plans
- `workout_plans` - Exercise plans
- `payments` - Payment records
- `ratings` - User ratings

---

## 🚀 Deployment

### Local Development
```bash
./mvnw spring-boot:run
```

### Production Build
```bash
./mvnw clean package -DskipTests
java -jar target/gym-0.0.1-SNAPSHOT.jar
```

### Docker
```bash
docker-compose up -d
```

**For detailed deployment instructions, see [DEPLOYMENT.md](DEPLOYMENT.md)**

---

## 🧪 Testing

### Manual Testing
1. Start the application
2. Register a new user
3. Login with credentials
4. Test role-specific features
5. Verify all endpoints

### Automated Testing
- Unit tests: To be implemented
- Integration tests: To be implemented
- E2E tests: To be implemented

---

## 📝 Configuration

### Database
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gym
spring.datasource.username=root
spring.datasource.password=your_password
```

### Email (Optional)
```properties
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

### Server
```properties
server.port=8080
```

**See [.env.example](.env.example) for all configuration options**

---

## 🐛 Troubleshooting

### Common Issues

**Port 8080 already in use**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

**Database connection failed**
- Check MySQL is running
- Verify credentials in application.properties
- Ensure database 'gym' exists

**Lombok errors in IDE**
- Install Lombok plugin
- Enable annotation processing
- Rebuild project

---

## 📈 Project Status

- ✅ All mapping errors fixed
- ✅ Professional UI implemented
- ✅ Complete documentation
- ✅ Production-ready code
- ⚠️ Tests to be added
- ⚠️ Production hardening needed

**For detailed status, see [PROJECT-STATUS.md](PROJECT-STATUS.md)**

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 👥 Support

### Documentation
- Quick Start: [QUICK-START.md](QUICK-START.md)
- Deployment: [DEPLOYMENT.md](DEPLOYMENT.md)
- API Reference: [MAPPING-VERIFICATION.md](MAPPING-VERIFICATION.md)

### Issues
- Check application logs
- Review documentation
- Verify configuration
- Test with curl/Postman

---

## 🎯 Roadmap

### Current Version (1.0.0)
- ✅ Core functionality
- ✅ User management
- ✅ Booking system
- ✅ Meal/Workout plans

### Future Enhancements
- [ ] File upload for profiles
- [ ] Real-time notifications
- [ ] Payment gateway integration
- [ ] Email verification
- [ ] Password reset
- [ ] Two-factor authentication
- [ ] Mobile app
- [ ] Advanced analytics

---

## 📞 Contact

For questions or support, please refer to the documentation files or create an issue in the repository.

---

## 🙏 Acknowledgments

Built with:
- Spring Boot
- Spring Security
- Thymeleaf
- MySQL
- Bootstrap concepts
- Modern CSS

---

**Version**: 1.0.0  
**Status**: Production Ready (with security hardening)  
**Last Updated**: February 9, 2026

---

## 📖 Quick Links

- [Quick Start Guide](QUICK-START.md)
- [Deployment Guide](DEPLOYMENT.md)
- [API Documentation](MAPPING-VERIFICATION.md)
- [Project Status](PROJECT-STATUS.md)
- [Recent Changes](CHANGES-SUMMARY.md)
- [Complete Report](FINAL-REPORT.md)

---

**Happy Coding! 🎉**
#   G y m - F i t n e s s - M a n a g e m e n t - S y s t e m  
 #   G y m - F i t n e s s - M a n a g e m e n t - S y s t e m  
 #   G y m - F i t n e s s - M a n a g e m e n t - S y s t e m  
 