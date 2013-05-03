package com.couchbase.spring.cache;

import com.couchbase.User;
import com.couchbase.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;

import static org.fest.assertions.Assertions.assertThat;

/**
 * com.couchbase.spring.cache.CouchbaseCacheTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 3. 오후 6:15
 */
@Slf4j
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = { CouchbaseCacheConfig.class } )
public class CouchbaseCacheTest {

    @Autowired
    CouchbaseCacheManager cacheManager;

    @Autowired
    UserRepository userRepository;

    @Test
    public void clearTest() {
        assertThat(cacheManager).isNotNull();
        Cache cache = cacheManager.getCache("user");
        Assertions.assertThat(cache).isNotNull();

        cache.clear();
    }

    @Test
    public void getUserFromCache() {

        StopWatch sw = new StopWatch("initial User");
        sw.start();
        User user1 = userRepository.getUser("debop", 100);
        sw.stop();
        log.debug("Elapsed time = [{}]", sw.getTotalTimeMillis());

        sw = new StopWatch("from Cache");
        sw.start();
        User user2 = userRepository.getUser("debop", 100);
        sw.stop();
        log.debug("Elapsed time = [{}]", sw.getTotalTimeMillis());

        assertThat(user2.getUsername()).isEqualTo(user1.getUsername());
        assertThat(user2.getPassword()).isEqualTo(user1.getPassword());
    }

    @Test
    public void componentConfigurationTest() {
        Assertions.assertThat(cacheManager).isNotNull();

        Cache cache = cacheManager.getCache("user");
        Assertions.assertThat(cache).isNotNull();
        Assertions.assertThat(userRepository).isNotNull();
    }
}
