package com.san.photon.ts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.san.photon.ts.Constants.ElementID;
import com.san.photon.ts.model.Task;
import com.san.photon.ts.model.User;

/**
 * Using XLS support added.
 * 
 * @author santhanasamy_a
 *
 */
public class TimeSheetFiller1 {

	private WebDriver mDriver = null;

	private TimeSheetScreen mTimeSheetScreen = null;

	private List<Task> mTask = null;

	public static void main(String[] args) {

		PrintStream lPrintStream = null;
		try {
			File lFile = new File("log_file.txt");
			lFile.createNewFile();
			lPrintStream = new PrintStream("log_file.txt");
			PrintStream orig = System.out;
			System.setOut(lPrintStream);
			new TimeSheetFiller1().init();
			System.setOut(orig);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != lPrintStream) {
				lPrintStream.close();
			}
		}
	}

	private void init() {

		System.out.println("[Initializing the server]");

		Utils.setChromeDriverProperty();

		mDriver = new ChromeDriver();
		mDriver.manage().window().maximize();
		mDriver.get(Constants.URL_TIME_SHEET_HOME);

		Utils.wait(1000);
		loginIntoPhoton();
	}

	private void loginIntoPhoton() {

		WebElement lUserName = mDriver.findElement(By.id(ElementID.USER_NAME));
		WebElement lPassword = mDriver.findElement(By.id(ElementID.PASSWORD));

		try {
			File lFile = Utils.getInputFile();
			if (!lFile.exists()) {
				System.out.println("[Error : Input file is missing in the path]");
				return;
			}

			User lUser = Utils.getUserCredential(lFile);

			if (null == lUser) {
				System.out.println("[Error : While reading user credentials]");
				return;
			}
			lUserName.sendKeys(lUser.getUserName());
			lPassword.sendKeys(lUser.getPassword());

			mTask = Utils.buildTask(lFile);
		} catch (FileNotFoundException e) {

			System.out.println("[Error][" + e.getMessage() + "]");
			return;
		} catch (IOException e) {
			System.out.println("[Error][" + e.getMessage() + "]");
			return;
		}

		WebElement loginBtn = mDriver.findElement(By.cssSelector(ElementID.LOGIN_BTN));
		loginBtn.click();

		verifyPageLoaded();
		closeDriver();
	}

	private void closeDriver() {

		System.out.println("[Closing Driver]");
		if (null == mDriver) {
			return;
		}
		mDriver.close();
		mDriver.quit();
	}

	public void verifyPageLoaded() {

		(new WebDriverWait(mDriver, 15)).until(new ExpectedCondition<Boolean>() {

			public Boolean apply(WebDriver d) {

				while (true) {
					try {
						if (d.getPageSource().contains(Constants.TIME_SHEET_LANDING_ASSERT_STRING)) {
							break;
						}
					} catch (Exception e) {
						Utils.printError("Checking home page loading status", e.getMessage());
					}
					Utils.wait(1000);
				}
				try {
					mTimeSheetScreen = new TimeSheetScreen(mDriver);
					mTimeSheetScreen.enterInfo(mTask);
				} catch (InputException e) {
					e.printStackTrace();
					Utils.printError("verifyPageLoaded : Not able to fill time sheet", e.getMessage());
				}

				return true;
			}
		});
	}
}