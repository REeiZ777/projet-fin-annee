package com.lbgconnect.repository;

import com.lbgconnect.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    @EntityGraph(attributePaths = {"tags"})
    List<Job> findAll();

    Optional<Job> findByTitle(String title);

    List<Job> findTop3ByPostedByIdOrderByCreatedAtDesc(Long postedById);

    @EntityGraph(attributePaths = {"tags", "postedBy"})
    Optional<Job> findWithTagsById(Long id);

    @EntityGraph(attributePaths = {"tags"})
    @Query(value = "select distinct j from Job j " +
            "left join j.tags t " +
            "where (:q is null or lower(j.title) like lower(concat('%', :q, '%')) or lower(j.description) like lower(concat('%', :q, '%'))) " +
            "and (:location is null or lower(j.location) like lower(concat('%', :location, '%'))) " +
            "and (:category is null or lower(j.category) like lower(concat('%', :category, '%'))) " +
            "and (:contractType is null or lower(j.contractType) like lower(concat('%', :contractType, '%'))) " +
            "and (:salaryMin is null or j.salaryMin >= :salaryMin) " +
            "and (:salaryMax is null or j.salaryMax <= :salaryMax) " +
            "and (:tag is null or lower(t) like lower(concat('%', :tag, '%')))",
            countQuery = "select count(distinct j) from Job j " +
                    "left join j.tags t " +
                    "where (:q is null or lower(j.title) like lower(concat('%', :q, '%')) or lower(j.description) like lower(concat('%', :q, '%'))) " +
                    "and (:location is null or lower(j.location) like lower(concat('%', :location, '%'))) " +
                    "and (:category is null or lower(j.category) like lower(concat('%', :category, '%'))) " +
                    "and (:contractType is null or lower(j.contractType) like lower(concat('%', :contractType, '%'))) " +
                    "and (:salaryMin is null or j.salaryMin >= :salaryMin) " +
                    "and (:salaryMax is null or j.salaryMax <= :salaryMax) " +
                    "and (:tag is null or lower(t) like lower(concat('%', :tag, '%')))")
    Page<Job> searchJobs(@Param("q") String q,
                         @Param("location") String location,
                         @Param("category") String category,
                         @Param("contractType") String contractType,
                         @Param("salaryMin") BigDecimal salaryMin,
                         @Param("salaryMax") BigDecimal salaryMax,
                         @Param("tag") String tag,
                         Pageable pageable);
}
