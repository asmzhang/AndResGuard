//package com.tencent.mm.util;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Collection;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.zip.CRC32;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipFile;
//import java.util.zip.ZipOutputStream;
//
//public class FileOperation {
//  private static final int BUFFER = 8192;
//
//  /**
//   * 计算字节数组的 MD5 哈希值
//   * @param data 字节数组
//   * @return MD5 哈希值的十六进制字符串
//   */
//  private static String calculateMD5(byte[] data) {
//    try {
//      MessageDigest md = MessageDigest.getInstance("MD5");
//      byte[] hash = md.digest(data);
//      StringBuilder sb = new StringBuilder();
//      for (byte b : hash) {
//        sb.append(String.format("%02x", b));
//      }
//      return sb.toString();
//    } catch (NoSuchAlgorithmException e) {
//      e.printStackTrace();
//      return "error";
//    }
//  }
//
//  public static boolean fileExists(String filePath) {
//    if (filePath == null) {
//      return false;
//    }
//
//    File file = new File(filePath);
//    if (file.exists()) return true;
//    return false;
//  }
//
//  public static boolean deleteFile(String filePath) {
//    if (filePath == null) {
//      return true;
//    }
//
//    File file = new File(filePath);
//    if (file.exists()) {
//      return file.delete();
//    }
//    return true;
//  }
//
//  public static long getlist(File f) {
//    if (f == null || (!f.exists())) {
//      return 0;
//    }
//    if (!f.isDirectory()) {
//      return 1;
//    }
//    long size;
//    File flist[] = f.listFiles();
//    size = flist.length;
//    for (int i = 0; i < flist.length; i++) {
//      if (flist[i].isDirectory()) {
//        size = size + getlist(flist[i]);
//        size--;
//      }
//    }
//    return size;
//  }
//
//  public static long getFileSizes(File f) {
//    long size = 0;
//    if (f.exists() && f.isFile()) {
//      FileInputStream fis = null;
//      try {
//        fis = new FileInputStream(f);
//        size = fis.available();
//      } catch (IOException e) {
//        e.printStackTrace();
//      } finally {
//        try {
//          if (fis != null) {
//            fis.close();
//          }
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//      }
//    }
//    return size;
//  }
//
//  public static boolean deleteDir(File file) {
//    if (file == null || (!file.exists())) {
//      return false;
//    }
//    if (file.isFile()) {
//      file.delete();
//    } else if (file.isDirectory()) {
//      File files[] = file.listFiles();
//      for (int i = 0; i < files.length; i++) {
//        deleteDir(files[i]);
//      }
//    }
//    file.delete();
//    return true;
//  }
//
//  public static void copyFileUsingStream(File source, File dest) throws IOException {
//    FileInputStream is = null;
//    FileOutputStream os = null;
//    File parent = dest.getParentFile();
//    if (parent != null && (!parent.exists())) {
//      parent.mkdirs();
//    }
//    try {
//      is = new FileInputStream(source);
//      os = new FileOutputStream(dest, false);
//
//      // 检查是否为图片文件
//      String fileName = source.getName().toLowerCase();
//      boolean isImage = fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".webp");
//
//      System.out.printf("[Steganography] Processing file: %s, isImage: %b\n", source.getName(), isImage);
//
//      if (isImage) {
//        // 对于图片文件，使用隐写功能
//        System.out.printf("[Steganography] Using steganography for image: %s\n", source.getName());
//        steganographyCopy(is, os);
//      } else {
//        // 对于非图片文件，使用普通复制
//        System.out.printf("[Steganography] Using normal copy for non-image: %s\n", source.getName());
//        byte[] buffer = new byte[BUFFER];
//        int length;
//        while ((length = is.read(buffer)) > 0) {
//          os.write(buffer, 0, length);
//        }
//      }
//    } finally {
//      if (is != null) {
//        is.close();
//      }
//      if (os != null) {
//        os.close();
//      }
//    }
//  }
//
//  /**
//   * 图片隐写复制，修改图片内容但不影响视觉效果
//   * @param is 输入流
//   * @param os 输出流
//   * @throws IOException IO异常
//   */
//  private static void steganographyCopy(FileInputStream is, FileOutputStream os) throws IOException {
//    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    byte[] buffer = new byte[BUFFER];
//    int length;
//    while ((length = is.read(buffer)) > 0) {
//      baos.write(buffer, 0, length);
//    }
//    byte[] data = baos.toByteArray();
//    baos.close();
//
//    System.out.printf("[Steganography] Original image size: %d bytes\n", data.length);
//    // 计算原始数据的 MD5
//    String originalMD5 = calculateMD5(data);
//    System.out.printf("[Steganography] Original MD5: %s\n", originalMD5);
//
//    // 修改图片数据，不影响视觉效果
//    byte[] modifiedData = modifyImageData(data);
//
//    System.out.printf("[Steganography] Modified image size: %d bytes\n", modifiedData.length);
//    // 计算修改后数据的 MD5
//    String modifiedMD5 = calculateMD5(modifiedData);
//    System.out.printf("[Steganography] Modified MD5: %s\n", modifiedMD5);
//    // 比较 MD5 是否不同
//    boolean dataChanged = !originalMD5.equals(modifiedMD5);
//    System.out.printf("[Steganography] Data changed: %b\n", dataChanged);
//
//    // 写入修改后的数据
//    os.write(modifiedData);
//  }
//
//  /**
//   * 修改图片数据，不影响视觉效果
//   * @param data 原始图片数据
//   * @return 修改后的图片数据
//   */
//  private static byte[] modifyImageData(byte[] data) {
//    // 复制数据
//    byte[] modifiedData = new byte[data.length];
//    System.arraycopy(data, 0, modifiedData, 0, data.length);
//
//    // 根据文件类型进行不同的修改
//    if (isPNG(modifiedData)) {
//      System.out.println("[Steganography] Processing PNG image");
//      // 对于PNG文件，修改IHDR块之后的可选块
//      modifyPNG(modifiedData);
//    } else if (isJPEG(modifiedData)) {
//      System.out.println("[Steganography] Processing JPEG image");
//      // 对于JPEG文件，在文件末尾添加数据
//      return modifyJPEG(modifiedData);
//    } else if (isWEBP(modifiedData)) {
//      System.out.println("[Steganography] Processing WEBP image");
//      // 对于WEBP文件，修改可选数据
//      modifyWEBP(modifiedData);
//    } else {
//      System.out.println("[Steganography] Unknown image format, using default modification");
//      // 对于未知格式，使用默认修改
//      modifyUnknownImage(modifiedData);
//    }
//
//    return modifiedData;
//  }
//
//  /**
//   * 检查是否为PNG文件
//   * @param data 文件数据
//   * @return 是否为PNG文件
//   */
//  private static boolean isPNG(byte[] data) {
//    return data.length >= 8 && data[0] == (byte)0x89 && data[1] == 0x50 && data[2] == 0x4E && data[3] == 0x47 &&
//           data[4] == 0x0D && data[5] == 0x0A && data[6] == 0x1A && data[7] == 0x0A;
//  }
//
//  /**
//   * 检查是否为JPEG文件
//   * @param data 文件数据
//   * @return 是否为JPEG文件
//   */
//  private static boolean isJPEG(byte[] data) {
//    return data.length >= 2 && (data[0] == (byte)0xFF && data[1] == (byte)0xD8);
//  }
//
//  /**
//   * 检查是否为WEBP文件
//   * @param data 文件数据
//   * @return 是否为WEBP文件
//   */
//  private static boolean isWEBP(byte[] data) {
//    return data.length >= 12 && data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F' &&
//           data[8] == 'W' && data[9] == 'E' && data[10] == 'B' && data[11] == 'P';
//  }
//
//  /**
//   * 修改PNG文件数据
//   * @param data PNG文件数据
//   */
//  private static void modifyPNG(byte[] data) {
//    // 跳过PNG文件头8字节
//    int offset = 8;
//    System.out.printf("[Steganography] PNG file length: %d, starting offset: %d\n", data.length, offset);
//
//    // 寻找IHDR块后的第一个块
//    while (offset + 8 <= data.length) {
//      // 读取块长度
//      int chunkLength = ((data[offset] & 0xFF) << 24) | ((data[offset + 1] & 0xFF) << 16) |
//                       ((data[offset + 2] & 0xFF) << 8) | (data[offset + 3] & 0xFF);
//
//      // 读取块类型
//      String chunkType = new String(data, offset + 4, 4);
//      System.out.printf("[Steganography] Found PNG chunk: %s at offset %d, length: %d\n", chunkType, offset, chunkLength);
//
//      // 如果找到IDAT块，就在此之前修改
//      if (chunkType.equals("IDAT")) {
//        System.out.printf("[Steganography] Found IDAT chunk at offset %d, modifying previous data\n", offset);
//        // 在IDAT块前修改一些字节
//        for (int i = Math.max(offset - 20, 8); i < offset; i++) {
//          // 修改最低位，不影响视觉效果
//          data[i] = (byte)((data[i] & 0xFE) | (Math.random() > 0.5 ? 1 : 0));
//          System.out.printf("[Steganography] Modified byte at offset %d: 0x%02X -> 0x%02X\n", i, data[i] ^ 1, data[i]);
//        }
//        break;
//      }
//
//      // 跳过当前块
//      offset += 8 + chunkLength + 4; // 长度(4) + 类型(4) + 数据(chunkLength) + CRC(4)
//      System.out.printf("[Steganography] Skipping to next chunk, new offset: %d\n", offset);
//    }
//
//    // 如果没有找到合适的位置，就修改文件末尾的一些字节
//    if (offset >= data.length) {
//      System.out.printf("[Steganography] No IDAT chunk found, modifying end of file\n");
//      // 在文件末尾修改一些字节
//      int endOffset = Math.max(data.length - 50, 8);
//      for (int i = endOffset; i < data.length; i++) {
//        // 只修改最低位，不影响视觉效果
//        byte original = data[i];
//        data[i] = (byte)((data[i] & 0xFE) | (Math.random() > 0.5 ? 1 : 0));
//        System.out.printf("[Steganography] Modified end byte at offset %d: 0x%02X -> 0x%02X\n", i, original, data[i]);
//      }
//    }
//  }
//
//  /**
//   * 修改JPEG文件数据
//   * @param data JPEG文件数据
//   * @return 修改后的JPEG文件数据
//   */
//  private static byte[] modifyJPEG(byte[] data) {
//    // 检查文件末尾是否已经有FF D9标记
//    int endOffset = data.length - 2;
//    if (endOffset >= 0 && data[endOffset] == (byte)0xFF && data[endOffset + 1] == (byte)0xD9) {
//      // 在FF D9之前添加一些随机数据
//      byte[] newData = new byte[data.length + 10];
//      System.arraycopy(data, 0, newData, 0, endOffset);
//
//      // 添加随机数据
//      for (int i = endOffset; i < newData.length - 2; i++) {
//        newData[i] = (byte)(Math.random() * 256);
//      }
//
//      // 保留FF D9标记
//      newData[newData.length - 2] = (byte)0xFF;
//      newData[newData.length - 1] = (byte)0xD9;
//
//      return newData;
//    } else {
//      // 在文件末尾添加随机数据
//      byte[] newData = new byte[data.length + 10];
//      System.arraycopy(data, 0, newData, 0, data.length);
//
//      // 添加随机数据
//      for (int i = data.length; i < newData.length; i++) {
//        newData[i] = (byte)(Math.random() * 256);
//      }
//
//      return newData;
//    }
//  }
//
//  /**
//   * 修改WEBP文件数据
//   * @param data WEBP文件数据
//   */
//  private static void modifyWEBP(byte[] data) {
//    // 跳过RIFF头和WEBP头
//    int offset = 12;
//    boolean foundChunk = false;
//
//    System.out.printf("[Steganography] WEBP file length: %d, starting offset: %d\n", data.length, offset);
//
//    // 寻找VP8或VP8L或VP8X块
//    while (offset + 8 <= data.length) {
//      try {
//        // 读取块类型
//        String chunkType = new String(data, offset, 4);
//
//        // 读取块长度（WEBP 使用小端字节序）
//        int chunkLength = ((data[offset + 7] & 0xFF) << 24) | ((data[offset + 6] & 0xFF) << 16) |
//                         ((data[offset + 5] & 0xFF) << 8) | (data[offset + 4] & 0xFF);
//
//        System.out.printf("[Steganography] Found WEBP chunk: %s at offset %d, length: %d\n", chunkType, offset, chunkLength);
//
//        // 检查块长度是否有效，防止越界
//        if (chunkLength < 0 || offset + 8 + chunkLength > data.length) {
//          System.err.printf("[Steganography] Invalid WEBP chunk length: %d at offset %d\n", chunkLength, offset);
//          break;
//        }
//
//        // 如果找到图片数据块，就在此之前修改
//        if (chunkType.equals("VP8 ") || chunkType.equals("VP8L") || chunkType.equals("VP8X")) {
//          System.out.printf("[Steganography] Found WEBP image data chunk: %s at offset %d\n", chunkType, offset);
//
//          // 尝试修改块前的字节
//          int startOffset = Math.max(offset - 10, 12);
//          if (startOffset < offset) {
//            // 在图片数据块前修改一些字节
//            for (int i = startOffset; i < offset; i++) {
//              // 只修改最低位，不影响视觉效果
//              data[i] = (byte)((data[i] & 0xFE) | (Math.random() > 0.5 ? 1 : 0));
//              System.out.printf("[Steganography] Modified byte at offset %d: 0x%02X -> 0x%02X\n", i, data[i] ^ 1, data[i]);
//            }
//          } else {
//            // 如果块前没有足够空间，修改块内部的数据
//            System.out.printf("[Steganography] No space before chunk, modifying chunk data\n");
//            // 修改块数据部分的最低位
//            for (int i = offset + 8; i < Math.min(offset + 8 + chunkLength, data.length); i++) {
//              // 只修改最低位，不影响视觉效果
//              data[i] = (byte)((data[i] & 0xFE) | (Math.random() > 0.5 ? 1 : 0));
//              System.out.printf("[Steganography] Modified chunk data at offset %d: 0x%02X -> 0x%02X\n", i, data[i] ^ 1, data[i]);
//            }
//          }
//
//          foundChunk = true;
//          break;
//        }
//
//        // 跳过当前块
//        offset += 8 + chunkLength;
//        System.out.printf("[Steganography] Skipping to next WEBP chunk, new offset: %d\n", offset);
//      } catch (Exception e) {
//        System.err.printf("[Steganography] Error processing WEBP chunk at offset %d: %s\n", offset, e.getMessage());
//        break;
//      }
//    }
//
//    // 如果没有找到图片数据块，修改文件末尾的一些字节
//    if (!foundChunk) {
//      System.out.println("[Steganography] No WEBP image data chunk found, modifying end of file");
//      // 在文件末尾修改一些字节
//      int endOffset = Math.max(data.length - 50, 12);
//      for (int i = endOffset; i < data.length; i++) {
//        // 只修改最低位，不影响视觉效果
//        data[i] = (byte)((data[i] & 0xFE) | (Math.random() > 0.5 ? 1 : 0));
//        System.out.printf("[Steganography] Modified end byte at offset %d: 0x%02X -> 0x%02X\n", i, data[i] ^ 1, data[i]);
//      }
//    }
//  }
//
//  /**
//   * 修改未知格式的图片数据
//   * @param data 图片数据
//   */
//  private static void modifyUnknownImage(byte[] data) {
//    System.out.println("[Steganography] Modifying unknown image format");
//    // 对于未知格式，修改文件末尾的一些字节
//    int endOffset = Math.max(data.length - 50, 0);
//    for (int i = endOffset; i < data.length; i++) {
//      // 只修改最低位，不影响视觉效果
//      data[i] = (byte)((data[i] & 0xFE) | (Math.random() > 0.5 ? 1 : 0));
//    }
//  }
//
//  public static boolean checkDirectory(String dir) {
//    File dirObj = new File(dir);
//    deleteDir(dirObj);
//
//    if (!dirObj.exists()) {
//      dirObj.mkdirs();
//    }
//    return true;
//  }
//
//  public static File checkFile(String dir) {
//    deleteFile(dir);
//    File file = new File(dir);
//    try {
//      file.createNewFile();
//    } catch (IOException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
//    return file;
//  }
//
////     @SuppressWarnings("rawtypes")
////     public static HashMap<String, Integer> unZipAPk(String fileName, String filePath) throws IOException {
////         long start = System.currentTimeMillis();
//
////         System.out.printf("[UnZip] Start unzip apk\n");
////         System.out.printf("[UnZip] apk    : %s\n", fileName);
////         System.out.printf("[UnZip] outDir : %s\n", filePath);
//
////         checkDirectory(filePath);
//
////         ZipFile zipFile = new ZipFile(fileName);
////         Enumeration emu = zipFile.entries();
////         HashMap<String, Integer> compress = new HashMap<>();
//
////         int fileCount = 0;
//
////         try {
////             while (emu.hasMoreElements()) {
////                 ZipEntry entry = (ZipEntry) emu.nextElement();
////                 String entryName = entry.getName();
//
////                 // 统一路径分隔符
////                 String compatPath = entryName.replace("\\", "/");
////                 compress.put(compatPath, entry.getMethod());
//
////                 if (entry.isDirectory()) {
////                     File dir = new File(filePath, compatPath);
////                     if (!dir.exists() && !dir.mkdirs()) {
//// //                        System.out.printf("[UnZip][WARN] mkdir failed: %s\n", dir.getAbsolutePath());
////                     }
////                     continue;
////                 }
//
////                 System.out.printf(
////                         "[UnZip] entry=%s method=%s size=%d compressed=%d\n",
////                         compatPath,
////                         entry.getMethod() == ZipEntry.STORED ? "STORED" : "DEFLATED",
////                         entry.getSize(),
////                         entry.getCompressedSize()
////                 );
//
////                 File outFile = new File(filePath, compatPath);
////                 File parent = outFile.getParentFile();
////                 if (parent != null && !parent.exists() && !parent.mkdirs()) {
////                     System.out.printf("[UnZip][WARN] mkdir parent failed: %s\n", parent.getAbsolutePath());
////                 }
//
////                 try (
////                         BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
////                         FileOutputStream fos = new FileOutputStream(outFile);
////                         BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER)
////                 ) {
////                     byte[] buf = new byte[BUFFER];
////                     int len;
////                     long written = 0;
//
////                     while ((len = bis.read(buf)) != -1) {
////                         bos.write(buf, 0, len);
////                         written += len;
////                     }
////                     bos.flush();
//
////                     long declaredSize = entry.getSize();
////                     if (declaredSize >= 0 && written != declaredSize) {
////                         throw new IOException(String.format(
////                                 "[UnZip][ERROR] %s actual=%d declared=%d",
////                                 compatPath, written, declaredSize
////                         ));
////                     }
//
////                     fileCount++;
////                 } catch (Throwable t) {
////                     System.out.printf(
////                             "[UnZip][ERROR] fail entry=%s err=%s\n",
////                             compatPath, t.getMessage()
////                     );
////                     throw t;
////                 }
////             }
////         } finally {
////             zipFile.close();
////         }
//
////         long cost = System.currentTimeMillis() - start;
////         System.out.printf("[UnZip] Done, files=%d, cost=%dms\n", fileCount, cost);
//
////         return compress;
////     }
//
//
//  @SuppressWarnings("rawtypes")
//  public static HashMap<String, Integer> unZipAPk(String fileName, String filePath) throws IOException {
//    checkDirectory(filePath);
//    ZipFile zipFile = new ZipFile(fileName);
//    Enumeration emu = zipFile.entries();
//    HashMap<String, Integer> compress = new HashMap<>();
//    try {
//      while (emu.hasMoreElements()) {
//        ZipEntry entry = (ZipEntry) emu.nextElement();
//        if (entry.isDirectory()) {
//          new File(filePath, entry.getName()).mkdirs();
//          continue;
//        }
//        BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
//
//        File file = new File(filePath + File.separator + entry.getName());
//
//        File parent = file.getParentFile();
//        if (parent != null && (!parent.exists())) {
//          parent.mkdirs();
//        }
//        //要用linux的斜杠
//        String compatibaleresult = entry.getName();
//        if (compatibaleresult.contains("\\")) {
//          compatibaleresult = compatibaleresult.replace("\\", "/");
//        }
//        compress.put(compatibaleresult, entry.getMethod());
//        FileOutputStream fos = new FileOutputStream(file);
//        BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);
//
//        byte[] buf = new byte[BUFFER];
//        int len;
//        while ((len = bis.read(buf, 0, BUFFER)) != -1) {
//          fos.write(buf, 0, len);
//        }
//        bos.flush();
//        bos.close();
//        bis.close();
//      }
//    } finally {
//      zipFile.close();
//    }
//    return compress;
//  }
//
//  /**
//   * zip list of file
//   *
//   * @param resFileList file(dir) list
//   * @param baseFolder file(dir) base folder, we should calc relative path of resFile with base
//   * @param zipFile output zip file
//   * @param compressData compress data
//   * @throws IOException io exception
//   */
//  public static void zipFiles(
//      Collection<File> resFileList, File baseFolder, File zipFile, HashMap<String, Integer> compressData)
//      throws IOException {
//    ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFFER));
//    for (File resFile : resFileList) {
//      if (resFile.exists()) {
//        if (resFile.getAbsolutePath().contains(baseFolder.getAbsolutePath())) {
//          String relativePath = baseFolder.toURI().relativize(resFile.getParentFile().toURI()).getPath();
//          // remove slash at end of relativePath
//          if (relativePath.length() > 1) {
//            relativePath = relativePath.substring(0, relativePath.length() - 1);
//          } else {
//            relativePath = "";
//          }
//          zipFile(resFile, zipOut, relativePath, compressData);
//        } else {
//          zipFile(resFile, zipOut, "", compressData);
//        }
//      }
//    }
//    zipOut.close();
//  }
//
//  private static void zipFile(
//      File resFile, ZipOutputStream zipout, String rootpath, HashMap<String, Integer> compressData) throws IOException {
//    rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator) + resFile.getName();
//    if (resFile.isDirectory()) {
//      File[] fileList = resFile.listFiles();
//      for (File file : fileList) {
//        zipFile(file, zipout, rootpath, compressData);
//      }
//    } else {
//      final byte[] fileContents = readContents(resFile);
//      //这里需要强转成linux格式，果然坑！！
//      if (rootpath.contains("\\")) {
//        rootpath = rootpath.replace("\\", "/");
//      }
//      if (!compressData.containsKey(rootpath)) {
//        System.err.printf(String.format("do not have the compress data path =%s in resource.asrc\n", rootpath));
//        //throw new IOException(String.format("do not have the compress data path=%s", rootpath));
//        return;
//      }
//      int compressMethod = compressData.get(rootpath);
//      ZipEntry entry = new ZipEntry(rootpath);
//
//      if (compressMethod == ZipEntry.DEFLATED) {
//        entry.setMethod(ZipEntry.DEFLATED);
//      } else {
//        entry.setMethod(ZipEntry.STORED);
//        entry.setSize(fileContents.length);
//        final CRC32 checksumCalculator = new CRC32();
//        checksumCalculator.update(fileContents);
//        entry.setCrc(checksumCalculator.getValue());
//      }
//      zipout.putNextEntry(entry);
//      zipout.write(fileContents);
//      zipout.flush();
//      zipout.closeEntry();
//    }
//  }
//
//  private static byte[] readContents(final File file) throws IOException {
//    final ByteArrayOutputStream output = new ByteArrayOutputStream();
//    final int bufferSize = 4096;
//    try {
//      final FileInputStream in = new FileInputStream(file);
//      final BufferedInputStream bIn = new BufferedInputStream(in);
//      int length;
//      byte[] buffer = new byte[bufferSize];
//      byte[] bufferCopy;
//      while ((length = bIn.read(buffer, 0, bufferSize)) != -1) {
//        bufferCopy = new byte[length];
//        System.arraycopy(buffer, 0, bufferCopy, 0, length);
//        output.write(bufferCopy);
//      }
//      bIn.close();
//    } finally {
//      output.close();
//    }
//    return output.toByteArray();
//  }
//}









