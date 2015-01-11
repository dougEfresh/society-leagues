package com.society.test;

import com.society.leagues.client.api.Role;
import org.springframework.jdbc.core.JdbcTemplate;

public class Schema {
    static boolean createdSchema;
    static boolean createdAccounts;
    static final String token = "create table token_cache(token varchar(64),\n" +
            "  player varchar(4096) NOT NULL ,\n" +
            "  created_date timestamp,\n" +
            "  PRIMARY KEY (token)\n" +
            ")\n";

    static final String league = "CREATE TABLE league (\n" +
            "  league_id int NOT NULL GENERATED ALWAYS AS IDENTITY,\n" +
            "  league_type varchar(32) NOT NULL,\n" +
            "  PRIMARY KEY (league_id)\n" +
            ")\n";

    static final String user = "CREATE TABLE users (\n" +
            "  user_id int NOT NULL GENERATED ALWAYS AS IDENTITY,\n" +
            "  login varchar(256) NOT NULL,\n" +
            "  role varchar(32) NOT NULL DEFAULT 'PLAYER',\n" +
            "  first_name varchar(128) DEFAULT NULL,\n" +
            "  last_name varchar(128) DEFAULT NULL,\n" +
            "  email varchar(256) DEFAULT NULL,\n" +
            "  passwd varchar(256) DEFAULT NULL,\n" +
            "  status int not null default 1,\n" +
            "  PRIMARY KEY (user_id),\n" +
            "  UNIQUE  (login)\n" +
            ")";
    static final String division = "CREATE TABLE division (\n" +
            "  division_id int NOT NULL GENERATED ALWAYS AS IDENTITY,\n" +
            "  league_id int NOT NULL CONSTRAINT DIV_L_FK REFERENCES league ON DELETE CASCADE ON UPDATE RESTRICT,\n" +
            "  type varchar(64) NOT NULL,\n" +
            "  PRIMARY KEY (division_id)\n" +
            ")\n";
    static final String season = " CREATE TABLE season (\n" +
            "  season_id    INT          NOT NULL GENERATED ALWAYS AS IDENTITY,\n" +
            "  division_id  INT          NOT NULL CONSTRAINT S_DIV_FK  REFERENCES division ON DELETE CASCADE ON UPDATE RESTRICT,\n" +
            "  name         VARCHAR(128) NOT NULL,\n" +
            "  start_date   DATE         NOT NULL,\n" +
            "  end_date     DATE,\n" +
            "  updated_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
            "  status       INT                   DEFAULT 1,\n" +
            "  PRIMARY KEY (season_id)\n" +
            " )\n";
    static final String team = "CREATE TABLE team (\n" +
            "  team_id int NOT NULL GENERATED ALWAYS AS IDENTITY,\n" +
            "  name varchar(128) NOT NULL,\n" +
            "  active boolean DEFAULT true,\n" +
            "  default_division_id int  NOT NULL CONSTRAINT TEAM_DIV_FK REFERENCES division ON DELETE CASCADE ON UPDATE RESTRICT,\n" +
            "  updated_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
            "  PRIMARY KEY (team_id)\n" +
            ")\n" +
            "\n";
    static final String player = "create table player  (\n" +
            " player_id int NOT NULL GENERATED ALWAYS AS IDENTITY,\n" +
            " season_id int NOT NULL,\n" +
            " user_id int NOT NULL,\n" +
            " team_id int NOT NULL,\n" +
            " PRIMARY KEY (player_id),\n" +
            "CONSTRAINT P_S_FK FOREIGN KEY (season_id) REFERENCES season(season_id) ON DELETE CASCADE ON UPDATE RESTRICT ,\n" +
            "CONSTRAINT P_U_FK FOREIGN KEY (user_id)   REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE RESTRICT ,\n" +
            "CONSTRAINT P_T_FK FOREIGN KEY (team_id)   REFERENCES team(team_id) ON DELETE CASCADE ON UPDATE RESTRICT\n" +
            ")\n";

    public static void createDb(JdbcTemplate jdbcTemplate) {
        if (createdSchema)
            return;

        jdbcTemplate.update(league);
        jdbcTemplate.update(token);
        jdbcTemplate.update(user);
        jdbcTemplate.update(division);
        jdbcTemplate.update(season);
        jdbcTemplate.update(team);
        jdbcTemplate.update(player);

        createdSchema= true;
    }

    public static void createAccounts(JdbcTemplate jdbcTemplate) {
        if (createdAccounts)
            return;

        jdbcTemplate.update("INSERT INTO users (login,role,passwd)" +
                        " VALUES (?,?,?)",
                TestBase.ADMIN_USER, Role.ADMIN.name(),TestBase.ADMIN_PASS);

        jdbcTemplate.update("INSERT INTO users (login,role,passwd) " +
                        "VALUES (?,?,?)",
                TestBase.NORMAL_USER, Role.PLAYER.name(), TestBase.NORMAL_PASS);

        createdAccounts = true;
    }
}
