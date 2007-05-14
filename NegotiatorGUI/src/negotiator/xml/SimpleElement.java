package negotiator.xml;
/*
 * @(#)SimpleElement.java
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * <code>SimpleElement</code> is the only node type for
 * simplified DOM model.
 */
public class SimpleElement {
	private String tagName;
	private String text;
	private HashMap attributes;
	private LinkedList childElements;

	public SimpleElement(String tagName) {
		this.tagName = tagName;
		attributes = new HashMap();
		childElements = new LinkedList();
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAttribute(String name) {
		return (String)attributes.get(name);
	}

	public void setAttribute(String name, String value) {
		attributes.put(name, value);
	}

	public void addChildElement(SimpleElement element) {
		childElements.add(element);
	}

	public Object[] getChildElements() {
		return childElements.toArray();
	}
        public Object[] getChildByTagName(String tagName) {
            LinkedList result = new LinkedList();
            ListIterator iter = childElements.listIterator();
            while(iter.hasNext()) {
                SimpleElement se = (SimpleElement)iter.next();
                if (se.getTagName().equals(tagName))
                    result.add(se);
            }
            return result.toArray();
                
        }
        public String toString() {
        	String lResult="";
        	lResult +="<" + tagName;
            //save all attributes
            for(int i=0;i<attributes.size();i++) {
            	String lAttrName = (String)(attributes.keySet().toArray()[i]);
            	String lAttrValue="";
            	if (attributes.entrySet().toArray()[i]!=null)
            		lAttrValue= (String)(attributes.get(lAttrName));
            	
            	lResult +=" "+lAttrName+"=\"" +lAttrValue+"\"";
            }
            lResult +="> \n";
            //save all children
            for(int i=0;i<childElements.size();i++) {
            	SimpleElement lSE = (SimpleElement)getChildElements()[i];
            	lResult += lSE.toString();
            }
            if(text!=null) {
            	lResult += text+" \n";}
            lResult +="</" + tagName+"> \n";
        	
        	return lResult;
        }
        public void saveToFile(String pFileName) {
        	try {
                BufferedWriter out = new BufferedWriter(new FileWriter(pFileName));
                String lXML = toString();
                out.write(lXML);
                out.close();
            } catch (IOException e) {
            	e.printStackTrace();
            }
        	
        }
}
