import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TutorAutomation {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		WebDriver driver = new EdgeDriver();
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2000));
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2000));
		login(driver, wait);
		classroom(driver);
		schedule(driver, wait);
		session(driver, wait);
		driver.navigate().refresh();
		driver.quit();
	}

	// Navigate to the login page as tutor
	public static void login(WebDriver driver, WebDriverWait wait) throws InterruptedException {

		driver.get("https://staging-web.wise.live");

		driver.findElement(By.xpath("//div[contains(@class,'primary--text')]")).click();

		Thread.sleep(2000);
		driver.findElement(By.xpath("//span[normalize-space()='Continue with Mobile']/parent::button")).click();

		WebElement phoneNumberField = driver.findElement(By.xpath("//input[@type='tel']"));
		phoneNumberField.sendKeys("1111100000");

		WebElement sendOtpButton = driver.findElement(By.xpath("//button[contains(@class,'mt-5')]"));
		sendOtpButton.click();

		otp(driver);

		Thread.sleep(2000);

		// Assert the institute name
		WebElement instituteName = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(@class,'institute-title')]")));
		Assert.assertEquals(instituteName.getText(), "Testing Institute");

	}

	// Providing OTP
	public static void otp(WebDriver driver) {
		List<WebElement> otpFields = driver.findElements(By.xpath("//input[@autocomplete='one-time-code']"));

		for (WebElement otpField : otpFields) {

			otpField.sendKeys("0");
		}
		WebElement verifyButton = driver.findElement(By.xpath("//span[normalize-space()='Verify']/parent::button"));
		verifyButton.click();
	}

	// Navigate to the classroom
	public static void classroom(WebDriver driver) {

		WebElement groupCoursesTab = driver.findElement(By.xpath("//a[contains(@href,'live-course')]"));
		groupCoursesTab.click();

		WebElement classroomLink = driver
				.findElement(By.xpath("//a[contains(text(),'Classroom for Automated testing')]"));
		classroomLink.click();

		WebElement classroomHeader = driver
				.findElement(By.xpath("//div[contains(text(),'Classroom for Automated testing')]"));
		Assert.assertEquals(classroomHeader.getText(), "Classroom for Automated testing");
	}

	// Schedule a session
	public static void schedule(WebDriver driver, WebDriverWait wait) throws InterruptedException {

		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d");
		String formattedDay = today.format(formatter);

		// Get the current system time
		LocalTime currentTime = LocalTime.now();
		LocalTime targetTime = LocalTime.of(22, 0); // 10:00 PM

		if (currentTime.isAfter(targetTime)) {
			today = today.plusDays(1); // Select next day
			formattedDay = today.format(formatter);
		}

		WebElement liveSessionsTab = driver.findElement(By.xpath("//a[contains(@href,'livesessions')]"));
		liveSessionsTab.click();

		WebElement scheduleSessionsButton = driver
				.findElement(By.xpath("//span[normalize-space()='Schedule sessions']/parent::button"));
		scheduleSessionsButton.click();

		WebElement addSessionButton = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//span[normalize-space()='Add session']/parent::button")));
		Thread.sleep(2000);

		addSessionButton.click();
		Thread.sleep(3000);

		WebElement dateText = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//div[contains(@style,'width')]//div[@aria-haspopup='true']")));
		dateText.click();

		List<WebElement> dates = driver.findElements(By.xpath("//div[@class='v-btn__content']"));

		for (WebElement date : dates) {
			if (date.getText().equals(formattedDay)) {
				date.click();
				break;
			}
		}

		Thread.sleep(3000);
		driver.findElement(By.xpath("//div[@role='combobox']")).click();

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(
				"document.querySelector('.v-menu__content.theme--light.menuable__content__active.v-autocomplete__content').scrollTop=1950");

		Thread.sleep(2000);

		driver.findElement(By.xpath("//div[text()='10:00']")).click();

		String slot = driver.findElement(By.xpath("//div[@class='text--16']")).getText();

		if (slot.equalsIgnoreCase("AM")) {
			driver.findElement(By.xpath("//div[@class='text--16']")).click();
		}

		driver.findElement(By.xpath("//span[normalize-space()='Create']/parent::button")).click();

	}

	// Assert the session details
	public static void session(WebDriver driver, WebDriverWait wait) throws InterruptedException {

		WebElement sessionTab = driver
				.findElement(By.xpath("//div[@class='session-list']//div[contains(@class,'row')]"));
		Assert.assertTrue(sessionTab.isDisplayed());

		String selectedDate = driver
				.findElement(By.xpath("//div[@class='session-list']//div[contains(@class,'text--12')]")).getText();

		driver.findElement(By.xpath("//div[@class='session-list']//div[contains(@class,'row')]")).click();

		WebElement sessionCard = driver.findElement(By.xpath("//div[@class='session-card-container']"));
		Assert.assertTrue(sessionCard.isDisplayed());

		WebElement sessionDetail = driver
				.findElement(By.xpath("//div[@class='session-card-container']//div[text()='Live session']"));

		WebElement instructorDetail = driver
				.findElement(By.xpath("//div[@class='session-card-container']//div[text()='HOSTED BY WISE TESTER']"));

		WebElement timeDetail = driver
				.findElement(By.xpath("//div[@class='session-card-container']//div[contains(@class,'text--12')]"));

		WebElement statusDetail = driver
				.findElement(By.xpath("//div[@class='session-card-container']//span[normalize-space()='Upcoming']"));

		Assert.assertEquals(sessionDetail.getText(), "Live session");
		Assert.assertEquals(instructorDetail.getText(), "HOSTED BY WISE TESTER");
		Assert.assertEquals(timeDetail.getText(), selectedDate);
		Assert.assertEquals(statusDetail.getText(), "Upcoming");

		/*
		 * WebElement closeButton =
		 * wait.until(ExpectedConditions.visibilityOfElementLocated( By.xpath(
		 * "//div[@class='session-card-container']//button[@aria-haspopup='true']")));
		 * 
		 * closeButton.click();
		 * 
		 * List<WebElement> options =
		 * driver.findElements(By.xpath("//div[@role='menuitem']")); for (WebElement
		 * option : options) {
		 * 
		 * if (option.getText().equalsIgnoreCase("Delete")) { option.click(); break; } }
		 * Thread.sleep(2000); driver.findElement(By.
		 * xpath("//span[normalize-space() = 'Delete']/parent::button")).click();
		 */

	}

}
