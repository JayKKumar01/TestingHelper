package jsoncreationhelper;

import jsoncreationhelper.constants.AppPaths;
import jsoncreationhelper.models.InputData;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JsonCreator {

    public static void createJson(String formId, List<InputData> models) {
        JSONObject json = new JSONObject();

        // Root CCM object
        JSONObject ccm = new JSONObject();
        json.put("CCM", ccm);

        // Transaction block
        JSONObject transaction = new JSONObject();
        ccm.put("Transaction", transaction);
        transaction.put("TransactionID", "500699096");
        transaction.put("FormID", formId);
        transaction.put("ProductCode", "ESISClaimsIntake");
        transaction.put("DocumentType", "Letter");
        transaction.put("NameInsured", "");

        // Load PolicyFormsData from file
        JSONObject policyFormsData = new JSONObject();
        try (FileReader reader = new FileReader(AppPaths.POLICY_FORMS_JSON)) {
            policyFormsData = new JSONObject(new JSONTokener(reader));
        } catch (IOException e) {
            System.err.println("⚠️ Failed to load PolicyFormsData from: " + AppPaths.POLICY_FORMS_JSON);
            e.printStackTrace();
        }
        transaction.put("PolicyFormsData", policyFormsData);

        // Forms block
        JSONObject forms = new JSONObject();
        transaction.put("Forms", forms);
        forms.put("FormID", formId);
        forms.put("FormDescription", "");
        forms.put("TransactionID", "500699096");

        // Process InputData list
        for (InputData data : models) {
            String rawXPath = data.getXPath();
            if (rawXPath == null || rawXPath.isEmpty()) continue;

            // Remove .{Variable} or {Variable}
            String cleanedXPath = rawXPath.replaceAll("\\.?\\{[^}]+}", "");
            String[] pathParts = cleanedXPath.split("\\.");
            if (pathParts.length == 0) continue;

            JSONObject current = json;
            for (int i = 0; i < pathParts.length; i++) {
                String part = pathParts[i];
                if (!current.has(part) || !(current.get(part) instanceof JSONObject)) {
                    current.put(part, new JSONObject());
                }
                current = current.getJSONObject(part);

                // If last node, insert tag and sample value
                if (i == pathParts.length - 1) {
                    current.put(data.getTagName(), data.getSampleData());
                }
            }
        }

        // Write to Downloads/Json Files/{formId}/General.json
        File outputFile = new File(AppPaths.JSON_OUTPUT_BASE + File.separator + formId + File.separator + "General.json");
        outputFile.getParentFile().mkdirs(); // Ensure directory structure exists

        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(json.toString(4));
            System.out.println("✅ JSON successfully saved to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
