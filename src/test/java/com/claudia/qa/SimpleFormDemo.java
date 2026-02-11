package com.claudia.qa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SimpleFormDemo {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test

    void validateSimpleFormDemoMessage(){

        // 1. Open Selenium 
        driver.get("https://www.testmuai.com/selenium-playground/");

        // 2. Click “Simple Form Demo”
        WebElement simpleFormLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.linkText("Simple Form Demo")
                )
                
        );
        
        simpleFormLink.click();
       
         

        // 3. Validate URL contains “simple-form-demo”
        wait.until(ExpectedConditions.urlContains("simple-form-demo"));
        Assertions.assertTrue(
                driver.getCurrentUrl().contains("simple-form-demo"),
                "URL does not contain 'simple-form-demo'"
        );

        // 4. Create variable
        String message = "Welcome to TestMu AI";

        // 5. Enter message in text box
        WebElement messageInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("user-message")
                )
        );
        messageInput.sendKeys(message);

        // 6. Click “Get Checked Value”
        WebElement getCheckedValueButton = driver.findElement(
                By.xpath("//button[contains(text(),'Get Checked Value')]")
        );
        getCheckedValueButton.click();

        // 7. Validate displayed message
        WebElement displayedMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("message")
                )
        );

        Assertions.assertEquals(
                message,
                displayedMessage.getText(),
                "Displayed message does not match entered message"
        );
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
