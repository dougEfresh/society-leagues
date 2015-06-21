package com.society.test.client;

import com.society.leagues.client.api.domain.Slot;
import com.society.leagues.dao.SlotDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public class TestSlots extends TestClientBase  {

    @Autowired SlotDao slotDao;

    @Test
    public void testSlots() throws Exception {
        /*
        List<Slot> slots = slotDao.get(LocalDateTime.now());
        assertNotNull(slots);
        assertFalse(slots.isEmpty());
        assertTrue(slots.size() > 1);
        for (Slot slot : slots) {
            assertNotNull(slot.getAllocated());
            assertTrue(slot.getAllocated() >= 0);
        }
        */
    }
}
