package com.zc.servlet;

import java.io.IOException;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.DocumentException;
import com.zc.po.TextMessage;
import com.zc.util.CheckUtil;
import com.zc.util.MessageUtil;

public class WeixinServlet extends HttpServlet {
	/**
	 * 接入验证
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 接受微信传过来的四个参数
		String signature = req.getParameter("signature");
		String timestamp = req.getParameter("timestamp");
		String nonce = req.getParameter("nonce");
		String echostr = req.getParameter("echostr");

		PrintWriter out = resp.getWriter();
		// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
		if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
			out.print(echostr);
		}
	}

	/**
	 * 消息的接收与响应
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		try {
			Map<String, String> map = MessageUtil.xmlToMap(req);
			String fromUserName = map.get("FromUserName");
			String toUserName = map.get("ToUserName");
			String msgType = map.get("MsgType");
			String mobiles = map.get("Content");
			String response = getshouji(mobiles);
			String message = null;
			if ("text".equals(msgType)) {
				TextMessage text = new TextMessage();
				text.setFromUserName(toUserName);
				text.setToUserName(fromUserName);
				text.setMsgType("text");
				text.setCreateTime(new Date().getTime());
				text.setContent(response);
				message = MessageUtil.textMessageToXml(text);
			}
			System.out.println(message);
			out.print(message);
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

	// 判断手机号码
	public boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	// 获取手机验证码
	public String getshouji(String mobiles) {
		boolean num = isMobileNO(mobiles);
		String content = "";
		if (num) {
			// 验证码接口
			String Url = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
			HttpClient client = new HttpClient();
			PostMethod method = new PostMethod(Url);

			client.getParams().setContentCharset("GBK");
			method.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=GBK");
			int mobile_code = (int) ((Math.random() * 9 + 1) * 100000);

			content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。");

			NameValuePair[] data = { // 提交短信
					new NameValuePair("account", "C24711564"), // 查看用户名是登录用户中心->验证码短信->产品总览->APIID
					new NameValuePair("password", "630b702657431780354384b6ebff5956"), // 查看密码请登录用户中心->验证码短信->产品总览->APIKEY
					new NameValuePair("mobile", mobiles), new NameValuePair("content", content), };
			method.setRequestBody(data);
		} else {
			content = "你输入的号码有误";
		}
		return content;
	}
}
