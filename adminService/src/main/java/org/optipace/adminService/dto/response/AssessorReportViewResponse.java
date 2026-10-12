package org.optipace.adminService.dto.response;

import java.util.List;
import java.util.Map;

public class AssessorReportViewResponse {
    private Map<String, Object> summary;
    private List<Map<String, Object>> tests;

    public AssessorReportViewResponse() {}

    public AssessorReportViewResponse(Map<String, Object> summary, List<Map<String, Object>> tests) {
        this.summary = summary;
        this.tests = tests;
    }

    public Map<String, Object> getSummary() {
        return summary;
    }

    public void setSummary(Map<String, Object> summary) {
        this.summary = summary;
    }

    public List<Map<String, Object>> getTests() {
        return tests;
    }

    public void setTests(List<Map<String, Object>> tests) {
        this.tests = tests;
    }
}
