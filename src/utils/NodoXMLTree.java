package utils;

import java.awt.Point;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class NodoXMLTree {
	private ArrayList<NodoXMLTree> hijos = new ArrayList<NodoXMLTree>();
	private String index, text, id, clase, paquete, contentDesc;
	private boolean checkable, checked, clickable, enabled, focusable, focused, scrollable, password, selected;
	private Point position;
	private int width, heigth, treeIndex, contador = 0;

	public NodoXMLTree(ArrayList<NodoXMLTree> hijos, String index, String text, String id, String clase, String paquete,
			String contentDesc, boolean checkable, boolean checked, boolean clickable, boolean enabled,
			boolean focusable, boolean focused, boolean scrollable, boolean password, boolean selected, Point position,
			int width, int heigth) {
		this.hijos = hijos;
		this.index = index;
		this.text = text;
		this.id = id;
		this.clase = clase;
		this.paquete = paquete;
		this.contentDesc = contentDesc;
		this.checkable = checkable;
		this.checked = checked;
		this.clickable = clickable;
		this.enabled = enabled;
		this.focusable = focusable;
		this.focused = focused;
		this.scrollable = scrollable;
		this.password = password;
		this.selected = selected;
		this.position = position;
		this.width = width;
		this.heigth = heigth;
	}
	
	public NodoXMLTree() {
		this.index = null;
		this.text = null;
		this.id = null;
		this.clase = null;
		this.paquete = null;
		this.contentDesc = null;
		this.checkable = false;
		this.checked = false;
		this.clickable = false;
		this.enabled = false;
		this.focusable = false;
		this.focused = false;
		this.scrollable = false;
		this.password = false;
		this.selected = false;
		this.position = null;
		this.width = 0;
		this.heigth = 0;
	}
	
	public NodoXMLTree(Node nodo) {
		Element eElement = (Element) nodo;
		this.index = eElement.getAttribute("index");
		this.text = eElement.getAttribute("text");
		this.id = eElement.getAttribute("resource-id");
		this.clase = eElement.getAttribute("class");
		this.paquete = eElement.getAttribute("package");
		this.contentDesc = eElement.getAttribute("content-desc");
		this.checkable = Boolean.valueOf(eElement.getAttribute("checkable"));
		this.checked = Boolean.valueOf(eElement.getAttribute("checked"));
		this.clickable = Boolean.valueOf(eElement.getAttribute("clickable"));
		this.enabled = Boolean.valueOf(eElement.getAttribute("enabled"));
		this.focusable = Boolean.valueOf(eElement.getAttribute("focusable"));
		this.focused = Boolean.valueOf(eElement.getAttribute("focused"));
		this.scrollable = Boolean.valueOf(eElement.getAttribute("scrollable"));
		this.password = Boolean.valueOf(eElement.getAttribute("password"));
		this.selected = Boolean.valueOf(eElement.getAttribute("selected"));
		ArrayList<Integer> bounds = separateBounds(eElement.getAttribute("bounds"));
		this.position = new Point(bounds.get(0), bounds.get(1));
		this.width = bounds.get(2) - bounds.get(0);
		this.heigth = bounds.get(3) - bounds.get(1);
	}
	
	public ArrayList<Integer> separateBounds(String bounds) {
		ArrayList<Integer> valores = new ArrayList<Integer>();
		String[] separado1 = bounds.split(",");
        String[] separado2 = separado1[1].split("]");
        valores.add(Integer.parseInt(separado1[0].substring(1, separado1[0].length())));
        valores.add(Integer.parseInt(separado2[0]));
        valores.add(Integer.parseInt(separado2[1].substring(1, separado2[1].length())));
        valores.add(Integer.parseInt(separado1[2].substring(0, separado1[2].length()-1)));
		return valores;
	}
	
	public void printData (NodoXMLTree cabeza, int level) {
		ArrayList<NodoXMLTree> hijos = cabeza.getHijos();
		for (int i = 0; i < hijos.size(); i++) {
			printData(hijos.get(i), level+1);
		}
	}
	
	public void createIndex (NodoXMLTree cabeza) {
		ArrayList<NodoXMLTree> hijos = cabeza.getHijos();
		for (int i = 0; i < hijos.size(); i++) {
			if (!hasChild(cabeza)) {
				contador++;
			}
			contador++;
			hijos.get(i).setTreeIndex(contador);
			createIndex(hijos.get(i));
		}
	}
	
	/*public NodoXMLTree getNodeN(NodoXMLTree cabeza, int n) {
		ArrayList<NodoXMLTree> hijos = cabeza.getHijos();
		this.contador = n;
		n--;
		if (n >= 1) {
			int i = 0;
			while (i < hijos.size()) {  
				NodoXMLTree temp =getNodeN(hijos.get(i), n);
				if (temp.contador <=0) {
					return temp;
				}
				n=temp.contador-1 ;
					if (i > 0 && hasChild(hijos.get(i))){
						n = hijos.get(i).contador -1;
					}
				
				i++;
			}
		}
		return cabeza;
	}*/
	
	public NodoXMLTree getNodeN(NodoXMLTree cabeza, int n) {
		ArrayList<NodoXMLTree> hijos = cabeza.getHijos();
		if (cabeza.getTreeIndex() == n) {
			return cabeza;
		} else {
			for (int i = 0; i < hijos.size(); i++) {
				if (hasChild(cabeza)) {
					NodoXMLTree temp = getNodeN (hijos.get(i), n);
					if (temp != null) {
						return temp;
					}
				}
			}
		}
		return null;
	}
	
	public boolean hasChild(NodoXMLTree nodo) {
		if (nodo.getHijos().isEmpty()) {
			return false;
		}
		return true;
	}
	
	public NodoXMLTree getNodeN2(NodoXMLTree cabeza, int n) {
		ArrayList<NodoXMLTree> hijos = cabeza.getHijos();
		contador = 0;
		if (contador != n) {
			for (int i = 0; i < hijos.size(); i++) {
				contador++;
				getNodeN2(hijos.get(i), n);
			}
		} 
		return cabeza;
	}
	
	/*private String separar(String className) { // Este método toma la última palabra del class para ponerlo como etiqueta en el nuevo XML
		String[] separado;
		separado = className.split("\\.");
		return separado[separado.length - 1];
	}*/
	
	public void addChild(NodoXMLTree nuevoHijo) {
		this.hijos.add(nuevoHijo);
	}

	public ArrayList<NodoXMLTree> getHijos() {
		return hijos;
	}

	public void setHijos(ArrayList<NodoXMLTree> hijos) {
		this.hijos = hijos;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}

	public String getPaquete() {
		return paquete;
	}

	public void setPaquete(String paquete) {
		this.paquete = paquete;
	}

	public String getContentDesc() {
		return contentDesc;
	}

	public void setContentDesc(String contentDesc) {
		this.contentDesc = contentDesc;
	}

	public boolean isCheckable() {
		return checkable;
	}

	public void setCheckable(boolean checkable) {
		this.checkable = checkable;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isClickable() {
		return clickable;
	}

	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isFocusable() {
		return focusable;
	}

	public void setFocusable(boolean focusable) {
		this.focusable = focusable;
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	public boolean isScrollable() {
		return scrollable;
	}

	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
	}

	public boolean isPassword() {
		return password;
	}

	public void setPassword(boolean password) {
		this.password = password;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeigth() {
		return heigth;
	}

	public void setHeigth(int heigth) {
		this.heigth = heigth;
	}

	public int getTreeIndex() {
		return treeIndex;
	}

	public void setTreeIndex(int treeIndex) {
		this.treeIndex = treeIndex;
	}
	
	
}
