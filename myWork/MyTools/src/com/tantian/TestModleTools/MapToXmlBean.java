package com.tantian.TestModleTools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class MapToXmlBean {

	public void createXmlModel(String function_id, String Test_Map, String FilePath)
			throws TransformerException, IOException, ParserConfigurationException {
		Document document = DocumentHelper.createDocument();
		Element TEST_PACK = document.addElement("TEST_PACK");
		TEST_PACK.addAttribute("note", "自动生成的测试用例");
		Element TEST = TEST_PACK.addElement("Test");
		Element sub = TEST.addElement("sub");
		sub.addAttribute("id", function_id);
		sub.addAttribute("block", "1");
		sub.addAttribute("livetime", "5000");
		sub.addAttribute("pri", "8");
		sub.addAttribute("pack_ver", "32");
		sub.addAttribute("note", "测试用例:" + function_id);
		Element route = sub.addElement("route");
		route.addAttribute("system", "");
		route.addAttribute("sub_system", "");
		route.addAttribute("branch", "");
		route.addAttribute("esb_name", "");
		route.addAttribute("esb_no", "0");
		route.addAttribute("neighbor", "");
		route.addAttribute("plugin", "");
		Element inparams = sub.addElement("inparams");
		inparams.addAttribute("note", function_id + "测试用例");
		String temp_Test_Map = Test_Map.substring(1, Test_Map.length() - 1);
		String key_val[] = temp_Test_Map.split(",");
		String node[] = null;
		for (String temp : key_val) {
			String couple = temp.trim();
			if (couple.equals("null")) {
				continue;
			}
			if (couple.contains("=") || couple.contains(" ")) {
				Element in = inparams.addElement("in");
				node = (couple.trim()).contains(" ") && !couple.contains("=") ? couple.trim().split(" ")
						: couple.trim().split("=");
				in.addAttribute("name", node[0].trim());
				if (node.length > 1) {
					in.addAttribute("value", node[1].trim());
				} else {
					in.addAttribute("value", "");
				}
			} else {
				List list = inparams.elements();
				Element lastElement = (Element) list.get(list.size() - 1);
				Attribute attribute = lastElement.attribute("value");
				String value = attribute.getText() + "," + couple;
				attribute.setText(value);
			}

		}
		document.setXMLEncoding("GBK");
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK"); // 给格式化输出器指定一个码表，xml文档什么编码，格式化输出器就是什么编码
		XMLWriter writer = new XMLWriter(new FileOutputStream(FilePath + function_id + ".xml"), format);
		writer.write(document);
		writer.close();
		System.out.println("转换用例：" + function_id + "成功！");
	}
}
