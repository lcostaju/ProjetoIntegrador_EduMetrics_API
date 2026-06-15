package br.edu.iftm.edumetrics.estruturas;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cache LRU (Least Recently Used) baseado em LinkedHashMap com accessOrder=true.
 * Quando a capacidade máxima é atingida, o elemento acessado há mais tempo é removido.
 *
 * @param <K> tipo da chave — deve ser imutável (String, Long, record)
 * @param <V> tipo do valor
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacidade;

    /**
     * @param capacidade número máximo de entradas antes de iniciar evicção
     */
    public LRUCache(int capacidade) {
        // accessOrder = true: cada get() move o no para o fim da lista
        // loadFactor = 0.75f: padrão JCF
        super(capacidade, 0.75f, /* accessOrder */ true);
        this.capacidade = capacidade;
    }

    /**
     * Invocado pelo LinkedHashMap após cada put().
     * Retorna true para remover a entrada mais antiga quando capacity é excedida.
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacidade;
    }

    /**
     * Fabrica thread-safe para uso em ambientes multi-thread (ex: requisições HTTP).
     */
    public static <K, V> Map<K, V> create(int capacidade) {
        return Collections.synchronizedMap(new LRUCache<>(capacidade));
    }

    public int getCapacidade() { return capacidade; }
}