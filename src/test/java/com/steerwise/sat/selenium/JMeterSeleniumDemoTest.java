package com.steerwise.sat.selenium;

import org.junit.Test;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.Test;
import org.testng.Assert;


import org.openqa.selenium.By;

import org.openqa.selenium.WebElement;

import org.openqa.selenium.firefox.FirefoxDriver;

 

public class JMeterSeleniumDemoTest {
	private   FirefoxDriver driver ;
	
	@Before
	public void setup() throws MalformedURLException, UnknownHostException{
	    driver = new FirefoxDriver();
	    driver.get("https://test-demo.assetdevops.steerwise.io");
	}

 

   @Test

   public void openTest() {

       driver.findElement(By.xpath("//a[@href='/product/new']")).click();

       WebElement title = driver.findElement(By.xpath("//title"));

       System.out.println("********************************************************");

       System.out.println("*" + title.getAttribute("text") + "*");
       String Actualtitle = driver.getTitle();
       System.out.println("Before Assetion " + Actualtitle);

       Assert.assertEquals(title.getAttribute("text"), "New Product");


       System.out.println("********************************************************");
       driver.quit();    

   }

   
   @Test
   public void testAuthenticationFailureWhenProvidingBadCredentials() throws InterruptedException{
  driver.findElement(By.xpath("//a[@href='/product/new']")).click();
  
    driver.findElement(By.id("productId")).sendKeys("1");
    driver.findElement(By.id("name")).sendKeys("a"); 
     driver.findElement(By.id("price")).sendKeys("1");     
    driver.findElement(By.xpath("//button")).click();
    System.out.println("********************************************************"+driver.getCurrentUrl());
    
    Assert.assertTrue(driver.getCurrentUrl().endsWith("/product/1"));
    driver.quit();
}
   

}