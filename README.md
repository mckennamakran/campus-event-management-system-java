# 🎓 Campus Event Management System (Java Console App)

**Author:** McKenna Makran <br>
**Language:** Java

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![OOP](https://img.shields.io/badge/Object%20Oriented-Programming-blueviolet?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-success?style=for-the-badge)
![Platform](https://img.shields.io/badge/Platform-Console-lightgrey?style=for-the-badge)
![Persistence](https://img.shields.io/badge/Data-File%20Storage-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-Academic-informational?style=for-the-badge)


## 📌 Overview

This project is a **Java console-based Campus Event Management System** developed as part of a Programming assignment.

The system simulates real-world event management on campus, allowing **staff and students** to interact with events through a structured, role-based system.

It demonstrates strong **Object-Oriented Programming (OOP)** principles, data handling, and system automation.


## ⚙️ Features

### 👥 User Roles & Access Control
- **Staff Users**
  - Create events
  - Update event details
  - Cancel events
  - View participants and waitlists

- **Student Users**
  - View available events
  - Register for events
  - Cancel registration
  - View registration status

- 🔒 Role-based restrictions enforced at runtime


### 📅 Event Management
- Create events with:
  - Unique Event ID
  - Name
  - Date & Time
  - Location
  - Maximum Participants

- Update:
  - Name
  - Time
  - Location

- View:
  - Registered participants count
  - Waitlist count

- Sort events:
  - By **Name**
  - By **Date**


### ⏳ Registration & Waitlist System
- Automatic handling:
  - ✅ Register if space is available
  - ⏳ Add to waitlist if full

- Cancellation system:
  - Removes user from event/waitlist

- 🔄 **Threaded Automation**
  - When a participant cancels:
    - First student in waitlist is promoted
    - Runs in a **separate thread**
    - Displays notification:
      

### 🔍 Search Functionality
- Search events by:
  - Event Name (partial/full match)
  - Event Date

- Displays:
  - Full event details
  - Registration & waitlist counts

---

### ⚠️ Validation & Error Handling
- Validates:
  - Unique numeric Event IDs
  - Date format (dd/mm/yyyy)
  - Time format (HH:mm)
  - Positive participant limits
  - Non-empty fields

- Handles:
  - Invalid input types
  - Duplicate registrations
  - Non-existent events


### 💾 Data Persistence
- Saves:
  - Events
  - Registrations
  - Waitlists

- Automatically loads data on startup

## 🧠 Technical Concepts Used

- Classes & Objects
- Inheritance & Polymorphism
- Encapsulation
- Collections Framework:
  - `ArrayList`
  - `Queue`
  - `Map`
- File I/O (Read/Write)
- Exception Handling
- Multithreading
- Modular Programming

