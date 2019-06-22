package com.caiyi.lottery.tradesystem.util.xml;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.caiyi.lottery.tradesystem.util.StringUtil;
import org.jdom.Attribute;
import org.jdom.Element;

public class JXmlWrapper implements Serializable {
	private static final long serialVersionUID = 5715917874756222506L;
	private Element contentEle;

	public JXmlWrapper(String paramString) {
		this.contentEle = new Element(paramString);
	}

	public JXmlWrapper(Element paramElement) {
		this.contentEle = paramElement;
	}

	public Element getXmlRoot() {
		return getXmlRoot(false);
	}

	public Element getXmlRoot(boolean paramBoolean) {
		if (!paramBoolean) {
			return this.contentEle;
		}
		for (Element localElement = this.contentEle; localElement.getParentElement() != null; 
				localElement = localElement.getParentElement()) {
			if(localElement.getParentElement() == null){
				return localElement;
			}
		}
		return contentEle;
	}

	public String toXmlString() {
		return toXmlString("gbk");
	}

	public String toXmlString(String paramString) {
		return toXmlString(this, paramString);
	}

	public static JXmlWrapper parse(String paramString) {
		Element localElement = JXmlUtil.string2Xml(paramString);
		if (localElement != null) {
			return new JXmlWrapper(localElement);
		}
		return null;
	}

	public static JXmlWrapper parse(File paramFile) {
		Element localElement = JXmlUtil.file2Xml(paramFile);
		if (localElement != null) {
			return new JXmlWrapper(localElement);
		}
		return null;
	}

	public static JXmlWrapper parseUrl(String paramString) throws Exception {
		return parseUrl(paramString, 10);
	}

	public static JXmlWrapper parseUrl(String paramString, int paramInt) throws Exception {
		Element localElement = JXmlUtil.url2Xml(paramString, paramInt);
		if (localElement != null) {
			return new JXmlWrapper(localElement);
		}
		return null;
	}

	public static JXmlWrapper parseUrl(String paramString1, String paramString2) throws Exception {
		return parseUrl(paramString1, paramString2, 10);
	}

	public static JXmlWrapper parseUrl(String paramString1, String paramString2, int paramInt) throws Exception {
		Element localElement = JXmlUtil.url2Xml(paramString1, paramString2, null, paramInt);
		if (localElement != null) {
			return new JXmlWrapper(localElement);
		}
		return null;
	}

	public static JXmlWrapper parseUrl(String paramString1, String paramString2, String paramString3, int paramInt)
			throws Exception {
		Element localElement = JXmlUtil.url2Xml(paramString1, paramString2, paramString3, paramInt);
		if (localElement != null) {
			return new JXmlWrapper(localElement);
		}
		return null;
	}

	public static String toXmlString(JXmlWrapper paramJXmlWrapper, String paramString) {
		return JXmlUtil.xml2String(paramJXmlWrapper.getXmlRoot(), paramString);
	}

	public void addValue(String paramString, int paramInt) {
		addValue(paramString, String.valueOf(paramInt));
	}

	public void addValue(String paramString, long paramLong) {
		addValue(paramString, String.valueOf(paramLong));
	}

	public void addValue(String paramString, double paramDouble) {
		addValue(paramString, String.valueOf(paramDouble));
	}

	public void addValue(String paramString, Date paramDate) {
		addValue(paramString, TimeUtil.customDateTime(paramDate));
	}

	public void addValue(String paramString, boolean paramBoolean) {
		addValue(paramString, String.valueOf(paramBoolean));
	}

	public void addStringValue(String paramString1, String paramString2) {
		addValue(paramString1, paramString2);
	}

	public void addValue(String paramString1, String paramString2) {
		NodeList localNodeList = parseNodeList(paramString1);
		Element localElement = parseNodeList(localNodeList);
		if (localElement == null) {
			throw new RuntimeException("指定路径" + paramString1 + "的节点创建失败");
		}
		if (localNodeList.check) {
			localElement.setAttribute(localNodeList.attr, paramString2);
		} else {
			List localList = localElement.removeContent();
			localElement.setText(paramString2);
			for (int i = 0; i < localList.size(); i++) {
				if ((localList.get(i) instanceof Element)) {
					localElement.addContent((Element) localList.get(i));
				}
			}
		}
	}

	public void setValue(String paramString, int paramInt) {
		setValue(paramString, String.valueOf(paramInt));
	}

	public void setValue(String paramString, long paramLong) {
		setValue(paramString, String.valueOf(paramLong));
	}

	public void setValue(String paramString, double paramDouble) {
		setValue(paramString, String.valueOf(paramDouble));
	}

	public void setValue(String paramString, Date paramDate) {
		setValue(paramString, TimeUtil.customDateTime(paramDate));
	}

	public void setValue(String paramString, boolean paramBoolean) {
		setValue(paramString, String.valueOf(paramBoolean));
	}

