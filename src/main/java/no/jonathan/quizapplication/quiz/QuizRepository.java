package no.jonathan.quizapplication.quiz;

import org.springframework.data.repository.CrudRepository;

interface QuizRepository extends CrudRepository<Quiz, Long> {}
