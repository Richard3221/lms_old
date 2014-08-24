package lms;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jobs.GameweekEndedChecker;
import jobs.GameweekStartedChecker;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;





import org.quartz.DateBuilder.IntervalUnit;
//import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

public class LMSScheduler {

	
	public LMSScheduler() throws SchedulerException {
		
	    System.out.println("LMSScheduler: setting up jobs/triggers");
 
	    // First we must get a reference to a scheduler
	    SchedulerFactory sf = new StdSchedulerFactory();
	    Scheduler sched = sf.getScheduler();   
	    Date startTime = new Date();

		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		cal.add(Calendar.SECOND, 7);
		Date newStartTime = cal.getTime();
	    
	    // job 1 will run every 20 seconds
	    JobDetail job = newJob(GameweekEndedChecker.class).withIdentity("GameweekEndedCheckerJob", "group1").build();
	    JobDetail job2 = newJob(GameweekStartedChecker.class).withIdentity("GameweekStartedCheckerJob", "group1").build();

	    SimpleTrigger trigger = newTrigger().withIdentity("trigger1", "group1").withSchedule(simpleSchedule().withIntervalInMinutes(30).repeatForever()).build();
	    SimpleTrigger trigger2 = newTrigger().withIdentity("trigger2", "group1").startAt(newStartTime).withSchedule(simpleSchedule().withIntervalInMinutes(30).repeatForever()).build();

	    sched.scheduleJob(job, trigger);
	    sched.scheduleJob(job2, trigger2);

	    sched.start();

        //sched.shutdown(true);    
	}
	

}
