# Sportcentrum Pálya- és Eszközkölcsönző Rendszer

Ez a projekt egy sportcentrum foglalási rendszerének teljes körű megvalósítása. Tartalmaz egy **Spring Boot (Java)** alapú REST API backendet és egy független **React (Vite/JavaScript)** Single Page Application (SPA) frontendet.

## Technológiai Stack
- **Backend:** Java 17+, Spring Boot 3.x, Spring Security (JWT), Spring Data JPA, H2 In-Memory Database
- **Frontend:** React 18+, Vite, Axios, React Router Dom

---

## Futtatás és Fordítás Parancssorból

Mielőtt elkezded, győződj meg róla, hogy a gépeden telepítve van:
- **Java JDK 25**
- **Node.js** és **npm**

### 1. A Backend (Spring Boot) indítása

- Nyiss egy parancssort/terminált, és lépj be a backend projekt gyökérkönyvtárába (ahol a `pom.xml` található): cd eleresi_ut/SpringNHF

## Fordítás: 

- Windows (Command Prompt / PowerShell):
    mvnw clean

- Linux / macOS:
    chmod +x mvnw
    ./mvnw clean package

## Alkalmazás indítása: 

- Windows:
    mvnw spring-boot:run

- Linux / macOS:
    ./mvnw spring-boot:run

A backend sikeres indulás után a http://localhost:8080 címen lesz elérhető. Az adatbázis konzolja a http://localhost:8080/h2-console címen érhető el 
| Driver Class | JDBC URL | Username | Password | 
| :--- | :--- | :--- | :--- |
| org.h2.Driver | jdbc:h2:file:./data/sportcentrum | sa | nincs |

### 2. A Frontend (React + Vite) indítása

- Nyiss egy új parancssort/terminált (a backendet hagyd futni!), és lépj be a frontend projekt könyvtárába (ahol a package.json található): cd eleresi_ut/sportcentrum-frontend

- Függőségek telepítése (csak az első futtatás előtt szükséges): 
    npm install

- Alkalmazás indítása fejlesztői módban: 
    npm run dev

A frontend sikeres indulás után a parancssorban kiírt címen (általában http://localhost:5173) érhető el. Nyisd meg ezt a linket a böngésződben!

Teszt Felhasználók (Data Seeder): A rendszer induláskor automatikusan feltölti az adatbázist teszt adatokkal. A bejelentkezéshez az alábbi fiókokat használhatod: 

| Szerepkör | E-mail cím | Jelszó |
| :--- | :--- | :--- |
| **ADMIN** | `admin@sportcentrum.hu` | `AdminJelszo123` |
| **CUSTOMER** | `teszt@sportcentrum.hu` | `titkosJelszo123` |

FONTOS TESZTELÉSI INFÓ: Ha egy időben, egyazon gépen teszteled az Admin és a Customer funkciókat, az egyik fiókot sima böngészőablakban, a másikat pedig Inkognitó (Privát) módban vagy egy másik böngészőben nyisd meg! Mivel az alkalmazás a JWT tokent a localStorage-ban tárolja, a sima fülek felülírnák egymás bejelentkezési munkamenetét.