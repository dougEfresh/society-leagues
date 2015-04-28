insert leagues.player (season_id,division_id,user_id,team_id,handicap)
select distinct m.season_id,d.division_id,p.player_id,r.team_id,
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
JOIN leagues.division d on d.division_type = 'NINE_BALL_TUESDAYS' 
join season s on s.season_id=m.season_id 
join season_name sn on s.season_number=sn.sn_id
left join leagues.player lp on lp.user_id = p.player_id and lp.season_id=s.season_id and d.division_id=lp.division_id and lp.team_id =  r.team_id 
where m.league_id = 1 and lp.user_id is null;



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
JOIN leagues.division d on d.division_type = 'EIGHT_BALL_WEDNESDAYS' 
  join season s on s.season_id=m.season_id 
 join season_name sn on s.season_number=sn.sn_id
left join leagues.player lp on lp.user_id = p.player_id and lp.season_id=s.season_id and d.division_id=lp.division_id and lp.team_id =  r.team_id 
where m.league_id = 6 and lp.user_id is null;

;

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
JOIN leagues.division d on d.division_type = 'EIGHT_BALL_THURSDAYS' 
join season s on s.season_id=m.season_id 
join season_name sn on s.season_number=sn.sn_id
left join leagues.player lp on lp.user_id = p.player_id and lp.season_id=s.season_id and d.division_id=lp.division_id and lp.team_id =  r.team_id 
where m.league_id = 2 and lp.user_id is null;
