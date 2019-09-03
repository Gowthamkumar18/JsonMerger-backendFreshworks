import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileJsonMerger {
	static HashMap<String, JSONArray> jsonHash = new HashMap<String, JSONArray>();
	
	//file count to 
	static int fileCount = 1;

	@SuppressWarnings("unchecked")
	public static void createFinalJson() throws IOException {
		
		JSONObject jsonObject = new JSONObject();
		
		//iterate through the hashmap to form the final jsonobject of all input files
		for (String key : jsonHash.keySet()) {
			//create the json object and add the values
			jsonObject.put(key, jsonHash.get(key));
		}
		
		//gson is json library to convert objects into json representaion
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		//file writer is to write the json object into the merge file
		try (FileWriter file = new FileWriter("src/merge.json")) {
			file.write(gson.toJson(jsonObject));
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//Attempted and tried the basic of the max file size problem by using buffered writer.

//		File file = new File("/src/merge1.json");
//		jsonObject.keySet().forEach(keyStr -> {
//
//			try {
//				BufferedWriter writer = Files.newBufferedWriter(file.toPath());
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			jsonObject.get(keyStr);
//			String data = gson.toJson(jsonObject.get(keyStr));
//			try {
//				if (file.length() + data.getBytes("UTF-8").length < 200) {
//					writer.write(data);
//					writer.flush();
//				} else {
//
//					file = new File("src/merge" + (fileCount + 1) + ".json");
//					writer = Files.newBufferedWriter(file.toPath());
//					fileCount++;
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		});

	}

	@SuppressWarnings("unchecked")
	public static void extractFile(File file) throws FileNotFoundException, IOException, ParseException {
		
		//parse the file to jsonobject
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader(file));
		
		//iterate through the json object to add them into the hashmap
		jsonObject.keySet().forEach(keyStr -> {
			JSONArray firstArray = (JSONArray) jsonObject.get(keyStr);
			
			//find if key is already present in the hashmap (eg: strikers)
			if (jsonHash.containsKey(keyStr)) {
				
				//if key is present appends the values to the corressponding key in the hashmap
				JSONArray seconArray = jsonHash.get(keyStr);
				for (int i = 0; i < firstArray.size(); i++) {
					seconArray.add(firstArray.get(i));
				}
				jsonHash.put(keyStr.toString(), seconArray);
			} 
			else {
				
				// if key is not present then add the value in the hashmap for first time
				jsonHash.put(keyStr.toString(), firstArray);
				
			}
		});
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		int i = 1;
		
		//search for input file existence dynamically
		while (true) {
			String filePath = "src/data" + i + ".json";
			File file = new File(filePath);
			if (file.exists()) {
				
				//extractFile(file) extracts different input file by parsing to -
				//json format and stores in a hashmap
				extractFile(file); 
				
			} else {
				break;
			}
			i++;
		}
		
		//creates a final json object from the hashmap
		createFinalJson();
		
	}

}
