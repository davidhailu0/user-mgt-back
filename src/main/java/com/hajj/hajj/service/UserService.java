package com.hajj.hajj.service;

import com.hajj.hajj.DTO.ResetPasswordDTO;
import com.hajj.hajj.DTO.UserRoleRequest;
import com.hajj.hajj.DTO.UsersRequest;
import com.hajj.hajj.model.*;
import com.hajj.hajj.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService {

    private static final String CHARACTERS = "123456789";

    final static int PASSWORDLENGTH = 4;
    @Autowired
    UsersRepo userRepo;


    @Autowired
    UserDetailRepo userDetailRepo;

    @Autowired
    MessageService messageService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    BranchRepo branchRepo;

    @Autowired
    UserRoleRepo userRoleRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    UserBranchRepo userBranchRepo;

//     @PostConstruct
//     void addUser(){
//         userRepo.save(new Users("fedila","1234",passwordEncoder.encode("1234"),null,null,null,Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
//     }
    public List<UserDetail> getAllUsers(){
        return userDetailRepo.findAll();
    }

    public Optional<Users> getUserById(@NonNull Long id){
        if(!userRepo.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return userRepo.findById(id);
    }

    public List<UserDetail> getUsersByBranch(String branchName){
        return userDetailRepo.findUsersByBranch(branchName);
    }

    public Object saveUser(@NonNull UsersRequest userInfo,Users admin){
        Users newUser = new Users();
        Optional<Branch> userBranch = branchRepo.findById(userInfo.getBranch());
        Users checkUser = userRepo.findUsersByUsername(userInfo.getUsername()).orElse(null);
        UserDetail checkUserDetail = userDetailRepo.findUserDetailByPhoneNumberContaining(userInfo.getPhoneNumber()).orElse(null);
        if(checkUser!=null){
            Map<String,Object> error = new HashMap<>();
            error.put("status",false);
            error.put("error","This username have been taken");
            return error;
        }
        if(checkUserDetail!=null){
            Map<String,Object> error = new HashMap<>();
            error.put("status",false);
            error.put("error","This phone number is used");
            return error;
        }
        userBranch.ifPresent(newUser::setBranch);
        newUser.setUsername(userInfo.getUsername());
        newUser.setStatus(userInfo.getStatus());
        LocalDateTime now = LocalDateTime.now();
        newUser.setCreated_at(Timestamp.valueOf(now));
        newUser.setUpdated_at(Timestamp.valueOf(now));
        newUser.setCreated_by(admin);
        newUser.setUpdated_by(admin);
        newUser.setRole(roleRepo.findById(userInfo.getRole()).get());
        newUser.setStatus("Active");
        UserDetail userDetail = null;
        UserRole userRole = null;
        UserBranch userBranch1 = null;
        try {
            newUser = userRepo.saveAndFlush(newUser);
            userDetail = saveUserDetail(userInfo, newUser, admin);
            userRole = saveUserRole(userInfo.getRole(), newUser, admin);
            userBranch1 = createUserBranch(userInfo, newUser, admin, now);
            generateDefaultPassword(newUser, userDetail,true);
        }
        catch(Exception e){
            if(newUser.getId()!=null){
                userRepo.delete(newUser);
            }
            if(userDetail!=null) {
                userDetailRepo.delete(userDetail);
            }
            if(userRole!=null){
                userRoleRepo.delete(userRole);
            }
            if(userBranch1!=null){
                userBranchRepo.delete(userBranch1);
            }
        }
        return newUser;
    }

    public Object changePassword(Users admin,ResetPasswordDTO resetPasswordDTO){
        if(passwordEncoder.matches(resetPasswordDTO.getPreviousPassword(),admin.getPassword())){
            admin.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
            admin.setConfirmPassword("");
            userRepo.save(admin);
            Map<String,Object> success = new HashMap<>();
            success.put("success",true);
            success.put("message","Successfully Changed Password");
            return success;
        }
        Map<String,Object> error = new HashMap<>();
        error.put("success",false);
        error.put("message","Make sure the previous password is entered correctly or Ask for Password Reset");
        return error;
    }

    public boolean resetPassword(String username){
        Users user = userRepo.findUsersByUsername(username).orElse(null);
        if(user==null){
            return false;
        }
        UserDetail userDetail = userDetailRepo.findUserDetailByUser(user).get();
        generateDefaultPassword(user,userDetail,false);
        return true;
    }

    private void generateDefaultPassword(Users user,UserDetail userDetail,boolean signUp){
        String rawPassword = generateRandomString(user.getUsername());
        String messageContent;
        if(signUp){
            messageContent = String.format("Dear %s,\nHajj Payment Portal account has been successfully created. Your username is %s, and your password is %s",userDetail.getFull_name(),user.getUsername(),rawPassword);
        }
        else{
            messageContent = String.format("Dear %s,\nYour password for Hajj Payment Portal account has been successfully rested. Your new password is %s.",userDetail.getFull_name(),rawPassword);
        }
        String password = passwordEncoder.encode(rawPassword);
        user.setPassword(password);
        user.setConfirmPassword(password);
        messageService.saveMessage(userDetail.getPhoneNumber(),messageContent);
        userRepo.save(user);
    }

    public Object updateUser(Long id,UsersRequest updatedUser,Users admin){
        if(!userRepo.existsById(id)){
            Map<String,Object> error = new HashMap<>();
            error.put("status",false);
            error.put("error","This user does not exist");
            return error;
        }
        Users updateUser = userRepo.findById(id).get();
        if(updatedUser.getPassword()!=null){
            updateUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            updateUser.setConfirmPassword(null);
        }
        UserDetail userDetail = userDetailRepo.findUserDetailByUser(updateUser).orElse(null);
        LocalDateTime now = LocalDateTime.now();
        if(userDetail!=null){
            userDetail.setFull_name(updatedUser.getFullname());
            int index = updatedUser.getPhoneNumber().startsWith("09")?2:updatedUser.getPhoneNumber().startsWith("251")?3:4;
            String phoneNumberFormatted = updatedUser.getPhoneNumber().substring(index);
            UserDetail checkPhone = userDetailRepo.findUserDetailByPhoneNumberContaining(phoneNumberFormatted).orElse(null);
            if(checkPhone!=null&&!Objects.equals(checkPhone.getId(), userDetail.getId())){
                Map<String,Object> error = new HashMap<>();
                error.put("status",false);
                error.put("error","This phone number is registered");
                return error;
            }
            userDetail.setPhoneNumber(updatedUser.getPhoneNumber());
            userDetail.setStatus(updatedUser.getStatus());
            userDetail.setUpdated_at(Timestamp.valueOf(now));
            userDetail.setUpdated_by(admin);
            userDetailRepo.save(userDetail);
        }
        if(!Objects.equals(updateUser.getBranch().getId(), updatedUser.getBranch())){
            createUserBranch(updatedUser,updateUser,admin,now);
        }
        saveUserRole(updatedUser.getRole(),updateUser,admin);
        Optional<Branch> userBranch = branchRepo.findById(updatedUser.getBranch());
        userBranch.ifPresent(updateUser::setBranch);
        updateUser.setStatus(updatedUser.getStatus());
        updateUser.setUpdated_by(admin);
        updateUser.setUpdated_at(Timestamp.valueOf(now));
        updateUser.setRole(roleRepo.findById(updatedUser.getRole()).get());
        userRepo.save(updateUser);
        return userDetailRepo.findUserDetailByUser(updateUser).get();
    }

    private UserBranch createUserBranch(UsersRequest usersRequest,Users user,Users admin,LocalDateTime now){
        UserBranch userBranch = new UserBranch();
        userBranch.setUser(user);
        userBranch.setBranch(branchRepo.findById(usersRequest.getBranch()).orElse(null));
        userBranch.setAssigned_by(admin);
        userBranch.setUpdated_by(admin);
        userBranch.setCreated_at(Timestamp.valueOf(now));
        userBranch.setUpdated_at(Timestamp.valueOf(now));
        return userBranchRepo.save(userBranch);
    }

    private UserDetail saveUserDetail(UsersRequest userInfo,Users newUser,Users admin){
        UserDetail newUserDetail = new UserDetail();
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.valueOf(now.toLocalDate());
        newUserDetail.setCreated_at(Timestamp.valueOf(now));
        newUserDetail.setUpdated_at(Timestamp.valueOf(now));
        newUserDetail.setStatus(userInfo.getStatus());
        newUserDetail.setFull_name(userInfo.getFullname());
        newUserDetail.setUser(newUser);
        newUserDetail.setStart_date(date);
        newUserDetail.setStatus_changed_on(date);
        newUserDetail.setCreated_by(admin);
        newUserDetail.setUpdated_by(admin);
        newUserDetail.setPhoneNumber(userInfo.getPhoneNumber());
        if(userInfo.getStatus()!=null){
            newUserDetail.setStatus(userInfo.getStatus());
        }
        else{
            newUserDetail.setStatus("Active");
        }
        return userDetailRepo.save(newUserDetail);
    }
    private UserRole saveUserRole(Long roleId,Users newUser,Users admin){
        UserRole userRole = new UserRole();
        Optional<Role> assignedRole = roleRepo.findById(roleId);
        assignedRole.ifPresent(userRole::setRole);
        userRole.setAssigned_by(admin);
        userRole.setUpdated_by(admin);
        LocalDateTime now = LocalDateTime.now();
        userRole.setCreated_at(Timestamp.valueOf(now));
        userRole.setUpdated_at(Timestamp.valueOf(now));
        userRole.setStatus("Active");
        userRole.setUser(newUser);
        return userRoleRepo.save(userRole);
    }

    public static String generateRandomString(String username) {
        String randomString = username.substring(0,2);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < PASSWORDLENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            randomString = randomString+randomChar;
        }
        return randomString;
    }
}
