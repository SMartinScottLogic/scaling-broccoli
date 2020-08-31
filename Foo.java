import java.awt.BorderLayout;
import java.awt.Font;

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import java.nio.file.*;

public class Foo {
	private JPanel panel_1 = new JPanel();
	private JMenuBar menuBar = new JMenuBar();
	private FooTableModel model = new FooTableModel();
	private JScrollPane scrollPane;

	public Foo() {
		model.setColumnNames(columnIdentifiers);
		model.setData(data);

		JTable table = new JTable(model);
		table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		scrollPane = new JScrollPane(table); 
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		DefaultListModel<String> listModel = new DefaultListModel<>();
		files.forEach(listModel::addElement);
		JList<String> list = new JList<>(listModel);

		panel_1.setLayout(new BorderLayout());
		panel_1.add(scrollPane, BorderLayout.EAST);
		panel_1.add(new JScrollPane(list), BorderLayout.WEST);

		populateMenu();
	}

	private void saveTSV() {
		System.err.print("Writing file to: '" + filename + "'...");
		File outputFile = new File(filename);
		try (FileWriter fw = new FileWriter(outputFile);
				PrintWriter printWriter = new PrintWriter(fw)) {
			for(int column = 0; column < model.getColumnCount(); column ++) {
				if(column > 0) {
					printWriter.printf("\t");
				}
				printWriter.printf("%s", model.getColumnName(column));
			}
			printWriter.printf("\r\n");
			for(int row = 0; row < model.getRowCount(); row ++) {
				for(int column = 0; column < model.getColumnCount(); column ++) {
					if(column > 0) {
						printWriter.printf("\t");
					}
					printWriter.printf("%s", model.getValueAt(row, column));
				}
				printWriter.printf("\r\n");
			}
			printWriter.close();
			System.err.println("done.");
				}
		catch (IOException ioe) {
			System.out.println("error: " + ioe.getMessage());
		}
	}

	private void populateMenu() {
		//Build the first menu.
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		//a group of JMenuItems
		JMenuItem menuItem = new JMenuItem("Dump", KeyEvent.VK_D);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				for(int column = 0; column < model.getColumnCount(); column ++) {
					if(column > 0) {
						System.err.printf("\t");
					}
					System.err.printf("%s", model.getColumnName(column));
				}
				System.err.printf("%n");
				for(int row = 0; row < model.getRowCount(); row ++) {
					for(int column = 0; column < model.getColumnCount(); column ++) {
						if(column > 0) {
							System.err.printf("\t");
						}
						System.err.printf("%s", model.getValueAt(row, column));
					}
					System.err.printf("%n");
				}
			}
		});
		menu.add(menuItem);

		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveTSV();
			}
		});
		menu.add(menuItem);
		menuItem = new JMenuItem("Save As...", KeyEvent.VK_A);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Specify a file to save");

				int userSelection = fileChooser.showSaveDialog(getPanel1());

				if (userSelection == JFileChooser.APPROVE_OPTION) {
					File fileToSave = fileChooser.getSelectedFile();
					filename = fileToSave.getAbsolutePath();
					saveTSV();
				}
			}
		});
		menu.add(menuItem);
		//a group of check box menu items
		menu.addSeparator();
		menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				System.exit(0);
			}
		});
		menu.add(menuItem);

		//Build the first menu.
		menu = new JMenu("Data");
		menu.setMnemonic(KeyEvent.VK_T);
		menuBar.add(menu);

		menuItem = new JMenuItem("Append Row", KeyEvent.VK_R);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				model.addRow(new ArrayList<String>());
			}
		});
		menu.add(menuItem);
	}

	public JPanel getPanel1() {
		return panel_1;
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}

	private static void createAndShowGui() {
		Foo mainPanel = new Foo();

		JFrame frame = new JFrame(path.toString());
		frame.setJMenuBar(mainPanel.getMenuBar());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(mainPanel.getPanel1());
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);

	}

    private static String filename = "test";
	private static Path path;
	private static java.util.List<java.util.List<String>> data = new ArrayList<>();
	private static java.util.List<String> columnIdentifiers = new ArrayList<>();
	private static java.util.List<String> files = new ArrayList<>();

	private static void readFile() {
		System.err.println("Reading file from: '" + filename + "'");
		File inputFile = new File(filename);
		try (FileReader fr = new FileReader(inputFile);
				BufferedReader br = new BufferedReader(fr)) 
		{
			String firstRow = br.readLine().trim();
			if (firstRow != null) {
				// headers:
				String[] ci = firstRow.split("\t");

				columnIdentifiers = new ArrayList<String>();
				for (int j =0; j < ci.length; j++) {
					columnIdentifiers.add(ci[j]);
				}
			}
			// rows
			Object[] tableLines = br.lines().toArray();
			// data rows
			for (int i = 0; i < tableLines.length; i++) {
				String line = tableLines[i].toString().trim();
				String[] dataRow = line.split("\t");
				ArrayList<String> strings = new ArrayList<>();
				for (int j =0; j < dataRow.length; j++) {
					strings.add(dataRow[j]);
				}
				data.add(strings);
			}

			fr.close();
		}
		catch (IOException ioe) {
			System.out.println("error: " + ioe.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		if(args.length == 0) {
			System.err.println("ERROR: Require filename to load.");
			System.exit(-1);
		}
		File file = new File(args[0]);
		
		if(!file.exists()) {
			System.err.printf("No such file: '%s'%n", args[0]);
			System.exit(-1);
		}
		
		if(file.isFile()) {
			filename = file.toString();
			file = file.getParentFile();
			
			readFile();
		}
		
		path = file.toPath();

		Files.list(path)
			.filter(s -> s.toString().endsWith(".txt"))
			.map(p -> path.relativize(p).toString())
			.sorted(Comparator.comparing(String::toLowerCase))
			.forEach(files::add);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGui();
			}
		});
	}
}
