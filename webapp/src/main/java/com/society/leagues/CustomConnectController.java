package com.society.leagues;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.PostConstruct;

@Controller
public class CustomConnectController  {

    @Autowired ConnectController connectController;
    @Value("${application.url:http://leaguesdev.societybilliards.com}")
    private String appUrl;

    @PostConstruct
    public void init() {
        connectController.setApplicationUrl(appUrl);
    }

    @RequestMapping(value="/api/facebook", method= RequestMethod.POST)
    public RedirectView connect(@RequestParam String email, NativeWebRequest request) {
        RedirectView view =  connectController.connect("facebook", request);
        return view;
    }

	@RequestMapping(value="/api/facebook", method=RequestMethod.GET, params="code")
	public RedirectView oauth2Callback(NativeWebRequest request) {
        RedirectView view =  connectController.oauth2Callback("facebook", request);
        return view;
	}
}
