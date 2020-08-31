import java.awt.BorderLayout;
import java.awt.Font;

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
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
		model.setFile(path, filename);

		JTable table = new JTable(model);
		table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		scrollPane = new JScrollPane(table); 
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		DefaultListModel<String> listModel = new DefaultListModel<>();
		JList<String> list = new JList<>(listModel);
		for(int index = 0; index < files.size(); index ++) {
			String file = files.get(index);
			if(file.equals(filename)) {
				list.getSelectionModel().clearSelection();
				list.getSelectionModel().setSelectionInterval(index, index);
			}
			listModel.addElement(file);
		}
		ListSelectionModel listSelectionModel = list.getSelectionModel();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());

		panel_1.setLayout(new BorderLayout());
		panel_1.add(scrollPane, BorderLayout.CENTER);
		panel_1.add(new JScrollPane(list), BorderLayout.WEST);

		populateMenu();
	}

	class SharedListSelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();

			if (lsm.isSelectionEmpty() || e.getValueIsAdjusting()) {
				return;
			}

			int firstIndex = e.getFirstIndex();
			int lastIndex = e.getLastIndex();

			// Find out which indexes are selected.
			int minIndex = lsm.getMinSelectionIndex();
			int maxIndex = lsm.getMaxSelectionIndex();
			for (int i = minIndex; i <= maxIndex; i++) {
				if (lsm.isSelectedIndex(i)) {
					model.setFile(path, files.get(i));
				}
			}
		}
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

	private static String filename;
	private static Path path;
	private static java.util.List<String> files = new ArrayList<>();

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
			path = file.getParentFile().toPath();
			filename = path.relativize(file.toPath()).toString();
		} else {
			path = file.toPath();
		}

		Files.list(path)
			.filter(s -> s.toString().endsWith(".txt"))
			.map(p -> path.relativize(p).toString())
			.sorted(Comparator.comparing(String::toLowerCase))
			.forEach(files::add);
		if(filename == null) {
			filename = files.get(0);
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGui();
			}
		});
	}
}
