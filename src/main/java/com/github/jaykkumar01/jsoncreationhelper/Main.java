package com.github.jaykkumar01.jsoncreationhelper;

import com.github.jaykkumar01.jsoncreationhelper.models.InputData;
import com.github.jaykkumar01.jsoncreationhelper.utils.InputDataProvider;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String formId = "form1"; // Example Form ID
        List<InputData> models = InputDataProvider.load(formId);

        if (models == null || models.isEmpty()){
            System.out.println("No input data");
            return;
        }

        // Specify the path to save the JSON file
        String filePath = "output.json";

        // Create the JSON based on the models and save it to a file
        JsonCreator.createJson(formId, models, filePath);
    }
}
