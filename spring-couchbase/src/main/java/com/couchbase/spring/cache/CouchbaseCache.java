/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.couchbase.spring.cache;

import com.couchbase.client.CouchbaseClient;
import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.internal.OperationFuture;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

/**
 * Couchbase를 저장소로 사용하는 Spring framework 에서 제공하는 {@link Cache} 를 구현했습니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 3. 오전 11:30
 */
@Slf4j
@SuppressWarnings( "unchecked" )
public class CouchbaseCache implements Cache {

    private final String name;
    private final CouchbaseClient client;
    private final int expiration;

    CouchbaseCache(String name, CouchbaseClient client, int expiration) {
        if (log.isDebugEnabled())
            log.debug("CouchbaseCache를 생성합니다... name=[{}], expiration=[{}]", name, expiration);
        Assert.hasText(name, "cache name should not be empty.");
        this.name = name;
        this.client = client;
        this.expiration = expiration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return client;
    }

    @Override
    public ValueWrapper get(Object key) {
        assert key != null;
        if (log.isTraceEnabled()) log.trace("캐시를 조회합니다. key=[{}]", key);

        Object result = client.get(buildCacheKey(key));
        return (result != null) ? new SimpleValueWrapper(result) : null;
    }

    @Override
    public void put(Object key, Object value) {
        assert key != null;
        if (log.isTraceEnabled()) log.trace("캐시를 저장합니다... key=[{}], value=[{}]", key, value);

        OperationFuture<Boolean> setOp = client.set(buildCacheKey(key), expiration, value);
        boolean success = setOp.getStatus().isSuccess();

        if (log.isTraceEnabled()) log.trace("캐시 저장 결과 : [{}]", success);
    }

    @Override
    public void evict(Object key) {
        if (log.isTraceEnabled()) log.trace("evict cache item... key=[{}]", key);

        OperationFuture<Boolean> evictOp = client.delete(buildCacheKey(key));
        boolean isEvict = evictOp.getStatus().isSuccess();

        if (log.isTraceEnabled()) log.trace("evict cache item... key=[{}], result=[{}]", key, isEvict);
    }

    @Override
    public void clear() {
        if (log.isTraceEnabled()) log.trace("캐시 전체를 삭제합니다...  name=[{}]", name);
        try {
            // TODO : client.flush는 bucket의 모든 캐시 항목을 삭제합니다. 현재 name에 해당하는 것 말고도 모두!!! ==> 저장되는 캐시 키를 기록해 두었다가 삭제해야 한다.
            OperationFuture<Boolean> clearOp = client.flush();
            boolean isCleared = clearOp.getStatus().isSuccess();
            if (log.isDebugEnabled()) log.debug("캐시 전체가 삭제되었습니다. name=[{}], cleared=[{}]", name, isCleared);
        } catch (Exception e) {
            log.warn("캐시 전체를 Clear 하는데 실패했습니다.");
        }
    }

    /** 캐시 키를 캐시 저장소별로 구분하기 위해 */
    private String buildCacheKey(Object key) {
        return name + "-|-" + key;
    }
}
