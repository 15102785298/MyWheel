package com.tantian.jsTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

public class createBeanFunction {

	public static void main(String[] args) throws IOException {
		System.out.println("请输入参数名称：");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		List<String> paramList = new LinkedList<>();
			String input_data = br.readLine();
			while (!"****".equals(input_data)) {
				paramList.add(input_data);
				input_data = br.readLine();
			}
			for (String param : paramList) {
				System.out.println("private String " + param + "; ");
			}
			System.out.println();
		}

}
