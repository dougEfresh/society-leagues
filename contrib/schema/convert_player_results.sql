insert into leagues.player_result(team_match_id,player_home_id,player_away_id,home_racks,away_racks)

select tm.team_match_id, 
       home.player_id as home_player_id, 
       away.player_id as away_player_id, 
       a.games_won as home_racks,
       b.games_won as away_racks
from 
(
select r.result_id,r.match_id,r.player_id,r.team_id, games_won,r.match_number
 from result_ind r
join match_schedule m on r.match_id=m.match_id and r.team_id=m.home_team_id
join leagues.team_match tm on tm.team_match_id=r.match_id
join leagues.division d on d.division_type = 'NINE_BALL_TUESDAYS'
join leagues.player p on p.user_id=r.player_id and p.season_id = m.season_id and r.team_id=p.team_id and d.division_id = p.division_id
 where m.league_id = 1  
) a
join 
(
select r.result_id,r.match_id,r.player_id,r.team_id, games_won,r.match_number
 from result_ind r
join match_schedule m on r.match_id=m.match_id and r.team_id=m.visit_team_id
join leagues.team_match tm on tm.team_match_id=r.match_id
join leagues.division d on d.division_type = 'NINE_BALL_TUESDAYS'
join leagues.player p on p.user_id=r.player_id and p.season_id = m.season_id and r.team_id=p.team_id and d.division_id = p.division_id
 where m.league_id = 1 
) b
on a.match_id=b.match_id and a.match_number=b.match_number
join leagues.team_match tm on tm.team_match_id=a.match_id

join leagues.player home on home.user_id = a.player_id and home.season_id=tm.season_id and tm.division_id=home.division_id and home.team_id = a.team_id 
join leagues.player away on away.user_id = b.player_id and away.season_id=tm.season_id and tm.division_id=away.division_id and away.team_id = b.team_id 
left join
 leagues.player_result lpr on lpr.team_match_id = tm.team_match_id and home.player_id = lpr.player_home_id and away.player_id = lpr.player_away_id and a.games_won = lpr.home_racks and b.games_won = lpr.away_racks

where lpr.team_match_id is null;


-----------------------------------------
insert into leagues.player_result(team_match_id,player_home_id,player_away_id,home_racks,away_racks)
select tm.team_match_id, 
       home.player_id as home_player_id, 
       away.player_id as away_player_id, 
       a.games_won as home_racks,
       b.games_won as away_racks
from 
(
select r.result_id,r.match_id,r.player_id,r.team_id, games_won,r.match_number
 from result_ind r
join match_schedule m on r.match_id=m.match_id and r.team_id=m.home_team_id
join leagues.team_match tm on tm.team_match_id=r.match_id
join leagues.division d on d.division_type = 'EIGHT_BALL_WEDNESDAYS'
join leagues.player p on p.user_id=r.player_id and p.season_id = m.season_id and r.team_id=p.team_id and d.division_id = p.division_id
 where m.league_id = 6
) a
join 
(
select r.result_id,r.match_id,r.player_id,r.team_id, games_won,r.match_number
 from result_ind r
join match_schedule m on r.match_id=m.match_id and r.team_id=m.visit_team_id
join leagues.team_match tm on tm.team_match_id=r.match_id
join leagues.division d on d.division_type = 'EIGHT_BALL_WEDNESDAYS'
join leagues.player p on p.user_id=r.player_id and p.season_id = m.season_id and r.team_id=p.team_id and d.division_id = p.division_id
 where m.league_id = 6
) b
on a.match_id=b.match_id and a.match_number=b.match_number
join leagues.team_match tm on tm.team_match_id=a.match_id
join leagues.player home on home.user_id = a.player_id and home.season_id=tm.season_id and tm.division_id=home.division_id and home.team_id = a.team_id 
join leagues.player away on away.user_id = b.player_id and away.season_id=tm.season_id and tm.division_id=away.division_id and away.team_id = b.team_id 
left join
 leagues.player_result lpr on lpr.team_match_id = tm.team_match_id and home.player_id = lpr.player_home_id and away.player_id = lpr.player_away_id and a.games_won = lpr.home_racks and b.games_won = lpr.away_racks
where lpr.team_match_id is null;
