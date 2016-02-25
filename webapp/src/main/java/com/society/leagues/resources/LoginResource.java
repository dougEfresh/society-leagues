package com.society.leagues.resources;

import com.society.leagues.client.api.UserApi;
import com.society.leagues.client.api.domain.User;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;

//@Controller
public class LoginResource  {

    @Value("${client.api.endpoint}")
    String restUrl;
    static Logger logger = org.slf4j.LoggerFactory.getLogger(LoginResource.class);
    RestTemplate restTemplate = new RestTemplate();
    @Autowired UserApi userApi;

    @PostConstruct
    public void init() {

    }

    @RequestMapping(value = {"/help"}, method = RequestMethod.GET)
    public String help(HttpServletResponse response) {
        return "help";
    }

    //@RequestMapping(value = {"/user/logout","/logout"}, method = RequestMethod.GET)
    public String logout(HttpServletResponse response) {
        response.setHeader("Set-Cookie","remember-me=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/");
        response.setHeader("Set-Cookie","JSESSIONID=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/");
        try {
            userApi.logout();
        } catch (Exception ignore ){}

        return "redirect:/login";
    }

    //@RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String loginPage(@RequestParam(required = false, defaultValue = "false") boolean error, Model model, HttpServletRequest request) {
        return "login";
    }


   // @RequestMapping(value = {"/login"}, method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String loginPage(@RequestParam String username, @RequestParam String password, Model model, HttpServletRequest request, ResponseFacade response) throws InterruptedException {
        logger.info("Login Request for " + username);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("username", username.toLowerCase());
        body.add("password",password);
        body.add("springRememberMe", "true");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<?> httpEntity = new HttpEntity<Object>(body, headers);

        try {
            ResponseEntity<User> responseEntity = restTemplate.exchange(restUrl + "/api/authenticate", HttpMethod.POST, httpEntity, User.class);
            User u = responseEntity.getBody();
            headers.clear();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String cookie = "";
            for (String s : responseEntity.getHeaders().get("Set-Cookie")) {
                logger.info("Adding cookie: " + s);
                cookie += s.split(";")[0] + " ; ";
                headers.set("Cookie", s);
                response.addHeader("Set-Cookie", s);
                logger.info("Setting cookie: " + s);
            }
            logger.info("Got back " + u.getName());
            Thread.sleep(100);
            headers.set("Cookie", cookie.substring(0, cookie.length() - 1));
            httpEntity = new HttpEntity<>(headers);
            responseEntity = restTemplate.exchange(restUrl + "/api/user", HttpMethod.GET, httpEntity, User.class);
        } catch (Exception e) {
            model.addAttribute("error", "Error logging in. Please try again");
            return "login";
        }
        //for (String s : responseEntity.getHeaders().get("Set-Cookie")) {
          //  logger.info("cookie: " + s);
            //cookie += s.split(";")[0] +  ":";
            //response.addHeader("Set-Cookie",s);
        //}
        //logger.info("Got back "  + u.getName());
        return "redirect:/home";
    }

}
