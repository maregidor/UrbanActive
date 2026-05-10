package es.upm.dit.isst.grupo10.urbanactive.Selenium;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

class UrbanActiveSeleniumTest {

private WebDriver driver;

private final String BASE_URL = "http://localhost:8081";

@BeforeEach

// Configuración del WebDriver para Google Chrome
void setUp() {
    WebDriverManager.chromedriver().setup();

    ChromeOptions options = new ChromeOptions();
    options.setPageLoadStrategy(PageLoadStrategy.EAGER);

    Map<String, Object> prefs = new HashMap<>();
    prefs.put("profile.default_content_setting_values.geolocation", 2);
    prefs.put("profile.default_content_setting_values.notifications", 2);
    options.setExperimentalOption("prefs", prefs);

    driver = new ChromeDriver(options);
    driver.manage().window().maximize();

    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
    driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(10));
}

// Configuración del WebDriver para Microsoft Edgeç
/* 
void setUp() {
    System.setProperty("webdriver.edge.driver", "src/test/resources/edgedriver_win64/msedgedriver.exe");
    EdgeOptions options = new EdgeOptions();
    options.setPageLoadStrategy(PageLoadStrategy.EAGER);

    Map<String, Object> prefs = new HashMap<>();
    prefs.put("profile.default_content_setting_values.geolocation", 2);
    prefs.put("profile.default_content_setting_values.notifications", 2);
    options.setExperimentalOption("prefs", prefs);

    driver = new EdgeDriver(options);
    driver.manage().window().maximize();

    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
    driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(10));
}
    */

@AfterEach
void tearDown() {
if (driver != null) {
driver.quit();
}
}

@Test
void loginCorrectoDebeEntrarEnActividades() throws InterruptedException {
driver.get(BASE_URL + "/login");

driver.findElement(By.name("username")).sendKeys("usuario1@gmail.com");
driver.findElement(By.name("password")).sendKeys("1234");
driver.findElement(By.cssSelector("button[type='submit']")).click();

Thread.sleep(1000); // Espera para que la página cargue
assertTrue(driver.getCurrentUrl().contains("/actividades"));
}

@Test
void loginIncorrectoDebeMostrarError() {
driver.get(BASE_URL + "/login");

driver.findElement(By.name("username")).sendKeys("usuario1@gmail.com");
driver.findElement(By.name("password")).sendKeys("passwordIncorrecta");
driver.findElement(By.cssSelector("button[type='submit']")).click();

assertTrue(driver.getCurrentUrl().contains("/login"));
assertTrue(driver.getCurrentUrl().contains("error"));
}

/*@Test
void reservarActividadDesdeInterfazDebeMostrarExito() {
login();

driver.get(BASE_URL + "/actividades/1");

WebElement botonReservar = driver.findElement(By.cssSelector("button[type='submit']"));
botonReservar.click();

assertTrue(driver.getCurrentUrl().contains("/reservas/exito")
|| driver.getPageSource().contains("Reserva")
|| driver.getPageSource().contains("confirmada"));
}*/

@Test
void reservarActividadDesdeInterfazDebeMostrarExito() {
    login();

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

    driver.get(BASE_URL + "/actividades");

    wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".activity-card")
    ));

    WebElement primerDetalle = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".activity-card a[href^='/actividades/']")
            )
    );

    ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block: 'center'});",
            primerDetalle
    );

    ((JavascriptExecutor) driver).executeScript(
            "arguments[0].click();",
            primerDetalle
    );

    WebElement botonReservar = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("form.reserve-form button[type='submit']")
            )
    );

    ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block: 'center'});",
            botonReservar
    );

    if (!botonReservar.isEnabled()) {
        assertTrue(
                driver.getPageSource().contains("Ya reservada")
                        || driver.getPageSource().contains("Sin plazas")
        );
        return;
    }

    ((JavascriptExecutor) driver).executeScript(
            "arguments[0].click();",
            botonReservar
    );

    wait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("/reservas/exito"),
            ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(text(),'Reserva') or contains(text(),'confirmada') or contains(text(),'plaza')]")
            )
    ));

    assertTrue(
            driver.getCurrentUrl().contains("/reservas/exito")
                    || driver.getPageSource().contains("Reserva")
                    || driver.getPageSource().contains("confirmada")
                    || driver.getPageSource().contains("plaza")
    );
}

@Test
void accederAMiPerfilDebeMostrarDatosDelUsuario() {
login();

driver.get(BASE_URL + "/mi-perfil");

assertTrue(driver.getPageSource().contains("Mi perfil"));
assertTrue(driver.getPageSource().contains("usuario1@gmail.com"));
}

private void login() {
driver.get(BASE_URL + "/login");

driver.findElement(By.name("username")).sendKeys("usuario1@gmail.com");
driver.findElement(By.name("password")).sendKeys("1234");
driver.findElement(By.cssSelector("button[type='submit']")).click();
}
}