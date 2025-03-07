package com.fizu.blogfiz.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfiguration {
    @Value("${spring.mail.host}")
    String host;

    @Value("${spring.mail.port}")
    int port;

    @Value("${spring.mail.password}")
    String password;

    @Value("${spring.mail.username}")
    String username;

    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setPassword(password);
        javaMailSender.setUsername(username);
        javaMailSender.setPort(port);
        javaMailSender.setHost(host);

        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return javaMailSender;
    }
}
