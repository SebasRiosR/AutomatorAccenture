package views;

import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import controllers.PrincipalController;
import utils.NodoXMLTree;
import utils.XmlJTree;
import java.awt.Component;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

public class MainView {

	private JFrame frame;
	JDialog dlg = new JDialog(frame, "Capturando la pantalla", true); // Para mostrar la barra de espera
	Canvas canvas = new Canvas(); // Para mostrar la imagen
	Image img;
	int xInitial, xFinal, yInitial, yFinal, control = 0; // Para controlar los eventos del mouse sobre el canvas
	private PrincipalController controller = new PrincipalController(); // Este objeto es mediante el cual se ejecutan
																		// la mayoría de funciones
	private XmlJTree XMLViewer = new XmlJTree(System.getProperty("user.dir") + "\\img\\base.xml"); // Objeto mediante el
																									// cual se muestra
																									// el XML

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainView window = new MainView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainView() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// Se obtiene el tamaño de la pantalla para posicionar la ventana
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// Se obtiene el punto central de la pantalla
		int width = (int) (screenSize.getWidth());
		int height = (int) (screenSize.getHeight());
		// Se crea el frame que contiene toda la app
		frame = new JFrame();
		// Se posiciona el frame principal y se le indica el tamaño
		frame.setBounds(15, 1, width - 530, height - 40);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Se divide el frame en columnas, una para el canvas y otra para las demas
		// cosas
		frame.getContentPane().setLayout(new GridLayout(0, 2));

		// Se define un panel en el que se mostrará el XML, los detalles y los botones
		JPanel panelPrincipal = new JPanel();
		panelPrincipal.setLayout(new GridLayout(3, 0));
		frame.getContentPane().add(panelPrincipal);
		XMLViewer.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				String[] etiqueta = XMLViewer.getLastSelectedPathComponent().toString().split("_");
				NodoXMLTree aux = new NodoXMLTree();
				aux = aux.getNodeN(controller.getNodoCabeza(), Integer.parseInt(etiqueta[1]));
				File pathToFile = new File("img/canvas1.png"); // Se abre la imagen
				// Se lleva la imagen al canvas
				try {
					img = ImageIO.read(pathToFile);
					//System.out.println("El tamaño es " + img.getHeight(null) + "x" + img.getWidth(null));
					img = img.getScaledInstance(canvas.getWidth(), canvas.getHeight(), 3);
					canvas.getGraphics().drawImage(img, 0, 0, null);
					canvas.repaint();
				} catch (IOException e) {
					e.printStackTrace();
				}
				paintRectangle(aux.getPosition(), aux.getWidth(), aux.getHeigth());
				System.out.println("ID: " + aux.getId());
				System.out.println("Class: " + aux.getClase());
				System.out.println("Position: " + aux.getPosition());
				System.out.println("Ancho: " + aux.getWidth());
				System.out.println("Ancho: " + aux.getHeigth());
				System.out.println("------------------------------------------");
			}
		});

		// Se define un panel con scroll para mostrar el arbol que representa el XML
		JScrollPane scrollTree = new JScrollPane(XMLViewer);
		panelPrincipal.add(scrollTree);
		scrollTree.setViewportView(XMLViewer);

		// Se define un panel para mostrar detalles de los elementos de la captura
		String[] columnNames = {"Propiedad", "Descripción"};
		Object[][] data = {{"",""}, {"",""}, {"",""}, {"",""}, {"",""}, {"",""}, {"",""}, {"",""}, {"",""}, {"",""}, {"",""}, {"",""}, {"",""}, {"",""}, {"",""}, {"",""}, {"",""}};
		Object[][] data2 = {{"holA","dfs"}};
		JTable table = new JTable(data, columnNames);
		JScrollPane panelDetalles = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		panelPrincipal.add(panelDetalles);
