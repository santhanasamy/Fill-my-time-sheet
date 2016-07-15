package com.san.photon.ts;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {

	public static final boolean DEBUG = false;

	public static final String DRIVER_KEY = "webdriver.chrome.driver";

	public static final String USER_DIR = "user";

	public static final String INPUT_FILE_TXT = "TimeSheetInput.txt";

	public static final String INPUT_FILE_XLS = "TimeSheetInput.xls";

	// "\\user\\TimeSheetInput.txt"
	public static final String INPUT_PATH = USER_DIR.concat(File.separator).concat(INPUT_FILE_XLS);

	public static final String CHROM_DIR = "chromedriver_win32";

	public static final String CHROM_EXE = "chromedriver.exe";

	// "\\user\\chromedriver_win32\\chromedriver.exe";
	public static final String CHROM_DRIVER_PATH = USER_DIR.concat(File.separator).concat(CHROM_DIR)
			.concat(File.separator).concat(CHROM_EXE);

	public static final String CHROME_DRIVER_EXE_PATH = "C:/Users/santhanasamy_a/Desktop/Selinium/chromedriver_win32/chromedriver.exe";

	public static final String URL_TIME_SHEET_HOME = "https://corp.photoninfotech.com/timetracker/html/index.html#home";

	public static final String URL_TIME_SHEET_LANDING_SCREEN = "https://corp.photoninfotech.com/timetracker/html/index.html";

	public static final String TIME_SHEET_LANDING_ASSERT_STRING = "::Time Tracker::";

	public static final SimpleDateFormat INPUT_DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");

	public static final SimpleDateFormat CAL_MONTH_FORMATTER = new SimpleDateFormat("MMMM", Locale.US);

	public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");

	public interface ElementID {

		String USER_NAME = "username";
		String PASSWORD = "password";
		String LOGIN_BTN = ".login-details form button";

		String MENU_ENTRY = "entryselect";
		String MENU_VIEW_TIME_SHEET = "viewtimesheet";
		String MENU_DEFAULT = "default";

		String DATE_PICKER = "datepicker";

		String FROM_TIME_HR_TXT = "from-time-display";

	}

	public interface ElementXPathID {

		String DATE_PICKER = "//*[@id='ui-datepicker-div']/table";

		String MONTH_TXT_VIEW = "//*[@id='ui-datepicker-div']/div/div/span[1]";

		String YEAR_TXT_VIEW = "//*[@id='ui-datepicker-div']/div/div/span[2]";

		String PREVIOUS_MONTH_BTN = "//*[@id='ui-datepicker-div']/div/a[1]";

		String NEXT_MONTH_BTN = "//*[@id='ui-datepicker-div']/div/a[2]";

		String SINGLE_DATE_BOX = "//*[@id='ui-datepicker-div']/table/tbody/tr[%d]/td[%d]";

		String NOTIFICATION_POP_CLOSE_BTN = "//*[@id='myModal']/div[1]/button";
	}

	public interface ClassName {
		String FROM_RANGE_SLIDE = "ui-slider-range ui-widget-header ui-slider-range-min";
		String FROM_RANGE_SLIDE_HANDLE = "ui-slider-handle ui-state-default ui-corner-all";

		String FROM_TIME_HR_TXT_PARENT = "hrsbg hrs brdr6";
		String FROM_TIME_HR_TXT = "hrs-txt";
	}

	public interface Model {
		String U_NAME = "Uname";
		String PWD = "Password";
		String PROJECT = "Project";
		String TASK_TYPE = "Task Type";
		String DATE = "Date";

		String TASK_TYPE_MEETING = "Meeting";
		String TASK_TYPE_QUICK = "Quick";
	}

}
