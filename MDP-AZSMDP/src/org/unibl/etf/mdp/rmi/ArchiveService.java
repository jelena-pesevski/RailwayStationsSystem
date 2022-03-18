package org.unibl.etf.mdp.rmi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.mdp.model.ReportMetadata;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class ArchiveService implements ArchiveInterface {

	public static final String CONFIG="resources/config.properties";
	public static String SERVER_POLICY;
	public static String DATE_TIME_PATTERN;
	public static String REPORTS_PATH;
	public static String JSON_PATH;
	public static String REPORT_EXT;
	public static String METADATA_EXT;
	public static String SEPARATOR;
	public static String RMI_STUB;
	public static int PORT;
	
	public ArchiveService() throws RemoteException{
		super();
	}
	
	public static void readConfig() {
		Properties prop=new Properties();
		try {
			prop.load(new FileInputStream(CONFIG));
			DATE_TIME_PATTERN=prop.getProperty("DATE_TIME_PATTERN");
			REPORTS_PATH=prop.getProperty("REPORTS_PATH");
			JSON_PATH=prop.getProperty("JSON_PATH");
			REPORT_EXT=prop.getProperty("REPORT_EXT");
			METADATA_EXT=prop.getProperty("METADATA_EXT");
			SEPARATOR=prop.getProperty("SEPARATOR");
			RMI_STUB=prop.getProperty("RMI_STUB");
			PORT=Integer.parseInt(prop.getProperty("PORT"));
			SERVER_POLICY=prop.getProperty("SERVER_POLICY");
			
		}catch(Exception e) {
			//e.printStackTrace();
			Logger.getLogger(ArchiveService.class.getName()).log(Level.WARNING, e.toString());
		}
	}
	
	//true ako je uspjesan upload
	@Override
	public boolean upload(byte[] data, String username, int stationId) throws RemoteException {	
		int size=data.length;
		SimpleDateFormat sdf=new SimpleDateFormat(DATE_TIME_PATTERN);
		String dateAndTime=sdf.format(new Date());
		String fileName=username+SEPARATOR+stationId+SEPARATOR+dateAndTime+REPORT_EXT;
		String jsonFileName=username+SEPARATOR+stationId+SEPARATOR+dateAndTime+METADATA_EXT;
		
		ReportMetadata metadata=new ReportMetadata(fileName, username, dateAndTime, size);
		File report=new File(REPORTS_PATH+fileName);
		File jsonMetdata=new File(JSON_PATH+jsonFileName);
		try {
			FileOutputStream out=new FileOutputStream(report);
			out.write(data);
			out.flush();
			out.close();	
			
			Writer jsonWriter=new FileWriter(jsonMetdata);
			Gson gson=new Gson();
			gson.toJson(metadata, jsonWriter);
			jsonWriter.close();
			return true;
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Logger.getLogger(ArchiveService.class.getName()).log(Level.WARNING, e.toString());
		} catch (IOException e) {
			//e.printStackTrace();
			Logger.getLogger(ArchiveService.class.getName()).log(Level.WARNING, e.toString());
		}
		return false;
	}

	@Override
	public byte[] download(String reportName, int size) throws RemoteException {
		byte[] data=new byte[size];
		File report=new File(REPORTS_PATH+reportName);
		FileInputStream in;
		try {
			in = new FileInputStream(report);
			in.read(data, 0, size);
			in.close();
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Logger.getLogger(ArchiveService.class.getName()).log(Level.WARNING, e.toString());
		} catch (IOException e) {
			//e.printStackTrace();
			Logger.getLogger(ArchiveService.class.getName()).log(Level.WARNING, e.toString());
		}
		return data;
	}

	@Override
	public ArrayList<ReportMetadata> list() throws RemoteException {
		ArrayList<ReportMetadata> result=new ArrayList<>();
		File dir=new File(JSON_PATH);
		File[] files=dir.listFiles();
		Gson gson=new Gson();
		for(File f : files) {
			ReportMetadata metadata;
			try {
				metadata = gson.fromJson(new FileReader(f), ReportMetadata.class);
				result.add(metadata);
			} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
				//e.printStackTrace();
				Logger.getLogger(ArchiveService.class.getName()).log(Level.WARNING, e.toString());
			}
		}
		return result;
	}

}
