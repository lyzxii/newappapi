package com.caiyi.lottery.tradesystem.util.xml;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.caiyi.lottery.tradesystem.util.StringUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

import com.caiyi.lottery.tradesystem.util.CheckUtil;

public class JXmlUtil {
	private static Format onlineFormat;
	private static Format multilinFormat;

	public JXmlUtil() {
	}

	public static Document newDocument() {
		return new Document();
	}

	public static final Element newDocument(String paramString) {
		try {
			Document localDocument = new Document();
			Element localElement = new Element(paramString);
			localDocument.addContent(localElement);
			return localElement;
		} catch (Exception localException) {
		}
		return null;
	}

	public static final Element newElement(Element paramElement, String paramString) {
		Element localElement = new Element(paramString);
		paramElement.addContent(localElement);
		return localElement;
	}

	public static final Element newElement(Element paramElement, String paramString, Hashtable paramHashtable) {
		Element localElement = new Element(paramString);
		paramElement.addContent(localElement);
		if (paramHashtable != null) {
			Iterator localIterator = paramHashtable.keySet().iterator();
			while (localIterator.hasNext()) {
				Object localObject = localIterator.next();
				String str1 = localObject.toString();
				String str2 = paramHashtable.get(localObject).toString();
				localElement.setAttribute(str1, str2);
			}
		}
		return localElement;
	}

	public static final Element child(Element paramElement, String paramString) {
		List localList = paramElement.getChildren(paramString);
		if ((localList != null) && (localList.size() == 1)) {
			return (Element) localList.get(0);
		}
		throw new RuntimeException("指定的Xml子节点没有找到");
	}

	public static final List<Element> children(Element paramElement, String paramString) {
		return paramElement.getChildren(paramString);
	}

	public static String xml2String(Element paramElement, String paramString) {
		return xml2String(paramElement, getOnelineXmlFormat(paramString));
	}

	public static String xml2String(Document paramDocument, String paramString) {
		return xml2String(paramDocument, getOnelineXmlFormat(paramString));
	}

	public static String xml2String(Element paramElement, Format paramFormat) {
		Element localElement = (Element) paramElement.clone();
		localElement.detach();
		Document localDocument = new Document(localElement);
		return xml2String(localDocument, paramFormat);
	}

	public static String xml2String(Document paramDocument, Format paramFormat) {
		try {
			XMLOutputter localXMLOutputter = new XMLOutputter();
			localXMLOutputter.setFormat(paramFormat);
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			localXMLOutputter.output(paramDocument, localByteArrayOutputStream);
			return localByteArrayOutputStream.toString();
		} catch (Throwable localThrowable) {
			throw new RuntimeException("Xml文档转换--->异常", localThrowable);
		}
	}

	public static Element string2Xml(String paramString) {
		try {
			SAXBuilder localSAXBuilder = new SAXBuilder();
			InputSource localInputSource = new InputSource(new StringReader(paramString));
			Document localDocument = localSAXBuilder.build(localInputSource);
			return localDocument.getRootElement();
		} catch (Throwable localThrowable) {
			throw new RuntimeException("Xml文档分析--->异常", localThrowable);
		}
	}

	public static Element file2Xml(String paramString) {
		try {
			SAXBuilder localSAXBuilder = new SAXBuilder(false);
			Document localDocument = localSAXBuilder.build(paramString);
			return localDocument.getRootElement();
		} catch (Throwable localThrowable) {
			throw new RuntimeException("Xml文档分析--->异常", localThrowable);
		}
	}

	public static Element file2Xml(File paramFile) {
		try {
			SAXBuilder localSAXBuilder = new SAXBuilder(false);
			Document localDocument = localSAXBuilder.build(paramFile);
			return localDocument.getRootElement();
		} catch (Throwable localThrowable) {
			throw new RuntimeException("Xml文档分析--->异常", localThrowable);
		}
	}

