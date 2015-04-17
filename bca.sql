select r.player_id,
CONCAT(min(first_name), ' ' ,min(last_name)) as name ,
 min(player_login) as email,
count(*)  as total,
sum(is_win) as wins, 
sum(games_lost) as lost
from result_ind r join player p on p.player_id = r.player_id 
 where match_id in
 (select match_id  from match_schedule where season_id=64) 
and r.player_id not in (218,224)
group by  r.player_id
order by CONCAT(min(first_name), ' ' ,min(last_name))
;
