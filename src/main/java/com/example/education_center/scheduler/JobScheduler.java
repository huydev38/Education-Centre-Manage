package com.example.education_center.scheduler;

import com.example.education_center.dto.UserDTO;
import com.example.education_center.email.EmailService;
import com.example.education_center.service.CourseService;
import com.example.education_center.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;

@Component
public class JobScheduler {
    @Autowired
    CourseService courseService;

    @Autowired
    EmailService emailService;

    @Autowired
    UserService userService;
    @Scheduled(cron = "0 0 0 * * *")
    public void checkCourseStatus(){
        courseService.checkStatus();
        courseService.checkOpen();
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void checkBirthday(){
        Date now = new Date();
        List<UserDTO> userDTOList = userService.findUserByBirthday(now);
        for(UserDTO u:userDTOList){
            emailService.sendBirthdayEmail(u.getUsername(), u.getEmail());
        }
    }

//    @Scheduled(fixedDelay = 5000)
//    public void sendTestEmail(){
//        emailService.sendEmail("chienbinhkendo@gmail.com", "test", "Testt");
//    }
}
