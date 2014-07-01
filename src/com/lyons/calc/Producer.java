package com.lyons.calc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable{

	private BlockingQueue<List<Coordinates>> q;
	
	public Producer(BlockingQueue<List<Coordinates>> q){
		this.q = q;
	}
	
	@Override
	public void run(){
		BufferedReader b;
		try {
			b = new BufferedReader(new FileReader("/Users/user/Desktop/GA_Features_20130602.txt"));
			int lineposition = 1;
			String strLine;
			while ((strLine = b.readLine()) != null){
				
				if(lineposition > 2){
					String []contents = strLine.split("\\|");
					
//					System.out.println(strLine);
					List<Coordinates> cList = new ArrayList<Coordinates>();
					
					String lat1 = contents[FileColumns.PRIMARY_LAT.getPosition()];
					String lat2 = contents[FileColumns.SECONDARY_LAT.getPosition()];
					String log1 = contents[FileColumns.PRIMARY_LONG.getPosition()];
					String log2 = contents[FileColumns.SECONDARY_LONG.getPosition()];
							
					
					double pLat = (!lat1.equals("")) ? 
							Double.parseDouble(lat1) : 0.0;
					double pLog = (!log1.equals("")) ? 
							Double.parseDouble(log1) : 0.0;
					double sLat = (!lat2.equals("")) ? 
							Double.parseDouble(lat2) : 0.0;
					double sLog = (!log2.equals("")) ? 
							Double.parseDouble(log2): 0.0;
							
					cList.add(
					new Coordinates(
							pLat,
							pLog
							)
					);
					
					cList.add(
						new Coordinates(
							sLat,
							sLog
							)
					);
					
					q.put(cList);
				}
				
				lineposition++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException ie){
			ie.printStackTrace();
		}
	}
}
