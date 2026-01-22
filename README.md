# Arekta E-Comm App

JavaFX e-commerce demo using Firebase.

## Setup
1. Copy `src/main/resources/config.example.properties` to `src/main/resources/config.properties` and set your values.
2. Provide Firebase credentials one of these ways:
   - Set `FIREBASE_CREDENTIALS_JSON` env var to the raw JSON, or
   - Set `GOOGLE_APPLICATION_CREDENTIALS` (or `FIREBASE_CREDENTIALS`) to the file path, or
   - Put `serviceAccountKey.json` in `src/main/resources/` (will be read from classpath).
3. Build and run with Maven.

## Notes

- Invoices are saved to `invoices/` (ignored by git).
