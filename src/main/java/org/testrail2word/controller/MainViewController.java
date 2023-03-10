package org.testrail2word.controller;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testrail2word.model.TestCase;
import org.testrail2word.model.TestCaseStep;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MainViewController {

    private static WebDriver driver;
    private static TestCase testCase;
    private String outputPath;

    public void print(List<String> testCasesUrls, String testRailVersion) {
        driver = new ChromeDriver();

        for (String testCaseUrl : testCasesUrls) {
            driver.navigate().to(testCaseUrl);

            waitForUserLogin();

            String testCaseCodeText;
            String testCaseTitleText;
            List<WebElement> testCaseNumbers;
            List<WebElement> testCaseDescriptions;

            switch (testRailVersion) {
                case "v7.8.0 Early Access (1136)":
                    testCaseCodeText = driver.findElement(By.className("content-header-id")).getText();
                    testCaseTitleText = driver.findElement(By.xpath("//*[@id=\"content-header\"]/div/div[4]")).getText().trim();
                    testCaseNumbers = driver.findElements(By.xpath("//*[@id=\"content-inner\"]/div[8]/div/table/tbody/tr//div/span"));
                    testCaseDescriptions = driver.findElements(By.xpath("//*[@id=\"content-inner\"]/div[8]/div/table/tbody/tr//td[2]/div[2]/div/p"));
                    break;
                default:
                    // "v6.5.7.1000"
                    testCaseCodeText = driver.findElement(By.className("content-header-id")).getText();
                    testCaseTitleText = driver.findElement(By.xpath("//*[@id=\"content-header\"]/div/div[4]")).getText().trim();
                    testCaseNumbers = driver.findElements(By.xpath("//*[@id=\"content-inner\"]//table/tbody/tr//div/span"));
                    testCaseDescriptions = driver.findElements(By.xpath("//*[@id=\"content-inner\"]/div[4]/table/tbody/tr/td[2]/div[@class=\"markdown\"]"));
            }

            testCase = new TestCase(testCaseUrl, testCaseCodeText, testCaseTitleText, getTestCasesSteps(testCaseNumbers, testCaseDescriptions));

            WordprocessingMLPackage wordPackage = getWordprocessingMLPackage();
            MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();

            writeTitle(testCase.getCode(), testCase.getName(), mainDocumentPart);
            writeSteps(mainDocumentPart, testCase.getSteps());
            saveDocxFile(wordPackage, getOutputFilePath(this.outputPath, getTestCaseFileName(testCase)));
        }

        driver.close();
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    private String getOutputPath() {
        return this.outputPath;
    }

    private File getOutputFilePath(String outputPath, String fileName) {
        File file = new File(outputPath.concat("\\").concat(fileName));
        String path = file.getPath();
        return new File(path);
    }

    private String getTestCaseFileName(TestCase testCase) {
        return testCase.getCode().concat(" - ").concat(testCase.getName()).concat(".docx");
    }

    private void waitForUserLogin() {
        driver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
    }

    private static List<TestCaseStep> getTestCasesSteps(List<WebElement> testCaseNumbers, List<WebElement> testCaseDescriptions) {
        List<TestCaseStep> testCaseSteps = new ArrayList<>();
        int commonAmount = Math.min(testCaseNumbers.size(), testCaseDescriptions.size());
        for (int i = 0; i < commonAmount; i++) {
            testCaseSteps.add(new TestCaseStep(testCaseNumbers.get(i).getText(), testCaseDescriptions.get(i).getText()));
        }
        return testCaseSteps;
    }

    // TODO - writePreconditions()

    private void writeTitle(String testCaseCodeText, String testCaseTitleText, MainDocumentPart mainDocumentPart) {
        mainDocumentPart.addStyledParagraphOfText("Title", testCaseCodeText.concat(System.lineSeparator()));
        mainDocumentPart.addStyledParagraphOfText("Title", testCaseTitleText);
    }

    private void writeSteps(MainDocumentPart mainDocumentPart, List<TestCaseStep> testCaseSteps) {
        for (TestCaseStep testCaseStep : testCaseSteps) {
            String testCaseNumber = testCaseStep.getNumber();
            String testCaseStepDescription = testCaseStep.getDescription();

            Scanner descriptionLine = new Scanner(testCaseStepDescription);
            mainDocumentPart.addParagraphOfText(testCaseNumber.concat(". ").concat(descriptionLine.nextLine()));
            while (descriptionLine.hasNext()) {
                mainDocumentPart.addParagraphOfText(descriptionLine.nextLine());
            }
        }
    }

    private void saveDocxFile(WordprocessingMLPackage wordPackage, File exportFile) {
        try {
            wordPackage.save(exportFile);
        } catch (Docx4JException e) {
            throw new RuntimeException(e);
        }
    }


    private WordprocessingMLPackage getWordprocessingMLPackage() {
        WordprocessingMLPackage wordPackage;
        try {
            wordPackage = WordprocessingMLPackage.createPackage();
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }
        return wordPackage;
    }
}
