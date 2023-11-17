package live.dgrr.domain.matching.service;

import live.dgrr.domain.matching.repository.MatchingRepository;
import live.dgrr.domain.waitingroom.entity.GameStartEvent;
import live.dgrr.global.config.redis.RankingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest
@ActiveProfiles("test")
public class MatchingServiceTest {
    private static final String WAITING_MEMBERS_KEY = "waitingQueue";
    private final String CUR_RANKING_KEY;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MatchingRepository matchingRepository;

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private TestEventListener testEventListener;

    @Autowired
    public MatchingServiceTest(RankingConfig rankingConfig) {
        this.CUR_RANKING_KEY = rankingConfig.getKey() + rankingConfig.getSeason();
    }

    @BeforeEach
    public void flushDatabase() {
        //레디스 flush
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
    @BeforeEach
    public void clearEvents() {
        testEventListener.clearLastEvent();
        testEventListener.clearEvents();
    }

    @Test
    public void 가까운_레이팅의_멤버가_없는_경우() {
        //given
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        zSetOps.add(WAITING_MEMBERS_KEY, "member1", 100);
        zSetOps.add(WAITING_MEMBERS_KEY, "member2", 200);
        zSetOps.add(WAITING_MEMBERS_KEY, "member3", 300);

        int range = 50;

        //when - 가까운 레이팅의 멤버가 없는 상황을 테스트
        String matchedMemberId = matchingRepository.findClosestRatingMember(300 + range*10); // 범위 밖의 레이팅.

        //then - 매치되지 않아야 한다.
        assertThat(matchedMemberId).isNull();
    }

    @Test
    public void 매칭중인_멤버가_없는_경우() {
        //when - 가까운 레이팅의 멤버가 없는 상황을 테스트
        String matchedMemberId = matchingRepository.findClosestRatingMember(1400);
        //then - 매치되지 않아야 한다.
        assertThat(matchedMemberId).isNull();
    }

    @Test
    public void 차이가_덜_나는_멤버와_매칭이_되는지() {
        //given
        redisTemplate.opsForZSet().add(CUR_RANKING_KEY, "1", 1000);
        redisTemplate.opsForZSet().add(WAITING_MEMBERS_KEY, "1",1000);
        redisTemplate.opsForZSet().add(CUR_RANKING_KEY, "2", 1001);
        redisTemplate.opsForZSet().add(WAITING_MEMBERS_KEY, "2",1001);
        redisTemplate.opsForZSet().add(CUR_RANKING_KEY, "3", 1002);

        //when
        matchingService.matchRandom("3");

        //then - rating 차이가 더 적은 member2와 매칭되어야 한다.
        GameStartEvent lastEvent = testEventListener.getLastEvent();
        assertThat(lastEvent).isNotNull();
        assertThat(lastEvent.memberOneId()).isEqualTo("2");
        assertThat(lastEvent.memberTwoId()).isEqualTo("3");

    }

    @Test
    public void 동시에_요청이_와도_잘_반영_되는지() {
        // given
        final int numberOfUsers = 50;
        for (int i = 1; i <= numberOfUsers; i++) {
            String memberId = String.valueOf(i);
            double score = 1000 + i;
            redisTemplate.opsForZSet().add(CUR_RANKING_KEY, memberId, score);
        }

        // when
        for (int i = 1; i <= numberOfUsers; i++) {
            matchingService.matchRandom(String.valueOf(i));
        }

        // then
        int eventsPublished = testEventListener.getEventsCount();

        // 매칭된 게임 *2 가 총 멤버 수인지 확인
        assertThat(eventsPublished * 2L).isEqualTo(50);


    }

    @Test
    public void 동시에_무작위_레이팅의_요청이_와도_잘_반영_되는지() {
        // given
        final int numberOfUsers = 50;
        Random random = new Random();
        for (int i = 1; i <= numberOfUsers; i++) {
            String memberId = String.valueOf(i);
            double score = 1000 + random.nextInt(501);; // 0과 500 사이의 무작위 정수를 더함
            redisTemplate.opsForZSet().add(CUR_RANKING_KEY, memberId, score);
        }

        // when
        for (int i = 1; i <= numberOfUsers; i++) {
            matchingService.matchRandom(String.valueOf(i));
        }

        // then
        int eventsPublished = testEventListener.getEventsCount();
        long waitingQueueSize = redisTemplate.opsForZSet().size(WAITING_MEMBERS_KEY);
        // 매칭된 게임 *2 + 대기중인 멤버 수가 총 멤버 수인지 확인
        assertThat(eventsPublished * 2L + waitingQueueSize).isEqualTo(50);

    }
}
