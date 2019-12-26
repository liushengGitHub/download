package liusheng.downloadCore.util;

import cn.hutool.core.util.ClassLoaderUtil;
import liusheng.downloadCore.DefaultPluginsLoader;
import liusheng.downloadInterface.Plugin;
import liusheng.downloadInterface.SearchPlugin;
import org.omg.CORBA.Object;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PluginUtils {
    private static List<SearchPlugin> list;

    public synchronized static List<SearchPlugin> getSearchPlugins() {
        if (Objects.nonNull(list)) return list;
        try {

            List<URLClassLoader> classLoaders = Files.list(Paths.get("plugins")).map(path -> {
                try {
                    return URLClassLoader.newInstance(new URL[]{path.toUri().toURL()});
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());

            List<Plugin>  plugins= classLoaders.stream().map(DefaultPluginsLoader.getPluginsLoader()::load).flatMap(List::stream).collect(Collectors.toList());
            list = plugins.stream().filter(o -> o instanceof SearchPlugin)
                    .map(o -> (SearchPlugin) o).collect(Collectors.toList());
            return list;
        } catch (IOException e) {
            return list = Collections.emptyList();
        }
    }
}
