package appointmentcalendar.model.database.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import appointmentcalendar.model.User;
import appointmentcalendar.utils.Day;
import appointmentcalendar.utils.TimeBlock;

/**
 * Receptionist. Service layer that handles all exchanges involving persisted data
 */
public class Receptionist {

	private static final Logger LOG = LogManager.getLogger();

	private UserDao userDao;
	private CalendarDao calendarDao;
	private WorkScheduleDao workScheduleDao;

	public Receptionist() {
		userDao = new UserDao();
		calendarDao = new CalendarDao();
		workScheduleDao = new WorkScheduleDao();
	}

	/**
	 * Attempt to register a user and return a response code
	 * 
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param password
	 * @param accessCode
	 * @return a response code a response code depending on the registration results:
	 *         1 - if the user registered successfully
	 *         2 - if the email address is already registered
	 *         3 - if there was a database error(SQLException)
	 *         4 - if the access code was incorrect
	 */
	public int createUser(String firstName, String lastName, String email, String password, String accessCode) {
		if (accessCode.equalsIgnoreCase(userDao.getAccessCode())) {
			try {
				User user = new User(firstName, lastName, email, password);
				userDao.add(user);
				LOG.info("User registered: " + user);
				return 1;
			} catch (MySQLIntegrityConstraintViolationException e) {
				return 2;
			} catch (SQLException e) {
				return 3;
			}
		} else {
			return 4;
		}
	}

	/**
	 * Check user credentials and return a response code
	 * 
	 * @param email
	 * @param password
	 * @return a response code depending on the login results:
	 *         1 - the user logged in successfully
	 *         2 - the user exists but the password is incorrect
	 *         3 - the email is not registered
	 *         4 - user is an admin
	 * @throws SQLException
	 */
	public int checkUserCredentials(String userName, String password) {
		int responseCode = 0;
		try {
			responseCode = userDao.checkUserCredentials(userName, password);
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
		return responseCode;
	}

	/**
	 * Return a user by email address
	 * 
	 * @param email
	 * @return the corresponding user
	 */
	public User getUser(String email) {
		User user = new User();
		try {
			user = userDao.getUser(email);
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
		return user;
	}

	public int bookAppointment(String day, String time, User user) {
		try {
			calendarDao.bookAppointment(day, time, user);
			LOG.info("Appointment booked: " + user.getEmail() + "= " + day + " @ " + time + user.getEmail());
			return 1;
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
			return 0;
		}
	}

	public List<String> getAppointmentsForUser(String email) {
		List<String> result = new ArrayList<>();
		try {
			result = calendarDao.getAppointmentsForUser(email);
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
		return result;
	}

	public void cancelAppointment(String appointment) {
		String[] temp = appointment.split("@");
		String date = temp[0].trim();
		String time = temp[1].trim();
		try {
			calendarDao.cancelAppointment(date, time);
			LOG.info("Appointment cancelled: " + date + " @ " + time);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addDay(Day day) {
		try {
			calendarDao.addDay(day);
		} catch (SQLException e) {
			LOG.error(e);
			e.printStackTrace();
		}
	}

	public void scheduleBreaks(String day, String breakList) {
		try {
			calendarDao.scheduleBreaks(day, breakList);
		} catch (SQLException e) {
			LOG.error(e);
			e.printStackTrace();
		}
	}

	public void deleteDay(String day) {
		try {
			calendarDao.deleteDay(day);
		} catch (SQLException e) {
			LOG.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * @return a list of all the days stored in the calendar
	 */
	public List<String> getAvailableDays() {
		List<String> result = new ArrayList<>();
		try {
			result = calendarDao.getAvailableDays();
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param day
	 * @return a list of all open time slots for a specific day
	 */
	public List<String> getAvailableTimesFromDay(String day) {
		List<String> result = new ArrayList<>();
		try {
			result = calendarDao.getAvailableTimesFromSpecificDay(day);
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param listSize
	 *            the number of appointments to return
	 * @return a list of the next appointments for today
	 */
	public List<String> getNextAppointments(int listSize) {
		String day = new Day(LocalDate.now()).getDateAndDay();
		String time = new TimeBlock(LocalTime.now().truncatedTo(ChronoUnit.HOURS)).getFormattedTime();

		List<String> result = new ArrayList<>();

		try {
			result = formatAppointmentList(calendarDao.getNextAppointments(listSize, day, time));
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param day
	 * @return a list of all book appointments for a specific day
	 */
	public List<String> getAppointmentsForSpecificDay(String day) {
		List<String> result = new ArrayList<>();
		try {
			result = formatAppointmentList(calendarDao.getAppointmentsForSpecificDay(day));
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @return a list of times which are auto generated as break times for each work day
	 */
	public List<String> getDailyBreaks() {
		List<String> result = new ArrayList<>();
		try {
			result = workScheduleDao.getDailyBreaks();
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @return a list of times which are auto generated as working hours for each work day
	 */
	public List<String> getWorkingHours() {
		List<String> result = new ArrayList<>();
		try {
			result = workScheduleDao.getWorkingHours();
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Schedule a time slot to be auto generated as a break time
	 * 
	 * @param time
	 */
	public void scheduleBreak(String time) {
		try {
			workScheduleDao.scheduleBreak(time);
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * Schedule a time slot to be auto generated as a working hour
	 * 
	 * @param time
	 */
	public void scheduleNonBreak(String time) {
		try {
			workScheduleDao.scheduleNonBreak(time);
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * @return a list of days of the week which are scheduled days off
	 */
	public List<String> getDaysOffSchedule() {
		List<String> result = new ArrayList<>();
		try {
			result = workScheduleDao.getDaysOffSchedule();
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Set a day of the week to be a scheduled day off
	 * 
	 * @param day
	 */
	public void scheduleDayOff(String day) {
		try {
			workScheduleDao.scheduleDayOff(day);
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * Set a day of the week to be a scheduled work day
	 * 
	 * @param day
	 */
	public void scheduleWorkDay(String day) {
		try {
			workScheduleDao.scheduleWorkDay(day);
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * @return the registration access code
	 */
	public String getAccessCode() {
		return userDao.getAccessCode();
	}

	/**
	 * Set the registration access code
	 * 
	 * @param accessCode
	 */
	public void setAccessCode(String accessCode) {
		userDao.setAccessCode(accessCode);
	}

	/**
	 * @param day
	 * @return a list of all time slots for the specified day
	 */
	public List<String> getTimeSlots(String day) {
		return calendarDao.getAllTimeSlots(day);
	}

	/**
	 * Set the status of a all time slots for a specific day
	 * 
	 * @param timeSlots
	 *            a formatted string representing time slots and their status
	 * @param day
	 */
	public void setTimeSlots(String timeSlots, String day) {
		try {
			calendarDao.setTimeSlots(timeSlots, day);
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * @param bookings
	 *            list of current bookings
	 * @return Format a list of appointments for view (Firstname LastName @ Time)
	 */
	private List<String> formatAppointmentList(List<String> bookings) {
		List<String> userList = new ArrayList<>();

		for (String booking : bookings) {
			String[] temp = booking.split("\\|");
			String email = temp[0];
			String time = temp[1];

			User user = getUser(email);

			String firstName = user.getFirstName();
			String lastName = user.getLastName();

			String bookingInfo = String.format("%s %s @ %s", firstName, lastName, time);

			userList.add(bookingInfo);
		}
		return userList;
	}

}
