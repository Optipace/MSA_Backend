//package org.optipace.authService.entity;
//
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//import org.hibernate.annotations.CreationTimestamp;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "refresh_token")
//@Getter
//@Setter
//public class RefreshToken {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    public Long id;
//
//    @Column(name = "refresh_token")
//    public String refreshToken;
//
//    @CreationTimestamp
//    @Column(name= "created_datetime")
//    public LocalDateTime createdDatetime;
//
//    @Column(name= "expire_datetime")
//    public LocalDateTime expireDatetime;
//
//    @Column(name = "employee_id")
//    public Long employeeId;
//}
