create or replace view team_home_result_vw
as
 select m.home_team_id as team_id ,m.season_id,
     case when home_racks > away_racks then 1 else 0 end as win, 
     case when home_racks < away_racks then 1 else 0 end as lost, 
     home_racks as racks_for,
     away_racks as racks_against 
      from team_result r join team_match m on r.team_match_id=m.team_match_id
      join division d on d.division_id=m.division_id where division_type not like '%CHALLENGE%' 

;

create or replace view team_away_result_vw
as
  select m.away_team_id as team_id,m.season_id,
     case when home_racks > away_racks then 0 else 1 end as win, 
     case when home_racks < away_racks then 0 else 1 end as lost, 
     home_racks as racks_for,
     away_racks as racks_against 
      from team_result r join team_match m on r.team_match_id=m.team_match_id
      join division d on d.division_id=m.division_id where division_type not like '%CHALLENGE%' 
;

create or replace view team_home_stats_vw
AS
select team_id,season_id,sum(win) as wins ,sum(lost) as loses, sum(racks_for) as racks_for ,sum(racks_against) as racks_against  from team_home_result_vw
 group by team_id,season_id;


create or replace view team_away_stats_vw
AS
select team_id,season_id,sum(win) as wins ,sum(lost) as loses, sum(racks_for) as racks_for ,sum(racks_against) as racks_against  from team_away_result_vw
 group by team_id,season_id;


create or replace view team_stats_vw
AS
select 
home.team_id,home.season_id,
sum(home.wins) + sum(away.wins) as wins,
sum(home.loses) + sum(away.loses) as loses,
sum(home.racks_for) + sum(away.racks_for) as racks_for,
sum(home.racks_against) + sum(away.racks_against) as racks_against
 from 
team_home_stats_vw home 
join team_away_stats_vw away 
on home.team_id=away.team_id and home.season_id=away.season_id 
group by  home.team_id,home.season_id,away.team_id,away.season_id
;
