package jsoncreationhelper;

import jsoncreationhelper.models.InputData;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JsonCreator {

    public static void createJson(String formId, List<InputData> models, String filePath) {
        JSONObject json = new JSONObject();

        // Root CCM object
        JSONObject ccm = new JSONObject();
        json.put("CCM", ccm);

        // Transaction block
        JSONObject transaction = new JSONObject();
        ccm.put("Transaction", transaction);
        transaction.put("TransactionID", "500699096");
        transaction.put("FormID", formId);
        transaction.put("ProductCode", "<ProductCode>");
        transaction.put("DocumentType", "<DocumentType>");
        transaction.put("NameInsured", "");
        transaction.put("PolicyFormsData", new JSONObject());

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

            // Remove any .{Variable} or {Variable} from path
            String cleanedXPath = rawXPath.replaceAll("\\.?\\{[^}]+}", "");

            String[] pathParts = cleanedXPath.split("\\.");
            if (pathParts.length == 0) continue;

            JSONObject current = json;

            // Traverse or create nested structure
            for (int i = 0; i < pathParts.length; i++) {
                String part = pathParts[i];
                if (!current.has(part) || !(current.get(part) instanceof JSONObject)) {
                    current.put(part, new JSONObject());
                }
                current = current.getJSONObject(part);
                if (i == pathParts.length - 1) {
                    // Final level - insert tag:value
                    current.put(data.getTagName(), data.getSampleData());
                }
            }
        }

        // Write the result
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(json.toString(4));
            System.out.println("✅ JSON successfully written to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
