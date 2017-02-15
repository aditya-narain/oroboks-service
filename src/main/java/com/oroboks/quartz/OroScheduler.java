package com.oroboks.quartz;

import javax.inject.Inject;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

public class OroScheduler {
    private final Scheduler scheduler;
    @Inject
    public OroScheduler(SchedulerFactory factory, final GuiceJobFactory jobFactory) throws SchedulerException{
	this.scheduler = factory.getScheduler();
	scheduler.setJobFactory(jobFactory);
	scheduler.start();
    }

    public final Scheduler getScheduler()
    {
	return scheduler;
    }

    public boolean shutDownScheduler() {
	if (scheduler == null) {
	    throw new IllegalArgumentException("scheduler cannot be null");
	}
	try {
	    scheduler.shutdown();
	} catch (SchedulerException se) {
	    // Log here
	    return false;
	}
	return true;
    }
}
