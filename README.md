# SneakPeak

SneakPeak is a Spring Boot MVP for sharing sneaker prices, sale finds, comments, and upvotes. It uses a structured post form and verifies that submitted posts are sneaker-related before saving them.

## Features

- Email/password signup and login
- Email verification before login
- Google OAuth login support
- Structured sneaker post form
- Sale/deal fields for platform, event name, offered price, and offer details
- Feed sorted newest first
- Post detail page with comments
- Upvote toggle
- PostgreSQL persistence
- Spring AI + OpenAI fallback check for sneaker-content verification

## Tech Stack

- Java 21
- Spring Boot
- Spring MVC + Thymeleaf
- Spring Security
- Spring Data JPA
- PostgreSQL
- Spring Mail
- Google OAuth2 Client
- Spring AI + OpenAI
- Maven

## Project Structure

```text
src/main/java/com/sneakpeak/streetpeak
├── auth        # Signup, email verification, auth services
├── comment     # Comment entity, repository, service
├── common      # Shared helpers and model advice
├── post        # Posts, form validation, feed, AI verification
├── security    # Spring Security, login, OAuth, current user
├── user        # User entity and repository
└── vote        # Upvote toggle logic
