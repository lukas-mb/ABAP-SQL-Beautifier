package com.abap.sql.beautifier.utility;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.abap.sql.beautifier.Activator;

public class BeautifierIcon {
	private static Image icon;

	public static Image get() {
		if (icon == null) {
			Activator.getDefault();
			icon = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/magicwand16.png").createImage();
		}
		return icon;
	}

}
