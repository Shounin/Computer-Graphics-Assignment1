package com.georg13.compGraph.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.georg13.compGraph.ComputerGraphics;

public class DesktopLauncher {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "GStyrmirRunarsson-georg13-Lab1";
		config.width = 1200;
		config.height = 600;
		new LwjglApplication(new ComputerGraphics(), config);
	}
}
