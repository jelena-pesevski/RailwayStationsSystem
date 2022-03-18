package org.unibl.etf.mdp.model;

public class Arrival {

	private Station station;
	private String scheduledTime;
	private String realArrivalTime;
	
	public Arrival() {
		super();
	}

	public Arrival(Station station, String scheduledTime) {
		super();
		this.station = station;
		this.scheduledTime = scheduledTime;
		this.realArrivalTime="";
	}
	
	public Arrival(Station station, String scheduledTime, String realArrivalTime) {
		super();
		this.station = station;
		this.scheduledTime = scheduledTime;
		this.realArrivalTime=realArrivalTime;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public String getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(String scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public String getRealArrivalTime() {
		return realArrivalTime;
	}

	public void setRealArrivalTime(String realArrivalTime) {
		this.realArrivalTime = realArrivalTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((realArrivalTime == null) ? 0 : realArrivalTime.hashCode());
		result = prime * result + ((scheduledTime == null) ? 0 : scheduledTime.hashCode());
		result = prime * result + ((station == null) ? 0 : station.hashCode());
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
		Arrival other = (Arrival) obj;
		if (realArrivalTime == null) {
			if (other.realArrivalTime != null)
				return false;
		} else if (!realArrivalTime.equals(other.realArrivalTime))
			return false;
		if (scheduledTime == null) {
			if (other.scheduledTime != null)
				return false;
		} else if (!scheduledTime.equals(other.scheduledTime))
			return false;
		if (station == null) {
			if (other.station != null)
				return false;
		} else if (!station.equals(other.station))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return station.getId() + "-" + station.getName()+ "(" + scheduledTime + ")";
				
	}
	
	public String timetableString() {
		boolean trainPassed=realArrivalTime.isEmpty();
		String line= station.getId() + "-" + station.getName()+ "(" + scheduledTime + 
				(!trainPassed?("," +realArrivalTime):"")+ ")" + (!trainPassed? "PROSAO":"");
		System.out.println(line);
		return line;
	}
}
