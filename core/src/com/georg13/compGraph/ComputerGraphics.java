package com.georg13.compGraph;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.badlogic.gdx.utils.BufferUtils;

public class ComputerGraphics extends ApplicationAdapter {

	private FloatBuffer vertexBuffer;

	private int speed;

    private float squareSize = 50;

	private FloatBuffer modelMatrix;
	private FloatBuffer projectionMatrix;

	private int renderingProgramID;
	private int vertexShaderID;
	private int fragmentShaderID;

	private int positionLoc;

	private int modelMatrixLoc;
	private int projectionMatrixLoc;

	private int colorLoc;


	ArrayList<Box> boxes;

	//Box instance
	private class Box{
		//POSITION
		private float position_x;
		private float position_y;

		//SCALE
		private float scale_x;
		private float scale_y;

		//DIRECTION
		private int horizontal;
		private int vertical;


		//FUNCTIONS
		public Box(float x, float y, int direction){
			position_x = x;
			position_y = y;

			scale_x = scale_y = 1;
			horizontal = direction;
			vertical = -horizontal;
		}

		void move(){
			//POSITION CHANGES
			if(Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && position_y <= Gdx.graphics.getHeight() - squareSize * scale_y){
				this.position_y += speed;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.DOWN) && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && position_y >= squareSize * scale_y){
				this.position_y -= speed;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && position_x >= squareSize * scale_x){
				this.position_x -= speed;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && position_x <= Gdx.graphics.getWidth() - squareSize * scale_x){
				this.position_x += speed;
			}

