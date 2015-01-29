package com.society.test;

import com.society.leagues.SchemaData;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class TestClientBase extends TestBase {

    @Autowired SchemaData schemaData;

    @Before
    public void setup() throws Exception {
        super.setup();
        schemaData.generateData();
    }
}
