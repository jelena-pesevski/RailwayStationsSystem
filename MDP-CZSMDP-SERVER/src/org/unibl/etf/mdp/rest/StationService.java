package org.unibl.etf.mdp.rest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.mdp.model.Station;
import org.unibl.etf.mdp.model.Timetable;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class StationService {
	
	private static String PROP_FILE="../resources/config.properties";
	private static String KEY_PREFIX_STATIONS;
	private static String REDIS_HOST;
	private static String STATION_SET_PREFIX;
	
	public ArrayList<Station> getStations(){
		ArrayList<Station> result=new ArrayList<>();
		Gson gson = new Gson();
		JedisPool pool = new JedisPool(REDIS_HOST);
		
		try (Jedis jedis = pool.getResource()) {
			Set<String> keys=jedis.keys(KEY_PREFIX_STATIONS+"*");
			for(String key : keys) {
				String json = jedis.get(key);
				Station s=gson.fromJson(json, Station.class);
				result.add(s);
			}
		}
		pool.close();
		return result;
	}
	
	public boolean add(Station station) {
		boolean result=true;
		JedisPool pool = new JedisPool(REDIS_HOST);
		
		try (Jedis jedis = pool.getResource()) {
			String newKey=KEY_PREFIX_STATIONS+station.getId();
			if(jedis.exists(newKey)) {
				result=false;
			}else {
				Gson gson = new Gson();
				String json = gson.toJson(station);
				jedis.set(newKey,json);
			}
		}
		pool.close();
		return result;
	}
	
	public boolean remove(int id) {
		//treba da nije moguce obrisati stanicu koja se nalazi u redovima voznje
		long result=0;
		
		JedisPool pool=new JedisPool(REDIS_HOST);
		try(Jedis jedis=pool.getResource()){
			Set<String> timetableIds=jedis.smembers(STATION_SET_PREFIX+id);
			if(timetableIds.isEmpty()) {
				//nema stanica koje tu prolaze
				result=jedis.del(KEY_PREFIX_STATIONS+id);
			}
		}
		pool.close();
		return result>0;
	}
	

	static {
		Properties prop=new Properties();
		try {
			InputStream is=StationService.class.getClassLoader().getResourceAsStream(PROP_FILE);
			prop.load(is);
			KEY_PREFIX_STATIONS = prop.getProperty("KEY_PREFIX_STATIONS");
			REDIS_HOST=prop.getProperty("REDIS_HOST");
			STATION_SET_PREFIX=prop.getProperty("STATION_SET_PREFIX");
			is.close();
		}catch(Exception e) {
			//e.printStackTrace();
			Logger.getLogger(StationService.class.getName()).log(Level.WARNING, e.toString());
		}
	}
	
	
}
