package org.unibl.etf.mdp.rest;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.unibl.etf.mdp.model.Station;

@Path("/stations")
public class APIStationService {

	StationService service;
	
	public APIStationService() {
		service=new StationService();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Station> getAll(){
		return service.getStations();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(Station station) {
		if(service.add(station)) {
			return Response.status(200).entity(station).build();
		}else {
			return Response.status(409).entity("Stanica vec postoji.").build();
		}
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

}
