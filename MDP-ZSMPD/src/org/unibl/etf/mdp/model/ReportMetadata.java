package org.unibl.etf.mdp.model;

import java.io.Serializable;

public class ReportMetadata implements Serializable{
	private String fileName;
	private String username;
	private String dateAndTime;
	private Integer size;
		
	public ReportMetadata() {
		super();
	}

	public ReportMetadata(String fileName, String username, String dateAndTime, Integer size) {
		super();
		this.fileName = fileName;
		this.username = username;
		this.dateAndTime = dateAndTime;
		this.size = size;
	}
	

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDateAndTime() {
		return dateAndTime;
	}

	public void setDateAndTime(String dateAndTime) {
		this.dateAndTime = dateAndTime;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateAndTime == null) ? 0 : dateAndTime.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		ReportMetadata other = (ReportMetadata) obj;
		if (dateAndTime == null) {
			if (other.dateAndTime != null)
				return false;
		} else if (!dateAndTime.equals(other.dateAndTime))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReportMetadata [fileName=" + fileName + ", username=" + username + ", dateAndTime=" + dateAndTime
				+ ", size=" + size + "]";
	}

	
	
	
}
