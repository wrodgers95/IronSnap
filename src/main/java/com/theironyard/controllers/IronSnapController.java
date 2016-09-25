package com.theironyard.controllers;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.services.PhotoRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.utilities.PasswordStorage;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@RestController
public class IronSnapController {
    @Autowired
    UserRepository users;

    @Autowired
    PhotoRepository photos;

    Server dbui = null;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(String username, String password, HttpSession session, HttpServletResponse response) throws Exception {
        User user = users.findFirstByName(username);
        if (user == null) {
            user = new User(username, PasswordStorage.createHash(password));
            users.save(user);
        }
        else if (!PasswordStorage.verifyPassword(password, user.getPassword())) {
            throw new Exception("Wrong password");
        }
        session.setAttribute("username", username);
        response.sendRedirect("/");
        return user;
    }

    @RequestMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response) throws IOException {
        session.invalidate();
        response.sendRedirect("/");
    }

    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public User getUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        return users.findFirstByName(username);
    }

    @RequestMapping("/upload")
    public Photo upload(

            HttpSession session,
            HttpServletResponse response,
            String receiver,
            MultipartFile photo,
            boolean photoPrivacy,
            Integer timer

    ) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }

        User senderUser = users.findFirstByName(username);
        User receiverUser = users.findFirstByName(receiver);

        if (receiverUser == null) {
            throw new Exception("Receiver name doesn't exist.");
        }

//        if (!photo.getContentType().endsWith("image")) {
//            throw new Exception("Only images are allowed.");
//        }

        File photoFile =
                File.createTempFile("photo",
                        photo.getOriginalFilename(),
                        new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        Photo p = new Photo();
        p.setSender(senderUser);
        p.setRecipient(receiverUser);
        p.setFileName(photoFile.getName());
        p.setPhotoPrivacy(photoPrivacy);
        p.setTimer(timer);
        photos.save(p);

        response.sendRedirect("/");

        return p;
    }
    @RequestMapping("/photos")
    public List<Photo> showPhotos(HttpSession session) throws Exception {

        String username = (String) session.getAttribute("username");

        if (username == null) {
            throw new Exception("Not logged in.");
        }

        User user = users.findFirstByName(username);

        for (int i = 0; i < photos.findByRecipient(user).size(); i++) {
            Photo p = photos.findByRecipient(user).get(i);

            int deletionTimer = photos.findOne(p.getId()).getTimer();

            String fileName = photos.findOne(p.getId()).getFileName();
            File f = new File("/Users/Blake/Code/NewCode/IronSnap/public" + fileName);
            Timer timer = new Timer();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    photos.delete(p.getId());
                    f.delete();
                }
            };

            long delay = deletionTimer * 1000;

            timer.schedule(task, delay);

        }

        return photos.findByRecipient(user);

    }


    @RequestMapping("/public-photos")
    public List<Photo> showPublicPhotos (HttpSession session) throws Exception {

        String username = (String) session.getAttribute("username");

        if (username == null) {
            throw new Exception("Not logged in.");
        }

        User user = users.findFirstByName(username);
        List<Photo> publicPhotos = photos.findByRecipient(user);

        for (int i = 0; i < publicPhotos.size(); i++) {
            Photo p = photos.findByRecipient(user).get(i);
            publicPhotos.removeIf(Photo::getPhotoPrivacy);
        }

        return publicPhotos;
    }


    @PostConstruct
    public void init() throws SQLException {
        dbui = Server.createWebServer().start();
    }

    @PreDestroy
    public void destroy() {
        dbui.stop();
    }
}
