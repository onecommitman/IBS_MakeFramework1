package ru.ibs.tests;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RosGosStrah {
    WebDriver driver;
    WebDriverWait wait;

    @Before
    public void before(){
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\User\\IdeaProjects\\MakeFrameworkSelenium\\src\\test\\resources\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10, 2000);
        driver.get("https://rgs.ru/"); //история перехода не сохраняется
        //driver.navigate().to("https://rgs.ru/"); //история перехода сохраняется
        driver.manage().window().maximize(); //на весь экран
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);

        //driver.manage().timeouts().setScriptTimeout(); //для выполнения JS-скрипта
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }
    @Test
    public void test(){
        //3. Компаниям
        WebElement baseMenu = driver.findElement(By.xpath("//a[contains(@href, '/for-companies')]"));
        wait.until(ExpectedConditions.elementToBeClickable(baseMenu));
        baseMenu.click();

        //3. Компаниям -> Здоровье
        WebElement subMenu = driver.findElement(By.xpath("//span[text()='Здоровье' and contains(@class, 'padding')]")); //обнаружили кнопку Здоровье
        wait.until(ExpectedConditions.elementToBeClickable(subMenu)); //Ожидаем пока кнопка будет доступна
        subMenu.click();

        //3. Компаниям -> Здоровье -> Добровольное медицинское страхование
        WebElement subMenuTransit = driver.findElement(By.xpath("//a[text()='Добровольное медицинское страхование' and contains(@href, 'strakhovanie')]"));
        subMenuTransit.click();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //4. Проверить наличие заголовка "Добровольное медицинское страхование"
        WebElement titleInsurancePage = driver.findElement(By.xpath("//h1[text()='Добровольное медицинское страхование' and contains(@class, 'title word-breaking')]"));

        Assert.assertTrue("Страница не загрузилась", titleInsurancePage.isDisplayed() && titleInsurancePage.getText().contains("Добровольное медицинское страхование"));
        //h1[text()='Добровольное медицинское страхование' and contains(@class, 'title word-breaking')]

        //5. Нажать кноку "Отправить заявку"
        WebElement sendAnApplicationButton = driver.findElement(By.xpath("//button[contains(@type, 'button')]"));
        sendAnApplicationButton.click();

        //6. Проверить, что открылась нужная страница
        WebElement titleCallBack = driver.findElement(By.xpath("//h2[contains(@class, 'section-basic__title title--h2 word-breaking title--h2') and contains(./text(), 'Оперативно перезвоним')]"));
        Assert.assertTrue("Страница не загрузилась", titleCallBack.isDisplayed());

        fillInputField(driver.findElement(By.xpath("//input[contains(@name, 'userName')]")), "Иванов Иван Иванович");
        fillInputFieldPhone(driver.findElement(By.xpath("//input[contains(@name, 'userTel')]")), "9991234567");
        fillInputField(driver.findElement(By.xpath("//input[contains(@name, 'userEmail')]")), "qwertyqwerty");
        fillInputField(driver.findElement(By.xpath("//input[@placeholder='Введите']")), "Москва");

        List<WebElement> adressList = driver.findElements(By.xpath("//input[@placeholder='Введите']/../..//span[contains(@class, item)]"));
        for(WebElement element:adressList){
            if(element.getText().contains("Москва")){
                element.click();
                break;
            }
        }

        WebElement checkbox = driver.findElement(By.xpath("//div//input[contains(@class, 'checkbox')]"));
        Actions action = new Actions(driver);
        action.click(checkbox).build().perform();

        WebElement submitButton = driver.findElement(By.xpath("//div//button[contains(@type, 'submit')]"));
        submitButton.click();

        //10. У поля ввода адреса электронной почты присутствует сообщение об ошибке
        WebElement errorMailMessage = driver.findElement(By.xpath("//div//span[contains(@class, 'input__error text--small')]"));
        errorMailMessage.isDisplayed();

        //Пауза перед закрытием браузера
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.quit();//Закрытие браузера, завершение сессии
    }

    private void scrollToElementJs(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private void fillInputField(WebElement element, String value) {
        scrollToElementJs(element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        //element.clear();
        element.sendKeys(value);
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", value));
        Assert.assertTrue("Поле было заполнено некорректно", checkFlag);
    }
    private void fillInputFieldPhone(WebElement element, String value) {
        scrollToElementJs(element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        element.clear();
        element.sendKeys(value);

        String number = value.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "+7 ($1) $2-$3");
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", number));
        Assert.assertTrue("Поле было заполнено некорректно", checkFlag);
    }
}
