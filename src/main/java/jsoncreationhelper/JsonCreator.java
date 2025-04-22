package jsoncreationhelper;

import jsoncreationhelper.constants.AppPaths;
import jsoncreationhelper.models.InputData;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JsonCreator {

    public static void createJson(String formId, List<InputData> models) {
        JSONObject baseJson = createBaseJson(formId);

        // Maps for holding simple and multi-value tags
        Map<InputData, List<String>> multiValueMap = new LinkedHashMap<>();

        // Start from baseJson and process single/multi-value inputs
        for (InputData data : models) {
            String rawXPath = data.getXPath();
            if (rawXPath == null || rawXPath.isEmpty()) continue;

            String cleanedXPath = rawXPath.replaceAll("\\.?\\{[^}]+}", "");
            String[] pathParts = cleanedXPath.split("\\.");
            if (pathParts.length == 0) continue;

            String sample = data.getSampleData();
            boolean isNullOrEmpty = sample == null || sample.trim().isEmpty();

            List<String> values = isNullOrEmpty ? List.of() : List.of(sample.split("\\s*,\\s*"));
            if (values.size() > 1) {
                multiValueMap.put(data, values);
                continue;
            }

            // Add to General.json (single value or fallback to variableName)
            JSONObject current = baseJson;
            for (int i = 0; i < pathParts.length; i++) {
                String part = pathParts[i];
                if (!current.has(part) || !(current.get(part) instanceof JSONObject)) {
                    current.put(part, new JSONObject());
                }
                current = current.getJSONObject(part);
                if (i == pathParts.length - 1) {
                    String value = isNullOrEmpty ? data.getVariableName() : values.get(0);
                    current.put(data.getTagName(), value);
                }
            }
        }

        // Save General.json
        saveJsonToFile(baseJson, formId, "General.json");

        // Determine max test cases needed
        int maxCases = multiValueMap.values().stream()
                .mapToInt(List::size)
                .max().orElse(0);

        // Create TestCase1.json ... TestCaseN.json
        for (int i = 0; i < maxCases; i++) {
            JSONObject testCaseJson = new JSONObject(baseJson.toString()); // clone base JSON

            for (Map.Entry<InputData, List<String>> entry : multiValueMap.entrySet()) {
                InputData data = entry.getKey();
                List<String> valueList = entry.getValue();

                if (i >= valueList.size()) continue; // Skip if this case doesn't have enough data

                String rawXPath = data.getXPath();
                String cleanedXPath = rawXPath.replaceAll("\\.?\\{[^}]+}", "");
                String[] pathParts = cleanedXPath.split("\\.");
                if (pathParts.length == 0) continue;

                JSONObject current = testCaseJson;
                for (int j = 0; j < pathParts.length; j++) {
                    String part = pathParts[j];
                    if (!current.has(part) || !(current.get(part) instanceof JSONObject)) {
                        current.put(part, new JSONObject());
                    }
                    current = current.getJSONObject(part);
                    if (j == pathParts.length - 1) {
                        current.put(data.getTagName(), valueList.get(i));
                    }
                }
            }

            saveJsonToFile(testCaseJson, formId, "TestCase" + (i + 1) + ".json");
        }
    }

    private static JSONObject createBaseJson(String formId) {
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

        return json;
    }

    private static void saveJsonToFile(JSONObject jsonObject, String formId, String fileName) {
        File outputFile = new File(AppPaths.JSON_OUTPUT_BASE + File.separator + formId + File.separator + fileName);
        outputFile.getParentFile().mkdirs(); // Ensure directory exists

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(jsonObject.toString(4)); // Pretty print
            System.out.println("✅ JSON saved: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Failed to save JSON: " + outputFile.getAbsolutePath());
            e.printStackTrace();
        }
    }
}
