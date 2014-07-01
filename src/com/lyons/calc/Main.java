package com.lyons.calc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int numThreads = 8;
		try {
			FileWriter w = new FileWriter("mileage.txt");
		
			ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
			CompletionService<Double> cThreadPool = new ExecutorCompletionService<Double>(threadPool);
			BlockingQueue<List<Coordinates>> q = new ArrayBlockingQueue<List<Coordinates>>(2000000);
			BlockingQueue<Double> q2 = new ArrayBlockingQueue<Double>(2000000);
		
			Future<?> startThread = threadPool.submit(new Producer(q));
			for(int i = 0; i < numThreads; i++){
				threadPool.execute(new CalculateMileage(q, q2, cThreadPool));
			}
		
			
			long start = System.currentTimeMillis();
			Double data = q2.poll(30, TimeUnit.SECONDS);
			while(data != null){
				w.write(data.toString());
				w.write("\n");
				data = q2.poll(30, TimeUnit.SECONDS);
			}
			long end = System.currentTimeMillis();
			long milli = (end - start);
			String time = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(milli),
					TimeUnit.MILLISECONDS.toSeconds(milli) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milli)));
			
			System.out.println(time);
			
			w.close();
			startThread.get();
			threadPool.shutdown();
			if(threadPool.awaitTermination(30, TimeUnit.SECONDS)){
				threadPool.shutdownNow();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(ExecutionException e2){
			e2.printStackTrace();
		}
	}

}
