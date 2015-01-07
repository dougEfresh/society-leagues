package com.society.test;


import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.admin.PlayerAdminApi;
import com.society.leagues.client.api.Role;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.exception.Unauthorized;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class, TestConfig.class, AdminTestConfig.class})
@IntegrationTest(value = {"server.port:0","daemon:true","debug:true"})
public class PlayerTest extends TestBase {

    @Test
    public void testCreate() {

    }
}
