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
import org.openqa.selenium.support.ui.Select;
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
/* void setUp() {
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
} */

// Configuración del WebDriver para Microsoft Edgeç

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

@Test
void reservarActividadDesdeInterfazDebeMostrarExito() {
    login();

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

    driver.get(BASE_URL + "/actividades");

    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".activity-card")));

    WebElement primerDetalle = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".activity-card a[href^='/actividades/']")));

    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});",primerDetalle);
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();",primerDetalle);

    WebElement botonReservar = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("form.reserve-form button[type='submit']")));

    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});",botonReservar);

    if (!botonReservar.isEnabled()) {
        assertTrue(driver.getPageSource().contains("Ya reservada") || driver.getPageSource().contains("Sin plazas"));
        return;
    }

    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonReservar);

    wait.until(ExpectedConditions.or(ExpectedConditions.urlContains("/reservas/exito"),ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'Reserva') or contains(text(),'confirmada') or contains(text(),'plaza')]"))));

    assertTrue(driver.getCurrentUrl().contains("/reservas/exito") || driver.getPageSource().contains("Reserva") || driver.getPageSource().contains("confirmada") || driver.getPageSource().contains("plaza"));
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


@Test
void crearActividadDebeAparecerEnActividades() throws InterruptedException {
    login();

    driver.get(BASE_URL + "/actividades/nueva");

    String titulo = "Actividad Test " + System.currentTimeMillis();

    driver.findElement(By.name("titulo")).sendKeys(titulo);
    driver.findElement(By.name("tipo")).sendKeys("Cardio");
    driver.findElement(By.name("nivelDificultad")).sendKeys("Principiante");
    driver.findElement(By.name("descripcion")).sendKeys("Actividad creada con Selenium");
    driver.findElement(By.name("imagen")).sendKeys("https://via.placeholder.com/400x200");

    // input type="date" necesita yyyy-MM-dd
    ((JavascriptExecutor) driver).executeScript(
        "document.querySelector('[name=\"fecha\"]').value='2026-12-20';"
    );

    driver.findElement(By.name("hora")).sendKeys("10:30");
    driver.findElement(By.name("duracion")).sendKeys("60 min");

    driver.findElement(By.name("plazasTotales")).clear();
    driver.findElement(By.name("plazasTotales")).sendKeys("10");

    // Por si el precio oculto queda vacío
    ((JavascriptExecutor) driver).executeScript(
        "var p = document.querySelector('[name=\"precio\"]'); if (p) p.value='0';"
    );

    Select espacio = new Select(driver.findElement(By.id("espacioSelect")));

    boolean espacioEncontrado = false;

    for (WebElement option : espacio.getOptions()) {
        String texto = option.getText().toLowerCase();
        System.out.println("ESPACIO: [" + option.getText() + "]");

        if (texto.contains("madrid") && texto.contains("arganzuela")) {
            option.click();
            espacioEncontrado = true;
            break;
        }
    }

    assertTrue(espacioEncontrado);

    WebElement boton = driver.findElement(By.cssSelector("button[type='submit']"));
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", boton);
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", boton);

    Thread.sleep(1500);

    System.out.println("URL actual después de crear: " + driver.getCurrentUrl());

    assertTrue(driver.getCurrentUrl().endsWith("/actividades"));

    driver.get(BASE_URL + "/actividades");
    Thread.sleep(1000);

    assertTrue(driver.getPageSource().contains(titulo));
}


@Test
void usuarioNoPuedeReservarActividadPropia() throws InterruptedException {
    login();

    driver.get(BASE_URL + "/actividades");

    Thread.sleep(1000);

    WebElement primerDetalle = driver.findElement(By.cssSelector(".activity-card a[href^='/actividades/']"));

    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});",primerDetalle);
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();",primerDetalle);

    Thread.sleep(1000);

    WebElement botonReservar = driver.findElement(By.cssSelector("form.reserve-form button[type='submit']"));

    if (!botonReservar.isEnabled()) {
        assertTrue(
            driver.getPageSource().contains("Ya reservada")
            || driver.getPageSource().contains("Sin plazas")
        );
        return;
    }

    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});",botonReservar);
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();",botonReservar);

    Thread.sleep(1000);

    assertFalse(driver.getCurrentUrl().contains("/reservas/exito"));
    assertTrue(driver.getPageSource().contains("No se pudo realizar la reserva") || driver.getPageSource().contains("Ya tienes esta actividad reservada") || driver.getPageSource().contains("error") || driver.getPageSource().contains("Sin plazas"));
}


@Test
void crearActividadConDatosErroneosDebeFallar() throws InterruptedException {
    login();

    driver.get(BASE_URL + "/actividades/nueva");

    driver.findElement(By.name("tipo")).sendKeys("Cardio");
    driver.findElement(By.name("nivelDificultad")).sendKeys("Principiante");
    driver.findElement(By.name("descripcion")).sendKeys("Actividad con datos incorrectos");
    driver.findElement(By.name("imagen")).sendKeys("https://via.placeholder.com/400x200");

    ((JavascriptExecutor) driver).executeScript("document.querySelector('[name=\"fecha\"]').value='2026-12-20';");

    driver.findElement(By.name("hora")).sendKeys("10:30");
    driver.findElement(By.name("duracion")).sendKeys("60 min");
    driver.findElement(By.name("plazasTotales")).clear();
    driver.findElement(By.name("plazasTotales")).sendKeys("10");

    Select espacio = new Select(driver.findElement(By.id("espacioSelect")));

    for (WebElement option : espacio.getOptions()) {
        String texto = option.getText().toLowerCase();

        if (texto.contains("madrid") && texto.contains("arganzuela")) {
            option.click();
            break;
        }
    }

    WebElement boton = driver.findElement(By.cssSelector("button[type='submit']"));

    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", boton);
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();",boton);

    Thread.sleep(1000);

    assertTrue(driver.getCurrentUrl().contains("/actividades/nueva"));
    assertFalse(driver.getPageSource().contains("Actividad con datos incorrectos"));
}

}