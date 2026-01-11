CREATE DATABASE IF NOT EXISTS lbgconnect CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lbgconnect;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    password VARCHAR(255),
    role VARCHAR(20) NOT NULL,
    location VARCHAR(255),
    avatar_url VARCHAR(255),
    headline VARCHAR(255),
    bio TEXT,
    rating DECIMAL(3,2),
    reviews_count INT,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS skills (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_skills (
    user_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, skill_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (skill_id) REFERENCES skills(id)
);

CREATE TABLE IF NOT EXISTS jobs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    location VARCHAR(150),
    contract_type VARCHAR(100),
    status VARCHAR(50),
    salary_min DECIMAL(12,2),
    salary_max DECIMAL(12,2),
    posted_by_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (posted_by_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS job_tags (
    job_id BIGINT NOT NULL,
    tag VARCHAR(100) NOT NULL,
    FOREIGN KEY (job_id) REFERENCES jobs(id)
);

CREATE TABLE IF NOT EXISTS job_applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_id BIGINT NOT NULL,
    applicant_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    cover_letter TEXT,
    expected_rate DECIMAL(12,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_id) REFERENCES jobs(id),
    FOREIGN KEY (applicant_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    subject VARCHAR(255),
    body TEXT,
    read_flag BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (recipient_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    artisan_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (artisan_id) REFERENCES users(id),
    FOREIGN KEY (reviewer_id) REFERENCES users(id)
);

-- Seed demo data
INSERT INTO skills (name) VALUES
('Menuiserie'), ('Electricite'), ('Plomberie'), ('Couture'), ('Peinture'), ('Maconnerie')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO users (full_name, email, phone, role, location, avatar_url, headline, bio, rating, reviews_count, verified)
VALUES
('Adjoua Kone', 'adjoua@lbgconnect.test', '+2250701010101', 'ARTISAN', 'Abidjan', '/assets/images/avatar-adjoua.png', 'Menuisiere specialisee en mobilier sur mesure', '10 ans d experience sur des chantiers premium.', 4.80, 42, TRUE),
('Amina Diallo', 'amina@lbgconnect.test', '+2250702020202', 'ARTISAN', 'Grand-Bassam', '/assets/images/avatar-amina.png', 'Couturiere & styliste', 'Creations modernes avec tissus locaux.', 4.60, 28, TRUE),
('Moussa Traore', 'moussa@lbgconnect.test', '+2250703030303', 'ARTISAN', 'Abidjan', '/assets/images/avatar-moussa.png', 'Electricien - depannage et chantiers tertiaires', 'Equipe de 3 personnes, interventions 7j/7.', 4.90, 61, TRUE),
('Abdul Camara', 'abdul@lbgconnect.test', '+2250704040404', 'APPRENTI', 'Yamoussoukro', '/assets/images/avatar-moussa.png', 'Apprenti macon, disponible week-end', 'Capable de preparer chantiers, couler dalle.', 4.10, 9, FALSE),
('Entreprise LBG', 'contact@lbgconnect.test', '+2250101010101', 'EMPLOYEUR', 'Abidjan Plateau', '/assets/images/hero.png', 'Entreprise generale du batiment', 'Recherche artisans fiables pour chantiers premium.', 4.50, 14, TRUE)
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);
-- Passwords are set by the app (BCrypt) on first run if empty.

-- User skills mapping (only if new rows were inserted)
INSERT IGNORE INTO user_skills (user_id, skill_id)
SELECT u.id, s.id FROM users u JOIN skills s ON s.name = 'Menuiserie' WHERE u.email = 'adjoua@lbgconnect.test';
INSERT IGNORE INTO user_skills (user_id, skill_id)
SELECT u.id, s.id FROM users u JOIN skills s ON s.name = 'Peinture' WHERE u.email = 'adjoua@lbgconnect.test';
INSERT IGNORE INTO user_skills (user_id, skill_id)
SELECT u.id, s.id FROM users u JOIN skills s ON s.name = 'Couture' WHERE u.email = 'amina@lbgconnect.test';
INSERT IGNORE INTO user_skills (user_id, skill_id)
SELECT u.id, s.id FROM users u JOIN skills s ON s.name = 'Electricite' WHERE u.email = 'moussa@lbgconnect.test';
INSERT IGNORE INTO user_skills (user_id, skill_id)
SELECT u.id, s.id FROM users u JOIN skills s ON s.name = 'Plomberie' WHERE u.email = 'moussa@lbgconnect.test';
INSERT IGNORE INTO user_skills (user_id, skill_id)
SELECT u.id, s.id FROM users u JOIN skills s ON s.name = 'Maconnerie' WHERE u.email = 'abdul@lbgconnect.test';

INSERT INTO jobs (title, description, category, location, contract_type, status, salary_min, salary_max, posted_by_id)
SELECT 'Menuisier pour agencer une boutique de mode',
       'Agencement complet (cimaises, comptoir, dressing). Plan 3D disponible.',
       'Menuiserie',
       'Abidjan Marcory',
       'Projet',
       'Ouvert',
       400000,
       550000,
       (SELECT id FROM users WHERE email='contact@lbgconnect.test' LIMIT 1)
WHERE NOT EXISTS (
    SELECT 1 FROM jobs WHERE title = 'Menuisier pour agencer une boutique de mode' AND location = 'Abidjan Marcory'
);

INSERT INTO jobs (title, description, category, location, contract_type, status, salary_min, salary_max, posted_by_id)
SELECT 'Electricien chantier residence',
       'Reprise tableau electrique + luminaires LED. Controle conformite.',
       'Electricite',
       'Abidjan Cocody',
       'Chantier',
       'Ouvert',
       300000,
       450000,
       (SELECT id FROM users WHERE email='contact@lbgconnect.test' LIMIT 1)
WHERE NOT EXISTS (
    SELECT 1 FROM jobs WHERE title = 'Electricien chantier residence' AND location = 'Abidjan Cocody'
);

INSERT INTO jobs (title, description, category, location, contract_type, status, salary_min, salary_max, posted_by_id)
SELECT 'Assistante couturiere pour atelier createur',
       'Gestion des coupes, surjets et finitions. Experience tissus wax appreciee.',
       'Couture',
       'Grand-Bassam',
       'Stage',
       'Ouvert',
       100000,
       150000,
       (SELECT id FROM users WHERE email='amina@lbgconnect.test' LIMIT 1)
WHERE NOT EXISTS (
    SELECT 1 FROM jobs WHERE title = 'Assistante couturiere pour atelier createur' AND location = 'Grand-Bassam'
);

INSERT IGNORE INTO job_tags (job_id, tag)
SELECT j.id, 'placards' FROM jobs j
WHERE j.title = 'Menuisier pour agencer une boutique de mode'
ORDER BY j.id DESC LIMIT 1;
INSERT IGNORE INTO job_tags (job_id, tag)
SELECT j.id, 'LED' FROM jobs j
WHERE j.title = 'Electricien chantier residence'
ORDER BY j.id DESC LIMIT 1;
INSERT IGNORE INTO job_tags (job_id, tag)
SELECT j.id, 'wax' FROM jobs j
WHERE j.title = 'Assistante couturiere pour atelier createur'
ORDER BY j.id DESC LIMIT 1;

INSERT INTO job_applications (job_id, applicant_id, status, cover_letter, expected_rate)
SELECT
    (SELECT id FROM jobs WHERE title = 'Menuisier pour agencer une boutique de mode' ORDER BY id DESC LIMIT 1),
    (SELECT id FROM users WHERE email='adjoua@lbgconnect.test' LIMIT 1),
    'EN_COURS',
    'Equipe mobile avec atelier a Treichville.',
    480000
WHERE NOT EXISTS (
    SELECT 1 FROM job_applications
    WHERE job_id = (SELECT id FROM jobs WHERE title = 'Menuisier pour agencer une boutique de mode' ORDER BY id DESC LIMIT 1)
      AND applicant_id = (SELECT id FROM users WHERE email='adjoua@lbgconnect.test' LIMIT 1)
);

INSERT INTO job_applications (job_id, applicant_id, status, cover_letter, expected_rate)
SELECT
    (SELECT id FROM jobs WHERE title = 'Electricien chantier residence' ORDER BY id DESC LIMIT 1),
    (SELECT id FROM users WHERE email='moussa@lbgconnect.test' LIMIT 1),
    'ACCEPTEE',
    'Garantie 12 mois sur les travaux.',
    420000
WHERE NOT EXISTS (
    SELECT 1 FROM job_applications
    WHERE job_id = (SELECT id FROM jobs WHERE title = 'Electricien chantier residence' ORDER BY id DESC LIMIT 1)
      AND applicant_id = (SELECT id FROM users WHERE email='moussa@lbgconnect.test' LIMIT 1)
);

INSERT INTO job_applications (job_id, applicant_id, status, cover_letter, expected_rate)
SELECT
    (SELECT id FROM jobs WHERE title = 'Assistante couturiere pour atelier createur' ORDER BY id DESC LIMIT 1),
    (SELECT id FROM users WHERE email='abdul@lbgconnect.test' LIMIT 1),
    'EN_COURS',
    'Disponible week-end, motive pour progresser.',
    120000
WHERE NOT EXISTS (
    SELECT 1 FROM job_applications
    WHERE job_id = (SELECT id FROM jobs WHERE title = 'Assistante couturiere pour atelier createur' ORDER BY id DESC LIMIT 1)
      AND applicant_id = (SELECT id FROM users WHERE email='abdul@lbgconnect.test' LIMIT 1)
);

INSERT INTO messages (sender_id, recipient_id, subject, body, read_flag)
SELECT
    (SELECT id FROM users WHERE email='contact@lbgconnect.test' LIMIT 1),
    (SELECT id FROM users WHERE email='adjoua@lbgconnect.test' LIMIT 1),
    'Brief boutique mode',
    'Bonjour Adjoua, planifions une visite du local cette semaine.',
    FALSE
WHERE NOT EXISTS (
    SELECT 1 FROM messages
    WHERE subject = 'Brief boutique mode'
      AND sender_id = (SELECT id FROM users WHERE email='contact@lbgconnect.test' LIMIT 1)
      AND recipient_id = (SELECT id FROM users WHERE email='adjoua@lbgconnect.test' LIMIT 1)
);

INSERT INTO messages (sender_id, recipient_id, subject, body, read_flag)
SELECT
    (SELECT id FROM users WHERE email='adjoua@lbgconnect.test' LIMIT 1),
    (SELECT id FROM users WHERE email='contact@lbgconnect.test' LIMIT 1),
    'Re: Brief boutique mode',
    'Disponible jeudi 10h ou vendredi 15h.',
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM messages
    WHERE subject = 'Re: Brief boutique mode'
      AND sender_id = (SELECT id FROM users WHERE email='adjoua@lbgconnect.test' LIMIT 1)
      AND recipient_id = (SELECT id FROM users WHERE email='contact@lbgconnect.test' LIMIT 1)
);

INSERT INTO reviews (artisan_id, reviewer_id, rating, comment)
SELECT
    (SELECT id FROM users WHERE email='adjoua@lbgconnect.test' LIMIT 1),
    (SELECT id FROM users WHERE email='contact@lbgconnect.test' LIMIT 1),
    5,
    'Travail tres propre et respect des delais.'
WHERE NOT EXISTS (
    SELECT 1 FROM reviews
    WHERE artisan_id = (SELECT id FROM users WHERE email='adjoua@lbgconnect.test' LIMIT 1)
      AND reviewer_id = (SELECT id FROM users WHERE email='contact@lbgconnect.test' LIMIT 1)
);

INSERT INTO reviews (artisan_id, reviewer_id, rating, comment)
SELECT
    (SELECT id FROM users WHERE email='moussa@lbgconnect.test' LIMIT 1),
    (SELECT id FROM users WHERE email='contact@lbgconnect.test' LIMIT 1),
    5,
    'Equipe reactive, conforme au devis.'
WHERE NOT EXISTS (
    SELECT 1 FROM reviews
    WHERE artisan_id = (SELECT id FROM users WHERE email='moussa@lbgconnect.test' LIMIT 1)
      AND reviewer_id = (SELECT id FROM users WHERE email='contact@lbgconnect.test' LIMIT 1)
);
