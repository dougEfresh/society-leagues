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

@Controller
public class LoginResource  {

    @Value("${client.api.endpoint}")
    String restUrl;
    static Logger logger = org.slf4j.LoggerFactory.getLogger(LoginResource.class);
    RestTemplate restTemplate = new RestTemplate();
    @Autowired UserApi userApi;
    @Value("${fb.endpoint}")
    String fbEndpoint;

    @PostConstruct
    public void init() {

    }

    @ModelAttribute
    public void setModels(Model model, HttpServletRequest request, ResponseFacade response) {
        model.addAttribute("fbEndpoint",fbEndpoint);
    }


    @RequestMapping(value = {"/help"}, method = RequestMethod.GET)
    public String help(HttpServletResponse response) {
        return "help";
    }

    @RequestMapping(value = {"/user/logout","/logout"}, method = RequestMethod.GET)
    public String logout(HttpServletResponse response) {
        response.setHeader("Set-Cookie","remember-me=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/");
        response.setHeader("Set-Cookie","JSESSIONID=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/");
        try {
            userApi.logout();
        } catch (Exception ignore ){}

        return "redirect:/login";
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String loginPage(@RequestParam(required = false, defaultValue = "false") boolean error, Model model, HttpServletRequest request) {
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


    @RequestMapping(value = {"/reset/{token}"}, method = RequestMethod.GET)
    public String resetTokenPage(@PathVariable String token, Model model, HttpServletRequest request) {
        model.addAttribute("token",token);
        return "reset-password";
    }

    @RequestMapping(value = {"/reset"}, method = RequestMethod.POST)
    public String reset(@RequestParam String token, @RequestParam String username, @RequestParam String password, Model model, HttpServletRequest request, ResponseFacade response) throws InterruptedException {
        Map<String,String> body = new HashMap<>();
        body.put("login",username);
        body.put("password", password);
        try {
            User u = userApi.resetPassword(token, body);
            if (u.equals(User.defaultUser())) {
                model.addAttribute("error","Error resetting password. Please try again");
                return "reset";
            }
            return loginPage(username,password,model,request,response);
        } catch (Exception e) {
            model.addAttribute("error","Error resetting password. Please try again");
            logger.error(e.getLocalizedMessage(),e);
            return "reset";
        }
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
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
