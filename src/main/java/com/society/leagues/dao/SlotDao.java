package com.society.leagues.dao;

import com.society.leagues.client.api.domain.Slot;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SlotDao extends Dao<Slot> {

    @Override
    public String getSql() {
        return "select * from slot";
    }

    @Override
    public RowMapper<Slot> getRowMapper() {
        return mapper;
    }

    final RowMapper<Slot> mapper = (rs, rowNum) -> {
        Slot slot = new Slot();
        slot.setId(rs.getInt("slot_id"));
        slot.setTime(rs.getTimestamp("slot_time").toLocalDateTime());
        slot.setAllocated(rs.getInt("allocated"));
        return slot;
    };

    final String CREATE = "INSERT INTO slot(slot_time,allocated) VALUES(?,?)";

    public List<Slot> get(LocalDateTime date) {
        List<Slot> slots = get().stream().filter(s-> s.getLocalDateTime().toLocalDate().isEqual(date.toLocalDate())).collect(Collectors.toList());
        if (slots.isEmpty()) {
            return create(date);
        }
        return slots;
    }

    public List<Slot> create(LocalDateTime date) {
        List<LocalDateTime> times = Slot.getDefault(date);
        List<Slot> slots = new ArrayList<>();

        for (final LocalDateTime time : times) {
            Slot s = new Slot();
            s.setTime(time);
            PreparedStatementCreator ps = con ->
            {
                PreparedStatement st = con.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
                st.setTimestamp(new Integer(1), Timestamp.valueOf(time));
                st.setInt(2,0);
                return st;
            };
            s = create(s, ps);
            slots.add(s);
        }
        return slots;
    }
}
