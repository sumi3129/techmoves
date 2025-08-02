# Techmoves Delivery Order System

A RESTful API for managing delivery orders with distance calculation using Google Maps API.

## Requirements

- Docker
- Docker Compose
- Google Maps API key (optional for local development)

## Setup

1. Clone the repository
2. Create `.env` file with your Google Maps API key:
   GOOGLE_MAPS_API_KEY=your_api_key_here
3. Run the application:
```bash
chmod +x start.sh
./start.sh
```

## API Documentation

- Create an Order

   POST /orders
   Request:

```json
{
    "origin": ["22.319181", "114.170008"],
    "destination": ["22.336093", "114.155288"]
}
```
   Response:

```json
{
    "id": 1,
    "distance": 2350,
    "status": "UNASSIGNED"
}
```

- Take an Order
   PATCH /orders/:id
   Request:

```json
{
    "status": "TAKEN"
}
```
   Response:

```json
{
    "status": "SUCCESS"
}
```

- List Orders
   GET /orders?page=1&limit=10
   Response:

```json
[
    {
        "id": 1,
        "distance": 2350,
        "status": "TAKEN"
    }
]
```