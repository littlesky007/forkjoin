package com.btx.forkjoin;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;

public class MyRunnable1 implements Runnable{
	private CountDownLatch countdownLatch;

	private String filePath;

	public MyRunnable1(CountDownLatch countdownLatch, String filePath) {
		this.countdownLatch = countdownLatch;
		this.filePath = filePath;
	}


	@Override
	public void run() {
		System.out.println(count(filePath));
		countdownLatch.countDown();
	}
	public int count(String filePath)
	{
		int count = 0;
		File file = new File(filePath);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if(files==null||files.length==0)
				return 0;
			for(File f : files)
			{
				if(f.isDirectory())
				{
					String lastName = f.getName();
					String newFilePath = filePath+File.separator+lastName;
					count+= count(newFilePath);
				}
				else
				{
					//用于测试性能
					try {
						Thread.currentThread().sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					count++;				
				}
			}
		}
		else
		{
			count = 1;
		}
		return count;
	}

	public static void main(String[] args) throws InterruptedException {
		Long start = System.currentTimeMillis();	
		CountDownLatch countdownLatch = new CountDownLatch(4);
		File [] roots = File.listRoots();

		//每一个盘符，用一个线程
		for(File f : roots)
		{			
			MyRunnable1 my = new MyRunnable1(countdownLatch, f.getAbsolutePath());
			new Thread(my).start();
		}

		countdownLatch.await();
		Long end = System.currentTimeMillis();
		System.out.println("用时："+(end - start)/1000);
	}
}