	public void setValue(String paramString1, String paramString2) {
		NodeList localNodeList = parseNodeList(paramString1);
		Element localElement = parseNodeList(localNodeList, 0);
		if (localElement == null) {
			throw new RuntimeException("指定路径" + paramString1 + "的节点未找到");
		}
		if (localNodeList.check) {
			localElement.setAttribute(localNodeList.attr, paramString2);
		} else {
			List localList = localElement.removeContent();
			localElement.setText(paramString2);
			for (int i = 0; i < localList.size(); i++) {
				if ((localList.get(i) instanceof Element)) {
					localElement.addContent((Element) localList.get(i));
				}
			}
		}
	}

	public int getIntValue(String paramString) {
		return Integer.parseInt(getStringValue(paramString));
	}

	public long getLongValue(String paramString) {
		return Long.parseLong(getStringValue(paramString));
	}

	public short getShortValue(String paramString) {
		return Short.parseShort(getStringValue(paramString));
	}

	public double getDoubleValue(String paramString) {
		return Double.parseDouble(getStringValue(paramString));
	}

	public Date getDateValue(String paramString) {
		return TimeUtil.parserDateTime(getStringValue(paramString));
	}

	public boolean getBoolValue(String paramString) {
		return Boolean.parseBoolean(getStringValue(paramString));
	}

	public int getIntValue(String paramString, int paramInt) {
		String str = getStringValue(paramString, String.valueOf(paramInt));
		if ((str == null) || (str.length() == 0)) {
			return paramInt;
		}
		return Integer.parseInt(str);
	}

	public long getLongValue(String paramString, long paramLong) {
		String str = getStringValue(paramString, String.valueOf(paramLong));
		if ((str == null) || (str.length() == 0)) {
			return paramLong;
		}
		return Long.parseLong(str);
	}

	public short getShortValue(String paramString, short paramShort) {
		String str = getStringValue(paramString, String.valueOf(paramShort));
		if ((str == null) || (str.length() == 0)) {
			return paramShort;
		}
		return Short.parseShort(str);
	}

	public double getDoubleValue(String paramString, double paramDouble) {
		String str = getStringValue(paramString, String.valueOf(paramDouble));
		if ((str == null) || (str.length() == 0)) {
			return paramDouble;
		}
		return Double.parseDouble(str);
	}

	public Date getDateValue(String paramString1, String paramString2) {
		String str = getStringValue(paramString1, paramString2);
		if ((str == null) || (str.length() == 0)) {
			str = paramString2;
		}
		return TimeUtil.parserDateTime(str);
	}

	public boolean getBoolValue(String paramString, boolean paramBoolean) {
		String str = getStringValue(paramString, String.valueOf(paramBoolean));
		if ((str == null) || (str.length() == 0)) {
			return paramBoolean;
		}
		return Boolean.parseBoolean(str);
	}

	public String getStringValue(String paramString) {
		return getStringValue(paramString, null);
	}

	public String getStringValue(String paramString1, String paramString2) {
		NodeList localNodeList = parseNodeList(paramString1);
		Element localElement = parseNodeList(localNodeList, 0);
		if (localElement != null) {
			if (localNodeList.check) {
				Attribute localAttribute = localElement.getAttribute(localNodeList.attr);
				if (localAttribute != null) {
					return localAttribute.getValue();
				}
			} else {
				return localElement.getText();
			}
		}
		return paramString2;
	}

	public Map<String, String> getXmlAttrs(String paramString) {
		NodeList localNodeList = parseNodeList(paramString);
		if (localNodeList.check) {
			throw new RuntimeException("当前路径<" + paramString + ">为属性路径");
		}
		Element localElement = parseNodeList(localNodeList, 0);
		if (localElement == null) {
			throw new RuntimeException("指定路径" + paramString + "的节点未找到");
		}
		Hashtable localHashtable = new Hashtable();
		List localList = localElement.getAttributes();
		for (int i = 0; i < localList.size(); i++) {
			localHashtable.put(((Attribute) localList.get(i)).getName(), ((Attribute) localList.get(i)).getValue());
		}
		return localHashtable;
	}

	public JXmlWrapper getXmlNode(String paramString) {
		NodeList localNodeList = parseNodeList(paramString);
		if (localNodeList.check) {
			throw new RuntimeException("当前路径<" + paramString + ">为属性路径");
		}
		Element localElement = parseNodeList(localNodeList, 0);
		if (localElement == null) {
			return null;
		}
		if (localElement == this.contentEle) {
			return this;
		}
		return new JXmlWrapper(localElement);
	}

	public JXmlWrapper addXmlNode(String paramString) {
		NodeList localNodeList = parseNodeList(paramString);
		if (localNodeList.check) {
			throw new RuntimeException("当前路径<" + paramString + ">为属性路径");
		}
		Element localElement = parseNodeList(localNodeList);
		if (localElement == null) {
			throw new RuntimeException("指定路径" + paramString + "的节点未创建成功");
		}
		if (localElement == this.contentEle) {
			return this;
		}
		return new JXmlWrapper(localElement);
	}

