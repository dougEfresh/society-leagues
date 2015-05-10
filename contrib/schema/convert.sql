REPLACE into leagues.users(user_id,login,first_name,last_name,email,status)
select
 player_id,player_login,first_name,last_name, player_login ,
  case when player_login like '%no_login%' then 'INACTIVE' else 'ACTIVE' end  status
from leagues_old.player o ;

update leagues.users set `passwd` = '$2a$10$FLsEHJa6l0/e44OdOfPzmOMveMuPJ/gNNfYQ6zWLVss.iwuR5VX5C';


replace into leagues.team(team_id,name)
select team_id,name from leagues_old.team;

replace into leagues.team(team_id,name) VALUES (32,'Unknown_32');
replace into leagues.team(team_id,name) VALUES (1000,'Unknown Team');
update leagues_old.match_schedule  set home_team_id = 1000 where home_team_id = 0;
update leagues_old.match_schedule  set visit_team_id = 1000 where visit_team_id = 0;

replace into leagues.season(season_id,name,start_date,end_date,division_id)
select m.season_id,concat(season_year,',',sn_name,',',league_game) as name,
 min(match_start_date) as start_date, max(match_start_date) as end_date,
  d.division_id
from 
 leagues_old.match_schedule m
 join leagues_old.season s on s.season_id=m.season_id
 join leagues_old.season_name sn on s.season_number=sn.sn_id
 join leagues_old.league l on m.league_id=l.league_id
 left join leagues.division d on d.division_type =
                            (case when concat(season_year,',',sn_name,',',league_game) like '%Thurs%' then 'EIGHT_BALL_THURSDAYS'
                             when concat(season_year,',',sn_name,',',league_game) like '%Wed%' then 'EIGHT_BALL_WEDNESDAYS'
                             when concat(season_year,',',sn_name,',',league_game) like '%9-ball%' then 'NINE_BALL_TUESDAYS'
                             when concat(season_year,',',sn_name,',',league_game) like '%Straight%' then 'STRAIGHT'
                             when concat(season_year,',',sn_name,',',league_game) like '%Mixed%' then 'EIGHT_BALL_MIXED_MONDAYS'
                               end
                            )

 group by m.season_id,m.league_id,d.division_id;

update leagues.season set start_date= '2014-01-14' where name = '2013,Summer,9-ball';
update leagues.season set start_date= '2013-05-08' where name = '2014,Winter,Mixed';
update leagues.season set season_status = 'ACTIVE' where name like '2015%';
update leagues.season set season_status = 'INACTIVE' where name not like '2015%';
update leagues.season set season_status = 'ACTIVE' where name like '%Challenge%';


/*
insert into leagues.division(division_type,league_type)
VALUES ('EIGHT_BALL_WEDNESDAYS','TEAM');
insert into leagues.division(division_type,league_type)
VALUES ('EIGHT_BALL_THURSDAYS','TEAM');
insert into leagues.division(division_type,league_type)
VALUES ('NINE_BALL_TUESDAYS','TEAM');
insert into leagues.division(division_type,league_type)
VALUES ('EIGHT_BALL_CHALLENGE','INDIVIDUAL');
insert into leagues.division(division_type,league_type)
VALUES ('NINE_BALL_CHALLENGE','INDIVIDUAL');
*/

replace into leagues.team_match (team_match_id,season_id,home_team_id,away_team_id,division_id,match_date)
select match_id,m.season_id,home_team_id,visit_team_id,d.division_id,match_start_date
 from leagues_old.match_schedule m
   left join leagues.season s on  m.season_id=s.season_id
   left join leagues.division d on d.division_type =
 (case
  when s.name like '%9-Ball%' then 'NINE_BALL_TUESDAYS'
  when s.name like '%Thurs%' then  'EIGHT_BALL_THURSDAYS'
  when s.name like '%Wed%' then 'EIGHT_BALL_WEDNESDAYS'
  when s.name like '%Mixed%' then 'EIGHT_BALL_MIXED_MONDAYS'
   else null
 end)
where m.league_id in (1,2,4,6) and home_team_id is not null
;

replace into leagues.team_result (team_result_id,team_match_id,home_racks,away_racks)
select home.match_id,home.match_id,home.racks as home_racks,away.racks as away_racks
 from 
(select m.match_id,m.home_team_id,
sum(games_won) racks  from 
  leagues_old.match_schedule m join leagues_old.result_ind rt on m.match_id=rt.match_id  and m.home_team_id = rt.team_id
 where league_id   in (1,2,4,6)
group by m.match_id,m.home_team_id) home 
join
   (select m.match_id,m.visit_team_id,
      sum(games_won) as racks  from
     leagues_old.match_schedule m join leagues_old.result_ind rt on m.match_id=rt.match_id  and m.visit_team_id = rt.team_id
   where league_id   in (1,2,4,6)
   group by m.match_id,m.visit_team_id
   ) away
on home.match_id=away.match_id
;

update leagues.team_match set match_date  = '1978-04-06' where match_date < '2000-01-01';

-- Team results with no player results

