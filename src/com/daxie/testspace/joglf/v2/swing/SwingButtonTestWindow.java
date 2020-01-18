package com.daxie.testspace.joglf.v2.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.matrix.Matrix;
import com.daxie.basis.matrix.MatrixFunctions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.draw.GLDrawFunctions3D;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.window.JOGLFSwingWindow;
import com.daxie.tool.MathFunctions;

public class SwingButtonTestWindow extends JOGLFSwingWindow implements ActionListener{
	public SwingButtonTestWindow() {
		
	}
	
	//Components
	private JButton button1;
	private JButton button2;
	private JButton button3;
	
	//Other
	private Vector camera_position;
	
	@Override
	protected void Init() {
		JFrame frame=this.GetFrame();
		
		//Create buttons.
		JPanel panel=new JPanel();
		
		button1=new JButton("Button 1");
		button2=new JButton("Button 2");
		button3=new JButton("Button 3");
		button1.addActionListener(this);
		button2.addActionListener(this);
		button3.addActionListener(this);
		panel.add(button1);
		panel.add(button2);
		panel.add(button3);
		
		Container content_pane=frame.getContentPane();
		content_pane.add(panel,BorderLayout.PAGE_START);
		
		frame.pack();
		
		camera_position=VectorFunctions.VGet(50.0f, 50.0f, 50.0f);
	}
	@Override
	protected void Update() {
		Matrix rot_y=MatrixFunctions.MGetRotY(MathFunctions.DegToRad(1.0f));
		camera_position=VectorFunctions.VTransform(camera_position, rot_y);
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(camera_position, VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	@Override
	protected void Draw() {
		GLDrawFunctions3D.DrawAxes(100.0f);
		GLDrawFunctions3D.DrawSphere3D(
				VectorFunctions.VGet(0.0f, 0.0f, 0.0f), 15.0f, 32, 32, ColorU8Functions.GetColorU8(1.0f, 1.0f, 0.0f, 1.0f));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source=e.getSource();
		JFrame frame=this.GetFrame();
		if(source==button1) {
			JOptionPane.showMessageDialog(frame, "Button 1");
		}
		else if(source==button2) {
			JOptionPane.showMessageDialog(frame, "Button 2");
		}
		else if(source==button3) {
			JOptionPane.showMessageDialog(frame, "Button 3");
		}
	}
}
