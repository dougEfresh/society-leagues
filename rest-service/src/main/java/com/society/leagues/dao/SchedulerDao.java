package com.society.leagues.dao;

import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class SchedulerDao extends Dao {

    public List<Map<String,Object>> getSchedule(int divisionId) {
        return queryForListMap(SCHEDULE_DIV, divisionId);
    }

    final static String SCHEDULE_DIV = "SELECT *, DATE_FORMAT(match_schedule.match_start_date, '%M %D') week " +
            "FROM match_schedule " +
            "WHERE match_schedule.division_id=? AND match_schedule.match_number>0 " +
            "GROUP BY match_schedule.match_number ";
}
