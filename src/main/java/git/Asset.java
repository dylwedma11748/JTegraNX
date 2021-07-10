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

/**
 * A simple data class containing the information from an asset from a GitHub
 * repository's release. The information contained in an <i>Asset</i> is as
 * follows.
 * <p>
 * - The name of the asset
 * <p>
 * - The associating release tag of the asset
 * <p>
 * - The download link of the asset
 * <p>
 * Be sure to compare the tag of the asset with the release tag to ensure you
 * are using the correct one.
 *
 * @author Dylan Wedman
 */
public class Asset {

    private final String assetName;
    private final String associatingTag;
    private final String assetLink;

    /**
     * A simple data class containing the information from an asset from a
     * GitHub repository's release.
     *
     * @param assetName The name of the asset
     * @param associatingTag The associating release tag of the asset
     * @param assetLink The download link of the asset
     */
    public Asset(String assetName, String associatingTag, String assetLink) {
        this.assetName = assetName;
        this.associatingTag = associatingTag;
        this.assetLink = assetLink;
    }

    /**
     * Returns the name of the asset
     *
     * @return the name of the asset
     */
    public String getAssetName() {
        return assetName;
    }

    /**
     * Returns the associating release tag of the asset
     *
     * @return the associating release tag of the asset
     */
    public String getAssociatingTag() {
        return associatingTag;
    }

    /**
     * Returns the download link of the asset
     *
     * @return the download link of the asset
     */
    public String getAssetLink() {
        return assetLink;
    }
}
