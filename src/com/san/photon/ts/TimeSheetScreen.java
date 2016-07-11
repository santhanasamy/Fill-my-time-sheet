package com.san.photon.ts;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import com.san.photon.ts.Constants.ElementXPathID;
import com.san.photon.ts.model.Task;

public class TimeSheetScreen {

	private static final int SLIDE_MIN_THRESHOLD = 4;

	private WebDriver mDriver = null;
	private WebElement mFromSlider = null;
	private WebElement mFromSliderFilled = null;
	private WebElement mFromSliderHandle = null;
	private WebElement mFromHrTime = null;
	private WebElement mFromMinTime = null;

	private WebElement lToSlider = null;
	private WebElement lToSliderFilled = null;
	private WebElement lToSliderHandle = null;
	private WebElement lToHrTime = null;
	private WebElement lToMinTime = null;

	private WebElement mProSelectSpinner = null;

	private WebElement mTaskToSelect = null;

	private WebElement mCommentTextView = null;

	private WebElement mSaveBtn = null;

	private WebElement mSaveAndNextBtn = null;

	private WebElement mCalButton = null;

	private WebElement monthTxt = null;

	private WebElement mPreMonthButton = null;

	private WebElement mNextMonthButton = null;

	private WebElement mProTaskSelectSpinner = null;

	public TimeSheetScreen(WebDriver aDriver) {
		mDriver = aDriver;
		initUIComponent();
	}

	public void initUIComponent() {

		boolean isUIInitialized = false;
		while (true) {

			try {
				// 1. Init Calendar
				mCalButton = mDriver.findElement(By.xpath("//*[@id='Leave_dis']/div[2]/a/button"));

				monthTxt = mDriver.findElement(By.xpath("//*[@id='ui-datepicker-div']/div/div/span[1]"));
				mPreMonthButton = mDriver.findElement(By.xpath("//*[@id='ui-datepicker-div']/div/a[1]"));
				mNextMonthButton = mDriver.findElement(By.xpath("//*[@id='ui-datepicker-div']/div/a[2]"));
				// l = mDriver.findElement(By.xpath(""));

				// 2. Init Project & Task type [Quick, Other]
				mProSelectSpinner = mDriver.findElement(By.xpath("//*[@id='selproject_chzn']/a/div/b"));

				// 3. Init From Time
				mFromHrTime = mDriver.findElement(By.xpath("//*[@id='from-time-display']"));

				mFromSlider = mDriver.findElement(By.xpath(
						"//div[@class='ui-slider ui-slider-horizontal ui-widget ui-widget-content ui-corner-all']"));

				mFromSliderFilled = mDriver
						.findElement(By.xpath("//div[@class='ui-slider-range ui-widget-header ui-slider-range-min']"));

				mFromSliderHandle = mDriver
						.findElement(By.xpath("//a[@class='ui-slider-handle ui-state-default ui-corner-all']"));

				// 4. Init to Time
				lToHrTime = mDriver.findElement(By.xpath("//*[@id='to-time-display']"));

				lToSlider = mDriver.findElement(By.xpath("//*[@id='to-time-select']"));

				lToSliderFilled = mDriver.findElement(By.xpath("//*[@id='to-time-select']/div"));

				lToSliderHandle = mDriver.findElement(By.xpath("//*[@id='to-time-select']/a"));

				// 5. Init comment in the comment box;
				mCommentTextView = mDriver.findElement(By.xpath("//*[@id='Engineering']/div[2]/textarea"));

				// 6. Init Save/ Save and Next
				mSaveBtn = mDriver.findElement(By.xpath("//*[@id='btnSave']"));
				mSaveAndNextBtn = mDriver.findElement(By.xpath("//*[@id='btnSaveNext']"));

				isUIInitialized = true;

			} catch (Exception exe) {
				
				Utils.printError("Error While Initing UI Components", exe.getMessage());
				isUIInitialized = false;
			}
			if (isUIInitialized) {
				break;
			}
		}
	}

