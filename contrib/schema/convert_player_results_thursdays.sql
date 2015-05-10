replace into leagues.player_result(player_result_id,team_match_id,player_home_id,player_away_id,home_racks,away_racks)
select a.result_id,tm.team_match_id,
       home.player_id as home_player_id,
       away.player_id as away_player_id,
       a.games_won as home_racks,
       b.games_won as away_racks
from
(
select r.result_id,r.match_id,r.player_id,r.team_id, games_won,r.match_number
 from leagues_old.result_ind r
join leagues_old.match_schedule m on r.match_id=m.match_id and r.team_id=m.home_team_id
join leagues.team_match tm on tm.team_match_id=r.match_id
join leagues.division d on d.division_type = 'EIGHT_BALL_THURSDAYS'
join leagues.player p on p.user_id=r.player_id and p.season_id = m.season_id and r.team_id=p.team_id and d.division_id = p.division_id
 where m.league_id = 2 and m.season_id = %%SEASON_ID%%
) a
join
(
select r.result_id,r.match_id,r.player_id,r.team_id, games_won,r.match_number
 from leagues_old.result_ind r
join leagues_old.match_schedule m on r.match_id=m.match_id and r.team_id=m.visit_team_id
join leagues.team_match tm on tm.team_match_id=r.match_id
join leagues.division d on d.division_type = 'EIGHT_BALL_THURSDAYS'
join leagues.player p on p.user_id=r.player_id and p.season_id = m.season_id and r.team_id=p.team_id and d.division_id = p.division_id
 where m.league_id = 2 and m.season_id = %%SEASON_ID%%
) b
on a.match_id=b.match_id and a.match_number=b.match_number
join leagues.team_match tm on tm.team_match_id=a.match_id
join leagues.player home on home.user_id = a.player_id and home.season_id=tm.season_id and tm.division_id=home.division_id and home.team_id = a.team_id
join leagues.player away on away.user_id = b.player_id and away.season_id=tm.season_id and tm.division_id=away.division_id and away.team_id = b.team_id
;
