package com.society.leagues.dao;

import com.society.leagues.domain.SocietyDao;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AccountDao extends SocietyDao {
    final static String  ACCT_INFO = "SELECT *," +
                                "hc_8B.hcd_name hc8B, "+
                                "hc_8.hcd_name hc8, "+
                                "hc_10.hcd_name hc10,"+
                                "hc_m8.hcd_name hcm8, "+
                                "hc_s.hcd_name hcs, "+
                                "hc_9.hcd_name hc9, "+
                                "hc_m9.hcd_name hcm9 " +
                                "FROM player " +
                                "LEFT JOIN handicap_display hc_8B ON hc_8B.hcd_id=player.hc_8Begin " +
                                "LEFT JOIN handicap_display hc_8 ON hc_8.hcd_id=player.hc_8 " +
                                "LEFT JOIN handicap_display hc_9 ON hc_9.hcd_id=player.hc_9 " +
                                "LEFT JOIN handicap_display hc_s ON hc_s.hcd_id=player.hc_straight " +
                                "LEFT JOIN handicap_display hc_m9 ON hc_m9.hcd_id=player.hc_m9 " +
                                "LEFT JOIN handicap_display hc_m8 ON hc_m8.hcd_id=player.hc_m8 " +
                                "LEFT JOIN handicap_display hc_10 ON hc_10.hcd_id=player.hc_10 " +
                                "WHERE player_id=?";

    public Map<String,Object> getAcctInfo(int id) {
        return queryForMap(ACCT_INFO,id);
    }
}
