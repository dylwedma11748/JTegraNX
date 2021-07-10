/*

JTegraNX - Another RCM payload injector

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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main handler for GitHandler
 *
 * @author Dylan Wedman
 */
public class GitHandler {

    private static final ArrayList<String> NAMES = new ArrayList<>();
    private static final ArrayList<String> TAGS = new ArrayList<>();
    private static final ArrayList<Asset> ASSETS = new ArrayList<>();
    private static final ArrayList<Release> RELEASES = new ArrayList<>();

    /**
     * Returns the latest release of the specified GitHub repository.
     *
     * @param gitURL The URL of the repository; this must be either the URL of
     * the repository or the URL of the repository's releases page.
     * @return the latest release of the specified GitHub repository.
     * @see git.Release
     */
    public static Release generateLatestRelease(String gitURL) {
        return generateReleases(gitURL).get(0);
    }

    /**
     * Returns an ArrayList of releases for the specified GitHub repository.
     *
     * @param gitURL The URL of the repository; this must be either the URL of
     * the repository or the URL of the repository's releases page.
     *
     * <p>
     * Enumerate through the list with a <i>for</i> loop like this:
     * <p>
     * <code>
     * for (Release release : releases)
     * </code>
     *
     * @return an ArrayList of releases for the specified GitHub repository
     * @see git.Release
     * @see #generateLatestRelease(String gitURL)
     */
    public static ArrayList<Release> generateReleases(String gitURL) {
        NAMES.clear();
        TAGS.clear();
        ASSETS.clear();
        RELEASES.clear();

        try {
            if (!gitURL.endsWith("/releases")) {
                gitURL = gitURL.concat("/releases");
            }

            URL url = new URL(gitURL);
            URLConnection connection = url.openConnection();

            try (InputStreamReader isr = new InputStreamReader(connection.getInputStream()); BufferedReader bReader = new BufferedReader(isr)) {
                String line;

                while ((line = bReader.readLine()) != null) {
                    if (line.endsWith("</a>")) {
                        if (line.contains("/releases/tag/")) {
                            String name = line.substring(line.indexOf(">") + 1, line.lastIndexOf("<"));

                            if (name.contains("&amp;")) {
                                name = name.replaceAll("&amp;", "&");
                            }

                            NAMES.add(name);
                            String tag = line.substring(line.indexOf("tag/") + 4, line.indexOf(">") - 1);
                            TAGS.add(tag);
                        }
                    }

                    for (String tag : TAGS) {
                        if (line.contains("/download/" + tag) && !line.endsWith("</a></p>")) {
                            String assetName = line.substring(line.indexOf(tag) + tag.length() + 1, line.lastIndexOf("rel") - 2);
                            String assetLink = gitURL + "/download/" + tag + "/" + assetName;
                            ASSETS.add(new Asset(assetName, tag, assetLink));
                        }

                        if (line.contains("/archive/" + tag + ".zip")) {
                            String assetName = "Source code (zip)";
                            String assetLink = gitURL.replaceFirst("/releases", "/archive/") + tag + ".zip";
                            ASSETS.add(new Asset(assetName, tag, assetLink));
                        }

                        if (line.contains("/archive/" + tag + ".tar.gz")) {
                            String assetName = "Source code (tar.gz)";
                            String assetLink = gitURL.replaceFirst("/releases", "/archive/") + tag + ".tar.gz";
                            ASSETS.add(new Asset(assetName, tag, assetLink));
                        }
                    }
                }

                int releaseIndex = 0;

                for (String tag : TAGS) {
                    ArrayList<Asset> associatingTagAssets = new ArrayList<>();

                    ASSETS.stream().filter((asset) -> (asset.getAssociatingTag().equals(tag))).forEachOrdered((asset) -> {
                        associatingTagAssets.add(asset);
                    });

                    RELEASES.add(new Release(NAMES.get(releaseIndex), tag, associatingTagAssets));
                    releaseIndex++;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GitHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return RELEASES;
    }

    /**
     * Downloads and saves the specified asset to the specified location.
     *
     * @param asset The asset to be downloaded; This must contain a valid
     * download link.
     * @param destination The destination of where to save the downloaded file
     * @return the downloaded file
     * @see git.Asset
     */
    public static File downloadAsset(Asset asset, String destination) {
        File output = null;

        try {
            URL download = new URL(asset.getAssetLink());

            try (BufferedInputStream bis = new BufferedInputStream(download.openStream()); FileOutputStream fos = new FileOutputStream((output = new File(destination)))) {
                byte[] buffer = new byte[1024];
                int bytes;

                while ((bytes = bis.read(buffer, 0, 1024)) != -1) {
                    fos.write(buffer, 0, bytes);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GitHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }
}
