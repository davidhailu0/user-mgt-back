package com.hajj.hajj.service;

import com.hajj.hajj.model.Logger;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.LoggerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoggerService {

    @Autowired
    LoggerRepo loggerRepo;

    public List<Logger> findAllLoggers(){
        return loggerRepo.findAll();
    }

    public void createNewLog(Users user,String request,String response){
        loggerRepo.save(new Logger(user,request,response, Timestamp.valueOf(LocalDateTime.now())));
    }
}
