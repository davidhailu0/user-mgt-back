package com.hajj.hajj.service;

import com.hajj.hajj.model.UserDetail;
import com.hajj.hajj.model.UserResetPassword;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.UserDetailRepo;
import com.hajj.hajj.repository.UserResetPasswordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserResetDetail {

    @Autowired
    UserDetailRepo userDetailRepo;
    @Autowired
    UserService userService;

    @Autowired
    UserResetPasswordRepo userResetPasswordRepo;

    public List<UserResetPassword> getPasswordResetRequest(){
        return userResetPasswordRepo.findUserResetRequest();
    }

    public Object approveMessage(Long id, Users admin){
        UserResetPassword userResetPassword = userResetPasswordRepo.findById(id).orElse(null);
        if(userResetPassword==null){
            Map<String,Object> error = new HashMap<>();
            error.put("success",false);
            error.put("error","User password reset with this ID does not exist");
            return error;
        }

        if(userResetPassword.getMaker().getId().equals(admin.getId())){
            Map<String,Object> error = new HashMap<>();
            error.put("success",false);
            error.put("error","You can not approve this password reset");
            return error;
        }
        userResetPassword.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        userResetPassword.setChecker(admin);
        userResetPasswordRepo.save(userResetPassword);
        UserDetail userDetail = userDetailRepo.findUserDetailByUser(userResetPassword.getReset_user()).orElse(null);
        userService.generateDefaultPassword(userResetPassword.getReset_user(),userDetail,false,userResetPassword.getMaker(),admin);
        Map<String,Object> success = new HashMap<>();
        success.put("success",true);
        success.put("message","You have successfully approved the reset password");
        return success;

    }
}
