/**
 * Copyright 2015 Nortal Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 **/

package com.nortal.jroad.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.namespace.QName;

/**
 * Encapsulates X-Tee query header object.
 * 
 * @author Roman Tekhov
 * @author Aleksei Bogdanov (aleksei.bogdanov@nortal.com)
 * @author Lauri Lättemäe (lauri.lattemae@nortal.com) - protocol 4.0
 */
public class XTeeHeader implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String XROAD_NS_URI = "http://x-road.eu/xsd/xroad.xsd";
  public static final String XROAD_ID_NS_URI = "http://x-road.eu/xsd/identifiers";

  public static final QName CLIENT = new QName(XROAD_NS_URI, "client");
  public static final QName SERVICE = new QName(XROAD_NS_URI, "service");
  public static final QName USER_ID = new QName(XROAD_NS_URI, "userId");
  public static final QName ID = new QName(XROAD_NS_URI, "id");
  public static final QName PROTOCOL_VERSION = new QName(XROAD_NS_URI, "protocolVersion");

  private List<XRoadHeaderElement> elements = new ArrayList<XRoadHeaderElement>();

  /**
   * Gets all the elements with values.
   * @return
   */
  @Deprecated
  public Map<QName, String> getElemendid() {
    Map<QName, String> oldElementList = new HashMap<QName, String>();
    for (XRoadHeaderElement element : elements) {
      oldElementList.putAll(getElementValues(element));
    }
    return oldElementList;
  }
  
  private Map<QName, String> getElementValues(XRoadHeaderElement headerElement) {
    Map<QName, String> elementList = new HashMap<QName, String>();
    if (headerElement.hasChildren()) {
      for (XRoadHeaderElement element : headerElement.getChildren()) {
        elementList.putAll(getElementValues(element));
      }
    } else {
      elementList.put(headerElement.getqName(), headerElement.getValue());
    }
    return elementList;
  }

  @Deprecated
  public void setElemendid(Map<QName, String> elements) {
    for (Map.Entry<QName, String> entry : elements.entrySet()) {
      this.elements.add(new XRoadHeaderElement(entry.getKey(), entry.getValue()));
    }
  }

  @Deprecated
  public void addElement(QName qName, String value) {
    this.elements.add(new XRoadHeaderElement(qName, value));
  }
  
  /**
   * As of protocol v4 getting Header element by QNames, the ordering of the QNames is important as the levels are searched in the provided order
   * 
   * e.g.
   *   <xroad:client id:objectType="SUBSYSTEM">
   *        <id:xRoadInstance>ee-dev</id:xRoadInstance>
   *        <id:memberClass>GOV</id:memberClass>
   *        <id:memberCode>10117341</id:memberCode>
   *        <id:userId>43009090226</id:userId>
   *        <id:subsystemCode>generic-consumer</id:subsystemCode>
   *    </xroad:client>
   *    
   *   Getting memberCode would require the path with 2 QNames
   *   First one for the xroad:client element and second one for id:memberCode element
   * 
   * @param names
   * @return
   */
  public XRoadHeaderElement getByQName(QName...names) {
    return getByQName(this.elements, names);
  }
  
  private XRoadHeaderElement getByQName(List<XRoadHeaderElement> elements, QName...names) {
    for (XRoadHeaderElement element : elements) {
      if (names == null) {
        return element;
      }
      if (element.getqName().equals(names[0])) {
        if (element.getChildren() == null || element.getChildren().isEmpty() || names.length == 1) {
          return element;
        }
        return getByQName(element.getChildren(), Arrays.copyOfRange(names, 1, names.length));
      }
    }
    return null;
  }
  
  public void addHeaderElement(XRoadHeaderElement element) {
    elements.add(element);
  }
}
