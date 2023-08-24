package com.example.education_center.repos;

import com.example.education_center.entity.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CourseScheduleRepo extends JpaRepository<CourseSchedule,Integer> {

    @Query("select c FROM CourseSchedule c where c.teacher.id=:x and c.dow=:y and c.time_start >=:t and c.time_end<=:t and c.course.start_date>=:d and c.course.end_date<=:d and c.course.status=1")
    List<CourseSchedule> searchByTeacherAndDateTime(@Param("x") int teacher_id, @Param("y") int dow, @Param("t") Date time_start, @Param("d") Date start_date);

    @Query("select c from CourseSchedule c where c.dow=:y and c.time_start<=:t and c.time_end>=:t and c.room.id=:x and c.course.start_date>=:d and c.course.end_date<=:d and c.course.status=1")
    List<CourseSchedule> searchByRoom(@Param("y") int dow, @Param("t") Date time_start, @Param("x") int room_id, @Param("d") Date start_date);
}
