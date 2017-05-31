package appointmentcalendar.model.database;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import appointmentcalendar.model.database.dao.Receptionist;
import appointmentcalendar.utils.Day;

/**
 * DatabaseAutomaticUpdater.
 */
public class DatabaseAutomaticUpdater {

	private static Logger LOG = LogManager.getLogger();
	private static final int CALENDAR_SIZE = 4;

	private Receptionist receptionist;

	private boolean success;

	public DatabaseAutomaticUpdater() {
		receptionist = new Receptionist();
		success = false;
	}

	/**
	 * Perform the daily update on the database.
	 * The day that has past gets deleted and a new day is added at the end of the schedule
	 */
	public boolean performDailyUpdate() {
		List<String> daysOff = null;
		List<String> breaks = null;
		List<String> currentDays = null;

		try {
			daysOff = receptionist.getDaysOffSchedule();
			breaks = receptionist.getDailyBreaks();
			currentDays = receptionist.getAvailableDays();

			deletePreviousDay(currentDays);
			LocalDate newDay = addNewDay(daysOff, receptionist.getAvailableDays());
			scheduleBreaksForNewDay(breaks, newDay);

			success = true;

		} catch (SQLException e) {
			LOG.error(e);
			e.printStackTrace();
		}
		return success;
	}

	/**
	 * Add a day to the end of the schedule. If the next day at the end of the schedule is a scheduled day off
	 * then it is advanced by one day until it equals a scheduled work day.
	 * 
	 * @param daysOff
	 *            the list of scheduled days off
	 * @param currentDays
	 *            days currently on the schedule
	 * @return the new day to be added
	 * @throws SQLException
	 */
	private LocalDate addNewDay(List<String> daysOff, List<String> currentDays) throws SQLException {
		LocalDate day14 = LocalDate.now().plusDays(CALENDAR_SIZE);

		if (daysOff.size() > 0) {
			for (String day : daysOff) {
				String dayToCheck = day14.getDayOfWeek().toString();

				if (dayToCheck.equals(day))
					day14 = day14.plusDays(1);
			}
		}

		Day day = new Day(day14);

		for (int i = 0; i < currentDays.size(); i++) {
			if (day.getDateAndDay().equals(currentDays.get(i))) {
				day14 = day14.plusDays(1);

				if (daysOff.size() > 0) {
					for (String dayOff : daysOff) {
						String dayToCheck = day14.getDayOfWeek().toString();

						if (dayToCheck.equals(dayOff))
							day14 = day14.plusDays(1);
					}
				}

				day.setDay(day14);
			}
		}
		day.setDay(day14);

		if (currentDays.size() <= CALENDAR_SIZE) {
			receptionist.addDay(day.getDate());
			LOG.info("Adding new day: " + day.getDateAndDay());
		} else {
			LOG.info("Attempted to add new day: " + day.getDateAndDay() + " but the calendar was full");
		}

		return day.getDate();
	}

	/**
	 * Schedule breaks for the newly added day
	 * 
	 * @param breaks
	 *            the list of scheduled break times for each day
	 * @param day
	 *            the day to schedule breaks for
	 * @throws SQLException
	 */
	private void scheduleBreaksForNewDay(List<String> breaks, LocalDate day) throws SQLException {
		if (breaks.size() > 0) {
			String breakSQLString = generateBreakSQLString(breaks);
			receptionist.scheduleBreaks(day, breakSQLString);
		}
	}

	/**
	 * Delete the day that has passed
	 * 
	 * @param currentDays
	 *            the list of currently scheduled days
	 * @throws SQLException
	 */
	private void deletePreviousDay(List<String> currentDays) throws SQLException {
		LocalDate yesterday = LocalDate.now().minusDays(1);

		for (int i = 0; i < currentDays.size(); i++) {
			if (yesterday.equals(currentDays.get(i))) {
				receptionist.deleteDay(yesterday);
			}
		}
	}

	/**
	 * Generate an partial SQL string listing the hours to set to 'break'
	 * 
	 * @param breaks
	 *            a list of scheduled breaks
	 * @return a string listing the times to set to 'break' in SQL syntax
	 */
	private String generateBreakSQLString(List<String> breaks) {
		StringBuilder sql = new StringBuilder();
		int size = breaks.size();

		if (size > 0) {
			sql.append(String.format("`%s`='break'", breaks.get(0)));

			if (size > 1) {
				for (int i = 1; i < size; i++)
					sql.append(String.format(",`%s`='break'", breaks.get(i)));
			}
		}
		return sql.toString();
	}

}
