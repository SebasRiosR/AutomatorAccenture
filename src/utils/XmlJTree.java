package utils;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public final class XmlJTree extends JTree{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DefaultTreeModel dtModel=null;

    /**
     * XmlJTree constructor
     * @param filePath
     */
    public XmlJTree(String filePath){
        if(filePath!=null)
        setPath(filePath);
    }

    public void setPath(String filePath){
        Node root = null;
        /*
            Parse the xml file
        */
        try {
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(filePath);
            root = (Node) doc.getDocumentElement();
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            JOptionPane.showMessageDialog(null,"Can't parse file",
                            "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        /*
            if any result set the appropriate model to the jTree
        */
        if(root!=null){
            dtModel= new DefaultTreeModel(builtTreeNode(root));
            this.setModel(dtModel);
        }
    }

    /**
     * fullTreeNode Method
     * construct the full jTree from any xml file
     * this method is recursive
     * @param root
     * @return DefaultMutableTreeNode
     */
    private DefaultMutableTreeNode builtTreeNode(Node root){
        DefaultMutableTreeNode dmtNode;
        dmtNode = new DefaultMutableTreeNode(root.getNodeName());
            NodeList nodeList = root.getChildNodes();
            for (int count = 0; count < nodeList.getLength(); count++) {
                Node tempNode = nodeList.item(count);
                // make sure it's element node.
                if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                    if (tempNode.hasChildNodes()) {
                        // loop again if has child nodes
                        dmtNode.add(builtTreeNode(tempNode));
                    }
                }
            }
        return dmtNode;
    }

    public static void main(String[] args) {
        /*
            Create simple frame for the example
        */
        JFrame myFrame = new JFrame();
        myFrame.setTitle("XmlJTreeExample");
        myFrame.setSize(300, 500);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setLocationRelativeTo(null);
        JPanel pan = new JPanel(new GridLayout(1, 1));
        /*
            Add jTree
        */
        final XmlJTree myTree = new XmlJTree(null);
        myFrame.add(new JScrollPane(myTree));
        /*
            Add a button to choose the file
        */
        JButton button = new JButton("Choose file");
        button.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "XML file", "xml");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    myTree.setPath(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        pan.add(button);
        /*
            Add the JPanel to the JFrame and set the JFrame visible
        */
        myFrame.add(pan,BorderLayout.SOUTH);
        myFrame.setVisible(true);
    }
}