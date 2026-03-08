package com.catalog.producer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProducerRepository extends JpaRepository<Producer, Long> {

    boolean existsByName(String name);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.producer.id = :producerId")
    int countProductsByProducerId(@Param("producerId") Long producerId);
}
