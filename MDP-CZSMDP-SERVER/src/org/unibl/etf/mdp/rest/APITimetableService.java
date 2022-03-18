package org.unibl.etf.mdp.rest;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.unibl.etf.mdp.model.Timetable;

import com.google.gson.Gson;

@Path("/timetables")
public class APITimetableService {

	private TimetableService service;
	
	public APITimetableService() {
		service=new TimetableService();
	}
	
	//jedan url koji vraca sve, za czsmdp
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Timetable> getAll(){
		return service.getTimetables();
	}
	
	//jedan koji vraca sve za odredjenu stanicu
	@GET
	@Path("/stations/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Timetable> getTimetablesForStation(@PathParam("id") int id){
		return service.getAllTimetablesForStation(id);
	}
	
	@DELETE
	@Path("/{id}")
	public Response remove(@PathParam("id") int id) {
		if(service.remove(id)) {
			return Response.status(200).build();
		}else {
			return Response.status(404).build();
		}
	}
	
	//post za postavljanje reda voznje
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addTimetable(Timetable t) {
		if(service.add(t)) {
			return Response.status(200).entity(t).build();
		}else {
			return Response.status(409).entity("Greska pri dodavanju.").build();
		}
	}
	
	//put za dodavanje novog prolaznog vremena za stanicu
	@PUT
	@Path("/{tId}/stations/{sId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPassing(@PathParam("tId")int tId, @PathParam("sId") int sId, String json) {		
		JSONObject inputJsonObj = new JSONObject(json);
		String time = (String) inputJsonObj.get("time");
		Timetable t=service.addTrainPassing(tId, sId, time);
		if(t!=null) {
			return Response.status(200).entity(t).build();
		}else {
			return Response.status(404).entity("Greska pri dodavanju.").build();
		}
	}
	
}
