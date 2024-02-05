package com.hajj.hajj.repository;

import com.hajj.hajj.model.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoggerRepo extends JpaRepository<Logger,Long> {
}
