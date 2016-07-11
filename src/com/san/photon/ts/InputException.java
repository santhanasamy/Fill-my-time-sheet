package com.san.photon.ts;

public class InputException extends Exception {

	public static final int INVALID_INPUT = 0;

	public static final int INVALID_DATE_FORMAT = INVALID_INPUT + 1;
	public static final int FUTURE_DATE = INVALID_DATE_FORMAT + 1;
	public static final int FUTURE_TIME = FUTURE_DATE + 1;

	public static final int MISSING_USER_NAME = FUTURE_TIME + 1;
	public static final int MISSING_PWD = MISSING_USER_NAME + 1;
	public static final int MISSING_INPUT_FILE = MISSING_PWD + 1;
	public static final int MISSING_PROJECT_INFO = MISSING_INPUT_FILE + 1;
	public static final int MISSING_PROJECT_TASK_INFO = MISSING_PROJECT_INFO + 1;

	private int mErrorCode = -1;

	public InputException(int aErrorCode) {
		mErrorCode = aErrorCode;
	}

	/**
	 * Invalid input, Inputs should be formatted in following order.
	 * <p/>
	 * Each input should be formatted as "Key:Value" pairs.
	 * </p>
	 * <li>1) UserName:Username to login into pulse system</li>
	 * <li>2) Password:Password to login into pulse system</li>
	 * <li>3) Project:Any word present in the project selection list</li>
	 * <li>4) Task type:[Quick/Meeting/Any JIRA ID]</li>
	 * </p>
	 * <li><i>5)<b> Task Definition</i> </b></li>
	 * <li>Date:dd/MM/yyyy</li>
	 * <li>StartTime-EndTime:Comment</li>
	 * </p>
	 * 
	 * <i><b> Sample task definition</i> </b>
	 * </p>
	 * 
	 * Date :29/06/2016 <br/>
	 * 8-9:Daily scrum meeting. <br/>
	 * 9-12:Daily scrum meeting. <br/>
	 * 13-16:Daily scrum meeting. <br/>
	 * 17-19:Onsite sync up call.<br/>
	 */
	@Override
	public String getMessage() {

		switch (mErrorCode) {

		case INVALID_DATE_FORMAT: {

			return "Date format is wrong. Supported format is [dd/MM/yyyy]";
		}
		case FUTURE_DATE:
		case FUTURE_TIME: {
			return "You can't enter task for future";
		}
		case MISSING_USER_NAME: {

			return "User name is missing";
		}
		case MISSING_PWD: {

			return "Password is missing";
		}
		case MISSING_INPUT_FILE: {

			return "Not able to locate input file. It should be present at [user/TimeSheetInput.txt]";
		}
		case MISSING_PROJECT_INFO: {
			return "Project information is missing.Required format => [Project :ProjectName]";
		}
		case MISSING_PROJECT_TASK_INFO: {

			return "Task type information is missing.Required format => [Task Type :Quick]";
		}

		default: { // INVALID_INPUT
			return "Invalid input, Inputs should be formatted in following order.<p/>Each input should be formatted as 'Key:Value' pairs."
					+ "</p><li>1) UserName:Username to login into pulse system</li>"
					+ "<li>2) Password:Password to login into pulse system</li>"
					+ "<li>3) Project:Any word present in the project selection list</li>"
					+ "<li>4) Task type:[Quick/Meeting/Any JIRA ID]</li></p>"
					+ "<li><i>5)<b> Task Definition</i> </b></li><li>Date:dd/MM/yyyy</li>"
					+ "<li>StartTime-EndTime:Comment</li></p><i><b> Sample task definition</i> "
					+ "</b></p>Date :29/06/2016 <br/>8-9:Daily scrum meeting. <br/>9-12:Daily scrum meeting. "
					+ "<br/>13-16:Daily scrum meeting. <br/>17-19:Onsite sync up call.<br/>";
		}
		}
	}

	public String toString() {
		return ("Exception = " + getMessage());
	}
}
