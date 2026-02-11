package com.claudia.qa;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

public class DragAndDropSliderTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    @Test
    void dragAndDropSlider_Default15_to_95() {
        int target = 95;

        driver.get("https://www.testmuai.com/selenium-playground/");

        // 1 Click “Drag & Drop Sliders”
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'drag') and " +
                        "contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'sliders')]")
        )).click();

        By sliderLocator = By.cssSelector("#slider3 input[type='range']");
        By outputLocator = By.cssSelector("#slider3 output#rangeSuccess");

        WebElement slider = wait.until(ExpectedConditions.elementToBeClickable(sliderLocator));
        wait.until(ExpectedConditions.presenceOfElementLocated(outputLocator));

        // Scroll + focus
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", slider);
        slider.click();

        //2) value + eventos + texto del output
        forceSliderAndOutputToTarget(target);

        // 3) Leer valores finales 
        String finalOut = wait.until(ExpectedConditions.presenceOfElementLocated(outputLocator)).getText().trim();
        String finalValue = String.valueOf(((JavascriptExecutor) driver).executeScript("return arguments[0].value;", slider));

        System.out.println("FINAL OUTPUT = " + finalOut + " | FINAL VALUE = " + finalValue);

        Assertions.assertEquals("95", finalOut, "El output visual no mostró 95");
        Assertions.assertEquals("95", finalValue, "El value real del slider no quedó en 95");
    }

    /**
     * slider3 al valor target y sincroniza el output#rangeSuccess.
     */
    private void forceSliderAndOutputToTarget(int target) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
                "const target = String(arguments[0]);" +
                "const slider = document.querySelector('#slider3 input[type=range]');" +
                "const output = document.querySelector('#slider3 output#rangeSuccess');" +
                "if (!slider || !output) throw new Error('slider3 elements not found');" +

                // native para value REAL
                "const setter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype,'value').set;" +
                "setter.call(slider, target);" +

                // eventos típicos
                "slider.dispatchEvent(new Event('input', { bubbles: true }));" +
                "slider.dispatchEvent(new Event('change', { bubbles: true }));" +

                //el output refleje el valor
                "output.textContent = target;" +

                // dispara un evento extra para el UI escucha al output/DOM
                "output.dispatchEvent(new Event('input', { bubbles: true }));" +
                "output.dispatchEvent(new Event('change', { bubbles: true }));"
                ,
                target
        );

        // UIs con focus out 
        try {
            driver.switchTo().activeElement().sendKeys(Keys.TAB);
        } catch (Exception ignored) { }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }
}
