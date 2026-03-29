# 🏨 Hotel Booking System (Spring Boot)

A full-stack Java application designed to manage hotel reservations, room availability, and guest details. This project integrates with **RapidAPI** for real-time hotel data and uses **MySQL** for persistent storage.

---

## 🚀 Features
* **Live Hotel Search:** Fetches real-time data using RapidAPI integration.
* **Booking Management:** Create, view, and manage room reservations.
* **Security:** Integrated Spring Security for Role-Based Access Control (Admin/User).
* **Automated Notifications:** Email service integrated for booking confirmations.
* **Database Persistence:** Managed via Spring Data JPA and Hibernate.

---

## 🛠️ Tech Stack
* **Backend:** Java 17, Spring Boot 3.x, Spring Data JPA, Spring Security
* **Database:** MySQL Workbench
* **API Integration:** RapidAPI (Hotel Booking API)
* **Build Tool:** Maven
* **IDE:** Eclipse / IntelliJ IDEA

---

## ⚙️ Prerequisites & Setup

### 1. Database Configuration
Create a schema in your MySQL Workbench:
```sql
CREATE DATABASE hotel_db;
