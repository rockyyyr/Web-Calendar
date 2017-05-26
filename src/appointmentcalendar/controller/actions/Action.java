package appointmentcalendar.controller.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import appointmentcalendar.model.database.dao.Receptionist;

/**
 * Action. Base class for all request actions.
 */
public abstract class Action {

	protected Receptionist receptionist;

	public Action() {
		receptionist = new Receptionist();
	}

	/**
	 * Respond to an HttpServletRequest
	 * 
	 * @param request
	 * @param response
	 */
	public abstract void respond(HttpServletRequest request, HttpServletResponse response);

}
