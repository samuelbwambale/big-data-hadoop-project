package tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;



public class TestRelFreq {
	
	static Double sum = 0.0;

	public static void main(String[] args) {

		Map<CustomPairWritable, List<Integer>> hashMap = new HashMap<CustomPairWritable, List<Integer>>();

		List<String> words = Arrays.asList("B11", "C31", "A10", "D76", "A12", "B12", "C31", "D76", "C31", "A10", "B12",
				"D76", "C31", "B11");
		int size = words.size();
		for (int i = 0; i < size; i++) {
			for (String v : windowOfWord(words.get(i), words.subList(i, size))) {
				CustomPairWritable p1 = new CustomPairWritable(words.get(i), v);
				System.out.println(" hashmap " + hashMap);
				System.out.println("value of u " + words.get(i) + " value of v " + v + " Pair (" + p1 + ", " + 1 + ")");
				if (hashMap.containsKey(p1)) {
					hashMap.get(p1).add(1);
				} else {
					List<Integer> p1List = new ArrayList<>();
					p1List.add(1);
					hashMap.put(p1, p1List);
				}

				CustomPairWritable p2 = new CustomPairWritable(words.get(i), "*");
				System.out.println(
						"value of u " + words.get(i) + " value of v " + v + " Star Pair (" + p2 + ", " + 1 + ")");
				if (hashMap.containsKey(p2)) {
					hashMap.get(p2).add(1);
				} else {
					List<Integer> p2List = new ArrayList<>();
					p2List.add(1);
					hashMap.put(p2, p2List);
				}

			}
		}
		
		Map<CustomPairWritable, List<Integer>> treeMap = new TreeMap<CustomPairWritable, List<Integer>>(hashMap);

		
		for (Entry<CustomPairWritable, List<Integer>> e : treeMap.entrySet()) {
			System.out.println("key " + e.getKey() + " values " + e.getValue());
			try {
				reduce(e.getKey(), e.getValue());
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

		}
	}

	static List<String> windowOfWord(String u, List<String> words) {
		List<String> window = new ArrayList<String>();
		for (int i = 1; i < words.size(); i++) {
			if (words.get(i).equals(u))
				return window;
			else
				window.add(words.get(i));
		}
		return window;
	}

	static void reduce(CustomPairWritable key, List<Integer> values) throws IOException, InterruptedException {
		Double s = 0.0;
		for (Integer val : values) {
			s += val;
		}
		
		if (key.getValue().toString().equals("*")) {
			sum = s;
		}
		
			
		else {
			Double relativeFreq = s / sum;
			System.out.println(key + " " + relativeFreq);

		}

	}

}
