alter view player_home_result_vw
as
 select r.player_home_id, p.user_id,
     case when home_racks > away_racks then 1 else 0 end as win, 
     case when home_racks < away_racks then 1 else 0 end as lost, 
     home_racks as racks_for,
     away_racks as racks_against 
      from player_result r
      join player p on r.player_home_id=p.player_id 

;

alter view player_away_result_vw
as
 select player_away_id, p.user_id,
     case when away_racks > home_racks then 1 else 0 end as win, 
     case when away_racks < home_racks then 1 else 0 end as lost, 
     away_racks as racks_for,
     home_racks as racks_against 
      from player_result r
      join player p on r.player_away_id=p.player_id 
;

create  view user_stats_home_vw
AS
select user_id,player_home_id,
count(*) as matches,
sum(win) as wins,
sum(lost) as loses,
sum(racks_for) as racks_for,
sum(racks_against) as racks_against
from player_home_result_vw r 
group by player_home_id,user_id
;

create  view user_stats_away_vw
AS
select user_id,player_away_id,
count(*) as matches,
sum(win) as wins,
sum(lost) as loses,
sum(racks_for) as racks_for,
sum(racks_against) as racks_against
from player_away_result_vw r 
group by player_away_id,user_id
;


create view user_stats_vw as 
select home.user_id,home.player_home_id,
       home.matches + away.matches as matches, 
       home.wins + away.wins  as wins , 
       home.loses + away.loses as loses,
       home.racks_for + away.racks_for as racks_for,
       home.racks_against + away.racks_against as racks_against
from user_stats_home_vw home 
join user_stats_away_vw away 
on home.user_id=away.user_id and home.player_home_id=away.player_away_id
;
