package org.unibl.etf.mdp.rest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.mdp.model.Arrival;
import org.unibl.etf.mdp.model.Station;
import org.unibl.etf.mdp.model.Timetable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TimetableService {

	private static String PROP_FILE="../resources/config.properties";
	private static String KEY_PREFIX_TIMETABLES;
	private static String REDIS_HOST;
	private static String STATION_SET_PREFIX;
	
	public ArrayList<Timetable> getTimetables(){
		ArrayList<Timetable> result=new ArrayList<>();
		Gson gson = new Gson();
		JedisPool pool = new JedisPool(REDIS_HOST);
		
		try (Jedis jedis = pool.getResource()) {
			Set<String> keys=jedis.keys(KEY_PREFIX_TIMETABLES+"*");
			for(String key : keys) {
				String json = jedis.get(key);
				Timetable t=gson.fromJson(json, Timetable.class);
				result.add(t);
			}
		}
		pool.close();
		return result;
	}
	
	public boolean add(Timetable timetable) {
		boolean result=true;
		
		JedisPool pool = new JedisPool(REDIS_HOST);
		try(Jedis jedis=pool.getResource()){
			String newKey=KEY_PREFIX_TIMETABLES+timetable.getId();
			if(jedis.exists(newKey)) {
				result=false;
			}else {
				Gson gson=new Gson();
				String json = gson.toJson(timetable);
				jedis.set(newKey,json);
				
				//sada za svaku stanicu iz reda voznje dodati red voznje u set
				String id=String.valueOf(timetable.getId());
				for(Arrival a: timetable.getArrivals()) {
					Station s=a.getStation();
					jedis.sadd(STATION_SET_PREFIX+s.getId(), id);
				}
			}	
		}
		pool.close();
		return result;
	}
	
	
	public Timetable addTrainPassing(int timetableId, int stationId, String time) {
		Timetable t=null;
		JedisPool pool=new JedisPool(REDIS_HOST);
		boolean existsArrival=false;
		try(Jedis jedis=pool.getResource()){
			String key=KEY_PREFIX_TIMETABLES+timetableId;
			if(!jedis.exists(key)) {
				return t;
			}
			String json = jedis.get(key);
			Gson gson=new Gson();
		    t=gson.fromJson(json, Timetable.class);
		    
			for(Arrival a: t.getArrivals()) {
				if(a.getStation().getId()==stationId) {
					a.setRealArrivalTime(time);
					existsArrival=true;
					break;
				}
			}
			if(existsArrival) {
				//upisujemo nazad u bazu
				String newJson=gson.toJson(t);
				jedis.set(key, newJson);
			}
		}
		pool.close();
		return t;
	}
	
	public ArrayList<Timetable> getAllTimetablesForStation(int stationId){
		ArrayList<Timetable> result=new ArrayList<Timetable>();
		Gson gson=new Gson();
		JedisPool pool=new JedisPool(REDIS_HOST);
		try(Jedis jedis=pool.getResource()){
			Set<String> timetableIds=jedis.smembers(STATION_SET_PREFIX+stationId);
			//dobili smo set id-jeva redova voznje
			for(String id: timetableIds) {
				String key=KEY_PREFIX_TIMETABLES+id;
				String json=jedis.get(key);
				Timetable t=gson.fromJson(json, Timetable.class);
				result.add(t);
			}
		}
		pool.close();
		return result;
	}
	
	//brise red voznje i njegov id sa stanica
	public boolean remove(int timetableId) {
		long result=0;
		Gson gson=new Gson();
		JedisPool pool=new JedisPool(REDIS_HOST);
		try(Jedis jedis=pool.getResource()){
			//dohvatimo timetable da bi mogli dobiti stanice
			String json=jedis.get(KEY_PREFIX_TIMETABLES+timetableId);
			Timetable t=gson.fromJson(json, Timetable.class);
			result=jedis.del(KEY_PREFIX_TIMETABLES+timetableId);
			
			for(Arrival a: t.getArrivals()) {
				int stationId=a.getStation().getId();
				jedis.srem(STATION_SET_PREFIX+stationId, String.valueOf(timetableId));
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
			KEY_PREFIX_TIMETABLES = prop.getProperty("KEY_PREFIX_TIMETABLES");
			REDIS_HOST=prop.getProperty("REDIS_HOST");
			STATION_SET_PREFIX=prop.getProperty("STATION_SET_PREFIX");
			is.close();
		}catch(Exception e) {
			//e.printStackTrace();
			Logger.getLogger(StationService.class.getName()).log(Level.WARNING, e.toString());
		}
	}
	
}
