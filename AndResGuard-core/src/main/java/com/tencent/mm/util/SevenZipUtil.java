package com.tencent.mm.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SevenZipUtil {

    private static final String SEVEN_ZIP_DIR = "executable";
    private static String cachedSevenZipPath;

    /**
     * 获取 SevenZip 可执行文件路径
     * 优先从缓存中获取，如果缓存中没有，则从 jar 中提取到临时目录
     * @return SevenZip 可执行文件路径
     * @throws IOException 如果提取失败
     */
    public static String getSevenZipPath() throws IOException {
        if (cachedSevenZipPath != null && new File(cachedSevenZipPath).exists()) {
            return cachedSevenZipPath;
        }

        String osName = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();
        String executableName;

        if (osName.contains("windows")) {
            if (arch.contains("64")) {
                executableName = "7za-windows-x86_64.exe";
            } else {
                executableName = "7za-windows-x86_32.exe";
            }
        } else if (osName.contains("linux")) {
            if (arch.contains("64")) {
                executableName = "7zz-linux-x64";
            } else {
                executableName = "7zz-linux-x86";
            }
        } else if (osName.contains("mac")) {
            executableName = "7zz-mac";
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + osName);
        }

        // 从 jar 中提取可执行文件
        String resourcePath = executableName;
        InputStream inputStream = SevenZipUtil.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Could not find SevenZip executable: " + resourcePath);
        }

        // 创建临时目录
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "AndResGuard-SevenZip");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        // 创建临时文件
        File tempExecutable = new File(tempDir, executableName);
        try (FileOutputStream outputStream = new FileOutputStream(tempExecutable)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            inputStream.close();
        }

        // 设置可执行权限
        tempExecutable.setExecutable(true);

        // 缓存路径
        cachedSevenZipPath = tempExecutable.getAbsolutePath();
        return cachedSevenZipPath;
    }
}
