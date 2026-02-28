🎟️ Event Ticketing System
A full-stack application for managing event listings and ticket purchases, built with a modern, reactive tech stack.

🚀 Overview
This project is a lightweight event management platform that allows users to browse upcoming events and purchase tickets in real-time. The system is designed for simplicity, utilizing a local file-based database for easy setup and development.

🛠️ Tech Stack
Backend
Language: Kotlin

Framework: Spring Boot 3.x

Data Access: Spring Data JPA

Database: H2 Database (Local file-based)

Build Tool: Gradle (Kotlin DSL)

Frontend
Framework: Vue 3 (Composition API)

Build Tool: Vite

Styling: Tailwind CSS

HTTP Client: Axios

📋 Key Features
Event Catalog: Browse a list of available events with real-time ticket availability.

Ticket Purchasing: Secure transaction logic to handle ticket sales.

Concurrency Management: Thread-safe backend to prevent over-selling of tickets.

Local Persistence: Data is stored locally in an H2 database file (./data/ticketdb).

🏗️ Getting Started
Prerequisites
JDK 17 or higher

Node.js (v18+)

Your favorite IDE (IntelliJ IDEA recommended)

Backend Setup
Navigate to the /backend directory.

Run ./gradlew bootRun.

The API will be available at http://localhost:8080.

Frontend Setup
Navigate to the /frontend directory.

Run npm install.

Run npm run dev.

Open your browser to http://localhost:5173.

🧪 Database Console
You can access the H2 console to view your data at:
http://localhost:8080/h2-console

JDBC URL: jdbc:h2:file:./data/ticketdb

User: sa

Password: (leave blank)

📝 Roadmap
[ ] Implement User Authentication (JWT).

[ ] Add Admin Dashboard for creating events.

[ ] Integrate Email notifications for ticket confirmation.

How to use this with Claude
- Prompt 1 - The Backend Foundation
Prompt: "I want to build an Event Ticketing System. Using Kotlin and Spring Boot, please create a REST API.
Use H2 as a local file-based database.
Create an Event entity (id, title, date, totalTickets, price) and a Ticket entity (id, eventId, customerName, purchaseDate).
Provide a TicketService with a thread-safe method to 'purchase' a ticket (check availability, decrement totalTickets, and save the Ticket).
Include a Controller with endpoints to GET all events and POST a purchase."
