/*

JTegraNX - Another GUI for TegraRcmSmash

Copyright (C) 2020 Dylan Wedman

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
package jtegranx.payloads;

public class Payload {

    private final String name;
    private final String version;
    private final String downloadURL;

    public Payload(String name, String version, String downloadURL) {
        this.name = name;
        this.version = version;
        this.downloadURL = downloadURL;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDownloadURL() {
        return downloadURL;
    }
}
