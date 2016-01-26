package com.society.leagues.test;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.*;
import com.society.leagues.resource.CacheResource;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Component
public class TestData {

    @Autowired List<MongoRepository> repos;
    @Autowired SeasonRepository seasonRepository;
    @Autowired TeamMembersRepository teamMembersRepo;
    @Autowired TeamRepository teamRepo;
    @Autowired SlotRepository slotRepo;
    @Autowired UserRepository userRepository;
    String defaultPassword = new BCryptPasswordEncoder().encode("abc123");

    @Autowired CacheResource cacheResource;

    @PostConstruct
    public void init() {
        setUp();
        cacheResource.refreshNonWeb();
    }

    public void setUp() {
        for (MongoRepository repo : repos) {
            repo.deleteAll();
        }
        Fairy fairy = Fairy.create();
        for(int i = 0 ; i< 605; i++) {
            Person person = fairy.person();
            User u = new User();
            u.setFirstName(person.firstName());
            u.setLastName(person.lastName());
            u.setLogin(String.format("%s.%s@example.com",u.getFirstName().toLowerCase(),u.getLastName().toLowerCase()));
            u.setEmail(u.getLogin());
            u.setPassword(defaultPassword);
            u.setRole(Role.PLAYER);
            u.setStatus(Status.ACTIVE);
            userRepository.save(u);
        }

        User u = new User();
        u.setFirstName("admin");
        u.setLastName("admin");
        u.setLogin(String.format("%s.%s@example.com",u.getFirstName().toLowerCase(),u.getLastName().toLowerCase()));
        u.setEmail(u.getLogin());
        u.setPassword(defaultPassword);
        u.setRole(Role.ADMIN);
        u.setStatus(Status.ACTIVE);
        userRepository.save(u);

        Season challenge = getSeason(Division.NINE_BALL_CHALLENGE, DayOfWeek.SUNDAY);
        Season scrmable = getSeason(Division.MIXED_MONDAYS_MIXED, DayOfWeek.MONDAY);
        Season tuesday = getSeason(Division.NINE_BALL_TUESDAYS, DayOfWeek.TUESDAY);
        Season wednesday = getSeason(Division.EIGHT_BALL_WEDNESDAYS, DayOfWeek.WEDNESDAY);
        Season thursday = getSeason(Division.EIGHT_BALL_THURSDAYS, DayOfWeek.THURSDAY);

        List<User> users = userRepository.findAll();

        for(int i = 0 ; i<10; i++) {
            TeamMembers tm = new TeamMembers();
            users.get(i).addHandicap(new HandicapSeason(Handicap.DPLUS,challenge));
            userRepository.save(users.get(i));
            tm.addMember(users.get(i));
            tm = teamMembersRepo.save(tm);
            Team t = new Team(challenge,String.format("team-%03d",(i + 1)));
            t.setMembers(tm);
            teamRepo.save(t);
        }

        createTeam(scrmable,users,100);
        createTeam(tuesday,users,200);
        createTeam(wednesday,users,300);
        createTeam(thursday,users,400);
    }

    private void createTeam(Season season, List<User> users , int start) {
        for(int i = 0 ; i<10; i++) {
            TeamMembers tm = new TeamMembers();
            int j = start + (i * 10);
            int max = j + 10;
            for (; j < max; j++) {
                User u =  users.get(j);
                System.out.println("Adding " + u.getName());
                u.addHandicap(new HandicapSeason(season.isNine() ? Handicap.DPLUS : Handicap.FOUR,season));
                userRepository.save(u);
                tm.addMember(u);
            }
            tm = teamMembersRepo.save(tm);
            Team t = new Team(season, String.format("team-%03d", (start + i) ));
            t.setMembers(tm);
            teamRepo.save(t);
        }
    }

    private Season getSeason(Division division, DayOfWeek day) {
        Season s = new Season();
        s.setRounds(10);
        s.setSeasonStatus(Status.ACTIVE);
        s.setDivision(division);
        LocalDate now = LocalDate.now();
        LocalDate startDate = null;
        int cnt = 0;
        do {
            startDate = now.minusDays(cnt);
            if (startDate.getDayOfWeek() == day) {
                s.setsDate(startDate);
            }
            cnt++;
        } while(cnt < 10 && s.getsDate() == null);
        return seasonRepository.save(s);
    }

}
