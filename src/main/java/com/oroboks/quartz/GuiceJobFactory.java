package com.oroboks.quartz;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import com.google.inject.Injector;

public class GuiceJobFactory implements JobFactory {
    private final Logger LOGGER = Logger.getLogger(GuiceJobFactory.class.getSimpleName());
    private final Injector injector;
    @Inject
    public GuiceJobFactory(Injector injector){
	this.injector = injector;
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler)
	    throws SchedulerException {
	// Get the job detail so we can get the job class
	JobDetail jobDetail = bundle.getJobDetail();
	Class<? extends Job> jobClass = jobDetail.getJobClass();

	try {
	    // Get a new instance of that class from Guice so we can do dependency injection
	    return injector.getInstance(jobClass);
	} catch (Exception e) {
	    LOGGER.log(Level.SEVERE, "Error occured while creating new Job.");
	    throw new RuntimeException("Exception thrown with error"+e);
	}
    }
}



