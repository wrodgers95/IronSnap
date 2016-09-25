package com.theironyard.entities;

import javax.persistence.*;

@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue
    Integer id;

    @ManyToOne
    User sender;

    @ManyToOne
    User recipient;

    @Column(nullable = false)
    String fileName;

    @Column(nullable = false)
    boolean photoPrivacy;

    @Column(nullable = false)
    int timer;

    public Photo() {
    }

    public Photo(User sender, User recipient, String fileName, boolean photoPrivacy, Integer timer) {
        this.sender = sender;
        this.recipient = recipient;
        this.fileName = fileName;
        this.photoPrivacy = photoPrivacy;
        this.timer = timer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(Integer timer) {
        this.timer = timer;
    }

    public boolean getPhotoPrivacy() {
        return photoPrivacy;
    }

    public void setPhotoPrivacy(boolean photoPrivacy) {
        this.photoPrivacy = photoPrivacy;
    }
}


