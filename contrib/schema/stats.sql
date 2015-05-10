create or replace view player_home_result_vw
as
 select r.player_home_id, p.user_id,
     case when home_racks > away_racks then 1 else 0 end as win, 
     case when home_racks < away_racks then 1 else 0 end as lost, 
     home_racks as racks_for,
     away_racks as racks_against,
     o.handicap as opponent_handicap
      from player_result r
      join player p on r.player_home_id=p.player_id 
      join player o on r.player_away_id = o.player_id
;

create or replace view player_away_result_vw
as
 select player_away_id, p.user_id,
     case when away_racks > home_racks then 1 else 0 end as win, 
     case when away_racks < home_racks then 1 else 0 end as lost, 
     away_racks as racks_for,
     home_racks as racks_against,
     o.handicap as opponent_handicap
      from player_result r
      join player p on r.player_away_id=p.player_id 
      join player o on r.player_home_id = o.player_id
;

create or replace view user_stats_home_vw
AS
select  user_id, opponent_handicap, count(*) as matches, sum(win) as wins, sum(lost) as loses, sum(racks_for) as racks_for ,sum(racks_against) as racks_against  from player_away_result_vw group by player_away_id,user_id,opponent_handicap;

;
create or replace view user_stats_handicap_home_vw
AS
select  p.user_id, opponent_handicap, season_id,count(*) as matches, sum(win) as wins, sum(lost) as loses, sum(racks_for) as racks_for ,sum(racks_against) as racks_against
  from player_home_result_vw home join player p on home.player_home_id=p.player_id
  group by player_home_id,p.user_id,opponent_handicap,p.season_id;

create or replace view user_stats_handicap_away_vw
AS
select  p.user_id, opponent_handicap, season_id,count(*) as matches, sum(win) as wins, sum(lost) as loses, sum(racks_for) as racks_for ,sum(racks_against) as racks_against
  from player_away_result_vw away join player p on away.player_away_id=p.player_id
  group by player_away_id,p.user_id,opponent_handicap,p.season_id;


create or replace view user_stats_union_handicap_vw as 
select * from user_stats_handicap_home_vw 
UNION ALL 
select * from user_stats_handicap_away_vw;

create or replace view user_stats_handicap_all_vw as
select user_id,
  opponent_handicap,
sum(matches) as matches,
sum(wins) as wins,
sum(loses) as loses,
sum(racks_for) as racks_for,
sum(racks_against) as racks_against
from user_stats_union_handicap_vw  group by
  user_id,opponent_handicap
;

create or replace view user_stats_handicap_season_vw as
select user_id,
  opponent_handicap,
  season_id,
sum(matches) as matches,
sum(wins) as wins,
sum(loses) as loses,
sum(racks_for) as racks_for,
sum(racks_against) as racks_against
from user_stats_union_handicap_vw  group by
  user_id,opponent_handicap,season_id
;

create or replace view user_stats_handicap_division_vw as
select user_id,
  opponent_handicap,
  s.division_id,
sum(matches) as matches,
sum(wins) as wins,
sum(loses) as loses,
sum(racks_for) as racks_for,
sum(racks_against) as racks_against
from user_stats_union_handicap_vw u join season s on s.season_id=u.season_id
  group by
  user_id,opponent_handicap,s.division_Id
;

create or replace view user_stats_handicap_challenge_vw as
select user_id,
  opponent_handicap,
sum(matches) as matches,
sum(wins) as wins,
sum(loses) as loses,
sum(racks_for) as racks_for,
sum(racks_against) as racks_against
from user_stats_union_handicap_vw u join season s on s.season_id=u.season_id
  where s.name like '%CHALLENGE%'
  group by  user_id,opponent_handicap
;

create or replace  view user_stats_home_vw as
select user_id,player_home_id,
count(*) as matches,
sum(win) as wins,
sum(lost) as loses,
sum(racks_for) as racks_for,
sum(racks_against) as racks_against
from player_home_result_vw r 
group by player_home_id,user_id
;

create or replace view user_stats_handicap_away_vw
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

create or replace view user_stats_vw as
select home.user_id,home.player_home_id as player_id,
       home.matches + away.matches as matches, 
       home.wins + away.wins  as wins , 
       home.loses + away.loses as loses,
       home.racks_for + away.racks_for as racks_for,
       home.racks_against + away.racks_against as racks_against
from user_stats_home_vw home 
join user_stats_away_vw away 
on home.user_id=away.user_id and home.player_home_id=away.player_away_id
;

create or replace view user_stats_all_vw as
  select user_id,sum(matches) as matches ,sum(wins) as wins ,sum(loses) as loses , sum(racks_for) as racks_for , sum(racks_against)  as racks_against from user_stats_vw group by user_id
 ;

create or replace view user_stats_season_vw as
  select s.user_id,season_id, sum(matches) as matches ,sum(wins) as wins ,sum(loses) as loses , sum(racks_for) as racks_for , sum(racks_against)  as racks_against from user_stats_vw s  join player p on p.player_id = s.player_id  group by s.user_id,season_id;

create or replace view user_stats_division_vw as
  select s.user_id,division_id, sum(matches) as matches ,sum(wins) as wins ,sum(loses) as loses , sum(racks_for) as racks_for , sum(racks_against)  as racks_against from user_stats_vw s  join player p on p.player_id = s.player_id  group by s.user_id,division_id;

create or replace view user_stats_challenge_vw as
  select s.user_id,sum(matches) as matches ,sum(wins) as wins ,sum(loses) as loses , sum(racks_for) as racks_for , sum(racks_against)  as racks_against from user_stats_vw s  join player p on p.player_id = s.player_id
    join division d on p.division_id = d.division_id where d.division_type like '%CHALLENGE%' group by s.user_id;
