/*

JTegraNX - Another RCM payload injector

Copyright (C) 2019-2021 Dylan Wedman

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
package configs;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import ui.fx.JTegraNX;

public class ConfigManager {

    private static final List<Config> CONFIGS = new ArrayList<Config>();

    public static void addConfig(Config... configs) {
        for (Config config : configs) {
            if (CONFIGS.add(config)) {
                updateConfigList();
            }
        }
    }

    public static void deleteConfig(Config config) {
        Platform.runLater(() -> {
            CONFIGS.remove(config);
            JTegraNX.getController().getConfigList().getItems().remove(config.getConfigName());
        });
    }

    public static Config findConfig(Object name) {
        for (Config config : CONFIGS) {
            if (config.getConfigName().equals(name)) {
                return config;
            }
        }

        return null;
    }

    public static void selectConfig(Object item) {
        Platform.runLater(() -> {
            if (item != null) {
                if (!item.equals("No configs")) {
                    CONFIGS.stream().filter((config) -> (config.getConfigName().equals(item))).map((config) -> {
                        JTegraNX.getController().getPayloadPathField().setText(config.getPayloadPath());
                        return config;
                    }).forEachOrdered((config) -> {
                        JTegraNX.getController().getConfigList().getSelectionModel().select(config.getConfigName());
                    });
                }
            }
        });
    }

    public static void updateConfigList() {
        Platform.runLater(() -> {
            JTegraNX.getController().getConfigList().getItems().removeAll(JTegraNX.getController().getConfigList().getItems());

            if (CONFIGS.size() > 0) {
                CONFIGS.forEach((config) -> {
                    JTegraNX.getController().getConfigList().getItems().add(config.getConfigName());
                });
            } else {
                JTegraNX.getController().getConfigList().getItems().add("No configs");
                JTegraNX.getController().getConfigList().getSelectionModel().select("No configs");
            }
        });
    }

    public static List<Config> getConfigList() {
        return CONFIGS;
    }

    public static StringBuilder getConfigs() {
        StringBuilder configString = new StringBuilder();

        int index = 0;

        for (Config config : CONFIGS) {
            index++;

            if (CONFIGS.size() == index) {
                configString.append("[JTEGRANX CONFIG]\nconfigName=").append(config.getConfigName()).append("\npayloadPath=").append(config.getPayloadPath());
            } else {
                configString.append("[JTEGRANX CONFIG]\nconfigName=").append(config.getConfigName()).append("\npayloadPath=").append(config.getPayloadPath()).append("\n\n");
            }
        }

        return configString;
    }
}
