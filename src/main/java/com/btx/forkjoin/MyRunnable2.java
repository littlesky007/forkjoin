package com.btx.forkjoin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MyRunnable2 {
	static class CountWork extends RecursiveTask<Integer>
	{

		private String filePath;
		public CountWork(String filePath) {
			super();
			this.filePath = filePath;
		}
		@Override
		protected Integer compute() {
			File file = new File(filePath);

			int count = 0;
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files == null || files.length == 0)
					return 0;


				List<CountWork> taskList = new ArrayList<CountWork>();

				for (File f : files) {
					if (f.isDirectory()) {
						String lastName = f.getName();
						String newFilePath = filePath + File.separator + lastName;
						//	 count+= count(newFilePath);
						CountWork countWork = new CountWork(newFilePath);
						taskList.add(countWork);
						
//						invokeAll(countWork);
//						count += countWork.join();
					} else {
						try {
							Thread.currentThread().sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						count++;
					}


				}

				if (!taskList.isEmpty()) {

					//方式1
					for (CountWork mtask : invokeAll(taskList)) {
						count += mtask.join();
					}
					
					//方式2,
					
//					for (CountWork mtask : taskList) {
//						invokeAll(mtask);						
//						
//						//Returns the result of the computation when it {@link #isDone is 一直等待到计算完成才返回结果
//						count += mtask.join();
//					}
				}
			}else {
				count=1;
			}
			
			//System.out.println(Thread.currentThread().getName());
			return count;
		}
	}


	//监控Fork/Join池相关方法
	private static void showLog(ForkJoinPool pool) {
		System.out.printf("**********************\n");

		System.out.printf("线程池的worker线程们的数量:%d\n",
				pool.getPoolSize());
		System.out.printf("当前执行任务的线程的数量:%d\n",
				pool.getActiveThreadCount());
		System.out.printf("没有被阻塞的正在工作的线程:%d\n",
				pool.getRunningThreadCount());
		System.out.printf("已经提交给池还没有开始执行的任务数:%d\n",
				pool.getQueuedSubmissionCount());
		System.out.printf("已经提交给池已经开始执行的任务数:%d\n",
				pool.getQueuedTaskCount());
		System.out.printf("线程偷取任务数:%d\n",
				pool.getStealCount());
		System.out.printf("池是否已经终止 :%s\n",
				pool.isTerminated());
		System.out.printf("**********************\n");
	}
	public static void main(String[] args) throws InterruptedException {
		
		ForkJoinPool forkJoinPool = new ForkJoinPool(15);
				
		File [] roots = File.listRoots();
		Long start = System.currentTimeMillis();
		//File e=new File("D:\\");
		for(File e:roots)
		{
			CountWork countWork = new CountWork(e.getAbsolutePath());
			forkJoinPool.invoke(countWork);
//			Thread.sleep(1000);
//			forkJoinPool.execute(countWork);
//			showLog(forkJoinPool);
			System.out.println(countWork.join());


		}

		Long end = System.currentTimeMillis();
		System.out.println("用时："+(end-start)/1000);
	}
}
