package org.quasar.geographs.application;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.*;

public class Interface {

	private Application app = null;
	private JFrame frame;
	
	public Interface() {
		frame = new JFrame("Interface Graph");

		// para que o botao de fechar a janela termine a aplicacao
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		//definir tamanho em pixels
		frame.setSize(300,300);
		addFrameContent();

		// para que a janela se redimensione de forma a ter todo o seu conteudo visivel
		//frame.pack();
	}

	public Application getApp() {
		return app;
	}

	public void setApp(Application app) {
		this.app = app;
	}
	
	public void open() {
		// para abrir a janela (torna-la visivel)
		frame.setVisible(true);
	}
	private void addFrameContent() {


		/* para organizar o conteudo em grelha (linhas x colunas)
		se um dos valores for zero, o numero de linhas ou colunas (respetivamente) fica indefinido,
		e estas sao acrescentadas automaticamente */
		frame.setLayout(new BorderLayout());

//		JLabel labelTitle = new JLabel("Title");
//		frame.add(labelTitle, BorderLayout.NORTH);
		
		JPanel painelInfoFormulario = new JPanel();
		painelInfoFormulario.setLayout(new GridLayout(8, 2));

		JLabel labelCampo1 = new JLabel("Latitude Ponto Inicial");
		//JTextField textfieldCampo1 = new JTextField("38.7146011");
		JTextField textfieldCampo1 = new JTextField("38.714618");
		
		JLabel labelCampo2 = new JLabel("Longitude Ponto Incial");
		//JTextField textfieldCampo2 = new JTextField("-9.1387581");
		JTextField textfieldCampo2 = new JTextField("-9.140675");
		
		JLabel labelCampo3 = new JLabel("Latitude Ponto Final");
		//JTextField textfieldCampo3 = new JTextField("38.7114444");
		JTextField textfieldCampo3 = new JTextField("38.708775");
		
		JLabel labelCampo4 = new JLabel("Longitude Ponto Final");
		//JTextField textfieldCampo4 = new JTextField("-9.1306691");
		JTextField textfieldCampo4 = new JTextField("-9.132108");
		
		JLabel labelCampo5 = new JLabel("Effort");
		JTextField textfieldCampo5 = new JTextField("1");
		JLabel labelCampo6 = new JLabel("Max Time");
		JTextField textfieldCampo6 = new JTextField("10.0");
		JLabel labelCampo7 = new JLabel("Initial Time");
		JTextField textfieldCampo7 = new JTextField("7.0");
		JLabel labelCampo8 = new JLabel("Categories");
		JTextField textfieldCampo8 = new JTextField("1,2,3,4,5,6,7,8");	
		
		painelInfoFormulario.add(labelCampo1);
		painelInfoFormulario.add(textfieldCampo1);
		painelInfoFormulario.add(labelCampo2);
		painelInfoFormulario.add(textfieldCampo2);
		painelInfoFormulario.add(labelCampo3);
		painelInfoFormulario.add(textfieldCampo3);
		painelInfoFormulario.add(labelCampo4);
		painelInfoFormulario.add(textfieldCampo4);
		painelInfoFormulario.add(labelCampo5);
		painelInfoFormulario.add(textfieldCampo5);
		painelInfoFormulario.add(labelCampo6);
		painelInfoFormulario.add(textfieldCampo6);
		painelInfoFormulario.add(labelCampo7);
		painelInfoFormulario.add(textfieldCampo7);
		painelInfoFormulario.add(labelCampo8);
		painelInfoFormulario.add(textfieldCampo8);
		
		frame.add(painelInfoFormulario, BorderLayout.CENTER);

		JButton button = new JButton("Calcular");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double lati = Double.parseDouble(textfieldCampo1.getText());
				double longi = Double.parseDouble(textfieldCampo2.getText());
				double latf = Double.parseDouble(textfieldCampo3.getText());
				double longf = Double.parseDouble(textfieldCampo4.getText());
				int effort = Integer.parseInt(textfieldCampo5.getText());
				double maxTime = Double.parseDouble(textfieldCampo6.getText());
				double initialTime = Double.parseDouble(textfieldCampo7.getText());
				LinkedList<Integer> categories = stringToIntList(textfieldCampo8.getText());
				
				app = new Application(lati,  latf,longi, longf, effort, maxTime, initialTime);
				app.setCategories(categories);
				
				frame.setVisible(false);
			}
		});
		frame.add(button,BorderLayout.SOUTH);	
	}
	
	private LinkedList<Integer> stringToIntList(String s){
		LinkedList<Integer> result = new LinkedList<Integer>();
		
		for(String a : s.split(",")) {
			result.add(Integer.parseInt(a));
		}
		
		return result;
	}

	public static void main(String[] args) {
		Interface window = new Interface();
		window.open();
	}
}
