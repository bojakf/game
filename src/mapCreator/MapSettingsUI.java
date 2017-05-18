package mapCreator;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JButton;

public class MapSettingsUI extends JFrame {
	
	public MapSettingsUI() {
		getContentPane().setLayout(null);
		setSize(700, 800);
		
		JCheckBox chckbxShowColliders = new JCheckBox("show Colliders");
		chckbxShowColliders.setBounds(17, 17, 179, 35);
		chckbxShowColliders.setSelected(MapCreator.showColliders);
		chckbxShowColliders.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MapCreator.showColliders = chckbxShowColliders.isSelected();
			}
		});
		getContentPane().add(chckbxShowColliders);
		
		JCheckBox chckbxClipObjects = new JCheckBox("Clip Objects to Grid");
		chckbxClipObjects.setBounds(17, 65, 248, 35);
		chckbxClipObjects.setSelected(MapCreatorSelected.clipToGrid);
		chckbxClipObjects.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MapCreatorSelected.clipToGrid = chckbxClipObjects.isSelected();
			}
		});
		getContentPane().add(chckbxClipObjects);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					MapCreator.save(JOptionPane.showInputDialog("Input name of map"));
				} catch (HeadlessException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnSave.setBounds(512, 673, 141, 35);
		getContentPane().add(btnSave);
	
		
		setVisible(true);
		
	}
}
