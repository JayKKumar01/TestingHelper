package com.github.jaykkumar01.jsoncreationhelper.models;

public class InputData {
    private String xPath;
    private String tagName;
    private String variableName;
    private String sampleData;

    // Getters and Setters
    public String getXPath() {
        return xPath;
    }

    public void setXPath(String xPath) {
        this.xPath = xPath;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getSampleData() {
        return sampleData;
    }

    public void setSampleData(String sampleData) {
        this.sampleData = sampleData;
    }

    @Override
    public String toString() {
        return "InputData{" +
                "xPath='" + xPath + '\'' +
                ", tagName='" + tagName + '\'' +
                ", variableName='" + variableName + '\'' +
                ", sampleData='" + sampleData + '\'' +
                '}';
    }
}
