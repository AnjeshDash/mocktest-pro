package com.mocktestpro.module.test.repository;

import com.mocktestpro.module.test.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    List<Question> findBySection_IdOrderByOrderIndexAsc(UUID sectionId);

    long countBySection_Id(UUID sectionId);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.section.mockTest.id = :testId")
    long countByTestId(@Param("testId") UUID testId);

    @Query("SELECT DISTINCT q.topic FROM Question q WHERE q.section.mockTest.id = :testId AND q.topic IS NOT NULL")
    List<String> findDistinctTopicsByTestId(@Param("testId") UUID testId);
}