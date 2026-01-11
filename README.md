## LBGconnect - Spring Boot + Thymeleaf

Plateforme pour connecter artisans, apprentis et employeurs. Front responsive (Bootstrap + MUI) servi par Thymeleaf, donnees via Spring Data JPA (H2 par defaut, profil MySQL pret).

### Lancer le projet (local, sans MySQL)
```bash
mvn spring-boot:run
 http://localhost:8080
```
- Profil par defaut : H2 en memoire (mode MySQL) avec donnees seed par `DataSeeder`.
- Console H2 : `/h2-console` (JDBC url `jdbc:h2:mem:lbgconnect`).

### Utiliser MySQL (profil `mysql`)
1. Importer le modele + seeds :
   ```bash
   "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p(Je ne peux pas exposer mon mot de passe) < db/schema_mysql.sql
   ```
2. Lancer l'app avec le profil MySQL :
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```
   - URL : `jdbc:mysql://localhost:3306/lbgconnect`
   - Identifiant attendu : `root` / `(je ne peux pas exposer mon mot de passe)` (modifiez `src/main/resources/application-mysql.yml` si besoin).

### Comptes de demo
Mot de passe par defaut (seed) : `Lbgconnect123!`
- `adjoua@lbgconnect.test` (ARTISAN)
- `amina@lbgconnect.test` (ARTISAN)
- `moussa@lbgconnect.test` (ARTISAN)
- `abdul@lbgconnect.test` (APPRENTI)
- `contact@lbgconnect.test` (EMPLOYEUR)

### Structure
- `src/main/java/com/lbgconnect/model` : entites (`UserAccount`, `Job`, `JobApplication`, `ConversationMessage`, `Review`, `Skill`, enums).
- `src/main/java/com/lbgconnect/web` : controleurs Thymeleaf (home, jobs, profil, messages, auth).
- `src/main/resources/templates` : vues (index, jobs, job-detail, job-create, artisan-profile, profile, messages, login, register).
- `src/main/resources/static/css/app.css` : theme sombre leger base Bootstrap/MUI.
- `db/schema_mysql.sql` : DDL + donnees de depart pour MySQL.
- `DATABASE_MODEL.md` : schema logique et dependances.

### Fonctionnalites livrees
- Authentification locale (Spring Security + BCrypt) + inscription.
- Listing dynamique des artisans/apprentis + competences + rating.
- Offres, details, creation d'offre (role EMPLOYEUR).
- Candidatures et messages cote serveur.
- Recherche/filtrage cote serveur (artisans/offres).
- Upload avatar en stockage local (`uploads/`).

### Upload avatar (stockage local)
- Dossier local : `uploads/` a la racine du projet (servi par `/uploads/**`).
- Pour passer sur un CDN, remplacez `LocalStorageService` par un client S3/CDN et renvoyez une URL distante.

### Tests / build
- Build : `mvn clean package`
- Tests : `mvn test`

