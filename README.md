# MixTape 🎵

Eine Plattform für digitale Kassetten-Mixtapes. Nutzer können Mixtapes erstellen, Songs via Spotify-Suche hinzufügen und ihre Kassetten mit anderen teilen.

## Architektur

```
┌──────────────┐     ┌──────────────────┐     ┌─────────────────┐
│   Frontend   │────▶│     Backend      │────▶│   Spotify API   │
│ React + Vite │◀────│  Spring Boot     │     │ (Song-Suche)    │
└──────────────┘     └────────┬─────────┘     └─────────────────┘
                              │
                        ┌─────▼─────┐
                        │ H2 / SQL  │
                        └───────────┘
```

- **Frontend:** React (Vite), läuft auf Port 5173
- **Backend:** Java 21, Spring Boot, läuft auf Port 8080
- **Datenbank:** H2 In-Memory (Entwicklung)
- **Externe API:** Spotify Web API (Client Credentials Flow) für Song-Suche

## Entitäten

- **User** – Nutzerkonto mit Username, Email, Bio
- **Mixtape** – Digitale Kassette (C60/C90/C120) mit Tracks und Tags
- **Track** – Song mit Spotify-Metadaten und Position auf dem Mixtape
- **Tag** – Schlagwörter für Mixtapes (ManyToMany)
- **Like** – Nutzer liked ein Mixtape
- **ShelfEntry** – Nutzer speichert ein Mixtape in seinem Regal

## Voraussetzungen

- Java 21
- Node.js 20+
- Maven (oder der enthaltene Maven Wrapper)

## Lokales Starten

### Backend

```bash
cd mixtape-backend
./mvnw spring-boot:run
```

Backend läuft auf: http://localhost:8080

### Frontend

```bash
cd mixtape-frontend
npm install
npm run dev
```

Frontend läuft auf: http://localhost:5173

### Umgebungsvariablen Backend

Erstelle `mixtape-backend/src/main/resources/application-local.properties` oder setze folgende Werte in `application.properties`:

```properties
spotify.client-id=DEIN_SPOTIFY_CLIENT_ID
spotify.client-secret=DEIN_SPOTIFY_CLIENT_SECRET
mixtape.jwt.secret=dein-geheimer-schluessel-mindestens-32-zeichen
mixtape.jwt.expiration=86400000
```

## Tests ausführen

```bash
cd mixtape-backend
./mvnw test
```

## API Dokumentation

Swagger UI ist verfügbar unter: http://localhost:8080/swagger-ui.html

## Drittanbieter-API

**Spotify Web API** – https://developer.spotify.com/documentation/web-api

- Authentifizierung: Client Credentials Flow (kein User-Login nötig)
- Verwendete Endpoints:
    - `GET /v1/search` – Song-Suche nach Titel/Artist
    - `GET /v1/tracks/{id}` – Track-Details abrufen
- Token wird im Backend gecacht und automatisch erneuert

## Geschäftsregeln

- **C60** = max. 3600 Sekunden Gesamtlaufzeit
- **C90** = max. 5400 Sekunden Gesamtlaufzeit
- **C120** = max. 7200 Sekunden Gesamtlaufzeit
- Ein Mixtape wird nach dem ersten Speichern gesperrt (`isLocked = true`)
- Ein Mixtape braucht mindestens einen Track zum Abschließen
- Usernames sind eindeutig

## Spotify-Export

Mixtapes können direkt in Spotify geöffnet werden:

- Jeder Track verlinkt direkt zum Song auf Spotify (Web/App)
- Alle Spotify-URIs eines Mixtapes lassen sich mit einem Klick kopieren und in eine eigene Playlist einfügen
- Kein zusätzlicher Spotify-Login nötig – die Links basieren auf den bereits gespeicherten Track-IDs