package org.testrail2word.controller;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testrail2word.model.TestCase;
import org.testrail2word.model.TestCaseStep;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MainViewController {

    private static WebDriver webDriver;
    private static TestCase testCase;
    private String outputPath;
    private int editValueIndex;


    public void print(List<String> testCasesUrls, String browser) {

        setWebDriverBrowser(browser);

        for (String testCaseUrl : testCasesUrls) {
            webDriver.navigate().to(testCaseUrl);

            waitForUserLogin();

            String testCaseCodeText = webDriver.findElement(By.className("content-header-id")).getText();
            String testCaseTitleText = webDriver.findElement(By.xpath("//*[@id=\"content-header\"]/div/div[4]")).getText().trim();
            List<WebElement> testCaseNumbers = webDriver.findElements(By.xpath("//*[@id=\"content-inner\"]//table/tbody/tr//div/span"));
            // 7.8: //*[@id="content-inner"]//td[@class="step-content"]/div[@class="hidden-vertical"]//div[contains(concat(' ', @class, ' '), ' markdown')]
            // 6: //*[@id="content-inner"]//td[@class="step-content"]/div[@class="markdown"]
            List<WebElement> testCaseDescriptions = webDriver.findElements(By.xpath("//*[@id=\"content-inner\"]//td[@class=\"step-content\"]/div[@class=\"hidden-vertical\"]//div[contains(concat(' ', @class, ' '), ' markdown')]"));

            testCase = new TestCase(testCaseUrl, testCaseCodeText, testCaseTitleText, getTestCasesSteps(testCaseNumbers, testCaseDescriptions));

            WordprocessingMLPackage wordPackage = getWordprocessingMLPackage();
            MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();

            writeTitle(testCase.getCode(), testCase.getName(), mainDocumentPart);
            writeSteps(mainDocumentPart, testCase.getSteps());
            saveDocxFile(wordPackage, getOutputFilePath(this.outputPath, getTestCaseFileName(testCase)));
        }

        webDriver.close();
    }

    public void setWebDriverBrowser(String browser) {
        switch (browser) {
            case "Firefox":
                webDriver = new FirefoxDriver();
                break;
            case "Edge":
                webDriver = new EdgeDriver();
                break;
            default:
                ChromeOptions co = new ChromeOptions().addArguments("--remote-allow-origins=*");
                webDriver = new ChromeDriver(co);
        }
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    private File getOutputFilePath(String outputPath, String fileName) {
        outputPath = isOutputPathMissing() ? "/TestRail2Word reports" : outputPath;
        File filePath = new File(outputPath);
        if (!filePath.exists()) {
            filePath.mkdir();
        }
        String path = new File(outputPath.concat("\\").concat(fileName)).getPath();
        System.out.println("Output path: " + path);
        return new File(path);
    }

    private boolean isOutputPathMissing() {
        return outputPath == null || outputPath.isEmpty();
    }

    private String getTestCaseFileName(TestCase testCase) {
        return testCase.getCode().concat(" - ").concat(testCase.getName()).concat(".docx").replaceAll("[^-a-zA-Z0-9() .]", "");
    }

    private void waitForUserLogin() {
        webDriver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
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
            mainDocumentPart.addParagraphOfText("");
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


    public void add(JButton addButton, DefaultListModel<String> testCasesUrls, JTextField testCaseUrlTextField, JList<String> testCasesUrlJList) {
        if (addButton.getText().equals("Apply")) {
            addButton.setText("Add");
            testCasesUrls.add(editValueIndex, testCaseUrlTextField.getText());
        } else {
            testCasesUrls.addElement(testCaseUrlTextField.getText());
        }

        testCasesUrlJList.setModel(testCasesUrls);
        addButton.setEnabled(false);
        testCaseUrlTextField.setText("");
    }

    public void edit(JList<String> testCasesUrlJList, DefaultListModel<String> testCasesUrls, JTextField testCaseUrlTextField, JButton addButton) {
        editValueIndex = testCasesUrlJList.getSelectedIndex();
        String selectedUrl = testCasesUrlJList.getSelectedValue();

        testCasesUrls.remove(editValueIndex);
        testCasesUrlJList.setModel(testCasesUrls);
        testCaseUrlTextField.setText(selectedUrl);
        addButton.setText("Apply");
    }

    public void setButtonStateOnListSelection(JTextField testCaseUrlTextField, JList<String> testCasesUrlJList, JButton editButton, JButton deleteButton, JButton printButton) {
        if (testCaseUrlTextField.getText().isEmpty()) {
            editButton.setEnabled(true);
        }
        deleteButton.setEnabled(true);
        printButton.setEnabled(true);

        if (testCasesUrlJList.getSelectedValuesList().size() > 1) {
            editButton.setEnabled(false);
        }

        if (testCasesUrlJList.getSelectedValuesList().size() == 0) {
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            printButton.setEnabled(false);
        }
    }

    public void chooseFile(JPanel jPanel, JTextField outputPathTextField) {
        final JFileChooser fileChooser = new JFileChooser(isOutputPathMissing() ? "." : outputPath);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showOpenDialog(jPanel);
        if (fileChooser.getSelectedFile() != null) {
            outputPathTextField.setText(String.valueOf(fileChooser.getSelectedFile()));
            this.outputPath = String.valueOf(fileChooser.getSelectedFile());
        }
    }

    public void setButtonStateOnUrlInput(JTextField testCaseUrlTextField, JButton editButton, JButton addButton) {
        if (!testCaseUrlTextField.getText().isEmpty()) {
            editButton.setEnabled(false);
            addButton.setEnabled(true);
        }
    }

    public void deleteSelection(JList<String> testCasesUrlJList, DefaultListModel<String> testCasesUrls) {
        int[] selectedIndices = testCasesUrlJList.getSelectedIndices();
        for (int i = selectedIndices.length - 1; i >= 0; i--) {
            testCasesUrls.removeElementAt(selectedIndices[i]);
        }
    }
}
