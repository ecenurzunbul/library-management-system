package com.library.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.library.model.TestEntity;

public interface TestEntityRepository extends JpaRepository<TestEntity, Long> {
}
