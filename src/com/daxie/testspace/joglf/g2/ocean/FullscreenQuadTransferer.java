package com.daxie.testspace.joglf.g2.ocean;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

class FullscreenQuadTransferer {
	private IntBuffer indices_vbo;
	private IntBuffer pos_vbo;
	private IntBuffer vao;
	
	public FullscreenQuadTransferer() {
		indices_vbo=Buffers.newDirectIntBuffer(1);
		pos_vbo=Buffers.newDirectIntBuffer(1);
		vao=Buffers.newDirectIntBuffer(1);
		
		GLWrapper.glGenBuffers(1, indices_vbo);
		GLWrapper.glGenBuffers(1, pos_vbo);
		GLWrapper.glGenVertexArrays(1, vao);
		
		IntBuffer indices_buffer=Buffers.newDirectIntBuffer(6);
		FloatBuffer pos_buffer=Buffers.newDirectFloatBuffer(4*2);
		
		//First triangle
		indices_buffer.put(0);
		indices_buffer.put(1);
		indices_buffer.put(2);
		//Second triangle
		indices_buffer.put(2);
		indices_buffer.put(3);
		indices_buffer.put(4);
		((Buffer)indices_buffer).flip();
		
		//Bottom left
		pos_buffer.put(-1.0f);
		pos_buffer.put(-1.0f);
		//Bottom right
		pos_buffer.put(1.0f);
		pos_buffer.put(-1.0f);
		//Top right
		pos_buffer.put(1.0f);
		pos_buffer.put(1.0f);
		//Top left
		pos_buffer.put(-1.0f);
		pos_buffer.put(1.0f);
		((Buffer)pos_buffer).flip();
		
		GLWrapper.glBindBuffer(GL4.GL_ARRAY_BUFFER, pos_vbo.get(0));
		GLWrapper.glBufferData(GL4.GL_ARRAY_BUFFER, 
				Buffers.SIZEOF_FLOAT*pos_buffer.capacity(), pos_buffer, GL4.GL_STATIC_DRAW);
		
		GLWrapper.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
		
		GLWrapper.glBindVertexArray(vao.get(0));
		
		GLWrapper.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, indices_vbo.get(0));
		GLWrapper.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, 
				Buffers.SIZEOF_INT*indices_buffer.capacity(), indices_buffer, GL4.GL_DYNAMIC_DRAW);
		
		GLWrapper.glBindBuffer(GL4.GL_ARRAY_BUFFER, pos_vbo.get(0));
		GLWrapper.glEnableVertexAttribArray(0);
		GLWrapper.glVertexAttribPointer(0, 2, GL4.GL_FLOAT, false, Buffers.SIZEOF_FLOAT*2, 0);
		
		GLWrapper.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
		GLWrapper.glBindVertexArray(0);
	}
	
	public void Transfer() {
		GLWrapper.glBindVertexArray(vao.get(0));
		GLWrapper.glDrawElements(GL4.GL_TRIANGLES, 6, GL4.GL_UNSIGNED_INT, 0);
		GLWrapper.glBindVertexArray(0);
	}
}
