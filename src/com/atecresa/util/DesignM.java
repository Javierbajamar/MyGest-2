package com.atecresa.util;

import android.content.res.ColorStateList;
import android.graphics.Color;

import com.atecresa.preferencias.TpvConfig;

public class DesignM {

	public static int genColor(int color) {
		return genColor(color, 0);
	}

	public static int genColor(int color, int ac) {

		final String colorSolido = "ff000000";
		String colorHex = Integer.toHexString(color);
		colorHex = "#" + colorSolido.substring(0, (8 - colorHex.length()))
				+ colorHex;
		String colorRGB = colorHex.substring(0, 3);
		if (ac == 0)
			colorRGB = colorRGB + colorHex.substring(7, 9)
					+ colorHex.substring(5, 7) + colorHex.substring(3, 5);
		else {
			int iR = Integer.parseInt(colorHex.substring(7, 9), 16) + ac;
			int iG = Integer.parseInt(colorHex.substring(5, 7), 16) + ac;
			int iB = Integer.parseInt(colorHex.substring(3, 5), 16) + ac;
			if (iR < 0)
				iR = 0;
			if (iG < 0)
				iR = 0;
			if (iB < 0)
				iR = 0;
			if (iR > 255)
				iR = 255;
			if (iG > 255)
				iR = 255;
			if (iB > 255)
				iR = 255;
			String sR = Integer.toHexString(iR);
			String sG = Integer.toHexString(iG);
			String sB = Integer.toHexString(iB);
			if (sR.length() < 2)
				sR = "0" + sR;
			if (sB.length() < 2)
				sR = "0" + sR;
			if (sG.length() < 2)
				sR = "0" + sR;
			colorRGB = colorRGB + sR + sB + sG;
		}
		color = Color.parseColor(colorRGB);
		return color;
	}

	//DIVISOR PARA LINEARLAYOUT
	public static final int divider_size=10;

	//region COLORLIST PARA FABBUTTON Y MENÚ
	private static final int[][] states = new int[][] {
			new int[] { android.R.attr.state_enabled}
	};

	private static int[] appBackColor = new int[] {
			TpvConfig.getAppBackColor()
	};

	private static int[] appForeColor = new int[] {
			TpvConfig.getAppForecolor()
	};

	public static ColorStateList colorListAppBackColor(){
		return new ColorStateList(states, appBackColor);
	}

	public static ColorStateList colorListAppForeColor(){
		return new ColorStateList(states, appForeColor);
	}

	//endregion

}
