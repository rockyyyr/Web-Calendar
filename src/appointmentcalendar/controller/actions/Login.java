package appointmentcalendar.controller.actions;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import appointmentcalendar.model.Data;
import appointmentcalendar.model.Responder;
import appointmentcalendar.model.User;
import appointmentcalendar.model.dto.LoginResponseDto;

/**
 * Servlet implementation class Login
 */
public class Login extends Action {

	// private static final Logger LOG = LogManager.getLogger();

	private LoginResponseDto dataTransfer;

	public Login() {
		dataTransfer = new LoginResponseDto();
	}

	@Override
	public void respond(HttpServletRequest request, HttpServletResponse response) {
		Data data = new Data(request);

		String email = data.get("login-email");
		String password = data.get("login-password");

		int responseCode = receptionist.checkUserCredentials(email, password);

		switch (responseCode) {
			case 1:
				User user = receptionist.getUser(email);
				dataTransfer.setFirstName(user.getFirstName());
				dataTransfer.setAppointments(receptionist.getAppointmentsForUser(email));
				request.getSession().setAttribute("user", user);
				break;
			case 4:
				response.addCookie(buildAdminCookie());
				break;
		}
		dataTransfer.setResponseCode(responseCode);
		Responder.send(dataTransfer, response);
	}

	/*
	 * Build an admin login cookie
	 */
	private Cookie buildAdminCookie() {
		Cookie cookie = new Cookie("admin", "true");
		cookie.setMaxAge(30 * 60);
		return cookie;
	}

}
