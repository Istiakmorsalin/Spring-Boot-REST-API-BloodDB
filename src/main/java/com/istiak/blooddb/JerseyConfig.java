package com.istiak.blooddb;

import com.istiak.blooddb.controllers.*;


import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

import javax.ws.rs.ApplicationPath;


@Configuration
@Controller
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {

        register(MultiPartFeature.class);

        //Controllers



        register(UserController.class);
        register(UserPostController.class);



        //
    }
}