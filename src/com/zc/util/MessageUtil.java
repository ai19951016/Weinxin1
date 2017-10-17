package com.zc.util;

import java.io.IOException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.zc.po.TextMessage;
/**
 * 消息封装类
 * @author Stephen
 *
 */
public class MessageUtil {	
	/**
	 * xml转为map集合
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> xmlToMap(HttpServletRequest request) throws IOException, DocumentException{
		Map<String, String> map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		//获取输入流
		InputStream ins = request.getInputStream();
		Document doc = reader.read(ins);
		//获取xml的根元素
		Element root = doc.getRootElement();
		//把获取的根元素的节点放入list集合中
		List<Element> list = root.elements();
		//遍历对象，把遍历的记过保存到map集合中
		for(Element e : list){
			map.put(e.getName(), e.getText());
		}
		ins.close();
		return map;
	}
	
	/**
	 * 将文本消息对象转为xml
	 * @param textMessage
	 * @return
	 */
	public static String textMessageToXml(TextMessage textMessage){
		XStream xstream = new XStream();
		//将xml的根节点替换成XML
		xstream.alias("xml", textMessage.getClass());
		return xstream.toXML(textMessage);
	}
	
}
