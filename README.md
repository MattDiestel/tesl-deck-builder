# TESL Deck Builder

A full-stack web application that allows users to browse, filter, and build decks from a collection of 1000+ The Elder Scrolls Legends cards, then download a print-ready PDF of their deck.

Note that Railway subscription has ended and only the frontend is currently deployed. This app will be back up May 22nd

**Semi-Live App:** https://frontend-gilt-kappa-25.vercel.app/
---

## Features

- Browse 1000+ cards organized by color
- Filter cards by color (Blue, Green, Red, Yellow, Purple, Neutral, Multi Color)
- Search cards by name/ID
- Build a deck with a minimum of 50 cards (max 3 copies of any card)
- Download your deck as a print-ready PDF
- Responsive card grid with pagination

---

## Tech Stack

**Frontend**
- React (Vite)
- CSS3
- Deployed on Vercel

**Backend**
- Java 21 with Spring Boot 4
- iText 7 for PDF generation
- Cloudinary API for image storage and retrieval
- Deployed on Railway

**Image Hosting**
- Cloudinary (1000+ card images, compressed from ~2MB to ~258KB each)

---

## Architecture