	public static void xml2File(String paramString, Element paramElement) {
		try {
			paramElement.detach();
			Format localFormat = Format.getCompactFormat();
			localFormat.setEncoding("gbk");
			localFormat.setIndent("    ");
			localFormat.setLineSeparator("\r\n");
			XMLOutputter localXMLOutputter = new XMLOutputter(localFormat);
			Document localDocument = new Document();
			localDocument.setRootElement(paramElement);
			String str = "";
			str = localXMLOutputter.outputString(localDocument);
			str = str.replaceAll("\n\n", "\n");
			BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(new File(paramString)));
			localBufferedWriter.write(str);
			localBufferedWriter.close();
		} catch (Throwable localThrowable) {
			throw new RuntimeException("保存Xml文档--->异常", localThrowable);
		}
	}

	public static void xml2FileEx(String paramString, JXmlWrapper paramJXmlWapper) {
		try {
			Element localElement = paramJXmlWapper.getXmlRoot();
			localElement.detach();
			Format localFormat = Format.getCompactFormat();
			localFormat.setEncoding("gbk");
			localFormat.setIndent("    ");
			localFormat.setLineSeparator("\r\n");
			XMLOutputter localXMLOutputter = new XMLOutputter(localFormat);
			Document localDocument = new Document();
			localDocument.setRootElement(localElement);
			String str = "";
			str = localXMLOutputter.outputString(localDocument);
			str = str.replaceAll("\n\n", "\n");
			BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(new File(paramString)));
			localBufferedWriter.write(str);
			localBufferedWriter.close();
		} catch (Throwable localThrowable) {
			throw new RuntimeException("保存Xml文档--->异常", localThrowable);
		}
	}

	public static Element url2Xml(String paramString1, String paramString2) throws Exception {
		return url2Xml(paramString1, paramString2, null, 10);
	}

	public static Element url2Xml(String paramString1, String paramString2, String paramString3, int paramInt)
			throws Exception {
		URL localURL = new URL(paramString1);
		HttpURLConnection localHttpURLConnection = (HttpURLConnection) localURL.openConnection();
		localHttpURLConnection.setConnectTimeout(1000 * paramInt);
		localHttpURLConnection.setReadTimeout(1000 * paramInt);
		DataOutputStream localDataOutputStream = null;
		if (!CheckUtil.isNullString(paramString2)) {
			localHttpURLConnection.setDoInput(true);
			localHttpURLConnection.setDoOutput(true);
			localHttpURLConnection.setAllowUserInteraction(false);
			localDataOutputStream = new DataOutputStream(localHttpURLConnection.getOutputStream());
			localDataOutputStream.writeBytes(paramString2);
			localDataOutputStream.flush();
		}
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		InputStream localInputStream = localHttpURLConnection.getInputStream();
		byte[] arrayOfByte = new byte['?'];
		int i;
		while ((i = localInputStream.read(arrayOfByte)) != -1) {
			localByteArrayOutputStream.write(arrayOfByte, 0, i);
		}
		if (localDataOutputStream != null) {
			localDataOutputStream.close();
			localDataOutputStream = null;
		}
		localInputStream.close();
		localInputStream = null;
		localHttpURLConnection.disconnect();
		localHttpURLConnection = null;
		String str = "";
		if (!CheckUtil.isNullString(paramString3)) {
			str = new String(localByteArrayOutputStream.toByteArray(), paramString3);
		} else {
			str = new String(localByteArrayOutputStream.toByteArray());
		}
		localByteArrayOutputStream.close();
		localByteArrayOutputStream = null;
		SAXBuilder localSAXBuilder = new SAXBuilder(false);
		StringReader localStringReader = new StringReader(str.trim());
		Document localDocument = localSAXBuilder.build(localStringReader);
		return localDocument.getRootElement();
	}

	public static Element url2Xml(String paramString) throws Exception {
		return url2Xml(paramString, 10);
	}

	public static Element url2Xml(String paramString, int paramInt) throws Exception {
		return url2Xml(paramString, null, null, paramInt);
	}

	public static Format getOnelineXmlFormat(String paramString) {
		if (onlineFormat == null) {
			onlineFormat = Format.getRawFormat();
			onlineFormat.setEncoding(paramString);
			onlineFormat.setLineSeparator("\r\n");
		}
		return onlineFormat;
	}

	public static Format getMultilineXmlFormat(String paramString) {
		if (multilinFormat == null) {
			multilinFormat = Format.getRawFormat();
			multilinFormat.setEncoding("gbk");
			multilinFormat.setIndent("  ");
		}
		return multilinFormat;
	}

	public static String decode(String paramString) {
		if (paramString == null) {
			return "";
		}
		String str = paramString;
		str = StringUtil.replaceString(str, "&lt;", "<");
		str = StringUtil.replaceString(str, "&gt;", ">");
		str = StringUtil.replaceString(str, "&amp;", "&");
		str = StringUtil.replaceString(str, "&quot;", "\"");
		return str;
	}

	public static String encode(String paramString) {
		if (paramString == null) {
			return "";
		}
		String str = paramString;
		str = StringUtil.replaceString(str, "&", "&amp;");
		str = StringUtil.replaceString(str, "<", "&lt;");
		str = StringUtil.replaceString(str, ">", "&gt;");
		str = StringUtil.replaceString(str, "\"", "&quot;");
		return str;
	}

	public static String createTagXml(String paramString1, String paramString2) {
		StringBuffer localStringBuffer = new StringBuffer("");
		localStringBuffer.append("<");
		localStringBuffer.append(encode(paramString1));
		if (paramString2.length() == 0) {
			localStringBuffer.append("/>");
		} else {
			localStringBuffer.append(">");
			localStringBuffer.append(encode(paramString2));
			localStringBuffer.append("</");
			localStringBuffer.append(paramString1);
			localStringBuffer.append(">");
		}
		return new String(localStringBuffer);
	}

	public static String createAttrXml(String paramString1, String paramString2) {
		StringBuffer localStringBuffer = new StringBuffer("");
		localStringBuffer.append(encode(paramString1));
		localStringBuffer.append("=\"");
		localStringBuffer.append(encode(paramString2));
		localStringBuffer.append("\" ");
		return new String(localStringBuffer);
	}
}
