package com.cstorage.service;

import com.cstorage.security.RandomString;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cstorage.entity.User;
import com.cstorage.repository.UserRepository;

import java.io.UnsupportedEncodingException;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    public void register(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        if (userExists(user))
            return;
        String randomCode = RandomString.randomString(20);
        user.setCode(randomCode);
        userRepository.save(user);
        sendVerificationEmail(user, siteURL);
    }

    private void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "matveisharov1211@yandex.ru";
        String senderName = "CStorage";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you<br>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        String verifyURL = siteURL + "/verify?code=" + user.getCode();
        content = content.replace("[[name]]", user.getUsername());
        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

    }

    public boolean verify(String code) {
        User user = userRepository.findByCode(code);
        if (user == null || user.getEnabled() == 1)
            return false;
        user.setEnabled(1);
        userRepository.save(user);
        return true;
    }

    public boolean userExists(User user) {
        return (userRepository.findByEmail(user.getEmail()) != null) ||
                (userRepository.findByUsername(user.getUsername()) != null);
    }

    public User findByCode(String code) {
        return userRepository.findByCode(code);
    }

    public User findByUsername(String name) {
        return userRepository.findByUsername(name);
    }

}
