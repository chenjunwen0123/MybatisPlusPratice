package com.example.demo.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class User {
    @TableId(type= IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private Integer age;
    private String email;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
    @Version
    private Integer version;
    public User(String name,Integer age,String email){
        this.name = name;
        this.age = age;
        this.email = email;
    }
    public User(){}
}
