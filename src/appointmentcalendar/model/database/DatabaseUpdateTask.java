package appointmentcalendar.model.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * DatabaseUpdateTask.
 */
public class DatabaseUpdateTask implements Job {

	private Logger LOG = LogManager.getLogger();

	/*
	 * (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext ex) throws JobExecutionException {
		DatabaseAutomaticUpdater updater = new DatabaseAutomaticUpdater();
		boolean success = updater.performDailyUpdate();

		if (success) {
			System.out.println("DatabaseUpdateTask executed.");
			LOG.info("DatabaseUpdateTask executed.");
		}
	}

}
