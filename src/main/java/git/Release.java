/*

GitHandler - A GitHub release handler

Copyright (C) 2021 Dylan Wedman

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
package git;

import java.util.ArrayList;

/**
 * A simple data class containing information for a GitHub repository's release.
 * The information in a <i>Release</i> is as follows.
 *
 * <p>
 * - The name of the release
 * <p>
 * - The tag of the release
 * <p>
 * - The assets of the release
 *
 * @author Dylan Wedman
 * @see git.Asset
 */
public class Release {

    private final String releaseName;
    private final String tag;
    private final ArrayList<Asset> assets;

    /**
     * A simple data class containing release information for a GitHub
     * repository's release.
     *
     * @param releaseName The name of the release
     * @param tag The tag of the release
     * @param assets The assets from the release
     * @see git.Asset
     */
    public Release(String releaseName, String tag, ArrayList<Asset> assets) {
        this.releaseName = releaseName;
        this.tag = tag;
        this.assets = assets;
    }

    /**
     * Returns the name of the release
     *
     * @return the name of the release
     */
    public String getReleaseName() {
        return releaseName;
    }

    /**
     * Returns the tag of the release
     *
     * @return the tag of the release
     */
    public String getTag() {
        return tag;
    }

    /**
     * Returns an ArrayList of assets from the release
     *
     * <p>
     * Enumerate the list with a <i>for</i> loop like this
     * <p>
     * <code>
     * for (Asset asset : assets)
     * </code>
     *
     * @return An ArrayList of assets from the release
     */
    public ArrayList<Asset> getAssets() {
        return assets;
    }
}
