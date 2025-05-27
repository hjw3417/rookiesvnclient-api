package com.rookiesvnclient.cache;

import com.rookiesvnclient.dto.SvnCredentials;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 인증된 SVN 사용자 정보를 메모리에 캐싱하는 서비스
 */
@Service
public class SvnCredentialCacheService {

    private final Map<String, SvnCredentials> cache = new ConcurrentHashMap<>();

    public void put(String username, String password) {
        cache.put(username, new SvnCredentials(username, password));
    }

    public Optional<SvnCredentials> get(String username) {
        return Optional.ofNullable(cache.get(username));
    }

    public void invalidate(String username) {
        cache.remove(username);
    }

    public void clearAll() {
        cache.clear();
    }
}
