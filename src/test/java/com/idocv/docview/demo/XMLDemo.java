package com.idocv.docview.demo;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLDemo {
	public static void main(String[] args) {
		try {
			File fXmlFile = new File("e:/invoice.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("Row");
			System.out.println(nList.getLength());

			System.out.println("发票号码\t客户名称\t税额\t合计金额\t价税合计");
			System.out.println("----------------------------");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				// System.out.println("\n-- " + (i + 1) + " ----------");
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String invoiceNum = eElement.getAttribute("发票号码");
					String name = eElement.getAttribute("客户名称");
					String invoiceSum = eElement.getAttribute("税额");
					String price = eElement.getAttribute("合计金额");
					String invoiceAndPriceSum = eElement.getAttribute("价税合计");
					
					System.out.println(invoiceNum + "\t" + name + "\t" + invoiceSum + "\t" + price + "\t" + invoiceAndPriceSum);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}