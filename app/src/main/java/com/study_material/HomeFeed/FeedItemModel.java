package com.study_material.HomeFeed;

public class FeedItemModel {

    private final String subject;
    private final String branch;
    private final String course;
    private final String fileName;

    private final String title;
    private final String uri;
    private final String userName;
    private final String userUID;
    private final String docID;
    private boolean isFavourite;

    public FeedItemModel(boolean isFavourite, String uid, String subject, String branch, String course, String fileName, String title, String uri, String userName, String userUID) {
        this.subject = subject;
        this.branch = branch;
        this.course = course;
        this.fileName = fileName;
        this.title = title;
        this.uri = uri;
        this.userName = userName;
        this.userUID = userUID;
        this.docID = uid;
        this.isFavourite = isFavourite;

    }

    public String getSubject() {
        return subject;
    }

    public String getDocID() {
        return docID;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public boolean getIsFavourite() {
        return isFavourite;
    }

    public String getBranch() {
        return branch;
    }

    public String getCourse() {
        return course;
    }

    public String getFileName() {
        return fileName;
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
