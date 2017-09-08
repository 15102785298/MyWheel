package com.tantian.jsTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

public class changeStringToJs {
	public static void main(String[] args) {
		while (true) {
			System.out.println("please input:");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try {
				String input_data = br.readLine();
				String input_str[] = StringUtils.split(input_data, ',');
				StringBuffer sb = new StringBuffer();
				String temp;
				int length = input_str.length;
				for (int i = 0; i < length; i++) {
					String full_str[] = StringUtils.split(input_str[i].trim(), ' ');
					int length_full_str = full_str.length;
					for (int j = 0; j < length_full_str; j++) {
						temp = full_str[j];
						if (temp.equals("String")) {
							continue;
						}
						if (StringUtils.isNotEmpty(temp)) {
							sb.append("\"");
							sb.append(temp);
							sb.append("\"");
							sb.append(" : ");
							sb.append("Horn.getCompById(\"");
							sb.append(temp);
							sb.append("\").getValue(),");
							sb.append("\n");
						}
					}
				}
				System.out.println(sb.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
