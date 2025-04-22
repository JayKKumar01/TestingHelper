package jsoncreationhelper;

import jsoncreationhelper.constants.AppPaths;
import jsoncreationhelper.models.InputData;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.*;

public class JsonCreator {

    public static void createJson(String formId, List<InputData> models) {
        JSONObject baseJson = createBaseJson(formId);

        // Maps for holding simple and multi-value tags
        Map<InputData, List<String>> multiValueMap = new LinkedHashMap<>();
        int maxCases = 0;



        // Start from baseJson and process single/multi-value inputs
        for (InputData data : models) {
            String rawXPath = data.getXPath();
            if (rawXPath == null || rawXPath.isEmpty()) continue;


            String cleanedXPath = rawXPath.replaceAll("\\.?\\{[^}]+}", "");
            String[] pathParts = cleanedXPath.split("\\.");
            if (pathParts.length == 0) continue;

            String sample = data.getSampleData();
            boolean isNullOrEmpty = sample == null || sample.trim().isEmpty();

            List<String> values = isNullOrEmpty
                    ? Collections.emptyList()
                    : List.of(sample.split("\\s*[,/]+\\s*"));


            if (values.size() > 1) {
                multiValueMap.put(data, values);
                maxCases = Math.max(maxCases,values.size());
                continue;
            }

            // Add to General.json (single value or fallback to variableName)
            String value = isNullOrEmpty ? data.getVariableName() : values.get(0);
            insertValue(baseJson, rawXPath, data.getTagName(), value);
        }

        // Save General.json
        saveJsonToFile(baseJson, formId, "General.json");

        // Create TC files - each in its own Runnable, run sequentially
        Map<String, Object> baseJsonMap = baseJson.toMap();
        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < maxCases; i++) {
            final int index = i;
            tasks.add(() -> {
                JSONObject testCaseJson = new JSONObject(baseJsonMap);
                for (Map.Entry<InputData, List<String>> entry : multiValueMap.entrySet()) {
                    InputData data = entry.getKey();
                    List<String> valueList = entry.getValue();

                    if (index >= valueList.size()) return;

                    String rawXPath = data.getXPath();
                    String cleanedXPath = rawXPath.replaceAll("\\.?\\{[^}]+}", "");
                    String[] pathParts = cleanedXPath.split("\\.");
                    if (pathParts.length == 0) return;

                    insertValue(testCaseJson, rawXPath, data.getTagName(), valueList.get(index));
                }

                saveJsonToFile(testCaseJson, formId, "TC " + (index + 1) + ".json");
            });
        }

        // Run each task one after another (not in parallel)
        for (Runnable task : tasks) {
            task.run();
        }

    }

    private static void insertValue(JSONObject root, String rawXPath, String tagName, String value) {
        String cleanedXPath = rawXPath.replaceAll("\\.?\\{[^}]+}", "");
        String[] pathParts = cleanedXPath.split("\\.");
        if (pathParts.length == 0) return;

        JSONObject current = root;
        for (int i = 0; i < pathParts.length; i++) {
            String part = pathParts[i];
            if (!current.has(part) || !(current.get(part) instanceof JSONObject)) {
                current.put(part, new JSONObject());
            }
            current = current.getJSONObject(part);
            if (i == pathParts.length - 1) {
                current.put(tagName, normalizeValue(value));
            }
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

    private static String sanitize(String value) {
        if (value == null) return null;
        return value
                .replace("\u2018", "'")
                .replace("\u2019", "'")
                .replace("\u201C", "\"")
                .replace("\u201D", "\"")
                .replace("\u2013", "-")
                .replace("\u2014", "-")
                .replace("\u00A0", " ") // non-breaking space
                .trim();
    }


    private static String normalizeValue(String value) {
        if (value == null) return "";

        // Sanitize first
        value = sanitize(value);

        // Then handle slash-splitting logic
        return value.contains("/") ? value.split("/")[0].trim() : value;
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
