package com.musti.codereviewer.repository;

import com.musti.codereviewer.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByLanguage(String language);

    List<Review> findAllByOrderByCreatedAtDesc();
}
