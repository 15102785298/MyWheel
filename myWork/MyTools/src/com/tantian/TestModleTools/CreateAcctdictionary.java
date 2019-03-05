package com.tantian.TestModleTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class CreateAcctdictionary {
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			String line = br.readLine();
			List<String> lineList = new LinkedList<>();
			while (true) {
				lineList.add(line);
				line = br.readLine();
				if (StringUtils.isEmpty(line) || StringUtils.equals(line, "1")) {
					break;
				}
			}
			for (String lineItem : lineList) {
				String[] lines = StringUtils.split(StringUtils.trim(lineItem), "\t");
				if (!"1".equals(lineItem)) {
					System.out.println(lines[0] + " " + lines[1]);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