			//SCALE CHANGES
			if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
				this.scale_x += 0.01;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.UP)){
				this.scale_y += 0.01;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.LEFT)){
				this.scale_x -= 0.01;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.DOWN)){
				this.scale_y -= 0.01;
			}
		}

		void motion(){
			if(horizontal > 0 && position_x >= Gdx.graphics.getWidth()){
				this.horizontal -= 4;
			}
			else if (horizontal < 0 && position_x <= 0){
				this.horizontal = 2;
			}
			if(vertical > 0 && position_y >= Gdx.graphics.getHeight()){
				this.vertical -= 4;
			}
			else if(vertical < 0 && position_y <= 0){
				this.vertical = 2;
			}
			position_y += vertical;
			position_x += horizontal;
		}
	}


	@Override
	public void create () {
		String vertexShaderString;
		String fragmentShaderString;

		vertexShaderString = Gdx.files.internal("shaders/simple2D.vert").readString();
		fragmentShaderString =  Gdx.files.internal("shaders/simple2D.frag").readString();

		vertexShaderID = Gdx.gl.glCreateShader(GL20.GL_VERTEX_SHADER);
		fragmentShaderID = Gdx.gl.glCreateShader(GL20.GL_FRAGMENT_SHADER);

		Gdx.gl.glShaderSource(vertexShaderID, vertexShaderString);
		Gdx.gl.glShaderSource(fragmentShaderID, fragmentShaderString);

		Gdx.gl.glCompileShader(vertexShaderID);
		Gdx.gl.glCompileShader(fragmentShaderID);

		renderingProgramID = Gdx.gl.glCreateProgram();

		Gdx.gl.glAttachShader(renderingProgramID, vertexShaderID);
		Gdx.gl.glAttachShader(renderingProgramID, fragmentShaderID);

		Gdx.gl.glLinkProgram(renderingProgramID);

		positionLoc				= Gdx.gl.glGetAttribLocation(renderingProgramID, "a_position");
		Gdx.gl.glEnableVertexAttribArray(positionLoc);

		modelMatrixLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_modelMatrix");
		projectionMatrixLoc	= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_projectionMatrix");

		colorLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_color");

		Gdx.gl.glUseProgram(renderingProgramID);

		float[] pm = new float[16];

		pm[0] = 2.0f / Gdx.graphics.getWidth(); pm[4] = 0.0f; pm[8] = 0.0f; pm[12] = -1.0f;
		pm[1] = 0.0f; pm[5] = 2.0f / Gdx.graphics.getHeight(); pm[9] = 0.0f; pm[13] = -1.0f;
		pm[2] = 0.0f; pm[6] = 0.0f; pm[10] = 1.0f; pm[14] = 0.0f;
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 0.0f; pm[15] = 1.0f;

		projectionMatrix = BufferUtils.newFloatBuffer(16);
		projectionMatrix.put(pm);
		projectionMatrix.rewind();
		Gdx.gl.glUniformMatrix4fv(projectionMatrixLoc, 1, false, projectionMatrix);


		float[] mm = new float[16];

		mm[0] = 1.0f; mm[4] = 0.0f; mm[8] = 0.0f; mm[12] = 0.0f;
		mm[1] = 0.0f; mm[5] = 1.0f; mm[9] = 0.0f; mm[13] = 0.0f;
		mm[2] = 0.0f; mm[6] = 0.0f; mm[10] = 1.0f; mm[14] = 0.0f;
		mm[3] = 0.0f; mm[7] = 0.0f; mm[11] = 0.0f; mm[15] = 1.0f;

		modelMatrix = BufferUtils.newFloatBuffer(16);
		modelMatrix.put(mm);
		modelMatrix.rewind();

		Gdx.gl.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrix);

		//COLOR IS SET HERE
		Gdx.gl.glUniform4f(colorLoc, 0.7f, 0.2f, 0, 1);

		//COLOR FOR CLEAR IS SET HERE
		Gdx.gl.glClearColor(0.4f, 0.6f, 1.0f, 1.0f);

		//VERTEX ARRAY IS FILLED HERE
		float[] array = {-squareSize, -squareSize,
				-squareSize, squareSize,
				squareSize, -squareSize,
				squareSize, squareSize};

		vertexBuffer = BufferUtils.newFloatBuffer(8);
		vertexBuffer.put(array);
		vertexBuffer.rewind();


		Gdx.gl.glVertexAttribPointer(positionLoc, 2, GL20.GL_FLOAT, false, 0, vertexBuffer);
		boxes = new ArrayList<Box>(2);
		boxes.add(new Box(400, 400, 0));

		speed = 10;
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//UPDATE CODE

		boxes.get(0).move();
		Gdx.gl.glUniform4f(colorLoc, 0, 0, 0, 0);
		setModelMatrixTranslation(boxes.get(0).position_x, boxes.get(0).position_y);
		setModelMatrixScale(boxes.get(0).scale_x, boxes.get(0).scale_y);
		Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4);
		setModelMatrixScale(1, 1);

		if(Gdx.input.justTouched()){
			boxes.add(new Box(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 2));
		}

		Gdx.gl.glUniform4f(colorLoc, 1, 0, 0, 0);
		for(int i = 1; i < boxes.size(); i++){
			boxes.get(i).motion();
			setModelMatrixTranslation(boxes.get(i).position_x, boxes.get(i).position_y);
			Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4);
		}

	}


	private void clearModelMatrix()
	{
		modelMatrix.put(0, 1.0f);
		modelMatrix.put(1, 0.0f);
		modelMatrix.put(2, 0.0f);
		modelMatrix.put(3, 0.0f);
		modelMatrix.put(4, 0.0f);
		modelMatrix.put(5, 1.0f);
		modelMatrix.put(6, 0.0f);
		modelMatrix.put(7, 0.0f);
		modelMatrix.put(8, 0.0f);
		modelMatrix.put(9, 0.0f);
		modelMatrix.put(10, 1.0f);
		modelMatrix.put(11, 0.0f);
		modelMatrix.put(12, 0.0f);
		modelMatrix.put(13, 0.0f);
		modelMatrix.put(14, 0.0f);
		modelMatrix.put(15, 1.0f);

		Gdx.gl.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrix);
	}
	private void setModelMatrixTranslation(float xTranslate, float yTranslate)
	{
		modelMatrix.put(12, xTranslate);
		modelMatrix.put(13, yTranslate);

		Gdx.gl.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrix);
	}
	private void setModelMatrixScale(float xScale, float yScale)
	{
		modelMatrix.put(0, xScale);
		modelMatrix.put(5, yScale);

		Gdx.gl.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrix);
	}
}
