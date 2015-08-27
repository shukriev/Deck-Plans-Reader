package src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GenerateOutout {
	public static void run(List<Deck>arrayPoints, HashMap<String, String>hashResult, String csvFile) {
		BufferedReader br = null;
		String line = "";
		String csvSplited = ",";
		String outputFileName = "";
		JSONArray result = new JSONArray();
		int exist = 0;
		int not_exist = 0;
		int maybe = 0;
		List<String> found = new ArrayList<String>();
		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] csvSplitedArr = line.split(csvSplited);
				outputFileName = csvSplitedArr[0];
				JSONObject targetList = new JSONObject();
				targetList.append("deck-name", csvSplitedArr[0]);
				targetList.append("tartget-count", csvSplitedArr[1]);
				result.put(targetList);

				for (int j = 2; j < csvSplitedArr.length; j++) {
					if (hashResult.containsKey(csvSplitedArr[j])) {
						String imageName = hashResult.get(csvSplitedArr[j]);

						for (Deck deck : arrayPoints) {
							String deckNumberArr[] = deck.ImageNumber.split("\\\\");
							String deckNumber = deckNumberArr[deckNumberArr.length-1];
							
							if(deckNumber.equals(imageName)){
								JSONObject ob = new JSONObject();
								ob.append("cabin", csvSplitedArr[j]);
								ob.append("status", "Appear");
								ob.append("screenshot", deckNumber);
								ob.append("x1", deck.getP1().x);
								ob.append("y1", deck.getP1().y);
								ob.append("x2", deck.getP2().x);
								ob.append("y2", deck.getP2().y);
								ob.append("x3", deck.getP3().x);
								ob.append("y3", deck.getP3().y);
								ob.append("x4", deck.getP4().x);
								ob.append("y4", deck.getP4().y);
								result.put(ob);
								hashResult.remove(csvSplitedArr[j]);
								found.add(csvSplitedArr[j]);
								exist++;
							}
						}
					}
				}
				
				for (int i = 2; i < csvSplitedArr.length; i++) {
					if(!found.contains(csvSplitedArr[i])){
						JSONObject ob = new JSONObject();
						ob.append("cabin", csvSplitedArr[i]);
						ob.append("status", "Not found");
						ob.append("screenshot", "");
						ob.append("x1", "");
						ob.append("y1", "");
						ob.append("x2", "");
						ob.append("y2", "");
						ob.append("x3", "");
						ob.append("y3", "");
						ob.append("x4", "");
						ob.append("y4", "");
						result.put(ob);
						not_exist++;
					}
				}
				for (String key : hashResult.keySet()) {
				    String value = hashResult.get(key);
					for (Deck deck : arrayPoints) {
						String deckNumberArr[] = deck.ImageNumber.split("\\\\");
						String deckNumber = deckNumberArr[deckNumberArr.length-1];
						
						if(deckNumber.equals(value)){
							JSONObject ob = new JSONObject();
							ob.append("cabin", key);
							ob.append("status", "Maybe");
							ob.append("screenshot", value);
							ob.append("x1", deck.getP1().x);
							ob.append("y1", deck.getP1().y);
							ob.append("x2", deck.getP2().x);
							ob.append("y2", deck.getP2().y);
							ob.append("x3", deck.getP3().x);
							ob.append("y3", deck.getP3().y);
							ob.append("x4", deck.getP4().x);
							ob.append("y4", deck.getP4().y);
							result.put(ob);
							found.add(key);
						    maybe++;
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			FileWriter file = new FileWriter("Result\\" + outputFileName + ".json");

			file.write(result.toString());
			file.flush();
			file.close();
			System.out.println("Json file has been created");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("exist = " + exist);
		System.out.println("not_exist = " + not_exist);
		System.out.println("maybe = " + maybe);

		System.out.println("Generate output done");
	  }
}
