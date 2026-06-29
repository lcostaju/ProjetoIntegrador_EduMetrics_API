package br.edu.iftm.edumetrics.estruturas;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LRUCacheTest {
    @Test
    void deveArmazenarRecuperarERespeitarCapacidade() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);

        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.put("d", 4);

        assertEquals(3, cache.size());
        assertNull(cache.get("a"));
        assertEquals(4, cache.get("d"));
    }

    @Test
    void devePreservarElementoRecemAcessado() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);

        cache.put("a", 1);
        cache.put("b", 2);
        cache.get("a");
        cache.put("c", 3);

        assertEquals(1, cache.get("a"));
        assertNull(cache.get("b"));
    }

    @Test
    void fabricaThreadSafeNaoDeveExcederCapacidade() throws InterruptedException {
        Map<Integer, Integer> cache = LRUCache.create(3);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        for (int thread = 0; thread < 2; thread++) {
            executor.submit(() -> {
                for (int i = 0; i < 1_000; i++) {
                    cache.put(i, i);
                    cache.get(i);
                }
                latch.countDown();
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        executor.shutdown();
        assertTrue(cache.size() <= 3);
    }
}
