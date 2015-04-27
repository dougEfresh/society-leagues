insert into leagues.users
(user_id, login,        first_name,last_name,email,passwd,status)
select
 player_id,player_login,first_name,last_name,CONCAT(last_name,'@example.com') as email ,password,1 as status  from leagues_old.player  ;

insert into leagues.team(team_id,name)
select team_id,name from leagues_old.team;

insert into leagues.team(team_id,name)
VALUES (32,'Unknown');

insert into leagues.season(season_id,name,start_date,end_date)
select m.season_id,concat(season_year,',',sn_name,',',league_game) as name,
 min(match_start_date) as start_date, max(match_start_date) as end_date
from 
 match_schedule m
 join season s on s.season_id=m.season_id 
 join season_name sn on s.season_number=sn.sn_id
 join league l on m.league_id=l.league_id 
where season_year != 2015
group by m.season_id,m.league_id;


insert into leagues.division(league_type,division_type)
VALUES ('EIGHT_BALL_WEDNESDAYS','TEAM');
insert into leagues.division(league_type,division_type)
VALUES ('EIGHT_BALL_THURSDAYS','TEAM');
insert into leagues.division(league_type,division_type)
VALUES ('NINE_BALL_TUESDAYS','TEAM');
insert into leagues.division(league_type,division_type)
VALUES ('EIGHT_BALL_CHALLENGE','INDIVIDUAL');
insert into leagues.division(league_type,division_type)
VALUES ('NINE_BALL_CHALLENGE','INDIVIDUAL');

insert into leagues.team_match (team_match_id,season_id,home_team_id,away_team_id,division_id,match_date)
select match_id,m.season_id,home_team_id,visit_team_id,d.division_id,match_start_date
 from match_schedule m join leagues.season s on  m.season_id=s.season_id
 join leagues.division d on d.league_type =
 (case 
 when s.name like '%9-Ball%' then 'NINE_BALL_TUESDAYS' 
 when s.name like '%Thurs%' then  'EIGHT_BALL_THURSDAYS' 
 when s.name like '%Wed%' then 'EIGHT_BALL_WEDNESDAYS' 
end)
where s.name  not like '%Mixed%' and s.name not like '%2015%' and s.name not like '%Straigh%';

insert into leagues.team_result (team_match_id,home_racks,away_racks)
select home.match_id,home.racks as home_racks,away.racks as away_racks
 from 
(select m.match_id,m.home_team_id,
sum(games_won) racks  from 
  match_schedule m join result_ind rt on m.match_id=rt.match_id  and m.home_team_id = rt.team_id
 where league_id in (1,2,6)
group by m.match_id,m.home_team_id) home 
join (select m.match_id,m.visit_team_id,
sum(games_won) as racks  from 
  match_schedule m join result_ind rt on m.match_id=rt.match_id  and m.visit_team_id = rt.team_id
 where league_id in (1,2,6)
group by m.match_id,m.visit_team_id) away 
on home.match_id=away.match_id 
join leagues.team_match tm on home.match_id=tm.team_match_id
;

insert leagues.player (season_id,division_id,user_id,team_id,handicap)
select distinct p.player_id,m.season_id,d.division_id,p.player_id,r.team_id,
(case when hc_9.hcd_name = '2' then '1'
      when hc_9.hcd_name = '3' then '3'
      when hc_9.hcd_name = '4' then '3' 
      when hc_9.hcd_name = '5' then '4'
when hc_9.hcd_name = '6' then '5' 
when hc_9.hcd_name = '7' then '6' 
when hc_9.hcd_name = '8' then '7' 
when hc_9.hcd_name = '9' then '8' 
when hc_9.hcd_name = '10' then '9'
when hc_9.hcd_name = 'D' then '17'
when hc_9.hcd_name = 'D+' then '18'
when hc_9.hcd_name = 'C' then '19'
when hc_9.hcd_name = 'C+' then '20'
when hc_9.hcd_name = 'B' then '21'
when hc_9.hcd_name = 'B+' then '22'
when hc_9.hcd_name = 'A' then '23'
when hc_9.hcd_name = 'A+' then '24'
when hc_9.hcd_name = 'O' then '25'
when hc_9.hcd_name = 'O+' then '26'
when hc_9.hcd_name = 'P' then '27	'
end) as hcd
from result_ind r  join match_schedule m on m.match_id=r.match_id
join player p on r.player_id = p.player_id
JOIN handicap_display hc_9 ON hc_9.hcd_id=p.hc_9
JOIN leagues.division d on d.league_type = 'NINE_BALL_TUESDAYS' 
  join season s on s.season_id=m.season_id 
 join season_name sn on s.season_number=sn.sn_id
where m.league_id = 1 and season_year != 2015;


insert leagues.player (season_id,division_id,user_id,team_id,handicap)
select distinct m.season_id,d.division_id,p.player_id,r.team_id,
(case when hc_8B.hcd_name = '2' then '1'
      when hc_8B.hcd_name = '3' then '3'
      when hc_8B.hcd_name = '4' then '3' 
      when hc_8B.hcd_name = '5' then '4'
when hc_8B.hcd_name = '6' then '5' 
when hc_8B.hcd_name = '7' then '6' 
when hc_8B.hcd_name = '8' then '7' 
when hc_8B.hcd_name = '9' then '8' 
when hc_8B.hcd_name = '10' then '9'
when hc_8B.hcd_name = 'D' then '17'
when hc_8B.hcd_name = 'D+' then '18'
when hc_8B.hcd_name = 'C' then '19'
when hc_8B.hcd_name = 'C+' then '20'
when hc_8B.hcd_name = 'B' then '21'
when hc_8B.hcd_name = 'B+' then '22'
when hc_8B.hcd_name = 'A' then '23'
when hc_8B.hcd_name = 'A+' then '24'
when hc_8B.hcd_name = 'O' then '25'
when hc_8B.hcd_name = 'O+' then '26'
when hc_8B.hcd_name = 'P' then '27	'
end) as hcd
from result_ind r  join match_schedule m on m.match_id=r.match_id
join player p on r.player_id = p.player_id
JOIN handicap_display hc_8B ON hc_8B.hcd_id=p.hc_8Begin
JOIN leagues.division d on d.league_type = 'EIGHT_BALL_WEDNESDAYS' 
  join season s on s.season_id=m.season_id 
 join season_name sn on s.season_number=sn.sn_id
where m.league_id = 6 and season_year != 2015;


select home.match_id,home.racks as home_racks,away.racks as away_racks
 from 
(select m.match_id,m.home_team_id,
sum(games_won) racks  from 
  match_schedule m join result_ind rt on m.match_id=rt.match_id  and m.home_team_id = rt.team_id
 where league_id in (1,2,6)
group by m.match_id,m.home_team_id) home 
join (select m.match_id,m.visit_team_id,
sum(games_won) as racks  from 
  match_schedule m join result_ind rt on m.match_id=rt.match_id  and m.visit_team_id = rt.team_id
 where league_id in (1,2,6)
group by m.match_id,m.visit_team_id) away 
on home.match_id=away.match_id 
join leagues.team_match tm on home.match_id=tm.team_match_id
;


