package org.testrail2word.model;

import org.openqa.selenium.WebElement;

import java.util.List;

public class TestCaseWebElements {

    private WebElement testCaseCode;
    private WebElement testCaseTitle;
    private List<WebElement> testCaseNumbers;
    private List<WebElement> testCaseDescriptions;

    public WebElement getTestCaseCode() {
        return testCaseCode;
    }

    public String getTestCaseCodeText() {
        return testCaseCode.getText();
    }

    public void setTestCaseCode(WebElement testCaseCode) {
        this.testCaseCode = testCaseCode;
    }

    public WebElement getTestCaseTitle() {
        return testCaseTitle;
    }

    public String getTestCaseTitleText() {
        return testCaseTitle.getText().trim();
    }

    public void setTestCaseTitle(WebElement testCaseTitle) {
        this.testCaseTitle = testCaseTitle;
    }

    public List<WebElement> getTestCaseNumbers() {
        return testCaseNumbers;
    }

    public void setTestCaseNumbers(List<WebElement> testCaseNumbers) {
        this.testCaseNumbers = testCaseNumbers;
    }

    public List<WebElement> getTestCaseDescriptions() {
        return testCaseDescriptions;
    }

    public void setTestCaseDescriptions(List<WebElement> testCaseDescriptions) {
        this.testCaseDescriptions = testCaseDescriptions;
    }
}
