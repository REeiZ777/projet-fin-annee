## Modélisation relationnelle LBGconnect

- **users**  
  - `id` (PK), `full_name`, `email` (unique), `phone`, `password`, `role` (ARTISAN | APPRENTI | EMPLOYEUR), `location`, `avatar_url`, `headline`, `bio`, `rating`, `reviews_count`, `verified`, `created_at`.  
  - Porte les infos d’authentification et de profil de base.

- **skills** / **user_skills**  
  - `skills` : catalogue des compétences.  
  - `user_skills` : table de jointure (PK composite) pour rattacher plusieurs compétences à un artisan/apprenti.

- **jobs**  
  - `id` (PK), `title`, `description`, `category`, `location`, `contract_type`, `status`, `salary_min`, `salary_max`, `posted_by_id` (FK → `users`), `created_at`.  
  - **job_tags** : tags libres (1‑N) sur les offres pour affiner la recherche.

- **job_applications**  
  - `id` (PK), `job_id` (FK), `applicant_id` (FK → `users`), `status` (EN_COURS | ACCEPTEE | REFUSEE), `cover_letter`, `expected_rate`, `created_at`.  
  - Traque les candidatures/apprentissages et leur statut.

- **messages**  
  - `id` (PK), `sender_id` (FK), `recipient_id` (FK), `subject`, `body`, `read_flag`, `created_at`.  
  - Correspond aux conversations simples entre profils.

- **reviews**  
  - `id` (PK), `artisan_id` (FK), `reviewer_id` (FK), `rating`, `comment`, `created_at`.  
  - Permet de calculer la réputation (`rating`, `reviews_count`) des artisans.

### Flux clé (dépendance forte → faible)
1. `users` (base)  
2. `skills` puis `user_skills` (profil enrichi)  
3. `jobs` (dépend de users.employeur)  
4. `job_tags` (affinage recherche)  
5. `job_applications` (dépend de jobs + users)  
6. `messages` (dépend de users)  
7. `reviews` (dépend de users; impacte réputation artisan)

### Données de départ
- Script SQL prêt à injecter dans MySQL : `db/schema_mysql.sql` (création DB + seeds réalistes).  
- Exemple d’utilisateurs : 3 artisans (menuiserie, couture, électricité), 1 apprenti, 1 employeur; offres, candidatures, messages et avis associés.
