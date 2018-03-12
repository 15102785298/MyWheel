package com.tantian.bopModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class BopJson {

	// 请求名称
	private String urlJson;
	// 请求对应常量名称
	private Set<String> url2constantNameList;
	// 读取的UFFUNCTION文件
	private static Set<String> ufFunctionlines = null;

	public String getUrlJson() {
		return urlJson;
	}

	public void setUrlJson(String urlJson) {
		this.urlJson = urlJson;
	}

	public Set<String> getUrl2constantNameList() {
		return url2constantNameList;
	}

	public void setUrl2constantNameList(Set<String> url2constantNameList) {
		this.url2constantNameList = url2constantNameList;
	}

	public BopJson(String urlJson, List<File> urlFileList) {
		this.urlJson = urlJson;
		this.url2constantNameList = new HashSet<>();
		for (File temp : urlFileList) {
			this.url2constantNameList.addAll(searchUrl(temp, this.urlJson));
		}
	}

	public void printfSelf() {
		System.out.println("请求名称：" + urlJson);
		int i = 1;
		for (String temp : url2constantNameList) {
			System.out.println("第" + i++ + "个常量名：" + temp);
		}
	}

	/**
	 * 查找url对应的常量名称
	 *
	 * @param readFile
	 * @param url
	 * @return
	 */
	private static Set<String> searchUrl(File readFile, String url) {
		Set<String> res = new HashSet<>();
		if (ufFunctionlines == null) {
			ufFunctionlines = new HashSet<String>();
			try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
				File filename = readFile; // 要读取以上路径的input。txt文件
				InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8"); // 建立一个输入流对象reader
				BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
				String line = "";
				line = br.readLine();
				while (line != null) {
					line = line.trim();
					ufFunctionlines.add(line);
					if (StringUtils.isNotBlank(line) && StringUtils.contains(line, url)
							&& !StringUtils.startsWith(line, "//")) {
						String finalString = line.split("=")[0].trim()
								.split(" ")[line.split("=")[0].trim().split(" ").length - 1].trim();
						res.add(finalString);
					}
					line = br.readLine(); // 一次读入一行数据
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		} else {
			for (String line : ufFunctionlines) {
				if (StringUtils.isNotBlank(line) && StringUtils.contains(line, url)
						&& !StringUtils.startsWith(line, "//")) {
					String finalString = line.split("=")[0].trim()
							.split(" ")[line.split("=")[0].trim().split(" ").length - 1].trim();
					res.add(finalString);
				}

			}
			return res;
		}

	}

}
