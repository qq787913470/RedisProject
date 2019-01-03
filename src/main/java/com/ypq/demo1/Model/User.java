package com.ypq.demo1.Model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="usersq")
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class User implements Serializable {
    @Id
    private int id;
    private String name;
    private String sex;
    private int age;
}
