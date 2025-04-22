package com.github.jaykkumar01.jsoncreationhelper;

import com.github.jaykkumar01.jsoncreationhelper.models.InputData;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JsonCreator {

    // Method to generate JSON from InputData
    public static void createJson(String formId, List<InputData> models, String filePath) {
        // Base structure of the JSON
        JSONObject json = new JSONObject();

        // Create the root object for CCM
        JSONObject ccm = new JSONObject();
        json.put("CCM", ccm);

        // Create the Transaction object inside CCM
        JSONObject transaction = new JSONObject();
        ccm.put("Transaction", transaction);

        // Set fixed values
        transaction.put("TransactionID", "500699096");
        transaction.put("FormID", formId);
        transaction.put("ProductCode", "<ProductCode>");
        transaction.put("DocumentType", "<DocumentType>");
        transaction.put("Letter", "<Letter>");
        transaction.put("NameInsured", "");

        // Create PolicyFormsData object (this will remain empty for now)
        JSONObject policyFormsData = new JSONObject();
        transaction.put("PolicyFormsData", policyFormsData);

        // Create Forms object inside Transaction
        JSONObject forms = new JSONObject();
        transaction.put("Forms", forms);

        // Add formID, description, and transactionID for Forms
        forms.put("FormID", formId);
        forms.put("FormDescription", "");
        forms.put("TransactionID", "500699096");

        // Iterate over the models to add dynamic fields from InputData
        for (InputData data : models) {
            // Extract the XPath and VariableName to dynamically update the JSON
            String[] pathParts = data.getXPath().split("\\.");

            // Start with the root of the JSON (CCM)
            JSONObject current = ccm;

            for (String part : pathParts) {
                // Check if the part is a variable (e.g., {Variable Name})
                if (part.contains("{")) {
                    part = data.getVariableName();
                }

                // Create the part if it doesn't exist
                if (!current.has(part)) {
                    current.put(part, new JSONObject());
                }

                // Move to the next level in the JSON
                current = current.getJSONObject(part);
            }

            // Add the sample data to the last node
            current.put(data.getVariableName(), data.getSampleData());
        }

        // Write the JSON to a file
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(json.toString(4)); // Pretty print with indentation of 4
            System.out.println("Successfully written JSON to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
