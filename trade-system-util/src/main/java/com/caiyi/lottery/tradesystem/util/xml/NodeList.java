package com.caiyi.lottery.tradesystem.util.xml;

public class NodeList {
	public String attr;
	public boolean check;
	public String[] nodes;

	NodeList(JXmlWrapper paramJXmlWapper) {
	}

	public String last() {
		return this.nodes[(this.nodes.length - 1)];
	}
}
