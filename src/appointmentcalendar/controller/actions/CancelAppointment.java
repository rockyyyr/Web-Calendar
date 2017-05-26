package appointmentcalendar.controller.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import appointmentcalendar.model.Data;

public class CancelAppointment extends Action {

	@Override
	public void respond(HttpServletRequest request, HttpServletResponse response) {
		Data data = new Data(request);
		String appointment = data.get("appointment");

		receptionist.cancelAppointment(appointment);
	}
}