	public List<JXmlWrapper> getXmlNodeList(String paramString) {
		NodeList localNodeList = parseNodeList(paramString);
		if (localNodeList.check) {
			throw new RuntimeException("当前路径<" + paramString + ">为属性路径");
		}
		Element localElement = parseNodeList(localNodeList, 1);
		if (localElement == null) {
			throw new RuntimeException("指定路径" + paramString + "的节点未找到");
		}
		ArrayList localArrayList = new ArrayList();
		List localList = localElement.getChildren(localNodeList.last());
		for (int i = 0; i < localList.size(); i++) {
			localArrayList.add(new JXmlWrapper((Element) localList.get(i)));
		}
		return localArrayList;
	}

	public int countXmlNodes(String paramString) {
		NodeList localNodeList = parseNodeList(paramString);
		if (localNodeList.check) {
			throw new RuntimeException("当前路径<" + paramString + ">为属性路径");
		}
		Element localElement = parseNodeList(localNodeList, 1);
		if (localElement == null) {
			return 0;
		}
		return localElement.getChildren(localNodeList.last()).size();
	}

	public boolean remove(String paramString) {
		NodeList localNodeList = parseNodeList(paramString);
		Element localElement1 = parseNodeList(localNodeList, 0);
		if (localElement1 == null) {
			return true;
		}
		if (localNodeList.check) {
			return localElement1.removeAttribute(localNodeList.attr);
		}
		Element localElement2 = localElement1.getParentElement();
		if (localElement2 != null) {
			List localList = localElement2.getChildren();
			if ((localList != null) && (localList.size() > 0)) {
				localList.remove(localElement1);
				return true;
			}
			return false;
		}
		return false;
	}

	private Element parseNodeList(NodeList paramNodeList, int paramInt) {
		Element localElement = getXmlRoot();
		for (int i = 0; i < paramNodeList.nodes.length - paramInt; i++) {
			Node localNode = parseNode(paramNodeList.nodes[i]);
			List localList = localElement.getChildren(localNode.node);
			if (localList.size() == 0) {
				return null;
			}
			if (localNode.check) {
				if (localNode.index >= localList.size()) {
					return null;
				}
				localElement = (Element) localList.get(localNode.index);
			} else {
				if (localList.size() > 1) {
					return null;
				}
				localElement = (Element) localList.get(0);
			}
		}
		return localElement;
	}

	private Element parseNodeList(NodeList paramNodeList) {
		Element localObject = getXmlRoot();
		for (int i = 0; i < paramNodeList.nodes.length; i++) {
			Node localNode = parseNode(paramNodeList.nodes[i]);
			List<Element> localList = localObject.getChildren(localNode.node);
			Element localElement;
			if (localNode.check) {
				if (localNode.index == localList.size()) {
					localElement = new Element(localNode.node);
					localObject.addContent(localElement);
					localObject = localElement;
				} else if (localNode.index < localList.size()) {
					localObject = localList.get(localNode.index);
				} else {
					return null;
				}
			} else if (localList.size() == 0) {
				localElement = new Element(localNode.node);
				localObject.addContent(localElement);
				localObject = localElement;
			} else if (i < paramNodeList.nodes.length - 1) {
				if (localList.size() == 1) {
					localObject = (Element) localList.get(0);
				} else {
					return null;
				}
			} else {
				localElement = new Element(localNode.node);
				localObject.addContent(localElement);
				localObject = localElement;
			}
		}
		return localObject;
	}

	private Node parseNode(String paramString) {
		Node localNode = new Node(this);
		if (paramString.endsWith("]")) {
			int i = paramString.indexOf("[");
			localNode.check = true;
			localNode.node = paramString.substring(0, i);
			localNode.index = Integer.parseInt(paramString.substring(i + 1, paramString.length() - 1));
		} else {
			localNode.node = paramString;
			localNode.check = false;
		}
		return localNode;
	}

	private NodeList parseNodeList(String paramString) {
		NodeList localNodeList = new NodeList(this);
		if (paramString.equals(".")) {
			localNodeList.check = false;
			localNodeList.nodes = new String[0];
		} else {
			String[] arrayOfString = StringUtil.splitter(paramString, ".");
			int i = arrayOfString.length;
			int j = arrayOfString.length - 1;
			if (arrayOfString[j].startsWith("@")) {
				localNodeList.check = true;
				localNodeList.attr = arrayOfString[j].substring(1);
				localNodeList.nodes = new String[j];
				System.arraycopy(arrayOfString, 0, localNodeList.nodes, 0, j);
			} else {
				localNodeList.check = false;
				localNodeList.nodes = new String[i];
				System.arraycopy(arrayOfString, 0, localNodeList.nodes, 0, i);
			}
		}
		return localNodeList;
	}
}
