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
		Document document = DocumentHelper.createDocument();  // ����һ��xml�ĵ�����ʱ��δд�룩
		Element TEST_PACK = document.addElement("TEST_PACK"); // ������ĵ��м���һ�����ڵ㣬ע�⣬����ڵ�ֻ�����һ��
		TEST_PACK.addAttribute("note", "�Զ����ɵĲ�������"); // Ϊ����ڵ��������
		Element TEST = TEST_PACK.addElement("Test"); // ��TEST_PACK�����һ���µĽ���Test�Ľڵ�
		Element sub = TEST.addElement("sub");
		sub.addAttribute("id", function_id);
		sub.addAttribute("block", "1");
		sub.addAttribute("livetime", "5000");
		sub.addAttribute("pri", "8");
		sub.addAttribute("pack_ver", "32");
		sub.addAttribute("note", "��������:" + function_id);
		Element route = sub.addElement("route");
		route.addAttribute("system", "");
		route.addAttribute("sub_system", "");
		route.addAttribute("branch", "");
		route.addAttribute("esb_name", "");
		route.addAttribute("esb_no", "0");
		route.addAttribute("neighbor", "");
		route.addAttribute("plugin", "");
		Element inparams = sub.addElement("inparams");
		inparams.addAttribute("note", function_id + "��������");
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
				List list = inparams.elements(); // ��ȡinparams�µ����нڵ�List
				Element lastElement = (Element) list.get(list.size() - 1); // �õ���List�����һ������
				Attribute attribute = lastElement.attribute("value"); // ��ȡ�ýڵ��nameΪvalue������
				String value = attribute.getText() + "," + couple; // ��ȡ������ֵ
				attribute.setText(value); // ���ø�������ֵ
			}
		}

		/******************************** ���������������������ʼ **********************************/
		sub = TEST.addElement("sub");
		sub.addAttribute("id", function_id);
		sub.addAttribute("block", "1");
		sub.addAttribute("livetime", "5000");
		sub.addAttribute("pri", "8");
		sub.addAttribute("pack_ver", "32");
		sub.addAttribute("note", "������������");
		route = sub.addElement("route");
		route.addAttribute("system", "");
		route.addAttribute("sub_system", "");
		route.addAttribute("branch", "");
		route.addAttribute("esb_name", "");
		route.addAttribute("esb_no", "0");
		route.addAttribute("neighbor", "");
		route.addAttribute("plugin", "");
		inparams = sub.addElement("inparams");
		inparams.addAttribute("note", "��������");
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
		/******************************** ����������������������� **********************************/

		document.setXMLEncoding("GBK"); // ���ñ��뷽ʽΪGBK
		OutputFormat format = OutputFormat.createPrettyPrint(); // ����һ����ʽ�������
		format.setEncoding("GBK"); // ����ʽ�������ָ��һ�����xml�ĵ�ʲô���룬��ʽ�����������ʲô����
		XMLWriter writer = new XMLWriter(new FileOutputStream(FilePath + function_id + ".xml"), format); // ����XMLWriterд���ļ�
		writer.write(document); // д�����
		writer.close(); // �ر��ļ�����
		System.out.println("ת��������" + function_id + "�ɹ���");
	}
}
