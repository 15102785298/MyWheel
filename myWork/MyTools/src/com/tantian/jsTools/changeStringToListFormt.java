package com.tantian.jsTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

public class changeStringToListFormt {

	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		String input = bf.readLine();
		String res = "";
		while (!"****".equals(input)) {
			String datas = input.substring(input.indexOf("\" ") + 2, input.indexOf(",\""));
			String name = input.substring(input.indexOf("String ") + 7, input.indexOf("=") - 1);
			datas = datas.replace(", ", "\", \"");
			datas = "private String[] " + name.toUpperCase() + " = {\"" + datas;
			datas = datas + "\"};\n";
			res += datas;
			input = bf.readLine();
		}
		System.out.println(res);
	}
}
