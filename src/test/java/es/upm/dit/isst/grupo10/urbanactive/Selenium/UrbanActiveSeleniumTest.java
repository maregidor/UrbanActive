package es.upm.dit.isst.grupo10.urbanactive.Selenium;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.*;

class UrbanActiveSeleniumTest {

private WebDriver driver;

private final String BASE_URL = "http://localhost:8082";

@BeforeEach
void setUp() {
WebDriverManager.chromedriver().setup();
driver = new ChromeDriver();
driver.manage().window().maximize();
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
driver.findElement(By.cssSelector("button[type='submit']"));

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

driver.get(BASE_URL + "/actividades/1");

WebElement botonReservar = driver.findElement(By.cssSelector("button[type='submit']"));
botonReservar.click();

assertTrue(driver.getCurrentUrl().contains("/reservas/exito")
|| driver.getPageSource().contains("Reserva")
|| driver.getPageSource().contains("confirmada"));
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