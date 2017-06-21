package appointmentcalendar.model.database.task;

import java.time.LocalDate;
import java.util.List;

import appointmentcalendar.model.analytics.dao.AnalyticService;
import appointmentcalendar.model.database.dao.Service;

/**
 * UserAnalyticsUpdateTask.
 */
public class UserAnalyticsUpdateTask implements DailyTask {

	private Service service;
	private AnalyticService analyticService;

	public UserAnalyticsUpdateTask() {
		service = new Service();
		analyticService = new AnalyticService();
	}

	/*
	 * (non-Javadoc)
	 * @see appointmentcalendar.model.database.task.DailyTask#performDailyUpdate()
	 */
	@Override
	public boolean performDailyUpdate() {
		LocalDate yesterday = LocalDate.now().minusDays(1);
		List<String> users = service.getUsersWhoHadAppointmentOnDay(yesterday);

		updateUserAnalytics(users, yesterday);
		updateBusinessAnalytics(users);

		return false;
	}

	private void updateUserAnalytics(List<String> users, LocalDate date) {
		for (String email : users) {
			analyticService.incrementBookingsTotal(email);
			analyticService.setLastAppointment(email, date);
		}
	}

	private void updateBusinessAnalytics(List<String> users) {

	}

}
