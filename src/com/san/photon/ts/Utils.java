package com.san.photon.ts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

import com.san.photon.ts.model.Project;
import com.san.photon.ts.model.Task;
import com.san.photon.ts.model.User;

public class Utils {

	private static final File mExeutionHome = new File(
			TimeSheetFiller.class.getProtectionDomain().getCodeSource().getLocation().getPath());

	public static void wait(int lTime) {
		try {
			Thread.sleep(lTime);
		} catch (Exception e) {
		}
	}

	public static File getExecutionEnvironment() {
		return mExeutionHome;
	}

	public static String getExecutionEnvironmentPath() {
		return mExeutionHome.getParentFile().getAbsolutePath();
	}

	public static boolean isInputFileAvailable() {

		File lInputFile = new File(getExecutionEnvironmentPath() + Constants.INPUT_PATH);
		System.out.println("[Searching Input file path in][" + lInputFile.getAbsolutePath() + "]");
		return lInputFile.exists();
	}

	public static File getInputFile() {

		return new File(getExecutionEnvironmentPath().concat(File.separator).concat(Constants.INPUT_PATH));
	}

	public static void setChromeDriverProperty() {

		String lChromDriverPath = Utils.getExecutionEnvironmentPath().concat(File.separator)
				.concat(Constants.CHROM_DRIVER_PATH);
		System.out.println("[Searching Chrome drover in][" + lChromDriverPath + "]");
		System.setProperty(Constants.DRIVER_KEY, lChromDriverPath);
	}

	public static User getUserCredential(File aFile) {

		POIFSFileSystem fs;
		try {
			fs = new POIFSFileSystem(new FileInputStream(aFile));
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);

			String lUserName = sheet.getRow(1).getCell(1).getStringCellValue();
			String lPassword = sheet.getRow(2).getCell(1).getStringCellValue();

			User lUser = new User();
			lUser.setUserName(lUserName);
			lUser.setPassword(lPassword);
			return lUser;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List<Task> buildTask(File aFile) throws IOException {

		int lFirstTaskRowIndex = 8;
		POIFSFileSystem fs = null;
		List<Task> lTaskList = new ArrayList<Task>();

		try {
			fs = new POIFSFileSystem(new FileInputStream(aFile));
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);

			int lRowCount = sheet.getPhysicalNumberOfRows();
			if (lRowCount < lFirstTaskRowIndex) {
				return null;
			}

			Project lPro = buildProject(aFile, sheet);
			int lTaskRowIdx = lFirstTaskRowIndex;
			Date lDate = null;
			String lFrmTime, lToTime, lTaskDes;
			while (true) {

				Cell lCell = sheet.getRow(lTaskRowIdx).getCell(0);

				int lType = lCell.getCellType();
				if (lCell != null && Cell.CELL_TYPE_BLANK != lType) {
					if (lType == Cell.CELL_TYPE_STRING) {

						String lValue = lCell.getStringCellValue();
						if (Constants.DEBUG) {
							System.out.println("[Data Value- In String format][" + lCell.getDateCellValue() + "]");
						}

						try {
							lDate = Constants.INPUT_DATE_FORMATTER.parse(lValue);
						} catch (ParseException e) {
							e.printStackTrace();
							throw new IOException("[Wrong pattern. While reading date][" + lValue + "]");
						}
					} else if (HSSFDateUtil.isCellDateFormatted(lCell)) {

						if (Constants.DEBUG) {
							System.out.println("[Data Value- In Date format][" + lCell.getDateCellValue() + "]");
						}

						lDate = lCell.getDateCellValue();
					}
				}

				lFrmTime = getStringFromCell(sheet.getRow(lTaskRowIdx).getCell(1));
				lToTime = getStringFromCell(sheet.getRow(lTaskRowIdx).getCell(2));
				lTaskDes = getStringFromCell(sheet.getRow(lTaskRowIdx).getCell(3));

				if (isEmpty(lFrmTime) && isEmpty(lToTime) && isEmpty(lTaskDes)) {
					return lTaskList;
				}

				if (null == lDate || 0 == lTaskDes.length() || 0 == lFrmTime.length() || 0 == lToTime.length()) {
					throw new IOException("Wrong pattern. Not able to create Task...]");
				}

				Task lTask = new Task();
				lTask.setProject(lPro);
				lTask.setDate(lDate);
				lTask.setFrom(Double.parseDouble(lFrmTime));
				lTask.setTo(Double.parseDouble(lToTime));
				lTask.setComment(lTaskDes);

				lTaskList.add(lTask);
				lTaskRowIdx++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return lTaskList;
	}

	private static Project buildProject(File aFile, HSSFSheet aSheet) throws IOException {

		int proIdex = 4;

		String lProjectName = aSheet.getRow(proIdex++).getCell(1).getStringCellValue();
		String lTaskType = aSheet.getRow(proIdex).getCell(1).getStringCellValue();

		if (null == lProjectName || null == lTaskType) {
			throw new IOException("Missing [Pro-Name, Pro-Value][" + lProjectName + "," + lTaskType + "]");
		}
		Project lPro = new Project();
		lPro.setProjectName(lProjectName);
		lPro.setTaskType(lTaskType);
		return lPro;
	}

	private static String getStringFromCell(Cell aCell) {

		if (aCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return String.valueOf(aCell.getNumericCellValue());
		} else if (aCell.getCellType() == Cell.CELL_TYPE_STRING) {
			return aCell.getStringCellValue();
		}
		return aCell.getStringCellValue();
	}

	@Deprecated
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

	private static Date sDate = null;

	@Deprecated
	private static Task buildTask(Scanner aScanner, Project lPro) throws IOException, ParseException {

		String lTxt = aScanner.nextLine();

		if (getName(lTxt).contains(Constants.Model.DATE)) {
			sDate = Constants.INPUT_DATE_FORMATTER.parse(getValue(lTxt));
			lTxt = aScanner.nextLine();
		}

		String[] lKeyValue = lTxt.split(":");
		String[] lTimeRange = null;
		String lComment = "";

		if (null != lKeyValue && lKeyValue.length >= 2) {
			lTimeRange = lKeyValue[0].split("-");
			lComment = lKeyValue[1];
		}

		if (null == sDate || null == lComment || 0 == lComment.length() || null == lTimeRange
				|| lTimeRange.length < 2) {
			throw new IOException("Wrong pattern. Not able to create Task...]");
		}
		Task lTask = new Task();
		lTask.setProject(lPro);
		lTask.setDate(sDate);
		lTask.setFrom(Double.parseDouble(lTimeRange[0]));
		lTask.setTo(Double.parseDouble(lTimeRange[1]));
		lTask.setComment(lComment);

		return lTask;
	}

	@Deprecated
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

	public static boolean isEmpty(String aStr) {

		return (aStr == null || aStr.length() == 0);
	}
}
