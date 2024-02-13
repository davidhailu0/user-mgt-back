package com.hajj.hajj.repository;

import com.hajj.hajj.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message,Long> {
    @Query(value = "SELECT m from Message m where m.messageStatus = false and m.checkedBy is null")
    List<Message> findUnsentMessages();

    @Query(value = "SELECT m from Message m where m.messageStatus = false and m.checkedBy is not null")
    List<Message> findMessagesToBeSent();
}
