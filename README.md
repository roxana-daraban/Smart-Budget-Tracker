# Smart Budget Tracker

Aplicație full-stack pentru buget personal: autentificare JWT, tranzacții (venituri / cheltuieli), categorii, dashboard cu statistici, conversie valutară (Frankfurter) și **sfaturi financiare generate cu Google Gemini** (pagina Reports).

## Funcționalități

- **Utilizatori:** Înregistrare, autentificare JWT, profil (username, email), monedă de bază.
- **Tranzacții:** CRUD pentru venituri și cheltuieli (sumă, dată, descriere, categorie, monedă).
- **Dashboard:** Totaluri (venituri, cheltuieli, sold), vizualizări pe categorii, filtre pe perioadă.
- **Rapoarte & AI:** Interval de date, sumar financiar și **`POST /api/reports/ai-advice`** — backend construiește un prompt din agregatele dashboard și apelează **Gemini**; cheia API rămâne doar pe server.
- **Valutar:** Pagină de conversie între monede folosind API-ul Frankfurter.

## Stack tehnic

| Strat | Tehnologii |
|--------|------------|
| **Backend** | Java 17, **Spring Boot 4**, Spring Web MVC, Spring Data JPA, MySQL, Spring Security (JWT), **`spring-boot-starter-json` (Jackson 3)** |
| **Frontend** | React 19, Vite, React Router, Axios, Tailwind (Vite plugin) |
| **Extern** | Frankfurter (cursuri), Google Gemini **Generative Language API** (`generateContent`) |

## Structură repo

```
Smart Budget Tracker/
├── backend/          # Spring Boot — port implicit 8080
├── frontend/       # Vite + React — port dev 8081
└── README.md
```

## Cerințe

- **JDK** 17 (sau 21)
- **Maven** (sau folosește `mvnw` / `mvnw.cmd` din `backend/`)
- **Node.js** 18+ și npm
- **MySQL** 8+ (local sau remote)

## Configurare și rulare

### Backend

1. Creează schema/baza MySQL (ex. `budget_tracker`), conform URL-ului din config.
2. **Nu comite** `application.properties` cu parole sau chei. Copiază șablonul:
   - din `backend/src/main/resources/templates/application.properties.exemple`
   - în `backend/src/main/resources/application.properties`
3. Completează cel puțin:
   - `spring.datasource.*` (URL, user, parolă)
   - `jwt.secret` (secret puternic, unic)
   - pentru AI: `gemini.api.key` și opțional `gemini.api.model` (ex. `gemini-2.5-flash`)
   - `currency.api.base-url` (implicit Frankfurter)

4. Pornește aplicația din `backend/`:

   ```bash
   ./mvnw spring-boot:run
   ```

   Pe Windows (PowerShell/CMD):

   ```bat
   mvnw.cmd spring-boot:run
   ```

### Frontend

Din `frontend/`:

```bash
npm install
npm run dev
```

UI dev: **http://localhost:8081** (vezi `vite.config.js`). Backend: **http://localhost:8080**. CORS este configurat pentru `localhost:8081` și `localhost:5173`.

## API — AI (rezumat)

- Autentificare obligatorie (Bearer JWT).
- **Request:** `POST /api/reports/ai-advice` — body JSON `{ "from": "YYYY-MM-DD", "to": "YYYY-MM-DD" }`.
- **Response:** `{ "adviceText": "..." }` — text generat de Gemini din sumarul financiar al utilizatorului pentru perioada aleasă.

## Securitate înainte de `git push`

- Verifică că **`application.properties`** nu este urmărit de Git (`.gitignore` la rădăcină).
- Nu include chei API sau `jwt.secret` reale în commit-uri; rotește cheile dacă au fost expuse.
- Confirmă în istoric: `git check-ignore -v backend/src/main/resources/application.properties`

## Licență

Proiect personal / educațional — adaugă licența dorită dacă publici repo-ul.
