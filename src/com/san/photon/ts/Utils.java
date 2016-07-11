package com.san.photon.ts;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.san.photon.ts.model.Project;
import com.san.photon.ts.model.Task;

public class Utils {

	public static void wait(int lTime) {
		try {
			Thread.sleep(lTime);
		} catch (Exception e) {
		}
	}

	public static List<Task> buildTask(Scanner aScanner) throws IOException {

		List<Task> lTask = new ArrayList<Task>();
		try {
			Project lPro = buildProject(aScanner);
			while (aScanner.hasNextLine()) {
				lTask.add(buildTask(aScanner, lPro));
			}

		} catch (Exception lExe) {
			throw new IOException("Input file formate is wrong - " + lExe.getMessage());
		}

		return lTask;
	}

	private static Date lDate = null;

	private static Task buildTask(Scanner aScanner, Project lPro) throws IOException, ParseException {

		String lTxt = aScanner.nextLine();

		if (getName(lTxt).contains(Constants.Model.DATE)) {
			lDate = Constants.INPUT_DATE_FORMATTER.parse(getValue(lTxt));
			lTxt = aScanner.nextLine();
		}

		String[] lKeyValue = lTxt.split(":");
		String[] lTimeRange = null;
		String lComment = "";

		if (null != lKeyValue && lKeyValue.length >= 2) {
			lTimeRange = lKeyValue[0].split("-");
			lComment = lKeyValue[1];
		}

		if (null == lDate || null == lComment || 0 == lComment.length() || null == lTimeRange
				|| lTimeRange.length < 2) {
			throw new IOException("Wrong pattern. Not able to create Task...]");
		}
		Task lTask = new Task();
		lTask.setProject(lPro);
		lTask.setDate(lDate);
		lTask.setFrom(Double.parseDouble(lTimeRange[0]));
		lTask.setTo(Double.parseDouble(lTimeRange[1]));
		lTask.setComment(lComment);

		return lTask;
	}

	private static Project buildProject(Scanner aScanner) throws IOException {
		String lTxt = aScanner.nextLine();

		String lProjectName = null, lTaskType = null;

		if (getName(lTxt).contains(Constants.Model.PROJECT)) {
			lProjectName = getValue(lTxt);
		}

		lTxt = aScanner.nextLine();
		if (getName(lTxt).contains(Constants.Model.TASK_TYPE)) {
			lTaskType = getValue(lTxt);
		}

		if (null == lProjectName || null == lTaskType) {
			throw new IOException("Missing [Pro-Name, Pro-Value][" + lProjectName + "," + lTaskType + "]");
		}
		Project lPro = new Project();
		lPro.setProjectName(lProjectName);
		lPro.setTaskType(lTaskType);
		return lPro;
	}

	private static String getName(String lTxt) throws IOException {

		return lTxt.split(":")[0];
	}

	private static String getValue(String lTxt) throws IOException {

		return lTxt.split(":")[1];
	}

	public static String getMonthValueStr(Date aDate) {
		return Constants.CAL_MONTH_FORMATTER.format(aDate);
	}

	public static int getMonthIndex(String aMonthStr) {
		int lMonthIdx = -1;
		try {
			Date date = Constants.CAL_MONTH_FORMATTER.parse(aMonthStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			lMonthIdx = cal.get(Calendar.MONTH);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return lMonthIdx;
	}

	public static void printError(String lMsg, String lError) {

		if (Constants.DEBUG) {
			System.out.println("[" + lMsg + "][" + lError + "]");
		} else {
			System.out.println("[" + lMsg + "]");
		}
	}
}
