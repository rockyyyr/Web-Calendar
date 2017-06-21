package appointmentcalendar.model.analytics.dao;

import java.sql.SQLException;

import appointmentcalendar.model.User;
import appointmentcalendar.model.analytics.dao.ClientAnalyticsDao.Field;
import appointmentcalendar.model.database.DBProperties;
import appointmentcalendar.model.database.dao.Dao;

/**
 * BusinessAnalyticsDao.
 */
public class BusinessAnalyticsDao extends Dao {

	private static final String TABLE_NAME = DBProperties.get("db.businessAnalytics.table");

	BusinessAnalyticsDao() {
		super(TABLE_NAME);
	}

	protected String select(String columns, String tableName, String condition, String expression) {
		return String.format(""
				+ "SELECT %s FROM %s WHERE %s='%s'",
				columns, tableName, condition, expression);
	}

	protected String update(String tableName, String column, String value, String condition, String expression) {
		return String.format(""
				+ "UPDATE %s SET %s='%s' WHERE %s='%s'",
				tableName, column, value, condition, expression);
	}

	public void getUser(User user) throws SQLException {
		executeUpdate(select("*", TABLE_NAME, Field.BOOKING_TOTAL.name, user.getFirstName()));
	}

}
