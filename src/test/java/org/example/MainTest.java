package org.example;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    void testSystemClassLoader() throws IOException {
        // myprop.properties は細書からresourcesに配置されているので、実行時のクラスパスに入る。
        // Mainクラスと同じクラスローダーで読み込まれるため、
        Properties properties = new Properties();
        String myprop = "/myprop.properties";
        properties.load(Main.class.getResourceAsStream(myprop));
        assertEquals("world", properties.getProperty("hello"));
    }

    @Test
    void testCustomClassloader() throws IOException {
        // myresource ディレクトリ内に "myprop2.properties" を作成
        var myresoruce = Path.of("./myresoruce");
        String myprop2 = "./myprop2.properties";
        var myprop2Path = myresoruce.resolve(myprop2);
        //noinspection NonAsciiCharacters
        クラスパス外ディレクトリにリソースファイルを配置(myresoruce, myprop2Path);
        // myprop2.propertiesはシステムクラスパスにないのでnullが返る
        assertNull(Main.class.getResourceAsStream(myprop2));

        // ./myresource 以下を読み込むクラスローダーを作成して、myprop2をロード
        try (var customClassLoader = new URLClassLoader(new URL[]{myresoruce.toUri().toURL()})) {
            assertNotNull(customClassLoader.getResourceAsStream(myprop2));
        } finally {
            Files.deleteIfExists(myprop2Path);
        }

    }

    @SuppressWarnings("NonAsciiCharacters")
    void クラスパス外ディレクトリにリソースファイルを配置(@NotNull Path path, @NotNull Path resourcePath) throws IOException {
        Files.createDirectories(path);
        Files.writeString(resourcePath, """
                hello=japan
                name=yusuke
                """);
    }

}