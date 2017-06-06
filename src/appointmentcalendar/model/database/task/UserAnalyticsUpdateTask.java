package appointmentcalendar.model.database.task;

import appointmentcalendar.model.database.dao.Service;

/**
 * UserAnalyticsUpdateTask.
 */
public class UserAnalyticsUpdateTask implements DailyTask {

	private Service service;

	public UserAnalyticsUpdateTask() {
		service = new Service();
	}

	/*
	 * (non-Javadoc)
	 * @see appointmentcalendar.model.database.task.DailyTask#performDailyUpdate()
	 */
	@Override
	public boolean performDailyUpdate() {

		return false;
	}

}
