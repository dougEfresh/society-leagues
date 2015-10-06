package com.society.leagues.verify;


import com.society.leagues.client.api.domain.LeagueObject;


public interface DaoVerifier {

    boolean verify(LeagueObject obj);

}
