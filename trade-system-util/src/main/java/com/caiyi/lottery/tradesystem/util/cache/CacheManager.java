package com.caiyi.lottery.tradesystem.util.cache;

import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {
	private ConcurrentHashMap<String,Cache> mapCacheMatch = new ConcurrentHashMap<String,Cache>();//缓存对阵列表
	
	private static CacheManager cm;
	   
	public static synchronized CacheManager getCacheManager() {
		if (cm == null) {
			cm = new CacheManager();
		}
		return cm;
	}
	private CacheManager() {
		mapCacheMatch = new ConcurrentHashMap<String, Cache>();
	}
	
	public void clearCache() {
		mapCacheMatch.clear();
	}
	
	public ConcurrentHashMap<String, Cache> getMapCache() {
		return mapCacheMatch;
	}

	public void setMapCache(ConcurrentHashMap<String, Cache> mapCache) {
		this.mapCacheMatch = mapCache;
	}
	
	//判断缓存是否终止
    public static boolean cacheExpired(Cache cache) {
        if (null == cache) { //传入的缓存不存在
            return false;
        }
        long nowDt = System.currentTimeMillis(); //系统当前的毫秒数
        long cacheDt = cache.getTimeOut(); //缓存内的过期毫秒数
        if (cacheDt <= 0||cacheDt>nowDt) { //过期时间小于等于零时,或者过期时间大于当前时间时，则为FALSE
            return false;
        } else { //大于过期时间 即过期
            return true;
        }
    }

    public Cache removeCacheMatch(String type,String pid) {
		return mapCacheMatch.remove(type + "_" + pid);
	}
    
	public Cache getCacheMatch(String type,String pid) {
		Cache cache= mapCacheMatch.get(type + "_" + pid);
		if (cache==null){
			return null;
		}else{
			if (cacheExpired(cache)) { //调用判断是否终止方法
	            cache.setExpired(true);
	        }
	        return cache;	 
		}		
	}
	
	public void putCacheMatch(String type,String pid,Cache cache) {
		mapCacheMatch.put(type + "_" + pid, cache);
	}
	
	public void clearCacheMatch() {
		mapCacheMatch.clear();
	}
	
	
}