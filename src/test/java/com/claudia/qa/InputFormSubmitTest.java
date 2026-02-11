package com.claudia.qa;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InputFormSubmitTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

       
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    public void inputFormSubmitValidation() {
        driver.get("https://www.testmuai.com/selenium-playground/");

        // 1) Click “Input Form Submit”
        clickStable(By.linkText("Input Form Submit"));

        // Espera a que el formulario exista 
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("inputEmail4")));

        // 2) Click “Submit” vacío
        clickStable(By.cssSelector("button.selenium_btn[type='submit']"));

        // 3) Assert mensaje required (navegador)
        WebElement nameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("name")));
        String validationMessage = nameField.getAttribute("validationMessage");

        // Acepta inglés o español según idioma del navegador
        assertTrue(
                validationMessage.toLowerCase().contains("please fill") ||
                validationMessage.toLowerCase().contains("completa") ||
                validationMessage.toLowerCase().contains("rellena"),
                "Validation message inesperado: " + validationMessage
        );

        // 4) Fill en Name, Email y demás
        typeStable(By.name("name"), "Claudia Sanchez");
        typeStable(By.id("inputEmail4"), "claudia@email.com");   
        typeStable(By.name("password"), "Test1234!");
        typeStable(By.name("company"), "QA Company");
        typeStable(By.name("website"), "https://cardosanz.com");

        // 5) Country: United States 
        WebElement countryEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("country")));
        scrollIntoView(countryEl);
        new Select(countryEl).selectByVisibleText("United States");

        // 6) Resto de campos
        typeStable(By.name("city"), "New York");
        typeStable(By.name("address_line1"), "Estatua Libertad");
        typeStable(By.name("address_line2"), "Apartment 126");

        // STATE 
        typeStable(By.id("inputState"), "NY");

        // ZIP 
        typeStable(By.id("inputZip"), "10001");

        // Submit final
        clickStable(By.cssSelector("button.selenium_btn[type='submit']"));

        // 7) Validar success message (BODY contains)
        String expectedMessage = "Thanks for contacting us, we will get back to you shortly.";

        wait.until(d -> d.findElement(By.tagName("body")).getText().contains(expectedMessage));
        String bodyText = driver.findElement(By.tagName("body")).getText();

        assertTrue(bodyText.contains(expectedMessage),
                "No se encontró el mensaje de éxito en el body.");
    }

    // ---------- Helpers ----------

    private void typeStable(By locator, String text) {
        for (int i = 0; i < 3; i++) {
            try {
                WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                scrollIntoView(el);
                wait.until(ExpectedConditions.visibilityOf(el));
                el.clear();
                el.sendKeys(text);
                return;
            } catch (StaleElementReferenceException ignored) {
                // reintenta
            }
        }
        
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        scrollIntoView(el);
        el.clear();
        el.sendKeys(text);
    }

    private void clickStable(By locator) {
        for (int i = 0; i < 3; i++) {
            try {
                WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                scrollIntoView(el);

              
                wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
                return;

            } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
         
                try {
                    WebElement el2 = driver.findElement(locator);
                    scrollIntoView(el2);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el2);
                    return;
                } catch (Exception ignored) { }
            }
        }

     
        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollIntoView(el);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element
        );
    }
}
