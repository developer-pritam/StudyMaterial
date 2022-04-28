package com.study_material.upload;

public class CourseModel {

    public String getName() {
        return name;
    }

private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CourseModel(String name, String id) {
        this.id = id;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;


    @Override
    public String toString() {
        return name;
    }
}
