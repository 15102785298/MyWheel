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
		Document document = DocumentHelper.createDocument();  // 创建一个xml文档（此时还未写入）
		Element TEST_PACK = document.addElement("TEST_PACK"); // 在这个文档中加入一个跟节点，注意，此类节点只能添加一个
		TEST_PACK.addAttribute("note", "自动生成的测试用例"); // 为这个节点添加描述
		Element TEST = TEST_PACK.addElement("Test"); // 在TEST_PACK下添加一个新的叫做Test的节点
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
				List list = inparams.elements(); // 获取inparams下的所有节点List
				Element lastElement = (Element) list.get(list.size() - 1); // 得到该List的最后一条数据
				Attribute attribute = lastElement.attribute("value"); // 获取该节点的name为value的描述
				String value = attribute.getText() + "," + couple; // 获取描述的值
				attribute.setText(value); // 设置该描述的值
			}
		}

		/******************************** 添加受理单流程启动用例开始 **********************************/
		sub = TEST.addElement("sub");
		sub.addAttribute("id", function_id);
		sub.addAttribute("block", "1");
		sub.addAttribute("livetime", "5000");
		sub.addAttribute("pri", "8");
		sub.addAttribute("pack_ver", "32");
		sub.addAttribute("note", "流程启动用例");
		route = sub.addElement("route");
		route.addAttribute("system", "");
		route.addAttribute("sub_system", "");
		route.addAttribute("branch", "");
		route.addAttribute("esb_name", "");
		route.addAttribute("esb_no", "0");
		route.addAttribute("neighbor", "");
		route.addAttribute("plugin", "");
		inparams = sub.addElement("inparams");
		inparams.addAttribute("note", "流程启动");
		Element in = inparams.addElement("in");
		in.addAttribute("name", "acpt_id");
		in.addAttribute("value", "");
		in = inparams.addElement("in");
		in.addAttribute("name", "entrust_way");
		in.addAttribute("value", "z");
		in = inparams.addElement("in");
		in.addAttribute("name", "op_station");
		in.addAttribute("value", "100");
		in = inparams.addElement("in");
		in.addAttribute("name", "branch_no");
		in.addAttribute("value", "100");
		/******************************** 添加受理单流程启动用例结束 **********************************/

		document.setXMLEncoding("GBK"); // 设置编码方式为GBK
		OutputFormat format = OutputFormat.createPrettyPrint(); // 创建一个格式化输出器
		format.setEncoding("GBK"); // 给格式化输出器指定一个码表，xml文档什么编码，格式化输出器就是什么编码
		XMLWriter writer = new XMLWriter(new FileOutputStream(FilePath + function_id + ".xml"), format); // 利用XMLWriter写入文件
		writer.write(document); // 写入操作
		writer.close(); // 关闭文件连接
		System.out.println("转换用例：" + function_id + "成功！");
	}
}
