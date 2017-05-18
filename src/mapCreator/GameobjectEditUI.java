package mapCreator;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;

import gameobject.Component;
import gameobject.Gameobject;

import java.awt.Window.Type;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Color;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSpinner;

public class GameobjectEditUI extends JFrame {

	private JPanel contentPane;
	
	private final Gameobject edit;

	private ArrayList<Setter> setters;
	
	/**
	 * Create the frame.
	 */
	public GameobjectEditUI(Gameobject edit) {
		
		this.edit = edit;
		
		setType(Type.UTILITY);
		setTitle("Edit Gameobject");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 842, 995);
		getContentPane().setLayout(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblInvalidValuesMay = new JLabel("Invalid values may corrupt the game");
		lblInvalidValuesMay.setForeground(Color.RED);
		lblInvalidValuesMay.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblInvalidValuesMay.setBounds(21, 21, 442, 26);
		contentPane.add(lblInvalidValuesMay);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(21, 67, 794, 800);
		scrollPane.setLayout(null);
		contentPane.add(scrollPane);
		
		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for(Setter s : setters) {
					s.set();
				}
			}
		});
		btnApply.setBounds(674, 888, 141, 35);
		contentPane.add(btnApply);
		
		setters = new ArrayList<>();
		
		ArrayList<Component> components = edit.getComponents();
		
		int h = 10;
		
		for(Component c : components) {
			
			if(c.getClass().equals(MapCreatorSelected.class)) continue;
			
			Label lName = new Label(c.getClass().getName());
			lName.setBounds(5, h, 400, 20);
			lName.setFont(new Font("Tahoma", Font.BOLD, 20));
			scrollPane.add(lName);
			h+=20;
			
			Iterable<Field> fields = getAllFields(c.getClass());
			for(Field f : fields) {
				
				f.setAccessible(true);
				Label lField = new Label(f.getName());
				lField.setBounds(110, h, 200, 20);
				scrollPane.add(lField);
				
				Label lFieldName = new Label(f.getType().getSimpleName());
				lFieldName.setBounds(10, h, 100, 20);
				scrollPane.add(lFieldName);
				
				/*
				 * TODO complete this
				 * TODO gameobject fields
				 * TODO set and create non primitive objects
				 * TODO add components
				 * TODO maybe call methods
				 * TODO hide transient fields
				 */
				if(Modifier.isFinal(f.getModifiers())) {
					/*
					 * Handle final fields
					 */
					//TODO add edit for final non primitves here
					try {
						JLabel l = new JLabel(f.get(c).toString());
						l.setBounds(320, h, 200, 20);
						scrollPane.add(l);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				} else {
					/*
					 * Handle non final fields
					 */
					if(f.getType() == int.class) {
						IntegerIn s = new IntegerIn(f, c);
						s.setBounds(320, h, 200, 20);
						scrollPane.add(s);
						setters.add(s);
					} else if(f.getType() == long.class) {
						LongIn s = new LongIn(f, c);
						s.setBounds(320, h, 200, 20);
						scrollPane.add(s);
						setters.add(s);
					} else if(f.getType() == short.class) {
						ShortIn s = new ShortIn(f, c);
						s.setBounds(320, h, 200, 20);
						scrollPane.add(s);
						setters.add(s);
					} else if(f.getType() == byte.class) {
						ByteIn s = new ByteIn(f, c);
						s.setBounds(320, h, 200, 20);
						scrollPane.add(s);
						setters.add(s);
					} else if(f.getType() == float.class) {
						FloatIn s = new FloatIn(f, c);
						s.setBounds(320, h, 200, 20);
						scrollPane.add(s);
						setters.add(s);
					} else if(f.getType() == double.class) {
						DoubleIn s = new DoubleIn(f, c);
						s.setBounds(320, h, 200, 20);
						scrollPane.add(s);
						setters.add(s);
					} else if(f.getType() == boolean.class) {
						BooleanIn s = new BooleanIn(f, c);
						s.setBounds(320, h, 200, 20);
						scrollPane.add(s);
						setters.add(s);
					} else if(f.getType() == char.class) {
						CharIn s = new CharIn(f, c);
						s.setBounds(320, h, 200, 20);
						scrollPane.add(s);
						setters.add(s);
					} else if(f.getType() == String.class) {
						StringIn s = new StringIn(f, c);
						s.setBounds(320, h, 200, 20);
						scrollPane.add(s);
						setters.add(s);
					}
				}
				
				
				h+=20;
				
			}
			
		}
		
		setVisible(true);
		
		
	}
	
	public static Iterable<Field> getAllFields(Class<?> startClass) {
		
		List<Field> currentClassFields = new ArrayList<>();
		for(Field f : startClass.getDeclaredFields()) {
			currentClassFields.add(f);
		}
		Class<?> parentClass = startClass.getSuperclass();
		
		if (parentClass != null) {
			List<Field> parentClassFields = (List<Field>) getAllFields(parentClass);
			currentClassFields.addAll(parentClassFields);
		}
		
		return currentClassFields;
	}
	
	private interface Setter {
		public void set();
	}
	
	private class StringIn extends JTextArea implements Setter {
		
		private Field f;
		private Object o;
		
		public StringIn(Field f, Object o) {
			this.f = f;
			this.o = o;
			
			try {
				setText((String)f.get(o));
			} catch (IllegalArgumentException | IllegalAccessException e2) {
				e2.printStackTrace();
			}
			
		}

		@Override
		public void set() {
			try {
				f.set(o, getText());
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	private class CharIn extends JTextArea implements Setter {
		
		private Field f;
		private Object o;
		
		public CharIn(Field f, Object o) {
			this.f = f;
			this.o = o;
			
			addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					if(getText().length() > 1) {
						setText(getText().substring(0, 1));
					}
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					
				}
			});
			
			try {
				setText((String)f.get(o));
			} catch (IllegalArgumentException | IllegalAccessException e2) {
				e2.printStackTrace();
			}
			
		}

		@Override
		public void set() {
			try {
				if(getText().length() == 0) {
					f.setChar(o, '\0');
				} else {
					f.setChar(o, getText().charAt(0));
				}
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	private class BooleanIn extends JCheckBox implements Setter {
		
		Field f;
		Object o;
		
		public BooleanIn(Field f, Object o) {
			
			this.f = f;
			this.o = o;
			
			try {
				setSelected(f.getBoolean(o));
			} catch (IllegalArgumentException | IllegalAccessException e2) {
				e2.printStackTrace();
			}
			
		}

		@Override
		public void set() {
			try {
				f.setBoolean(o, isSelected());
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	private class IntegerIn extends JSpinner implements Setter {

		Field f;
		Object o;
		
		public IntegerIn(Field f, Object o) {
			this.f = f;
			this.o = o;
			try {
				setModel(new SpinnerNumberModel(f.getInt(o), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void set() {
			try {
				f.setInt(o, (int)getValue());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class ShortIn extends JSpinner implements Setter {

		Field f;
		Object o;
		
		public ShortIn(Field f, Object o) {
			this.f = f;
			this.o = o;
			try {
				setModel(new SpinnerNumberModel(f.getInt(o), Short.MIN_VALUE, Short.MAX_VALUE, 1));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void set() {
			try {
				f.setShort(o, (short)getValue());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class ByteIn extends JSpinner implements Setter {

		Field f;
		Object o;
		
		public ByteIn(Field f, Object o) {
			this.f = f;
			this.o = o;
			try {
				setModel(new SpinnerNumberModel(f.getInt(o), Byte.MIN_VALUE, Byte.MAX_VALUE, 1));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void set() {
			try {
				f.setByte(o, (byte)getValue());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class LongIn extends JSpinner implements Setter {

		Field f;
		Object o;
		
		public LongIn(Field f, Object o) {
			this.f = f;
			this.o = o;
			try {
				setModel(new SpinnerNumberModel(f.getInt(o), Long.MIN_VALUE, Long.MAX_VALUE, 1));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void set() {
			try {
				f.setLong(o, (long)getValue());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class FloatIn extends JSpinner implements Setter {
		
		Field f;
		Object o;
		
		public FloatIn(Field f, Object o) {
			this.f = f;
			this.o = o;
			try {
				setModel(new SpinnerNumberModel(f.getFloat(o), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0.01f));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void set() {
			try {
				f.setFloat(o, (float)getValue());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class DoubleIn extends JSpinner implements Setter {
		
		Field f;
		Object o;
		
		public DoubleIn(Field f, Object o) {
			this.f = f;
			this.o = o;
			try {
				setModel(new SpinnerNumberModel(f.getDouble(o), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0.01f));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void set() {
			try {
				f.setDouble(o, (double)getValue());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}

}
