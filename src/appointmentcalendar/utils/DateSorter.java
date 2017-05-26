package appointmentcalendar.utils;

import java.util.Comparator;

/**
 * DateSorter.
 */
public class DateSorter {

	public static Comparator<String> sort() {
		return new Comparator<String>() {

			@Override
			public int compare(String a, String b) {
				int dateA = fetchDate(a);
				int dateB = fetchDate(b);

				if (dateA > dateB)
					return dateA - dateB > 10 ? -1 : 1;
				else if (dateA < dateB)
					return dateB - dateA > 10 ? 1 : -1;
				else
					return 0;
			}
		};
	}

	private static int fetchDate(String dateString) {
		String[] dateSplit = dateString.split("\\s");

		int date;

		if (dateSplit[0].length() == 4)
			date = Integer.parseInt(dateString.substring(0, 2));
		else
			date = Integer.parseInt(dateString.substring(0, 1));

		return date;
	}

}