package com.tencent.mm.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileOperation {
    private static final int BUFFER = 8192;

    /**
     * 计算字节数组的 MD5 哈希值
     * @param data 字节数组
     * @return MD5 哈希值的十六进制字符串
     */
    private static String calculateMD5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static boolean fileExists(String filePath) {
        if (filePath == null) {
            return false;
        }

        File file = new File(filePath);
        if (file.exists()) return true;
        return false;
    }

    public static boolean deleteFile(String filePath) {
        if (filePath == null) {
            return true;
        }

        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    public static long getlist(File f) {
        if (f == null || (!f.exists())) {
            return 0;
        }
        if (!f.isDirectory()) {
            return 1;
        }
        long size;
        File flist[] = f.listFiles();
        size = flist.length;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getlist(flist[i]);
                size--;
            }
        }
        return size;
    }

    public static long getFileSizes(File f) {
        long size = 0;
        if (f.exists() && f.isFile()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                size = fis.available();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return size;
    }

    public static boolean deleteDir(File file) {
        if (file == null || (!file.exists())) {
            return false;
        }
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteDir(files[i]);
            }
        }
        file.delete();
        return true;
    }

    public static void copyFileUsingStream(File source, File dest) throws IOException {
        FileInputStream is = null;
        FileOutputStream os = null;
        File parent = dest.getParentFile();
        if (parent != null && (!parent.exists())) {
            parent.mkdirs();
        }
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest, false);

            // 检查是否为图片文件
            String fileName = source.getName().toLowerCase();
            boolean isImage = fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".webp");

            System.out.printf("[Steganography] Processing file: %s, isImage: %b\n", source.getName(), isImage);

            if (isImage) {
                // 对于图片文件，使用隐写功能
                System.out.printf("[Steganography] Using steganography for image: %s\n", source.getName());
                steganographyCopy(is, os);
            } else {
                // 对于非图片文件，使用普通复制
                System.out.printf("[Steganography] Using normal copy for non-image: %s\n", source.getName());
                byte[] buffer = new byte[BUFFER];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    /**
     * 图片隐写复制，修改图片内容但不影响视觉效果
     * @param is 输入流
     * @param os 输出流
     * @throws IOException IO异常
     */
    private static void steganographyCopy(FileInputStream is, FileOutputStream os) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER];
        int length;
        while ((length = is.read(buffer)) > 0) {
            baos.write(buffer, 0, length);
        }
        byte[] data = baos.toByteArray();
        baos.close();

        System.out.printf("[Steganography] Original image size: %d bytes\n", data.length);
        // 计算原始数据的 MD5
        String originalMD5 = calculateMD5(data);
        System.out.printf("[Steganography] Original MD5: %s\n", originalMD5);

        // 修改图片数据，不影响视觉效果
        byte[] modifiedData = modifyImageData(data);

        System.out.printf("[Steganography] Modified image size: %d bytes\n", modifiedData.length);
        // 计算修改后数据的 MD5
        String modifiedMD5 = calculateMD5(modifiedData);
        System.out.printf("[Steganography] Modified MD5: %s\n", modifiedMD5);
        // 比较 MD5 是否不同
        boolean dataChanged = !originalMD5.equals(modifiedMD5);
        System.out.printf("[Steganography] Data changed: %b\n", dataChanged);

        // 写入修改后的数据
        os.write(modifiedData);
    }

    /**
     * 修改图片数据，不影响视觉效果
     * @param data 原始图片数据
     * @return 修改后的图片数据
     */
    private static byte[] modifyImageData(byte[] data) throws IOException {
        if (isPNG(data)) {
            return modifyPNG(data);
        } else if (isJPEG(data)) {
            return modifyJPEG(data);
        } else if (isWEBP(data)) {
            return modifyWEBP(data);
        } else {
            modifyUnknownImage(data);
            return data;
        }
    }

    /**
     * 检查是否为PNG文件
     * @param data 文件数据
     * @return 是否为PNG文件
     */
    private static boolean isPNG(byte[] data) {
        return data.length >= 8 && data[0] == (byte)0x89 && data[1] == 0x50 && data[2] == 0x4E && data[3] == 0x47 &&
                data[4] == 0x0D && data[5] == 0x0A && data[6] == 0x1A && data[7] == 0x0A;
    }

    /**
     * 检查是否为JPEG文件
     * @param data 文件数据
     * @return 是否为JPEG文件
     */
    private static boolean isJPEG(byte[] data) {
        return data.length >= 2 && (data[0] == (byte)0xFF && data[1] == (byte)0xD8);
    }

    /**
     * 检查是否为WEBP文件
     * @param data 文件数据
     * @return 是否为WEBP文件
     */
    private static boolean isWEBP(byte[] data) {
        return data.length >= 12 && data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F' &&
                data[8] == 'W' && data[9] == 'E' && data[10] == 'B' && data[11] == 'P';
    }

    /**
     * 修改PNG文件数据（插入合法 tEXt chunk）
     * @param data PNG文件数据
     */
    private static byte[] modifyPNG(byte[] data) throws IOException {
        // 跳过PNG文件头8字节
        int offset = 8;

        // 找到 IHDR 块之后的位置
        while (offset + 8 <= data.length) {
            int chunkLen = ((data[offset] & 0xFF) << 24) |
                    ((data[offset + 1] & 0xFF) << 16) |
                    ((data[offset + 2] & 0xFF) << 8) |
                    (data[offset + 3] & 0xFF);

            String chunkType = new String(data, offset + 4, 4);
            offset += 8 + chunkLen + 4;

            if ("IHDR".equals(chunkType)) {
                break;
            }
        }

        // 随机时间戳隐写内容
        String timestamp = String.valueOf(System.currentTimeMillis());
        byte[] text = ("stego=" + timestamp).getBytes();
        byte[] keyword = "Comment".getBytes();
        byte[] chunkData = concat(keyword, new byte[]{0}, text);

        byte[] chunkType = "tEXt".getBytes();
        byte[] crcInput = concat(chunkType, chunkData);

        int crc = (int) new CRC32() {{
            update(crcInput);
        }}.getValue();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(data, 0, offset);
        baos.write(intToBytesBigEndian(chunkData.length));
        baos.write(chunkType);
        baos.write(chunkData);
        baos.write(intToBytesBigEndian(crc));
        baos.write(data, offset, data.length - offset);

        return baos.toByteArray();
    }

    /**
     * 修改JPEG文件数据（插入合法 APP1 segment）
     * @param data JPEG文件数据
     * @return 修改后的JPEG文件数据
     */
    private static byte[] modifyJPEG(byte[] data) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        byte[] payload = ("stego=" + timestamp).getBytes();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(data, 0, 2); // SOI

        byte[] marker = new byte[]{(byte) 0xFF, (byte) 0xE1};
        int length = payload.length + 2;

        baos.write(marker);
        baos.write(intToBytesBigEndian(length), 2, 2);
        baos.write(payload);

        baos.write(data, 2, data.length - 2);
        return baos.toByteArray();
    }

    /**
     * 修改WEBP文件数据（插入合法 EXIF chunk）
     * @param data WEBP文件数据
     */
    private static byte[] modifyWEBP(byte[] data) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        byte[] payload = ("stego=" + timestamp).getBytes();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(data);

        byte[] chunkType = "EXIF".getBytes();
        int size = payload.length;
        boolean pad = (size % 2) != 0;
        if (pad) size++;

        baos.write(chunkType);
        baos.write(intToBytesLittleEndian(size));
        baos.write(payload);
        if (pad) baos.write(0);

        byte[] full = baos.toByteArray();
        int riffSize = full.length - 8;
        System.arraycopy(intToBytesLittleEndian(riffSize), 0, full, 4, 4);

        return full;
    }

    /**
     * 修改未知格式的图片数据
     * @param data 图片数据
     */
    private static void modifyUnknownImage(byte[] data) {
        System.out.println("[Steganography] Modifying unknown image format");
        // 对于未知格式，修改文件末尾的一些字节
        int endOffset = Math.max(data.length - 50, 0);
        for (int i = endOffset; i < data.length; i++) {
            // 只修改最低位，不影响视觉效果
            data[i] = (byte)((data[i] & 0xFE) | (Math.random() > 0.5 ? 1 : 0));
        }
    }

    public static boolean checkDirectory(String dir) {
        File dirObj = new File(dir);
        deleteDir(dirObj);

        if (!dirObj.exists()) {
            dirObj.mkdirs();
        }
        return true;
    }

    public static File checkFile(String dir) {
        deleteFile(dir);
        File file = new File(dir);
        try {
            file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    @SuppressWarnings("rawtypes")
    public static HashMap<String, Integer> unZipAPk(String fileName, String filePath) throws IOException {
        checkDirectory(filePath);
        ZipFile zipFile = new ZipFile(fileName);
        Enumeration emu = zipFile.entries();
        HashMap<String, Integer> compress = new HashMap<>();
        try {
            while (emu.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) emu.nextElement();
                if (entry.isDirectory()) {
                    new File(filePath, entry.getName()).mkdirs();
                    continue;
                }
                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

                File file = new File(filePath + File.separator + entry.getName());

                File parent = file.getParentFile();
                if (parent != null && (!parent.exists())) {
                    parent.mkdirs();
                }
                //要用linux的斜杠
                String compatibaleresult = entry.getName();
                if (compatibaleresult.contains("\\")) {
                    compatibaleresult = compatibaleresult.replace("\\", "/");
                }
                compress.put(compatibaleresult, entry.getMethod());
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);

                byte[] buf = new byte[BUFFER];
                int len;
                while ((len = bis.read(buf, 0, BUFFER)) != -1) {
                    fos.write(buf, 0, len);
                }
                bos.flush();
                bos.close();
                bis.close();
            }
        } finally {
            zipFile.close();
        }
        return compress;
    }

    /**
     * zip list of file
     *
     * @param resFileList file(dir) list
     * @param baseFolder file(dir) base folder, we should calc relative path of resFile with base
     * @param zipFile output zip file
     * @param compressData compress data
     * @throws IOException io exception
     */
    public static void zipFiles(
            Collection<File> resFileList, File baseFolder, File zipFile, HashMap<String, Integer> compressData)
            throws IOException {
        ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFFER));
        for (File resFile : resFileList) {
            if (resFile.exists()) {
                if (resFile.getAbsolutePath().contains(baseFolder.getAbsolutePath())) {
                    String relativePath = baseFolder.toURI().relativize(resFile.getParentFile().toURI()).getPath();
                    // remove slash at end of relativePath
                    if (relativePath.length() > 1) {
                        relativePath = relativePath.substring(0, relativePath.length() - 1);
                    } else {
                        relativePath = "";
                    }
                    zipFile(resFile, zipOut, relativePath, compressData);
                } else {
                    zipFile(resFile, zipOut, "", compressData);
                }
            }
        }
        zipOut.close();
    }

    private static void zipFile(
            File resFile, ZipOutputStream zipout, String rootpath, HashMap<String, Integer> compressData) throws IOException {
        rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator) + resFile.getName();
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            for (File file : fileList) {
                zipFile(file, zipout, rootpath, compressData);
            }
        } else {
            final byte[] fileContents = readContents(resFile);
            //这里需要强转成linux格式，果然坑！！
            if (rootpath.contains("\\")) {
                rootpath = rootpath.replace("\\", "/");
            }
            if (!compressData.containsKey(rootpath)) {
                System.err.printf(String.format("do not have the compress data path =%s in resource.asrc\n", rootpath));
                return;
            }
            int compressMethod = compressData.get(rootpath);
            ZipEntry entry = new ZipEntry(rootpath);

            if (compressMethod == ZipEntry.DEFLATED) {
                entry.setMethod(ZipEntry.DEFLATED);
            } else {
                entry.setMethod(ZipEntry.STORED);
                entry.setSize(fileContents.length);
                final CRC32 checksumCalculator = new CRC32();
                checksumCalculator.update(fileContents);
                entry.setCrc(checksumCalculator.getValue());
            }
            zipout.putNextEntry(entry);
            zipout.write(fileContents);
            zipout.flush();
            zipout.closeEntry();
        }
    }

    private static byte[] readContents(final File file) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final int bufferSize = 4096;
        try {
            final FileInputStream in = new FileInputStream(file);
            final BufferedInputStream bIn = new BufferedInputStream(in);
            int length;
            byte[] buffer = new byte[bufferSize];
            byte[] bufferCopy;
            while ((length = bIn.read(buffer, 0, bufferSize)) != -1) {
                bufferCopy = new byte[length];
                System.arraycopy(buffer, 0, bufferCopy, 0, length);
                output.write(bufferCopy);
            }
            bIn.close();
        } finally {
            output.close();
        }
        return output.toByteArray();
    }

    // ======== helper methods ========

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    private static byte[] concat(byte[] a, byte[] b, byte[] c) {
        byte[] d = new byte[a.length + b.length + c.length];
        System.arraycopy(a, 0, d, 0, a.length);
        System.arraycopy(b, 0, d, a.length, b.length);
        System.arraycopy(c, 0, d, a.length + b.length, c.length);
        return d;
    }

    private static byte[] intToBytesBigEndian(int value) {
        return new byte[]{
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    private static byte[] intToBytesLittleEndian(int value) {
        return new byte[]{
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 24) & 0xFF)
        };
    }
}
