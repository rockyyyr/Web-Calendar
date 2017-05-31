package appointmentcalendar.model.database.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import appointmentcalendar.controller.DBConnectionPool;
import appointmentcalendar.model.User;
import appointmentcalendar.utils.DateSorter;

/**
 * CalendarDao.
 */
public class CalendarDao extends Dao {

	private static final String TABLE_NAME = "calendar";
	private static final String BREAK = "break";

	CalendarDao() {
		super(TABLE_NAME);
	}

	/**
	 * Add a day to the database
	 * 
	 * @param day
	 *            the day to add
	 * @throws SQLException
	 */
	public void addDay(LocalDate day) throws SQLException {
		String sql = String.format(""
				+ "INSERT INTO %s (%s) "
				+ "VALUES ('%s')",
				TABLE_NAME, Field.DAY.name,
				Date.valueOf(day));

		executeUpdate(sql);
	}

	/**
	 * Book an appointment
	 * 
	 * @param day
	 *            The day of the appointment (Day.getDateAndDay();)
	 * @param time
	 *            The formatted time of the appointment (Timeblock.getFormattedTime();)
	 * @param user
	 *            The user who is booking
	 * @throws SQLException
	 */
	public void bookAppointment(LocalDate day, String time, User user) throws SQLException {
		String sql = String.format(""
				+ "UPDATE %s "
				+ "SET `%s`='%s' "
				+ "WHERE %s='%s';",
				TABLE_NAME,
				time, user.getEmail(),
				Field.DAY.name, Date.valueOf(day));

		executeUpdate(sql);
	}

	public void cancelAppointment(LocalDate day, String time) throws SQLException {
		String sql = String.format(""
				+ "UPDATE %s "
				+ "SET `%s`=NULL "
				+ "WHERE %s='%s';",
				TABLE_NAME,
				time,
				Field.DAY.name, Date.valueOf(day));

		executeUpdate(sql);
	}

	public void scheduleBreaks(LocalDate day, String breakList) throws SQLException {
		String sql = String.format(""
				+ "UPDATE %s "
				+ "SET %s "
				+ "WHERE %s='%s';",
				TABLE_NAME,
				breakList,
				Field.DAY.name, Date.valueOf(day));

		executeUpdate(sql);
	}

	public List<String> getAvailableTimesFromSpecificDay(LocalDate day) throws SQLException {
		List<String> times = new ArrayList<>();

		String sql = String.format(""
				+ "SELECT * "
				+ "FROM %s "
				+ "WHERE %s='%s'",
				TABLE_NAME,
				Field.DAY.name, Date.valueOf(day));

		Connection connection = null;;
		Statement statement = null;
		ResultSet rs = null;

		try {
			connection = DBConnectionPool.getConnection();
			statement = connection.createStatement();

			rs = statement.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			rs.first();

			int count = 1;
			while (count <= rsmd.getColumnCount()) {
				String label = rsmd.getColumnLabel(count++);
				String value = rs.getString(label);

				if (value == null)
					times.add(label);
			}
		} catch (Exception e) {
			logError(e, sql);
			e.printStackTrace();
		} finally {
			close(rs);
			close(statement);
			DBConnectionPool.freeConnection(connection);
		}
		return times;
	}

	public List<String> getAvailableDays() throws SQLException {
		List<String> days = new ArrayList<>();

		String sql = String.format(""
				+ "SELECT %s "
				+ "FROM %s",
				Field.DAY.name,
				TABLE_NAME);

		Connection connection = null;;
		Statement statement = null;
		ResultSet rs = null;

		try {
			connection = DBConnectionPool.getConnection();
			statement = connection.createStatement();

			rs = statement.executeQuery(sql);

			while (rs.next())
				days.add(rs.getString(Field.DAY.name));

		} catch (Exception e) {
			logError(e, sql);
			e.printStackTrace();
		} finally {
			close(rs);
			close(statement);
			DBConnectionPool.freeConnection(connection);
		}

		days.sort(DateSorter.sort());
		return days;
	}

