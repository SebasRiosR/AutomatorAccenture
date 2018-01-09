package controllers;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JProgressBar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.ExecuteShellComand;
import utils.NodoXMLTree;
import utils.XmlJTree;

public class PrincipalController {
	private ExecuteShellComand exec = new ExecuteShellComand(); // Objeto mediante el cual se ejecutan comandos en la consola
	private int modo; // El modo es visualización, captura de pantalla o manipular
	private int estadoCaptura = 0;
	private int contador = 0;
	private int widthCanvas, heightCanvas, widthCel, heightCel; //Tamaños del canvas y de la pantalla del celular
	private NodoXMLTree nodoCabeza;
	private List<String> lines = new ArrayList<String>(); // Almacena las líneas a escribir en el nuevo XML

	public void captureScreen(Canvas canvas, JProgressBar progressBar) {
		Image img;
		progressBar.setValue(20); // Se muestra el progreso en el progressBar
		exec.executeCommand("adb shell screencap -p /sdcard/screenshot.png"); // Se captura la pantalla y se almacena en la ruta especificada en el dispositivo
		progressBar.setValue(40);
		exec.executeCommand("adb pull /sdcard/screenshot.png " + System.getProperty("user.dir") + "\\img\\canvas1.png"); // Se trae el screenshot al pc
		progressBar.setValue(60);
		File pathToFile = new File("img/canvas1.png"); // Se abre la imagen
		// Se lleva la imagen al canvas
		try {
			img = ImageIO.read(pathToFile);
			//System.out.println("El tamaño es " + img.getHeight(null) + "x" + img.getWidth(null));
			progressBar.setValue(80);
			img = img.getScaledInstance(canvas.getWidth(), canvas.getHeight(), 3);
			canvas.getGraphics().drawImage(img, 0, 0, null);
			canvas.repaint();
		} catch (IOException e) {
			e.printStackTrace();
		}
		progressBar.setValue(100);
	}

	// Este método es similar al anterior, la diferencia es que está dentro de un ciclo para que vaya refrescando la vista automaticamente
	public void handleScreen(Canvas canvas, JProgressBar progressBar) {
		Thread visualizar;
		progressBar.setValue(20);
		initialScreen(canvas, progressBar);
		progressBar.setValue(100);
		visualizar = new Thread() {
			public void run() {
				Image img;
				while (modo == 1) { // Se verifica que esté en modo manipular
					exec.executeCommand("adb shell screencap -p /sdcard/screenshot.png");
					exec.executeCommand("adb pull /sdcard/screenshot.png " + System.getProperty("user.dir") + "\\img\\canvas1.png");
					File pathToFile = new File("img/canvas1.png");
					try {
						img = ImageIO.read(pathToFile);
						img = img.getScaledInstance(canvas.getWidth(), canvas.getHeight(), 3);
						canvas.getGraphics().drawImage(img, 0, 0, null);
						canvas.repaint();
						//System.out.println("estado"+estadoCaptura);
						estadoCaptura++;
					} catch (IOException e) {
						// TODO
					}
					
				}
			}
		};
		visualizar.start();
	}

