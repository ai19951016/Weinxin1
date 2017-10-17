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
 * ��Ϣ��װ��
 * @author Stephen
 *
 */
public class MessageUtil {	
	/**
	 * xmlתΪmap����
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> xmlToMap(HttpServletRequest request) throws IOException, DocumentException{
		Map<String, String> map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		//��ȡ������
		InputStream ins = request.getInputStream();
		Document doc = reader.read(ins);
		//��ȡxml�ĸ�Ԫ��
		Element root = doc.getRootElement();
		//�ѻ�ȡ�ĸ�Ԫ�صĽڵ����list������
		List<Element> list = root.elements();
		//�������󣬰ѱ����ļǹ����浽map������
		for(Element e : list){
			map.put(e.getName(), e.getText());
		}
		ins.close();
		return map;
	}
	
	/**
	 * ���ı���Ϣ����תΪxml
	 * @param textMessage
	 * @return
	 */
	public static String textMessageToXml(TextMessage textMessage){
		XStream xstream = new XStream();
		//��xml�ĸ��ڵ��滻��XML
		xstream.alias("xml", textMessage.getClass());
		return xstream.toXML(textMessage);
	}
	
}
