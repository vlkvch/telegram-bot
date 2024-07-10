package org.example.tgbot.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.example.tgbot.entity.Exam;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends CrudRepository<Exam, Long> {

    @Query("select e from Exam e where e.date = ?1")
    Optional<Exam> findByDate(LocalDate date);

}
