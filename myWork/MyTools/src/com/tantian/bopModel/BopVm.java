package com.tantian.bopModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.tantian.bopUtils.FileUtils;
import com.tantian.bopUtils.PatternUtils;

public class BopVm {
	// 项目路径
	private String projectPath;
	// 类名
	private String vmsName;
	// 路径
	private String vmPath;
	// 文件
	private File vmFile;
	// vm界面中调用的json
	private Set<BopJson> urlJson;
	// vm界面原生请求
	String urlSelfHtm;

	public BopVm(File vmFile, String projectPath, List<File> urlFileList) {
		this.projectPath = projectPath;
		this.vmFile = vmFile;
		this.vmsName = StringUtils.split(vmFile.getName(), ".")[0];
		this.vmPath = vmFile.getAbsolutePath();
		this.urlJson = searchJsonAndHtm(vmFile, urlFileList);
		this.urlSelfHtm = PatternUtils.patternString(vmPath, "\\\\bop\\\\(\\S*).vm", 0).size() == 0 ? ""
				: PatternUtils.patternString(vmPath, "\\\\bop\\\\(\\S*).vm", 0).get(0);
	}

	public void printfSelf() {
		System.out.println("项目路径：" + projectPath);
		System.out.println("类名：" + vmsName);
		System.out.println("类路径：" + vmPath);
		System.out.println("vm界面原生请求：" + urlSelfHtm);
		System.out.println("--------vm界面" + vmsName + "中调用的json------");
		for (BopJson temp : urlJson) {
			temp.printfSelf();
		}
		System.out.println("--------vm界面" + vmsName + "中调用的json------");
	}

	/**
	 * 查找文件中所有的Json请求
	 *
	 * @param readFile
	 * @return
	 */
	private static Set<BopJson> searchJsonAndHtm(File readFile, List<File> urlFileList) {
		Set<BopJson> res = new HashSet<>();
		try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
			File filename = readFile; // 要读取以上路径的input。txt文件
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
			BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String line = "";
			line = br.readLine();
			while (line != null) {
				line = line.trim();
				if (StringUtils.isNotBlank(line)
						&& (StringUtils.contains(line, ".json") || StringUtils.contains(line, ".htm"))
						&& !StringUtils.startsWith("//", line) && !StringUtils.startsWith("##", line)) {
					if (line.indexOf("/") < 0) {
						line = br.readLine(); // 一次读入一行数据
						continue;
					}
					String spliteFlag = ".json";
					if (StringUtils.contains(line, ".htm")) {
						spliteFlag = ".htm";
					}
					String[] list = line.split(spliteFlag);
					String finalString = list[0].split("\"")[list[0].split("\"").length - 1] + spliteFlag;
					if (StringUtils.indexOf(finalString, "/'") > -1) {
						finalString = finalString.split("\'")[finalString.split("\'").length - 1];
					}
					if (finalString.trim().startsWith("$contain.get")) {
						line = br.readLine(); // 一次读入一行数据
						continue;
					}
					res.add(new BopJson(finalString, urlFileList));
				}
				line = br.readLine(); // 一次读入一行数据
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

}
