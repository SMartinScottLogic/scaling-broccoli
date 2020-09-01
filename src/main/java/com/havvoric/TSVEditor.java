package com.havvoric;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;

public class TSVEditor {
	private final JPanel panel_1 = new JPanel();
	private final JMenuBar menuBar = new JMenuBar();
	private final TSVEditorTableModel model = new TSVEditorTableModel();

	public TSVEditor(String filename) {
		model.setFile(path, filename);

		JTable table = new JTable(model);
		table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		JScrollPane scrollPane = new JScrollPane(table);
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

	private void populateMenu() {
		//Build the first menu.
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		//a group of JMenuItems
		JMenuItem menuItem = new JMenuItem("Dump", KeyEvent.VK_D);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_DOWN_MASK));
		menuItem.addActionListener(ev -> {
			for(int column = 0; column < model.getColumnCount(); column ++) {
				if(column > 0) {
					System.err.print("\t");
				}
				System.err.printf("%s", model.getColumnName(column));
			}
			System.err.printf("%n");
			for(int row = 0; row < model.getRowCount(); row ++) {
				for(int column = 0; column < model.getColumnCount(); column ++) {
					if(column > 0) {
						System.err.print("\t");
					}
					System.err.printf("%s", model.getValueAt(row, column));
				}
				System.err.printf("%n");
			}
		});
		menu.add(menuItem);

		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		menuItem.addActionListener(ev -> model.save());
		menu.add(menuItem);
		menuItem = new JMenuItem("Save As...", KeyEvent.VK_A);
		menuItem.addActionListener(ev -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Specify a file to save");

			int userSelection = fileChooser.showSaveDialog(getPanel1());

			if (userSelection == JFileChooser.APPROVE_OPTION) {
				File fileToSave = fileChooser.getSelectedFile();
				String filename = fileToSave.getAbsolutePath();
				model.save(filename);
			}
		});
		menu.add(menuItem);
		menuItem = new JMenuItem("Save All");
		menuItem.addActionListener(ev -> model.saveAll());
		menu.add(menuItem);
		//a group of check box menu items
		menu.addSeparator();
		menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItem.addActionListener(ev -> System.exit(0));
		menu.add(menuItem);

		//Build the first menu.
		menu = new JMenu("Data");
		menu.setMnemonic(KeyEvent.VK_T);
		menuBar.add(menu);

		menuItem = new JMenuItem("Append Row", KeyEvent.VK_R);
		menuItem.addActionListener(ev -> model.addRow(new ArrayList<>()));
		menu.add(menuItem);
	}

	public JPanel getPanel1() {
		return panel_1;
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}

	private static void createAndShowGui(String filename) {
		TSVEditor mainPanel = new TSVEditor(filename);

		JFrame frame = new JFrame(path.toString());
		frame.setJMenuBar(mainPanel.getMenuBar());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(mainPanel.getPanel1());
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

	private static Path path;
	private static final java.util.List<String> files = new ArrayList<>();

	private static void start(String filename) {	
		SwingUtilities.invokeLater(() -> createAndShowGui(filename));
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
		
		String filename = null;
		
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
		
		start(filename);
	}
}
