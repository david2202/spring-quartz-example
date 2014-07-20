package org.david.quartz.example;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Simple job to show when the job is being executed.
 * 
 * @author howed
 */
public class SimpleJob implements Job {

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		Integer i = (Integer) context.getJobDetail().getJobDataMap().get("i");
		System.out.println("Executing " + i);
	}
}
