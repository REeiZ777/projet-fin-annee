package com.lbgconnect;

import com.lbgconnect.model.*;
import com.lbgconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserAccountRepository userRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final ConversationMessageRepository messageRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "Lbgconnect123!";

    @Override
    public void run(String... args) {
        String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);

        Skill menuiserie = getOrCreateSkill("Menuiserie");
        Skill electricite = getOrCreateSkill("Electricite");
        Skill plomberie = getOrCreateSkill("Plomberie");
        Skill couture = getOrCreateSkill("Couture");
        Skill peinture = getOrCreateSkill("Peinture");
        Skill maconnerie = getOrCreateSkill("Maconnerie");

        UserAccount adjoua = upsertUser(UserAccount.builder()
                .fullName("Adjoua Kone")
                .email("adjoua@lbgconnect.test")
                .phone("+2250701010101")
                .role(Role.ARTISAN)
                .location("Abidjan")
                .headline("Menuisiere specialisee en mobilier sur mesure")
                .bio("10 ans d experience sur des chantiers premium. Delivery rapide et suivi client.")
                .avatarUrl("/assets/images/avatar-adjoua.png")
                .rating(4.8)
                .reviewsCount(42)
                .verified(true)
                .skills(List.of(menuiserie, peinture))
                .password(encodedPassword)
                .build());

        UserAccount amina = upsertUser(UserAccount.builder()
                .fullName("Amina Diallo")
                .email("amina@lbgconnect.test")
                .phone("+2250702020202")
                .role(Role.ARTISAN)
                .location("Grand-Bassam")
                .headline("Couturiere & styliste")
                .bio("Creations modernes avec tissus locaux. Prise de mesures a domicile.")
                .avatarUrl("/assets/images/avatar-amina.png")
                .rating(4.6)
                .reviewsCount(28)
                .verified(true)
                .skills(List.of(couture, peinture))
                .password(encodedPassword)
                .build());

        UserAccount moussa = upsertUser(UserAccount.builder()
                .fullName("Moussa Traore")
                .email("moussa@lbgconnect.test")
                .phone("+2250703030303")
                .role(Role.ARTISAN)
                .location("Abidjan")
                .headline("Electricien - depannage et chantiers tertiaires")
                .bio("Equipe de 3 personnes, interventions 7j/7. Agree CIE.")
                .avatarUrl("/assets/images/avatar-moussa.png")
                .rating(4.9)
                .reviewsCount(61)
                .verified(true)
                .skills(List.of(electricite, plomberie))
                .password(encodedPassword)
                .build());

        UserAccount abdul = upsertUser(UserAccount.builder()
                .fullName("Abdul Camara")
                .email("abdul@lbgconnect.test")
                .phone("+2250704040404")
                .role(Role.APPRENTI)
                .location("Yamoussoukro")
                .headline("Apprenti macon, disponible week-end")
                .bio("Capable de preparer chantiers, couler dalle et finitions simples.")
                .avatarUrl("/assets/images/avatar-moussa.png")
                .rating(4.1)
                .reviewsCount(9)
                .verified(false)
                .skills(List.of(maconnerie))
                .password(encodedPassword)
                .build());

        UserAccount employeur = upsertUser(UserAccount.builder()
                .fullName("Entreprise LBG")
                .email("contact@lbgconnect.test")
                .phone("+2250101010101")
                .role(Role.EMPLOYEUR)
                .location("Abidjan Plateau")
                .headline("Entreprise generale du batiment")
                .bio("Recherche artisans fiables pour chantiers d amenagement premium.")
                .avatarUrl("/assets/images/hero.png")
                .rating(4.5)
                .reviewsCount(14)
                .verified(true)
                .password(encodedPassword)
                .build());

        if (jobRepository.count() == 0) {
            Job job1 = jobRepository.save(Job.builder()
                    .title("Menuisier pour agencer une boutique de mode")
                    .description("Agencement complet (cimaises, comptoir, dressing). Plan 3D disponible. Delai 3 semaines.")
                    .category("Menuiserie")
                    .location("Abidjan Marcory")
                    .contractType("Projet")
                    .salaryMin(BigDecimal.valueOf(400_000))
                    .salaryMax(BigDecimal.valueOf(550_000))
                    .status("Ouvert")
                    .tags(List.of("placards", "bois massif", "retail"))
                    .postedBy(employeur)
                    .build());

            Job job2 = jobRepository.save(Job.builder()
                    .title("Electricien chantier residence")
                    .description("Reprise tableau electrique + luminaires LED. Controle conformite. Livrable: DOE.")
                    .category("Electricite")
                    .location("Abidjan Cocody")
                    .contractType("Chantier")
                    .salaryMin(BigDecimal.valueOf(300_000))
                    .salaryMax(BigDecimal.valueOf(450_000))
                    .status("Ouvert")
                    .tags(List.of("tableau", "LED", "norme CIE"))
                    .postedBy(employeur)
                    .build());

            Job job3 = jobRepository.save(Job.builder()
                    .title("Assistante couturiere pour atelier createur")
                    .description("Gestion des coupes, surjets et finitions. Experience tissus wax appreciee.")
                    .category("Couture")
                    .location("Grand-Bassam")
                    .contractType("Stage")
                    .salaryMin(BigDecimal.valueOf(100_000))
                    .salaryMax(BigDecimal.valueOf(150_000))
                    .status("Ouvert")
                    .tags(List.of("wax", "stage", "atelier"))
                    .postedBy(amina)
                    .build());

            if (applicationRepository.count() == 0) {
                applicationRepository.save(JobApplication.builder()
                        .job(job1)
                        .applicant(adjoua)
                        .status(ApplicationStatus.EN_COURS)
                        .coverLetter("Disponible en decembre, equipe mobile et atelier a Treichville.")
                        .expectedRate(BigDecimal.valueOf(480_000))
                        .build());

                applicationRepository.save(JobApplication.builder()
                        .job(job2)
                        .applicant(moussa)
                        .status(ApplicationStatus.ACCEPTEE)
                        .coverLetter("Equipe de 3 electriciens, garantie 12 mois sur les travaux.")
                        .expectedRate(BigDecimal.valueOf(420_000))
                        .build());

                applicationRepository.save(JobApplication.builder()
                        .job(job3)
                        .applicant(abdul)
                        .status(ApplicationStatus.EN_COURS)
                        .coverLetter("Je souhaite monter en competence sur la couture, disponible week-end.")
                        .expectedRate(BigDecimal.valueOf(120_000))
                        .build());
            }

            if (messageRepository.count() == 0) {
                messageRepository.save(ConversationMessage.builder()
                        .sender(employeur)
                        .recipient(adjoua)
                        .subject("Brief boutique mode")
                        .body("Bonjour Adjoua, pouvons-nous planifier une visite du local cette semaine ?")
                        .readFlag(false)
                        .build());

                messageRepository.save(ConversationMessage.builder()
                        .sender(adjoua)
                        .recipient(employeur)
                        .subject("Re: Brief boutique mode")
                        .body("Bonjour, je suis disponible jeudi 10h ou vendredi 15h.")
                        .readFlag(true)
                        .build());
            }

            if (reviewRepository.count() == 0) {
                reviewRepository.save(Review.builder()
                        .artisan(adjoua)
                        .reviewer(employeur)
                        .rating(5)
                        .comment("Travail tres propre et respect des delais.")
                        .build());

                reviewRepository.save(Review.builder()
                        .artisan(moussa)
                        .reviewer(employeur)
                        .rating(5)
                        .comment("Equipe reactive, conforme au devis.")
                        .build());
            }
        }
    }

    private Skill getOrCreateSkill(String name) {
        return skillRepository.findByName(name)
                .orElseGet(() -> skillRepository.save(Skill.builder().name(name).build()));
    }

    private UserAccount upsertUser(UserAccount seed) {
        return userRepository.findByEmail(seed.getEmail())
                .map(existing -> {
                    boolean changed = false;
                    if (existing.getPassword() == null || existing.getPassword().isBlank()) {
                        existing.setPassword(seed.getPassword());
                        changed = true;
                    }
                    if (existing.getAvatarUrl() == null || existing.getAvatarUrl().isBlank()) {
                        existing.setAvatarUrl(seed.getAvatarUrl());
                        changed = true;
                    }
                    if (existing.getHeadline() == null || existing.getHeadline().isBlank()) {
                        existing.setHeadline(seed.getHeadline());
                        changed = true;
                    }
                    if (changed) {
                        return userRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> userRepository.save(seed));
    }
}
