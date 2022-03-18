package org.unibl.etf.mdp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Timetable implements Serializable {

	private int id;
	private ArrayList<Arrival> arrivals;
	
	public Timetable() {
		super();
	}

	public Timetable(int id, ArrayList<Arrival> arrivals) {
		super();
		this.id = id;
		this.arrivals = arrivals;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Arrival> getArrivals() {
		return arrivals;
	}

	public void setArrivals(ArrayList<Arrival> arrivals) {
		this.arrivals = arrivals;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Timetable other = (Timetable) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Timetable [id=" + id + ", arrivals=" + arrivals + "]";
	}
	

}
