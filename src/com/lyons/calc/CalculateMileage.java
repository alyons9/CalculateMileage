package com.lyons.calc;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CalculateMileage implements Runnable{
	
	private BlockingQueue<List<Coordinates>> q;
	private BlockingQueue<Double> q2;
	private CompletionService<Double> threadPool;
	
	public CalculateMileage(BlockingQueue<List<Coordinates>> q, BlockingQueue<Double> q2){
		this.q = q;
		this.q2 = q2;
	}
	
	public CalculateMileage(BlockingQueue<List<Coordinates>> q, BlockingQueue<Double> q2, CompletionService<Double> threadPool){
		this.q = q;
		this.q2 = q2;
		this.threadPool = threadPool;
	}
	
	@Override
	public void run(){
		
		try {
			List<Coordinates> data = q.poll(30, TimeUnit.SECONDS);
			while(data != null){
				
				Coordinates c1 = data.get(0);
				Coordinates c2 = data.get(1);
				q2.put(calcMileageThreaded(c1, c2));
				data = q.poll(30, TimeUnit.SECONDS);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(ExecutionException e1){
			e1.printStackTrace();
		}
		
	}

	public double calcMileage(Coordinates c1, Coordinates c2){
		
		double x = 69.1 * (c2.getLatitude() - c1.getLatitude());
		double y = 69.1 * (c2.getLongitude() - c1.getLongitude()) * Math.cos(c1.getLatitude()/57.3); 
		
		return Math.sqrt(x * x + y * y);
	}
	
	public double calcMileageThreaded(Coordinates c1, Coordinates c2) throws InterruptedException, ExecutionException{
		threadPool.submit(new calcX(c1.getLatitude(), c2.getLatitude()));
		threadPool.submit(new calcY(c1.getLongitude(), c2.getLongitude()));
		
		Future<Double> task1 = threadPool.take();
		Future<Double> task2 = threadPool.take();
		double x = task1.get().doubleValue();
		double y = task2.get().doubleValue();
		
		return Math.sqrt(x * x + y * y);
	}
	
	public class calcX implements Callable<Double>{
		private double lat1;
		private double lat2;
		
		public calcX(double lat1, double lat2){
			this.lat1 = lat1;
			this.lat2 = lat2;
		}
		
		public Double call(){
			return 69.1 * (lat2 - lat1);
		}
	}
	
	public class calcY implements Callable<Double>{
		private double log1;
		private double log2;
		
		public calcY(double log1, double log2){
			this.log1 = log1;
			this.log2 = log2;
		}
		
		public Double call(){
			return 69.1 * (log2 - log1) * Math.cos(log1/57.3);
		}
	}
}
