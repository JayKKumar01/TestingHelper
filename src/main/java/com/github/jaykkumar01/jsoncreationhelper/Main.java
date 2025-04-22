package com.github.jaykkumar01.jsoncreationhelper;

import com.github.jaykkumar01.jsoncreationhelper.models.InputData;

import java.util.List;

public class Main {
    public static void main(String[] args){
        String formId = "";
        List<InputData> models = SheetUtil.getModels(formId);
    }
}
