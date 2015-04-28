insert into leagues.player_result(team_match_id,player_home_id,player_away_id,home_racks,away_racks)

select tm.team_match_id, 
       a.player_id as home_player_id, 
       b.player_id as away_player_id, 
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
join leagues.division d on d.league_type = 'NINE_BALL_TUESDAYS' 
join leagues.player p on p.user_id=r.player_id and p.season_id = m.season_id and r.team_id=p.team_id and d.division_id = p.division_id
where m.league_id = 1
) b
on a.match_id=b.match_id and a.match_number=b.match_number
join leagues.team_match tm on tm.team_match_id=a.match_id

where p.player_id is null;


