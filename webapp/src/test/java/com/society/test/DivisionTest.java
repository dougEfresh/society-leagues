package com.society.test;

import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.DivisionDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class DivisionTest extends TestBase {
    @Autowired DivisionDao api;

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void testCreate() {
        Division division = new Division(DivisionType.EIGHT_BALL_THURSDAYS);
        Division returned = api.create(division);
        assertNotNull(returned);
        assertEquals(division.getType(), returned.getType());
        assertNotNull(returned.getId());

        division.setType(null);
        assertNull(api.create(division));
    }

    @Test
    public void testDelete() {
        Division division = new Division(DivisionType.EIGHT_BALL_WEDNESDAYS);
        Division returned = api.create(division);
        assertTrue(api.delete(returned));
        assertFalse(api.delete(returned));
        returned = api.create(division);
        returned.setId(null);
        assertFalse(api.delete(returned));
    }

    @Test
    public void testModify() {
        Division division = new Division(DivisionType.NINE_BALL_TUESDAYS);
        Division returned = api.create(division);

        returned.setType(DivisionType.NINE_BALL_CHALLENGE);

        Division modified = api.modify(returned);
        assertNotNull(modified);
        assertEquals(modified.getType(), DivisionType.NINE_BALL_CHALLENGE);

        returned.setId(null);
        assertNull(api.modify(returned));
    }

}
