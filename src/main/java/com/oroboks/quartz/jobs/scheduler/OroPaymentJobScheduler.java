package com.oroboks.quartz.jobs.scheduler;

import javax.inject.Inject;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;

import com.oroboks.quartz.OroScheduler;
import com.oroboks.quartz.jobs.OroPaymentJob;

public class OroPaymentJobScheduler {
    private final OroScheduler oroScheduler;
    @Inject
    public OroPaymentJobScheduler(OroScheduler oroScheduler){
	this.oroScheduler = oroScheduler;
	initializeScheduler();
    }
    public void initializeScheduler(){
	Scheduler scheduler = oroScheduler.getScheduler();
	JobDetail job = JobBuilder.newJob(OroPaymentJob.class)
		.withIdentity("job3", "group3").build();
	// Triggers build everyday at 2:30PM
	CronTrigger trigger = TriggerBuilder.newTrigger()
		.withIdentity("trigger1", "group3")
		// Everyday at 14:30 hrs Build will be triggered.
		.withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(14, 30))
		.build();
	try {
	    scheduler.scheduleJob(job, trigger);
	} catch (SchedulerException e) {
	    //TODO : Log Message here.
	}
    }
}
