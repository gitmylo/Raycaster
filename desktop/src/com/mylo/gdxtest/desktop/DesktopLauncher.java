package com.mylo.gdxtest.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mylo.gdxtest.GdxTest;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Mylo's libgdx raycaster";
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new GdxTest(), config);
	}
}
