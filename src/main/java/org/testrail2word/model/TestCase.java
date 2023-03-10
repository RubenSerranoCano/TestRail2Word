package org.testrail2word.model;

import java.util.List;

public class TestCase {
    private String url;
    private String code;
    private String name;
    private List<TestCaseStep> steps;

    public TestCase(String url, String code, String name, List<TestCaseStep> steps) {
        this.url = url;
        this.code = code;
        this.name = name;
        this.steps = steps;
    }

    public String getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

    public List<TestCaseStep> getSteps() {
        return steps;
    }
}