	public void enterInfo(List<Task> aTask) throws InputException {

		Date lDate = null;

		for (Task lTask : aTask) {

			if (lDate != lTask.getDate()) {
				lDate = lTask.getDate();
				// 0. Choose date
				mCalButton.click();
				chooseDate(lTask);
				Utils.wait(1000);
			}

			switchToMasterWindow();

			// 1. Choose Project
			chooseProject(lTask);

			Utils.wait(5000);

			// 2. Select Task type [Quick, Other]
			chooseProjectTask(lTask);
			Utils.wait(1000);

			// 3. Choose From Time
			performSeek(mFromSlider, mFromHrTime, (int) lTask.getFrom(), 0);
			Utils.wait(1000);

			// 4. Choose to Time
			performSeek(lToSlider, lToHrTime, (int) lTask.getTo(), 0);

			// 5. Enter comment in the comment box;
			mCommentTextView.sendKeys(lTask.getComment());

			// 6. Save/ Save and Next
			if (!Constants.DEBUG) {
				JavascriptExecutor executor = (JavascriptExecutor) mDriver;
				executor.executeScript("arguments[0].click();", mSaveAndNextBtn);
			}
			Utils.wait(5000);
		}

	}

	private void chooseProject(Task lTask) {

		mProSelectSpinner.click();

		String lProjectIdx = "", lStrProName = "";
		int lIdx = 0;
		while (true) {

			lProjectIdx = String.format("//*[@id='selproject_chzn_o_%d']", lIdx++);

			try {

				mTaskToSelect = mDriver.findElement(By.xpath(lProjectIdx));
				lStrProName = mTaskToSelect.getText();

				if (null != lStrProName && lStrProName.length() > 0) {
					if (lStrProName.contains(lTask.getProject().getProjectName())) {
						mTaskToSelect.click();
						break;
					}
				}

			} catch (Exception e) {

				mTaskToSelect = null;

				Utils.printError("Error while choosing project", e.getMessage());
			}

			if (null == mTaskToSelect) {
				break;
			}
		}
	}

	private void chooseProjectTask(Task lTask) {

		mProTaskSelectSpinner = mDriver.findElement(By.xpath("//*[@id='seltracker_chzn']/a/div/b"));
		mProTaskSelectSpinner.click();

		String lTaskIdx = "", lStrTaskName = "";
		int lIdx = 0;

		while (true) {

			lTaskIdx = String.format("//*[@id='seltracker_chzn_o_%d']", lIdx++);

			try {

				mTaskToSelect = mDriver.findElement(By.xpath(lTaskIdx));
				lStrTaskName = mTaskToSelect.getText();

				if (null != lStrTaskName && lStrTaskName.length() > 0) {
					if (lStrTaskName.contains(lTask.getProject().getTaskType())) {
						mTaskToSelect.click();
						break;
					}
				}

			} catch (Exception e) {

				mTaskToSelect = null;
				Utils.printError("Error while choosing project Task", e.getMessage());
			}

			if (null == mTaskToSelect) {
				System.out.println("[Error while choosing project][ Their is no project assigned in your name]");
				closeDriver();
				break;
			}
		}

	}

