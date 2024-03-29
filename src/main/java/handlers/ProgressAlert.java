/*

JTegraNX - Another RCM payload injector

Copyright (C) 2019-2022 Dylan Wedman

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

 */

package handlers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;

public class ProgressAlert {
	private Alert alert;
	private ProgressBar bar;
	
	public ProgressAlert(Alert alert, ProgressBar bar) {
		this.alert = alert;
		this.bar = bar;
	}
	
	public Alert getAlert() {
		return alert;
	}

	public ProgressBar getBar() {
		return bar;
	}
	
	public void show() {
		this.alert.show();
	}
	
	public void close() {
		this.alert.getButtonTypes().add(ButtonType.OK);
		this.alert.close();
	}
}
