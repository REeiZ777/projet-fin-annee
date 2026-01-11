package com.lbgconnect.repository;

import com.lbgconnect.model.Role;
import com.lbgconnect.model.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);

    @EntityGraph(attributePaths = {"skills"})
    List<UserAccount> findByRole(Role role);

    @EntityGraph(attributePaths = {"skills"})
    Optional<UserAccount> findWithSkillsById(Long id);

    @EntityGraph(attributePaths = {"skills"})
    @Query(value = "select distinct u from UserAccount u " +
            "left join u.skills s " +
            "where u.role = :role " +
            "and (:q is null or lower(u.fullName) like lower(concat('%', :q, '%')) or lower(u.headline) like lower(concat('%', :q, '%'))) " +
            "and (:location is null or lower(u.location) like lower(concat('%', :location, '%'))) " +
            "and (:skill is null or lower(s.name) like lower(concat('%', :skill, '%'))) " +
            "and (:verified is null or u.verified = :verified) " +
            "and (:ratingMin is null or u.rating >= :ratingMin)",
            countQuery = "select count(distinct u) from UserAccount u " +
                    "left join u.skills s " +
                    "where u.role = :role " +
                    "and (:q is null or lower(u.fullName) like lower(concat('%', :q, '%')) or lower(u.headline) like lower(concat('%', :q, '%'))) " +
                    "and (:location is null or lower(u.location) like lower(concat('%', :location, '%'))) " +
                    "and (:skill is null or lower(s.name) like lower(concat('%', :skill, '%'))) " +
                    "and (:verified is null or u.verified = :verified) " +
                    "and (:ratingMin is null or u.rating >= :ratingMin)")
    Page<UserAccount> searchByRole(@Param("role") Role role,
                                   @Param("q") String q,
                                   @Param("location") String location,
                                   @Param("skill") String skill,
                                   @Param("verified") Boolean verified,
                                   @Param("ratingMin") Double ratingMin,
                                   Pageable pageable);
}
