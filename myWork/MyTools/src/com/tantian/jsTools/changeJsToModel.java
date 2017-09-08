package com.tantian.jsTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

public class changeJsToModel {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String temp = br.readLine();
		String res = "{";
		while(!temp.equals("}")){
			if(temp.equals("{")){
				temp = br.readLine();
				continue;
			}
			temp = temp.replace('\"', ' ');
			temp = temp.replace(':', '=');
			temp = temp.replace('\n', ' ');
			res = res + temp.trim();
			temp = br.readLine();
		}
		res += "}";
		System.out.println(res);
	}
}
