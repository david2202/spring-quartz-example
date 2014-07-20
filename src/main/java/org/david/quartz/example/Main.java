package org.david.quartz.example;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.UUID;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Sample application to POC Quartz scheduler using Spring. Demonstrates:
 * <ul>
 * <li>configuring the scheduler through Spring</li>
 * <li>persisting job information in a database</li>
 * <li>running in a cluster</li>
 * <li>scheduling ad-hoc jobs with job data</li>
 * <li>pausing all jobs</li>
 * <li>resuming all jobs</li>
 * </ul>
 * 
 * @author howed
 */
public class Main {
	public static final String TRIGGER_GROUP = "jobs";
	public static final String JOB_GROUP = "jobs";

	public static void main(String[] args) throws SchedulerException,
			InterruptedException {
		ClassPathXmlApplicationContext ctx = null;
		try {
			ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
			Scheduler scheduler = ctx.getBean(Scheduler.class);

			System.out.println("Starting scheduling of jobs");
			for (int i = 0; i < 50; i++) {
				System.out.println("Scheduling " + i);
				String uuid = UUID.randomUUID().toString();
				JobDetail jobDetail = newJob(SimpleJob.class).withIdentity(
						uuid, JOB_GROUP).build();
				jobDetail.getJobDataMap().put("i", Integer.valueOf(i));

				Trigger trigger = newTrigger()
						.withIdentity(uuid, TRIGGER_GROUP)
						.startAt(
								new Date(System.currentTimeMillis()
										+ (i * 1000))).build();

				scheduler.scheduleJob(jobDetail, trigger);
			}
			// Now we need to wait for a bit while the jobs execute
			System.out.println("Finished scheduling of jobs");
			Thread.sleep(10000);
			System.out.println("Pausing trigger group");
			// This works across the cluster
			scheduler.pauseTriggers(GroupMatcher
					.triggerGroupEquals(TRIGGER_GROUP));
			Thread.sleep(20000);
			System.out.println("Resuming trigger group");
			scheduler.resumeTriggers(GroupMatcher
					.triggerGroupEquals(TRIGGER_GROUP));
			// You should see the scheduler catch up the jobs that it missed
			// while paused

			// Wait for the jobs to all finish
			while (scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup())
					.size() > 0) {
				Thread.sleep(1000);
			}
		} finally {
			if (ctx != null) {
				ctx.close();
			}
		}
	}
}
