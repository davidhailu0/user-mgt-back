package com.hajj.hajj.service;

import com.hajj.hajj.DTO.ResetPasswordDTO;
import com.hajj.hajj.DTO.UsersRequest;
import com.hajj.hajj.model.*;
import com.hajj.hajj.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    private static final String CHARACTERS = "123456789";

    final static int PASSWORDLENGTH = 4;
    @Autowired
    UsersRepo userRepo;

    @Autowired
    UserUpdateRepo userUpdateRepo;

    @Autowired
    UserResetPasswordRepo userResetPasswordRepo;

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

    public List<UserDetail> allUnapprovedUsers(String branchName){
        if(branchName.equals("All")){
            return userDetailRepo.findUnapprovedData();
        }
        return userDetailRepo.findUnapprovedDataWithBranch(branchName);
    }

    public List<UserDetail> getUsersByBranch(String branchName){
        if(branchName.equals("All")){
            return userDetailRepo.findAll();
        }
        return userDetailRepo.findUsersByBranch(branchName);
    }


    @Transactional
    public Object saveUser(@NonNull UsersRequest userInfo,Users admin){
        Users newUser = new Users();
        Optional<Branch> userBranch = branchRepo.findById(userInfo.getBranch());
        Users checkUser = userRepo.findUsersByUsername(userInfo.getUsername()).orElse(null);
        int startIndex = userInfo.getPhoneNumber().trim().length()-9;
        String checkPhone = userInfo.getPhoneNumber().trim().substring(startIndex);
        List<UserDetail> checkUserDetail = userDetailRepo.findUserDetailByPhoneNumberContaining(checkPhone.trim());
        if(checkUser!=null){
            Map<String,Object> error = new HashMap<>();
            error.put("status",false);
            error.put("error","This username have been taken");
            return error;
        }
        if(!checkUserDetail.isEmpty()){
            Map<String,Object> error = new HashMap<>();
            error.put("status",false);
            error.put("error","This phone number is used");
            return error;
        }
        userBranch.ifPresent(newUser::setBranch);
        newUser.setUsername(userInfo.getUsername().trim().toLowerCase());
        LocalDateTime now = LocalDateTime.now();
        newUser.setCreated_at(Timestamp.valueOf(now));
        newUser.setUpdated_at(Timestamp.valueOf(now));
        newUser.setCreated_by(admin);
        newUser.setUpdated_by(admin);
        newUser.setRole(roleRepo.findById(userInfo.getRole()).get());
        newUser.setLocked(false);
        newUser.setStatus("Inactive");
        newUser.setConfirmPassword("First Time");
        try {
            newUser = userRepo.saveAndFlush(newUser);
            saveUserDetail(userInfo, newUser, admin);
            saveUserRole(userInfo.getRole(), newUser, admin);
            createUserBranch(userInfo, newUser, admin, now);
            UserUpdate userUpdate = new UserUpdate();
            userUpdate.setUser(newUser);
            userUpdate.setNewName(userInfo.getFullname());
            userUpdate.setNewPhoneNumber(userInfo.getPhoneNumber());
            userUpdate.setNewRole(roleRepo.findById(userInfo.getRole()).orElse(null));
            userUpdate.setNewBranch(branchRepo.findById(userInfo.getBranch()).orElse(null));
            userUpdate.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
            userUpdate.setCreated_by(admin);
            userUpdate.setNewAccountLockStatus(false);
            userUpdate.setNewStatus("Inactive");
            userUpdateRepo.save(userUpdate);
        }
        catch(Exception e){
            throw new RuntimeException("Creating User Failed");
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

    public Object approveUser(Long id,Users admin){
        Users user = userRepo.findById(id).orElse(null);
        Map<String,Object> error = new HashMap<>();
        error.put("success",false);
        if(user!=null){
            UserDetail userDetail = userDetailRepo.findUserDetailByUser(user).orElse(null);
            if(Objects.equals(user.getCreated_by().getId(), admin.getId())){
                error.put("error","You can not authorize user you created");
                return error;
            }
            if(userDetail!=null){
                if(user.getConfirmPassword()!=null&&!user.getConfirmPassword().equals("null")&&
                !user.getConfirmPassword().isBlank()&&!user.getConfirmPassword().isEmpty()){
                    generateDefaultPassword(user, userDetail,true,admin);
                }
                user.setStatus("Active");
                UserDetail userDetail1 = userDetailRepo.findUserDetailByUser(user).orElse(null);
                if(userDetail1!=null){
                    userDetail1.setChecker(admin);
                    userDetailRepo.save(userDetail1);
                }
                userRepo.save(user);
                Map<String,Object> success = new HashMap<>();
                UserUpdate userUpdate = new UserUpdate();
                userUpdate.setUser(user);
                userUpdate.setPreviousStatus("Inactive");
                userUpdate.setNewStatus("Active");
                userUpdate.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
                userUpdate.setCreated_by(admin);
                userUpdateRepo.save(userUpdate);
                success.put("success",true);
                success.put("message","User Approved Successfully");
                return success;
            }
            error.put("error","User Detail does not exist");
            return error;
        }
        error.put("error","User does not exist");
        return error;
    }

    public boolean resetPassword(String username,Users admin){
        Users user = userRepo.findUsersByUsername(username).orElse(null);
        if(user==null){
            return false;
        }
        UserDetail userDetail = userDetailRepo.findUserDetailByUser(user).get();
        generateDefaultPassword(user,userDetail,false,admin);
        return true;
    }

    private void generateDefaultPassword(Users user,UserDetail userDetail,boolean fromSignUp,Users admin){
        String rawPassword = generateRandomString(user.getUsername());
        String messageContent;
        if(fromSignUp){
            messageContent = String.format("Dear %s,\nHajj Payment Portal account has been successfully created. Your username is %s, and your password is %s.",userDetail.getFull_name(),user.getUsername(),rawPassword);
        }
        else{
            messageContent = String.format("Dear %s,\nYour password for Hajj Payment Portal account has been successfully reset. Your new password is %s.",userDetail.getFull_name(),rawPassword);
        }
        String password = passwordEncoder.encode(rawPassword);
        user.setPassword(password);
        user.setConfirmPassword(password);
        Message messageId = messageService.saveMessage(userDetail,messageContent,admin);
        if(!fromSignUp){
            UserResetPassword newResetPassword = new UserResetPassword();
            newResetPassword.setReset_user(user);
            newResetPassword.setMaker(admin);
            newResetPassword.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
            newResetPassword.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
            newResetPassword.setMessage(messageId);
            userResetPasswordRepo.save(newResetPassword);
        }
        userRepo.save(user);
    }

    public Object updateUser(Long id,UsersRequest updatedUser,Users admin){
        if(!userRepo.existsById(id)){
            Map<String,Object> error = new HashMap<>();
            error.put("success",false);
            error.put("error","This user does not exist");
            return error;
        }
        Users updateUser = userRepo.findById(id).get();
        UserDetail userDetail = userDetailRepo.findUserDetailByUser(updateUser).orElse(null);
        if(updatedUser.getPassword()!=null){
            updateUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            updateUser.setConfirmPassword(null);
        }
        // if(updateUser.getCreated_by().getId().equals(admin.getId())&&!updateUser.getRole().getId().equals(updatedUser.getRole())){
        //     updateUser.setStatus("Inactive");
        // }
        LocalDateTime now = LocalDateTime.now();
        int startIndex = updatedUser.getPhoneNumber().length()-9;
        String checkPhoneNumber = updatedUser.getPhoneNumber().substring(startIndex);
        List<UserDetail> checkPhone = userDetailRepo.findUserDetailByPhoneNumberContaining(checkPhoneNumber);
        if(!checkPhone.isEmpty()&&!Objects.equals(checkPhone.get(0).getId(), userDetail.getId())){
            Map<String,Object> error = new HashMap<>();
            error.put("status",false);
            error.put("error","This phone number is registered");
            return error;
        }
        updateUserUpdate(userDetail, updatedUser, admin);
        userDetail.setFull_name(updatedUser.getFullname());
        userDetail.setPhoneNumber(updatedUser.getPhoneNumber());
        userDetail.setUpdated_at(Timestamp.valueOf(now));
        userDetail.setUpdated_by(admin);
        userDetailRepo.save(userDetail);
        if(!Objects.equals(updateUser.getBranch().getId(), updatedUser.getBranch())){
            createUserBranch(updatedUser,updateUser,admin,now);
        }
        saveUserRole(updatedUser.getRole(),updateUser,admin);
        Optional<Branch> userBranch = branchRepo.findById(updatedUser.getBranch());
        userBranch.ifPresent(updateUser::setBranch);
        updateUser.setUpdated_by(admin);
        updateUser.setLocked(updatedUser.isAccountLocked());
        updateUser.setUpdated_at(Timestamp.valueOf(now));
        updateUser.setRole(roleRepo.findById(updatedUser.getRole()).get());
        userRepo.save(updateUser);
        Map<String,Object> success = new HashMap<>();
        success.put("success",true);
        return success;
    }

    private void createUserBranch(UsersRequest usersRequest, Users user, Users admin, LocalDateTime now){
        UserBranch userBranch = new UserBranch();
        userBranch.setUser(user);
        userBranch.setBranch(branchRepo.findById(usersRequest.getBranch()).orElse(null));
        userBranch.setAssigned_by(admin);
        userBranch.setUpdated_by(admin);
        userBranch.setCreated_at(Timestamp.valueOf(now));
        userBranch.setUpdated_at(Timestamp.valueOf(now));
        userBranchRepo.save(userBranch);
    }

    private void saveUserDetail(UsersRequest userInfo, Users newUser, Users admin){
        UserDetail newUserDetail = new UserDetail();
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.valueOf(now.toLocalDate());
        newUserDetail.setCreated_at(Timestamp.valueOf(now));
        newUserDetail.setUpdated_at(Timestamp.valueOf(now));
        newUserDetail.setFull_name(userInfo.getFullname().trim());
        newUserDetail.setUser(newUser);
        newUserDetail.setStart_date(date);
        newUserDetail.setStatus_changed_on(date);
        newUserDetail.setCreated_by(admin);
        newUserDetail.setUpdated_by(admin);
        newUserDetail.setPhoneNumber(userInfo.getPhoneNumber());
        userDetailRepo.save(newUserDetail);
    }
    private void saveUserRole(Long roleId, Users newUser, Users admin){
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
        userRoleRepo.save(userRole);
    }

    public static String generateRandomString(String username) {
        StringBuilder randomString = new StringBuilder(username.substring(0, 2).toLowerCase());
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < PASSWORDLENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            randomString.append(randomChar);
        }
        return randomString.toString();
    }

    public void updateUserUpdate(UserDetail userDetail,UsersRequest updatedUser,Users admin){
        UserUpdate userUpdate = new UserUpdate();
        userUpdate.setUser(userDetail.getUser());
        userUpdate.setCreated_by(admin);
        if(!userDetail.getFull_name().equals(updatedUser.getFullname())){
            userUpdate.setPreviousName(userDetail.getFull_name());
            userUpdate.setNewName(updatedUser.getFullname());
        }
        if(!userDetail.getPhoneNumber().equals(updatedUser.getPhoneNumber())){
            userUpdate.setPreviousPhoneNumber(userDetail.getPhoneNumber());
            userUpdate.setNewName(updatedUser.getFullname());
        }
        if(!userDetail.getUser().getBranch().getId().equals(updatedUser.getBranch())){
            userUpdate.setPreviousBranch(userDetail.getUser().getBranch());
            userUpdate.setNewBranch(branchRepo.findById(updatedUser.getBranch()).orElse(null));
        }
        if(!userDetail.getUser().getRole().getId().equals(updatedUser.getRole())){
            userUpdate.setPreviousRole(userDetail.getUser().getRole());
            userUpdate.setNewRole(roleRepo.findById(updatedUser.getRole()).orElse(null));
        }
        if(userDetail.getUser().isLocked()!=updatedUser.isAccountLocked()){
            userUpdate.setPreviousAccountLockStatus(userDetail.getUser().isLocked());
            userUpdate.setNewAccountLockStatus(updatedUser.isAccountLocked());
        }
        userUpdate.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
        userUpdateRepo.save(userUpdate);
    }
}
