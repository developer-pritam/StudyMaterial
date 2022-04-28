package com.study_material.HomeFeed;

public class FeedItemModel {

    private final String subject;
    private final String branch;
    private final String course;
    private final String description;

    private final String title;
    private final String uri;
    private final String userName;
    private final String userUID;

    public FeedItemModel(String subject, String branch, String course, String description, String title, String uri, String userName, String userUID) {
        this.subject = subject;
        this.branch = branch;
        this.course = course;
        this.description = description;
        this.title = title;
        this.uri = uri;
        this.userName = userName;
        this.userUID = userUID;
    }

    public String getSubject() {
        return subject;
    }

    public String getBranch() {
        return branch;
    }

    public String getCourse() {
        return course;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getUri() {
        return uri;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserUID() {
        return userUID;
    }
}