	public void initialScreen(Canvas canvas, JProgressBar progressBar) {
		Image img;
		exec.executeCommand("adb shell screencap -p /sdcard/screenshot.png");
		exec.executeCommand("adb pull /sdcard/screenshot.png " + System.getProperty("user.dir") + "\\img\\canvas1.png");
		File pathToFile = new File("img/canvas1.png");
		progressBar.setValue(40);
		try {
			img = ImageIO.read(pathToFile);
			heightCel = img.getHeight(null);
			widthCel = img.getWidth(null);
			progressBar.setValue(60);
			img = img.getScaledInstance(canvas.getWidth(), canvas.getHeight(), 3);
			canvas.getGraphics().drawImage(img, 0, 0, null);
			progressBar.setValue(80);
			canvas.repaint();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void viewScreen() {
		exec.executeCommandWithoutWait("cmd.exe /c \"start C:\\Users\\sebastian.rios\\Documents\\Eclipse-workspace\\AutomatorAccenture\\ffmpeg\\stream.BAT");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		exec.executeCommand("adb shell input tap 100 10");
		exec.executeCommand("adb shell input tap 100 10");
		exec.executeCommand("adb shell input tap 100 10");
	}

	public void calculateCanvasSize(Canvas canvas) { // Calcula el tamaño del canvas
		Dimension size = canvas.getSize();
		widthCanvas = (int) size.getWidth();
		heightCanvas = (int) size.getHeight();
	}

	public void click(int x, int y, int estado) {
		// Se corre el comando para el click de la forma "adb shell input tap x y" se hace regla de tres para mantener las proporciones
		exec.executeCommand("adb shell input tap " + (x * widthCel) / widthCanvas + " " + (y * heightCel) / heightCanvas);
		while (estado > estadoCaptura) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		estadoCaptura = 0;
	}

	public void swipe(int xI, int yI, int xF, int yF, int estado) {
		// Para el caso del scroll el comando es de la forma "adb shell input swipe x1 y1 x2 y2"
		exec.executeCommand("adb shell input swipe " + (xI * widthCel) / widthCanvas + " " + (yI * heightCel) / heightCanvas + " "
						+ (xF * widthCel) / widthCanvas + " " + (yF * heightCel) / heightCanvas);
		while (estado > estadoCaptura) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		estadoCaptura = 0;
	}

	public void captureXML(XmlJTree arbol) {
		exec.executeCommand("adb shell /system/bin/uiautomator dump /sdcard/uidump.xml"); // Genera el XML en el dispositivo
		exec.executeCommand("adb pull /sdcard/uidump.xml " + System.getProperty("user.dir") + "/data"); // Trae el XML al pc
		convertirXML(); // Se convierte el XML puesto que en el original todas las etiquetas reciben el nombre de "node", por lo cual se genera uno con nombres más específicos
		arbol.setPath(System.getProperty("user.dir") + "/data/Convert.xml"); // Refresca el árbol en la vista
	}

	public void convertirXML() {
		String espacio = "";
		lines.clear();
		try {
			File fXmlFile = new File(System.getProperty("user.dir") + "/data/uidump.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("node");
			lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			NodoXMLTree cabecera = new NodoXMLTree(nList.item(0));
			builtTreeNode(nList.item(0), espacio, 1, cabecera);
			cabecera.createIndex(cabecera);
			cabecera.printData(cabecera, 1);
			nodoCabeza = cabecera;
			NodoXMLTree prueba = cabecera.getNodeN(cabecera, 10);
			System.out.println("Se supone que este es " + prueba.getClase());
			Path file = Paths.get(System.getProperty("user.dir") + "/data/Convert.xml");
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void builtTreeNode(Node root, String espacio, int level, NodoXMLTree nodo) {
		int contadorCerrar;
		
		contadorCerrar = contador;
		//System.out.println("Contador: " + contador);
		Element eElement = (Element) root;
		lines.add(espacio + "<" + separar(eElement.getAttribute("class")) + "_" + contador + ">");
		contador++;
		//lines.add("index = \"" + contador + "\"");
		//System.out.println("Nivel " + level + ": " + separar(eElement.getAttribute("class")));
		NodeList nodeList = root.getChildNodes();
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);
			NodoXMLTree hijo = new NodoXMLTree(tempNode);
			nodo.addChild(hijo);
			espacio = espacio + ("  ");
			Element eElement2 = (Element) tempNode;
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				if (tempNode.hasChildNodes()) {
					builtTreeNode(tempNode, espacio, level+1, hijo);
				} else {
					
					//System.out.println("Contador: " + contador);					
					//System.out.println("soy "+ separar(eElement2.getAttribute("class")) + " hijo de " + separar(eElement.getAttribute("class")));
					lines.add(espacio.concat("<" + separar(eElement2.getAttribute("class")) + "_" + contador + ">"));
					//lines.add("index = \"" + contador + "\"");
					//System.out.println("Nivel " + (level+1) + ": " + separar(eElement2.getAttribute("class")));
					lines.add(espacio.concat("</" + separar(eElement2.getAttribute("class")) + "_" + contador + ">"));
					contador++;
					espacio = espacio.substring(0, espacio.length() - 2);
				}
			}
		}
		espacio = espacio.substring(0, espacio.length() - 2);
		lines.add(espacio + "</" + separar(eElement.getAttribute("class")) + "_" + contadorCerrar + ">");
	}

	private String separar(String className) { // Este método toma la última palabra del class para ponerlo como etiqueta en el nuevo XML
		String[] separado;
		separado = className.split("\\.");
		return separado[separado.length - 1];
	}

	public int getModo() {
		return modo;
	}

	public void setModo(int modo) {
		this.modo = modo;
	}

	public int getWidthCanvas() {
		return widthCanvas;
	}

	public void setWidthCanvas(int widthCanvas) {
		this.widthCanvas = widthCanvas;
	}

	public int getHeightCanvas() {
		return heightCanvas;
	}

	public void setHeightCanvas(int heightCanvas) {
		this.heightCanvas = heightCanvas;
	}

	public int getWidthCel() {
		return widthCel;
	}

	public void setWidthCel(int widthCel) {
		this.widthCel = widthCel;
	}

	public int getHeightCel() {
		return heightCel;
	}

	public void setHeightCel(int heightCel) {
		this.heightCel = heightCel;
	}

	public int getEstadoCaptura() {
		return estadoCaptura;
	}

	public void setEstadoCaptura(int estadoCaptura) {
		this.estadoCaptura = estadoCaptura;
	}

	public NodoXMLTree getNodoCabeza() {
		return nodoCabeza;
	}
}