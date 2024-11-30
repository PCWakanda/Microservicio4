package org.example.microservicio4.repository;

import org.example.microservicio4.residuos.Vertedero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VertederoRepository extends JpaRepository<Vertedero, Long> {}