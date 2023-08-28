package com.example.education_center.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)

public abstract class TimeAuditable {
    @CreatedDate //auto gen date
    @Temporal(TemporalType.DATE)
    @Column(updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.DATE)
    private Date updatedAt;
}
