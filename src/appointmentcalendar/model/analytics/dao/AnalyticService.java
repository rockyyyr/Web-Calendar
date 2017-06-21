package appointmentcalendar.model.analytics.dao;

import java.sql.SQLException;
import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import appointmentcalendar.model.User;

/**
 * AnalyticService.
 */
public class AnalyticService {

	private static final Logger LOG = LogManager.getLogger();

	private ClientAnalyticsDao clientAnalyticsDao;

	public AnalyticService() {
		clientAnalyticsDao = new ClientAnalyticsDao();
	}

	/**
	 * Add a user for analytics tracking
	 * 
	 * @param user
	 */
	public void addUser(User user) {
		try {
			clientAnalyticsDao.add(user);
		} catch (SQLException e) {
			LOG.error(e);
			e.printStackTrace();
		}
	}

	public void updateLoginAnalytics(User user) {
		clientAnalyticsDao.incrementLoginTotalAndUpdateLastLogin(user);
	}

	public void incrementBookingsTotal(String email) {
		clientAnalyticsDao.incrementBookingsTotal(email);
	}

	public void decrementBookingsTotal(String email) {
		clientAnalyticsDao.decrementBookingsTotal(email);
	}

	public void setLastAppointment(String email, LocalDate date) {
		clientAnalyticsDao.setLastAppointment(email, date);
	}

}