//		for (int i = 0; i < data.length; i++) {
//			data[i][1] = "Hola " + i;
//		}
		data[0][0] = data2[0][0];

		// Se crea el panel donde estarán los tres botones para cada funcionalidad
		JPanel panelBotones = new JPanel();
		panelPrincipal.add(panelBotones);
		panelBotones.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
		//panelBotones.setBounds(0, 0, 250, 300);
		//panelBotones.setPreferredSize(panelBotones.getPreferredSize());

		// Se crean los 3 botones y se agregan al panel
		JButton captureButton = new JButton("Capturar");
		JButton handleButton = new JButton("Manipular");
		JButton viewButton = new JButton("Visualizar");
		panelBotones.add(captureButton);
		panelBotones.add(handleButton);
		panelBotones.add(viewButton);

		// Se crea la barra que mostrara el progreso de la captura de pantalla
		JProgressBar progressBar = new JProgressBar(0, 100);
		dlg.getContentPane().add(BorderLayout.CENTER, progressBar);
		dlg.getContentPane().add(BorderLayout.NORTH, new JLabel("Espere..."));
		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dlg.setSize(300, 75);
		dlg.setLocationRelativeTo(frame);

		// Se agrega los listeners para cada botón
		captureButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				controller.setModo(0);
				// Se crea un hilo para mostrar la barra de progreso
				Thread dialog = new Thread(new Runnable() {
					public void run() {
						dlg.setVisible(true);
					}
				});
				// Se crea un hilo para traer el xml desde el dispositivo
				Thread xml = new Thread(new Runnable() {
					public void run() {
						controller.captureXML(XMLViewer);
					}
				});
				// Se crea un hilo para tomar y traer el pantallazo
				Thread capture = new Thread(new Runnable() {
					public void run() {
						controller.captureScreen(canvas, progressBar);
						dlg.setVisible(false);
						progressBar.setValue(0);
					}
				});
				dialog.start();
				capture.start();
				xml.start();
			}
		});

		handleButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				controller.setModo(1);
				Thread dialog = new Thread(new Runnable() {
					public void run() {
						dlg.setVisible(true);
					}
				});
				Thread manipular = new Thread(new Runnable() {
					public void run() {
						controller.calculateCanvasSize(canvas); // Se obtiene el tamaño del canvas para mantener las
																// proporciones con el tamaño del dispositivo
						controller.handleScreen(canvas, progressBar);
						dlg.setVisible(false);
					}
				});
				dialog.start();
				manipular.start();
			}
		});

		viewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				controller.setModo(2);
				controller.viewScreen();
			}
		});

		canvas.addMouseListener(new MouseAdapter() {
			// Se crea el listener para cuando presionen el click sobre el cambas
			@Override
			public void mousePressed(MouseEvent e) {
				// Se obtiene la posición del evento
				xInitial = e.getX();
				yInitial = e.getY();
			}

			// Se crea el listener para cuando suelten el click
			@Override
			public void mouseReleased(MouseEvent e) {
				Thread dialog = new Thread(new Runnable() {
					public void run() {
						progressBar.setIndeterminate(true);
						dlg.setVisible(true);
					}
				});
				Thread execHandle = new Thread(new Runnable() {
					int estadoScreenShot;
					public void run() {
						if (controller.getModo() == 1) {
							control++;
							estadoScreenShot = controller.getEstadoCaptura();
							// Se obtienen la posición donde se soltó el click
							xFinal = e.getX();
							yFinal = e.getY();
							// Se verifica si fue click o scroll
							if (xInitial == xFinal && yInitial == yFinal) {
								controller.click(xInitial, yInitial, estadoScreenShot+2);
							} else {
								controller.swipe(xInitial, yInitial, xFinal, yFinal, estadoScreenShot+2);
							}
						}
						dlg.setVisible(false);
						progressBar.setIndeterminate(false);
					}
				});
				dialog.start();
				execHandle.start();
			}
		});

		captureButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		captureButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		frame.getContentPane().add(canvas);
		canvas.setIgnoreRepaint(true);
		canvas.setBackground(Color.WHITE);
		canvas.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	}
	
	private void paintRectangle(Point punto, int width, int heigth) {
		Graphics g =canvas.getGraphics();
		new Color(255);
		g.setColor( Color.RED);
		int x = (punto.x*controller.getWidthCanvas())/controller.getWidthCel();
		int y = (punto.y*controller.getHeightCanvas())/controller.getHeightCel();
		int width2 = (width*controller.getWidthCanvas())/controller.getWidthCel();
		int heigth2 = (heigth*controller.getHeightCanvas())/controller.getHeightCel();
	    g.drawRect(x, y, width2, heigth2);
	    canvas.repaint(); 
	}
	
}