	public List<String> getAppointmentsForUser(String email) throws SQLException {
		List<String> appointments = new ArrayList<>();

		String sql = String.format(""
				+ "SELECT * "
				+ "FROM %s ", TABLE_NAME);

		Connection connection = null;;
		Statement statement = null;
		ResultSet rs = null;

		try {
			connection = DBConnectionPool.getConnection();
			statement = connection.createStatement();

			rs = statement.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				int count = 1;
				while (count <= rsmd.getColumnCount()) {
					String label = rsmd.getColumnLabel(count++);
					String value = rs.getString(label);

					if (value != null && value.equals(email)) {
						String day = rs.getString(1);
						appointments.add(day + " @ " + rsmd.getColumnLabel(count - 1));
					}
				}
			}
		} catch (Exception e) {
			logError(e, sql);
			e.printStackTrace();
		} finally {
			close(rs);
			close(statement);
			DBConnectionPool.freeConnection(connection);
		}
		return appointments;
	}

	public List<String> getNextAppointments(int totalListSize, LocalDate day, String time) throws SQLException {
		List<String> appointments = new ArrayList<>();

		String sql = String.format(""
				+ "SELECT * "
				+ "FROM %s "
				+ "WHERE %s='%s'",
				TABLE_NAME,
				Field.DAY.name, Date.valueOf(day));

		Connection connection = null;;
		Statement statement = null;
		ResultSet rs = null;

		try {
			connection = DBConnectionPool.getConnection();
			statement = connection.createStatement();

			rs = statement.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			rs.first();

			int index = 1;
			int columnIndex = 1;
			while (index <= rsmd.getColumnCount()) {
				String column = rsmd.getColumnLabel(index);

				if (column.equals(time)) {
					columnIndex = index;
					break;
				}
				index++;
			}

			int listSize = 0;
			while (columnIndex <= rsmd.getColumnCount()) {
				String email = rs.getString(columnIndex);

				if (email != null && !email.equals(day) && !email.equals(BREAK) && listSize < totalListSize) {
					appointments.add(email + "|" + rsmd.getColumnLabel(columnIndex));
					listSize++;
				}
				columnIndex++;
			}
		} catch (Exception e) {
			logError(e, sql);
			e.printStackTrace();
		} finally {
			close(rs);
			close(statement);
			DBConnectionPool.freeConnection(connection);
		}
		return appointments;
	}

	public List<String> getAppointmentsForSpecificDay(LocalDate day) throws SQLException {
		List<String> appointments = new ArrayList<>();
		String sql = String.format(""
				+ "SELECT * "
				+ "FROM %s "
				+ "WHERE %s='%s'",
				TABLE_NAME,
				Field.DAY.name, Date.valueOf(day));

		Connection connection = null;;
		Statement statement = null;
		ResultSet rs = null;

		try {
			connection = DBConnectionPool.getConnection();
			statement = connection.createStatement();

			rs = statement.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				int index = 2;
				while (index <= rsmd.getColumnCount()) {
					String result = rs.getString(index);

					if (result != null && !result.equals(BREAK)) {
						appointments.add(result + "|" + rsmd.getColumnLabel(index));
					}
					index++;
				}
			}
		} catch (Exception e) {
			logError(e, sql);
			e.printStackTrace();
		} finally {
			close(rs);
			close(statement);
			DBConnectionPool.freeConnection(connection);
		}
		return appointments;
	}

	public void deleteDay(LocalDate day) throws SQLException {
		String sql = String.format(""
				+ "DELETE FROM %s "
				+ "WHERE %s='%s'",
				TABLE_NAME,
				Field.DAY.name, Date.valueOf(day));

		executeUpdate(sql);
	}

	public List<String> getAllTimeSlots(LocalDate day) {
		List<String> timeSlots = new ArrayList<>();
		String sql = String.format(""
				+ "SELECT * "
				+ "FROM %s "
				+ "WHERE %s='%s'",
				TABLE_NAME,
				Field.DAY.name, Date.valueOf(day));

		Connection connection = null;;
		Statement statement = null;
		ResultSet rs = null;

		try {
			connection = DBConnectionPool.getConnection();
			statement = connection.createStatement();

			rs = statement.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				int index = 2;
				while (index <= rsmd.getColumnCount()) {
					String column = rsmd.getColumnLabel(index++);
					timeSlots.add(column + "|" + rs.getString(column));
				}
			}

		} catch (Exception e) {
			logError(e, sql);
			e.printStackTrace();
		} finally {
			close(rs);
			close(statement);
			DBConnectionPool.freeConnection(connection);
		}

		return timeSlots;
	}

	public void setTimeSlots(String timeSlots, LocalDate day) throws SQLException {
		String[] arr = timeSlots.split("\\.");
		String modifier = "";

		for (int i = 0; i < arr.length; i++) {
			String[] temp = arr[i].split("\\|");
			String time = temp[0];
			String status = temp[1];

			modifier += "`" + time + "`=" + (status.equals("null") ? "NULL" : "'break'") + ", ";
		}

		modifier = modifier.substring(0, modifier.length() - 2); // remove trailing comma

		String sql = String.format(""
				+ "UPDATE %s "
				+ "SET %s "
				+ "WHERE %s='%s'",
				TABLE_NAME,
				modifier,
				Field.DAY.name, Date.valueOf(day));

		executeUpdate(sql);
	}

	public enum Field {
		DAY("day", "VARCHAR(20)");

		String name;
		String type;

		Field(String name, String type) {
			this.name = name;
			this.type = type;
		}
	}

}
