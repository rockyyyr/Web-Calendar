package appointmentcalendar.model.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * DailyDatabaseUpdater.
 */
public class DailyDatabaseUpdater implements Job {

	private Logger LOG = LogManager.getLogger();

	/*
	 * (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext ex) throws JobExecutionException {
		AutomaticCalendarUpdater updater = new AutomaticCalendarUpdater();
		boolean success = updater.performDailyUpdate();

		if (success) {
			System.out.println("DailyDatabaseUpdater executed.");
			LOG.info("DailyDatabaseUpdater executed.");
		} else {
			System.out.println("DailyDatabaseUpdater failed to execute properly.");
			LOG.info("DailyDatabaseUpdater failed to execute properly.");
		}
	}

}
