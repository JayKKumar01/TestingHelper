package jsoncreationhelper;

import jsoncreationhelper.models.InputData;
import jsoncreationhelper.utils.InputDataProvider;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String formId = "form1"; // Example Form ID
        List<InputData> models = InputDataProvider.load(formId);

        if (models == null || models.isEmpty()){
            System.out.println("No input data");
            return;
        }

        // Create the JSON based on the models and save it to a file
        JsonCreator.createJson(formId, models);
    }
}
