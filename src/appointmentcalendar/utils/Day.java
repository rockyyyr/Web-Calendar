package appointmentcalendar.utils;

import java.time.LocalDate;

/**
 * Day. Utility class used to format a LocalDate object
 */
public class Day {

	private LocalDate date;

	public Day(LocalDate date) {
		this.date = date;
	}

	public LocalDate getDate() {
		return date;
	}

	public String getDayOfWeek() {
		return date.getDayOfWeek().toString();
	}

	public int getDayOfMonth() {
		return date.getDayOfMonth();
	}

	/**
	 * @return a formatted string representing the date and day (3rd - THURSDAY)
	 */
	public String getDateAndDay() {
		String suffix = "";
		//@formatter:off
		switch (getDayOfMonth()) {
			case 1: suffix = "st"; break;
			case 2: suffix = "nd" ;break;
			case 3: suffix = "rd"; break;
			default: suffix = "th"; break;
		}
		//@formatter:on
		return String.format("%s%s - %s", getDayOfMonth(), suffix, getDayOfWeek());
	}

	public void setDay(LocalDate day) {
		date = day;
	}

	public void plusDay(int num) {
		date.plusDays(num);
	}

	@Override
	public String toString() {
		return getDateAndDay();
	}

}
