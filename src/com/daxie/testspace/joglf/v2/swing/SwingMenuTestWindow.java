package com.daxie.testspace.joglf.v2.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.matrix.Matrix;
import com.daxie.basis.matrix.MatrixFunctions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.window.JOGLFSwingWindow;
import com.daxie.joglf.gl.wrapper.GLDrawFunctions3D;
import com.daxie.tool.MathFunctions;

public class SwingMenuTestWindow extends JOGLFSwingWindow implements ActionListener{
	public SwingMenuTestWindow() {
		
	}
	
	//Components
	private JMenuItem menuitem_open;
	private JMenuItem menuitem_exit;
	
	//Other
	private Vector camera_position;
	
	@Override
	protected void Init() {
		JFrame frame=this.GetFrame();
		
		JMenuBar menubar=new JMenuBar();
		JMenu menu_file=new JMenu("File");
		menubar.add(menu_file);
		menuitem_open=new JMenuItem("Open");
		menuitem_exit=new JMenuItem("Exit");
		menuitem_open.addActionListener(this);
		menuitem_exit.addActionListener(this);
		menu_file.add(menuitem_open);
		menu_file.addSeparator();
		menu_file.add(menuitem_exit);
		
		frame.setJMenuBar(menubar);
		
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
		GLDrawFunctions3D.DrawCapsule3D(
				VectorFunctions.VGet(0.0f, -10.0f, 0.0f), VectorFunctions.VGet(0.0f, 10.0f, 0.0f), 
				10.0f, 32, 32, ColorU8Functions.GetColorU8(1.0f, 0.0f, 1.0f, 1.0f));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source=e.getSource();
		if(source==menuitem_open) {
			JFrame frame=this.GetFrame();
			
			JFileChooser file_chooser=new JFileChooser();
			int res=file_chooser.showOpenDialog(frame);
			if(res==JFileChooser.APPROVE_OPTION) {
				File file=file_chooser.getSelectedFile();
				String filename=file.getPath();
				
				JOptionPane.showMessageDialog(frame, filename);
			}
		}
		else if(source==menuitem_exit) {
			this.CloseWindow();
		}
	}
}
