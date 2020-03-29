package tools;

import com.google.gson.*;
import java.io.*;


/**
 * @author Giacomo Tommaso Petrucci
 * This is an helper class for the gson library
 */
public final class JSONCreator {
    private static final Gson gson = new GsonBuilder().create();

    private JSONCreator() {
    }

    ;


    public static String generateJSON(Object object) {
        String JSON = gson.toJson(object);
        return JSON;
    }

    public static void printJSON(Object object) {        //utile per debug
        System.out.println(generateJSON(object));
    }

    public static void saveJSON(Object object, String path) throws IOException { //path deve includere il nome del file
        FileWriter writer = new FileWriter(path);
        writer.write(gson.toJson(object));
        writer.close();


    }


    public static int parseIntFieldFromFile(String path, String fieldName) throws FileNotFoundException {
        JsonElement jelement = new JsonParser().parse(new FileReader(path));
        JsonObject jobject = jelement.getAsJsonObject();
        int number = jobject.get(fieldName).getAsInt();
        return number;
    }

    public static boolean parseBooleanFieldFromFile(String path, String fieldName) throws FileNotFoundException {
        JsonElement jelement = new JsonParser().parse(new FileReader(path));
        JsonObject jobject = jelement.getAsJsonObject();
        boolean variable = jobject.get(fieldName).getAsBoolean();
        return variable;
    }

    public static int[][] parseMatrixFieldFromFile(String path, String fieldName, int rows, int columns) throws FileNotFoundException {

        JsonElement jelement = new JsonParser().parse(new FileReader(path));
        JsonObject jobject = jelement.getAsJsonObject();
        JsonArray matrixTexture = jobject.getAsJsonArray(fieldName);
        int[][] matrix = new int[rows][columns];

        for (int r = 0; r < matrixTexture.size(); r++) {
            JsonArray row = matrixTexture.get(r).getAsJsonArray();
            for (int c = 0; c < row.size(); c++) {
                matrix[r][c] = row.get(c).getAsInt();
            }
        }
        return matrix;
    }


    public static String parseStringFieldFromFile(String path, String fieldName) throws FileNotFoundException {
        JsonElement jelement = new JsonParser().parse(new FileReader(path));
        JsonObject jobject = jelement.getAsJsonObject();
        String string = jobject.get(fieldName).getAsString();
        return string;
    }

    public static String parseStringFiledFromJson(String json, String fieldName){
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        String fieldValue = obj.get(fieldName).getAsString();
        return fieldValue;
    }

    public static long parseLongFieldFromFile(String path, String fieldName) throws FileNotFoundException {
        JsonElement jelement = new JsonParser().parse(new FileReader(path));
        JsonObject jobject = jelement.getAsJsonObject();
        long number = jobject.get(fieldName).getAsLong();
        return number;
    }

    public static float parseFloatFiledFromJson(String json, String fieldName){
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        float fieldValue = obj.get(fieldName).getAsFloat();
        return fieldValue;
    }

}