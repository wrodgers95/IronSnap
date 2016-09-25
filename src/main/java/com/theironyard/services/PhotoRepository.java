package com.theironyard.services;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PhotoRepository extends CrudRepository<Photo, Integer> {

    List<Photo> findByRecipient(User recipient);
    List<Photo> findBySender(User sender);


//    @Query ("SELECT p FROM Photo p WHERE p.recipient = ?1 AND p.id = ?2")
//    Photo findByRecipientPhoto (String Receiver, int id);
//
//    @Query ("SELECT p FROM Photo p WHERE p.filename = ?1 AND p.id = ?2")
//    Photo findByPhotoFileName ();
}