	private void chooseDate(Task lTask) {

		WebElement lMonthEle = mDriver.findElement(By.xpath(ElementXPathID.MONTH_TXT_VIEW));

		WebElement lYearEle = mDriver.findElement(By.xpath(ElementXPathID.YEAR_TXT_VIEW));

		WebElement lPreMonthBtn = mDriver.findElement(By.xpath(ElementXPathID.PREVIOUS_MONTH_BTN));

		WebElement lNextMonthBtn = mDriver.findElement(By.xpath(ElementXPathID.NEXT_MONTH_BTN));

		Calendar lCal = Calendar.getInstance();// (Calendar.YEAR)
		lCal.setTime(lTask.getDate());

		int lTaskDateInt = lCal.get(Calendar.DATE);
		int lTaskMonthInt = lCal.get(Calendar.MONTH) + 1;
		int lTaskYearInt = lCal.get(Calendar.YEAR);

		String lTaskDateStr = "" + lTaskDateInt;
		String lTaskMonthStr = Utils.getMonthValueStr(lTask.getDate());
		String lTaskYearStr = "" + lTaskYearInt;

		int lVisibleYear = Integer.parseInt(lYearEle.getText());
		int lVisibleMonth = Utils.getMonthIndex(lMonthEle.getText()) + 1;

		if (lTaskYearInt != lVisibleYear || lTaskMonthInt != lVisibleMonth) {

			while (lVisibleYear > lTaskYearInt) {
				lPreMonthBtn.click();
				lYearEle = mDriver.findElement(By.xpath(ElementXPathID.YEAR_TXT_VIEW));
				lVisibleYear = Integer.parseInt(lYearEle.getText());
			}
			while (lVisibleYear < lTaskYearInt) {
				lNextMonthBtn.click();
				lYearEle = mDriver.findElement(By.xpath(ElementXPathID.YEAR_TXT_VIEW));
				lVisibleYear = Integer.parseInt(lYearEle.getText());
			}

			while (lVisibleMonth > lTaskMonthInt) {
				lPreMonthBtn.click();
				lMonthEle = mDriver.findElement(By.xpath(ElementXPathID.MONTH_TXT_VIEW));
				lVisibleMonth = Utils.getMonthIndex(lMonthEle.getText()) + 1;
			}
			while (lVisibleMonth < lTaskMonthInt) {
				lNextMonthBtn.click();
				lMonthEle = mDriver.findElement(By.xpath(ElementXPathID.MONTH_TXT_VIEW));
				lVisibleMonth = Utils.getMonthIndex(lMonthEle.getText()) + 1;
			}

		}

		WebElement lCalTable = mDriver.findElement(By.xpath(ElementXPathID.DATE_PICKER));

		// Now get all the TR elements from the table
		List<WebElement> allRows = lCalTable.findElements(By.tagName("tr"));
		// And iterate over them, getting the cells

		boolean lStarted = false;

		for (int i = 0; i < allRows.size(); i++) {
			WebElement row = allRows.get(i);

			List<WebElement> cells = row.findElements(By.tagName("td"));

			for (int j = 0; j < cells.size(); j++) {
				WebElement cell = cells.get(j);
				System.out.println(cell.getText());

				if (!lStarted) {
					lStarted = "1".equalsIgnoreCase(cell.getText());
				}

				if (lStarted && lTaskDateStr.equalsIgnoreCase(cell.getText())) {

					String lBox = String.format(ElementXPathID.SINGLE_DATE_BOX, i, j + 1);
					mDriver.findElement(By.xpath(lBox)).click();
					return;
				}
			}
		}
	}

	public void performSeek(WebElement aSlider, WebElement aTxtHolder, int aMoveToHr, int aMoveToMin) {

		int lMovePos = 0, lTime = 0;
		while (lTime != aMoveToHr) {

			System.out.println("performSeek[TxtView, Text][" + aTxtHolder + "," + aTxtHolder.getText() + "]");
			lMovePos += SLIDE_MIN_THRESHOLD;

			Actions builder = new Actions(mDriver);
			Action action = builder.moveToElement(aSlider, lMovePos, 0).click().build();
			action.perform();
			lTime = Integer.parseInt(aTxtHolder.getText());
		}
	}

	private void closeDriver() {

		System.out.println("[Closing Driver]");
		if (null == mDriver) {
			return;
		}
		mDriver.close();
		mDriver.quit();
	}

	private void switchToMasterWindow() {

		String master = mDriver.getWindowHandle();
		int timeCount = 1;

		do {
			mDriver.getWindowHandles();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			timeCount++;
			if (timeCount > 10) {
				break;
			}
		} while (mDriver.getWindowHandles().size() == 1);

		mDriver.switchTo().window(master);
	}
}
