package ninaRow.utils;


import ninaRow.managers.GameRoomsManager;
import ninaRow.managers.UserManager;

import javax.servlet.ServletContext;

public class ServletUtils {

	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String GAME_ROOMS_MANAGER_ATTRIBUTE_NAME = "gameRoomsManager";

	/*
	Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
	the actual fetch of them is remained unchronicled for performance POV
	 */
	private static final Object userManagerLock = new Object();
	private static final Object gameRoomsManagerLock = new Object();

	public static UserManager getUserManager(ServletContext servletContext) {

		synchronized (userManagerLock) {
			if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
			}
		}
		return (UserManager)servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}

	public static GameRoomsManager getGameRoomsManager(ServletContext servletContext) {
		synchronized (gameRoomsManagerLock) {
			if (servletContext.getAttribute(GAME_ROOMS_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(GAME_ROOMS_MANAGER_ATTRIBUTE_NAME, new GameRoomsManager());
			}
		}
		return (GameRoomsManager) servletContext.getAttribute(GAME_ROOMS_MANAGER_ATTRIBUTE_NAME);
	}
}
