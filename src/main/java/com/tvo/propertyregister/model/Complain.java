package com.tvo.propertyregister.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Complain {

    private int id;
    private String subject;
    private String text;
    private int userId;

}
