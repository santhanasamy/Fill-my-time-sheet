package com.san.photon.ts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.san.photon.ts.Constants.ElementID;
import com.san.photon.ts.model.Task;

/**
 * Read data from txt files.
 * @author santhanasamy_a
 *
 */
public class TimeSheetFiller {

	private WebDriver mDriver = null;

	private TimeSheetScreen mTimeSheetScreen = null;

	private List<Task> mTask = null;

	private File mExeutionHome = null;

	public static void main(String[] args) {

		new TimeSheetFiller().init();
	}

	private void init() {

		System.out.println("[Initializing the server]");

		mExeutionHome = new File(TimeSheetFiller.class.getProtectionDomain().getCodeSource().getLocation().getPath());

		String lChromDriverPath = mExeutionHome.getParentFile().getAbsolutePath()
				+ "\\user\\chromedriver_win32\\chromedriver.exe";
		System.setProperty(Constants.DRIVER_KEY, lChromDriverPath);

		mDriver = new ChromeDriver();
		mDriver.manage().window().maximize();
		mDriver.get(Constants.URL_TIME_SHEET_HOME);

		Utils.wait(1000);

		loginIntoPhoton();
	}

	private void loginIntoPhoton() {

		WebElement lUserName = mDriver.findElement(By.id(ElementID.USER_NAME));
		WebElement lPassword = mDriver.findElement(By.id(ElementID.PASSWORD));

		Scanner lScanner = null;
		try {

			String path = mExeutionHome.getParentFile().getAbsolutePath() + "\\user\\TimeSheetInput.txt";

			File lInputFile = new File(path);

			if (!lInputFile.exists()) {
				System.out.println("[Error : Input file is missing in the path][" + lInputFile.getAbsolutePath() + "]");
				return;
			}
			System.out.println("[Input file found at][" + path + "]");

			// load the file handle for main.properties
			FileInputStream file = new FileInputStream(path);
			lScanner = new Scanner(file);
			lUserName.sendKeys(lScanner.nextLine().split(":")[1]);

			String lPasswordStr = lScanner.nextLine();
			int x = lPasswordStr.indexOf(":") + 1;
			lPasswordStr = lPasswordStr.substring(x, lPasswordStr.length());
			lPassword.sendKeys(lPasswordStr);

			mTask = Utils.buildTask(lScanner);
		} catch (FileNotFoundException e) {

			System.out.println("[Error][" + e.getMessage() + "]");
			return;
		} catch (IOException e) {
			System.out.println("[Error][" + e.getMessage() + "]");
			return;
		} finally {
			lScanner.close();
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