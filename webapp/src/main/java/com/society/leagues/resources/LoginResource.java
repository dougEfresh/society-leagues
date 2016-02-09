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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginResource  {

    @Value("${rest.url}")
    String restUrl;
    static Logger logger = org.slf4j.LoggerFactory.getLogger(LoginResource.class);
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    @Autowired UserApi userApi;

    @PostConstruct
    public void init() {
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String loginPage(@RequestParam(required = false, defaultValue = "false") boolean error, HttpServletRequest request) {
        return "login";
    }

    @RequestMapping(value = {"/login/legacy"}, method = RequestMethod.GET)
    public String loginLegacyPage(@RequestParam(required = false, defaultValue = "false") boolean error, HttpServletRequest request) {
        return "login-legacy";
    }

    @RequestMapping(value = {"/login/reset"}, method = RequestMethod.GET)
    public String resetRequestPage(@RequestParam(required = false, defaultValue = "false") boolean error, HttpServletRequest request) {
        return "reset";
    }

    @RequestMapping(value = {"/login/reset"}, method = RequestMethod.POST)
    public String resetRequestSubmit(@RequestParam("username") String username, RequestFacade request) {
        User u = new User();
        u.setLogin(username);
        try {
            userApi.resetRequest(u);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(),e);
        }
        return "reset-link";
    }

    @RequestMapping(value = {"/login/reset/link"}, method = RequestMethod.GET)
    public String resetLinkPage(@RequestParam(required = false, defaultValue = "false") boolean error, HttpServletRequest request) {
        return "reset-link";
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String loginPage(@RequestParam String username, @RequestParam String password, Model model, HttpServletRequest request, ResponseFacade response) throws InterruptedException {
        logger.info("Login Request for " + username);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("username", username.toLowerCase());
        body.add("password",password);
        body.add("springRememberMe", "true");
        HttpEntity<?> httpEntity = new HttpEntity<Object>(body, headers);

        ResponseEntity<User> responseEntity = restTemplate.exchange(restUrl + "/api/authenticate", HttpMethod.POST, httpEntity, User.class);
        User u = responseEntity.getBody();
        for (String s : responseEntity.getHeaders().get("Set-Cookie")) {
            logger.info("Adding cookie: " + s);
            response.addHeader("Set-Cookie",s);
        }
        logger.info("Got back "  + u.getName());
        Thread.sleep(500);

        return "redirect:/app/home";
    }

}
