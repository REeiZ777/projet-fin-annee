package com.lbgconnect.repository;

import com.lbgconnect.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    Optional<JobApplication> findByJobIdAndApplicantId(Long jobId, Long applicantId);
}
