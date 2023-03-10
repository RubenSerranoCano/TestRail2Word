package org.testrail2word.model;

public class TestCaseStep {
    private String number;
    private String description;

    public TestCaseStep(String number, String description) {
        this.number = number;
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public String getDescription() {
        return description;
    }
}
