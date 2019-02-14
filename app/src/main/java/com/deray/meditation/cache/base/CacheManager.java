package com.deray.meditation.cache.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.LruCache;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Chris on 2018/4/24.
 */

public class CacheManager {

    /**
     * 只使用内存缓存(LruCache)
     */
    public static final int ONLY_LRU = 1;
    /**
     * 只使用硬盘缓存(DiskLruCache)
     */
    public static final int ONLY_DISKLRU = 2;
    /**
     * 同时使用内存缓存(LruCache)与硬盘缓存(DiskLruCache)
     */
    public static final int ALL_ALLOW = 0;

    /**
     * 设置类型为硬盘缓存——用于取硬盘缓存大小
     */
    public static final int DISKSIZE = 0;
    /**
     * 设置类型为内存缓存——用于取内存缓存大小
     */
    public static final int MEMORYSIZE = 1;

    //设置硬盘缓存的最大值，单位为M
    private static int maxSizeForDiskLruCache = 0;
    //设置内存缓存的最大值，单位为M
    private static int maxMemoryForLruCache = 0;
    //设置自定义的硬盘缓存文件夹名称
    private static String dirNameForDiskLruCache = "";
    //记录硬盘缓存与内存缓存起效标志
    private static int model = 0;
    //硬盘缓存管理类
    private static DiskLruCacheManager diskLruCacheManager;
    //内存缓存管理类
    private static LruCacheManager lruCacheManager;
    private static Context ct;

    /**
     * 初始化缓存管理
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        ct = context;
        init_();
    }

    //根据传入的标志，初始化内存缓存以及硬盘缓存，默认开启是同时使用
    private static void init_() {
        switch (model) {
            case ALL_ALLOW:
                initDiskLruCacheManager();
                initLruCacheManager();
                break;
            case ONLY_LRU:
                initLruCacheManager();
                break;
            case ONLY_DISKLRU:
                initDiskLruCacheManager();
                break;
            default:
                break;
        }
    }

    //初始化内存缓存管理
    private static void initLruCacheManager() {
        if (maxMemoryForLruCache > 0) {
            lruCacheManager = new LruCacheManager(maxMemoryForLruCache);
        } else {
            lruCacheManager = new LruCacheManager();
        }
    }

    //初始化硬盘缓存管理
    private static void initDiskLruCacheManager() {
        if (maxSizeForDiskLruCache > 0 && !TextUtils.isEmpty(dirNameForDiskLruCache)) {
            diskLruCacheManager = new DiskLruCacheManager(ct, dirNameForDiskLruCache, maxSizeForDiskLruCache * 1024 * 1024);
        } else if (maxSizeForDiskLruCache > 0) {
            diskLruCacheManager = new DiskLruCacheManager(ct, maxSizeForDiskLruCache * 1024 * 1024);
        } else if (!TextUtils.isEmpty(dirNameForDiskLruCache)) {
            diskLruCacheManager = new DiskLruCacheManager(ct, dirNameForDiskLruCache);
        } else {
            diskLruCacheManager = new DiskLruCacheManager(ct);
        }
    }

    /**
     * 设置硬盘缓存的最大值，单位为兆（M）.
     *
     * @param maxSizeForDisk 硬盘缓存最大值，单位为兆（M）
     */
    public static void setMaxSize(int maxSizeForDisk) {
        maxSizeForDiskLruCache = maxSizeForDisk;
    }

    /**
     * 设置内存缓存的最大值，单位为兆（M）.
     *
     * @param maxMemory 内存缓存最大值，单位为兆（M）
     */
    public static void setMaxMemory(int maxMemory) {
        maxMemoryForLruCache = maxMemory;
    }

    /**
     * 设置硬盘缓存自定义的文件名
     *
     * @param dirName 自定义文件名
     */
    public static void setDirName(String dirName) {
        dirNameForDiskLruCache = dirName;
    }

    /**
     * 索引key对应的bitmap写入缓存
     *
     * @param key    缓存索引
     * @param bitmap bitmap格式数据
     */
    public static void put(String key, Bitmap bitmap) {
        switch (model) {
            case ALL_ALLOW:
                if (lruCacheManager != null && diskLruCacheManager != null) {
                    //设置硬盘缓存成功后，再设置内存缓存
                    if (diskLruCacheManager.putDiskCache(key, bitmap)) {
                        lruCacheManager.putCache(key, bitmap);
                    }
                }
                break;
            case ONLY_LRU:
                if (lruCacheManager != null) {
                    lruCacheManager.putCache(key, bitmap);
                }
                break;
            case ONLY_DISKLRU:
                if (diskLruCacheManager != null) {
                    diskLruCacheManager.putDiskCache(key, bitmap);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取索引key对应的缓存内容
     *
     * @param key 缓存索引key
     * @return key索引对应的Bitmap数据
     */
    public static Bitmap get(String key) {
        Bitmap bitmap = null;
        switch (model) {
            case ALL_ALLOW:
                if (lruCacheManager != null && diskLruCacheManager != null) {
                    bitmap = lruCacheManager.getCache(key);
                    if (bitmap == null) {
                        //如果硬盘缓存内容存在，内存缓存不存在。则在获取硬盘缓存后，将内容写入内存缓存
                        bitmap = diskLruCacheManager.getDiskCache(key);
                        lruCacheManager.putCache(key, bitmap);
                    }
                }
                break;
            case ONLY_LRU:
                if (lruCacheManager != null) {
                    bitmap = lruCacheManager.getCache(key);
                }
                break;
            case ONLY_DISKLRU:
                if (diskLruCacheManager != null) {
                    bitmap = diskLruCacheManager.getDiskCache(key);
                }
                break;

            default:
                break;
        }
        return bitmap;
    }

    /**
     * 删除所有缓存
     */
    public static void delete() {
        switch (model) {
            case ALL_ALLOW:
                if (lruCacheManager != null && diskLruCacheManager != null) {
                    lruCacheManager.deleteCache();
                    diskLruCacheManager.deleteDiskCache();
                }
                break;
            case ONLY_LRU:
                if (lruCacheManager != null) {
                    lruCacheManager.deleteCache();
                }
                break;
            case ONLY_DISKLRU:
                if (diskLruCacheManager != null) {
                    diskLruCacheManager.deleteDiskCache();
                }
                break;

            default:
                break;
        }
    }

    /**
     * 移除一条索引key对应的缓存
     *
     * @param key 索引
     */
    public static void remove(String key) {
        switch (model) {
            case ALL_ALLOW:
                if (lruCacheManager != null && diskLruCacheManager != null) {
                    lruCacheManager.removeCache(key);
                    diskLruCacheManager.removeDiskCache(key);
                }
                break;
            case ONLY_LRU:
                if (lruCacheManager != null) {
                    lruCacheManager.removeCache(key);
                }
                break;
            case ONLY_DISKLRU:
                if (diskLruCacheManager != null) {
                    diskLruCacheManager.removeDiskCache(key);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 缓存数据同步
     */
    public static void flush() {
        switch (model) {
            case ALL_ALLOW:
                if (lruCacheManager != null && diskLruCacheManager != null) {
                    diskLruCacheManager.fluchCache();
                }
                break;
            case ONLY_LRU:
                break;
            case ONLY_DISKLRU:
                if (diskLruCacheManager != null) {
                    diskLruCacheManager.fluchCache();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置缓存模式
     *
     * @param modelSet ONLY_LRU、ONLY_DISK、ALL_ALLOW
     */
    public static void setCacheModel(int modelSet) {
        model = modelSet;
    }

    /**
     * 删除特定文件名的缓存文件
     *
     * @param dirName 文件名
     */
    public static void deleteFile(String dirName) {
        if (diskLruCacheManager != null) {
            diskLruCacheManager.deleteFile(ct, dirName);
        }
    }

    /**
     * 获取缓存大小——内存缓存+硬盘缓存
     *
     * @return
     */
    public static int size() {
        int size = 0;
        if (diskLruCacheManager != null) {
            size += diskLruCacheManager.size();
        }
        if (lruCacheManager != null) {
            size += lruCacheManager.size();
        }
        return size;
    }


    /**
     * 获取缓存大小
     *
     * @param type 硬盘缓存类型：DISKSIZE、内存缓存类型：MEMORYSIZE
     * @return 对应类型的缓存大小
     */
    public static int size(int type) {
        int size = 0;
        switch (type) {
            case DISKSIZE:
                if (diskLruCacheManager != null) {
                    size += diskLruCacheManager.size();
                }
                break;
            case MEMORYSIZE:
                if (lruCacheManager != null) {
                    size += lruCacheManager.size();
                }
                break;

            default:
                break;
        }
        return size;
    }

    /**
     * 关闭缓存
     */
    public static void close() {
        if (diskLruCacheManager != null) {
            diskLruCacheManager.close();
        }
    }
}

class DiskLruCacheManager {

    private static int maxSize = 100 * 1024 * 1024;
    private DiskLruCache mDiskLruCache;
    private final static String mImageCacheName = "ImageCache";


    public DiskLruCacheManager(Context context) {
        this(context, mImageCacheName, maxSize);
    }

    public DiskLruCacheManager(Context context, int maxDiskLruCacheSize) {
        this(context, mImageCacheName, maxDiskLruCacheSize);
    }

    public DiskLruCacheManager(Context context, String dirName) {
        this(context, dirName, maxSize);
    }

    public DiskLruCacheManager(Context context, String dirName, int maxDiskLruCacheSize) {
        try {
            mDiskLruCache = DiskLruCache.open(getDiskCacheFile(context, dirName), getAppVersion(context), 1, maxDiskLruCacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件夹地址，如果不存在，则创建
     *
     * @param context 上下文
     * @param dirName 文件名
     * @return File 文件
     */
    private File getDiskCacheFile(Context context, String dirName) {
        File cacheDir = packDiskCacheFile(context, dirName);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    /**
     * 获取文件夹地址
     *
     * @param context 上下文
     * @param dirName 文件名
     * @return File 文件
     */
    private File packDiskCacheFile(Context context, String dirName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + dirName);
    }

    /**
     * 获取当前应用程序的版本号。
     */
    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 使用MD5算法对传入的key进行加密并返回。
     */
    private String Md5(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Bitmap格式数据写入到outputstream中
     *
     * @param bm   Bitmap数据
     * @param baos outputstream
     * @return outputstream
     */
    private OutputStream Bitmap2OutputStream(Bitmap bm, OutputStream baos) {
        if (bm != null) {
            bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        }
        return baos;
    }

    /**
     * 将缓存记录同步到journal文件中。
     */
    public void fluchCache() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取硬盘缓存
     *
     * @param key 所有
     * @return Bitmap格式缓存
     */
    public Bitmap getDiskCache(String key) {
        String md5Key = Md5(key);
        Bitmap bitmap = null;
        try {
            if (mDiskLruCache != null) {
                DiskLruCache.Snapshot snapshot = mDiskLruCache.get(md5Key);
                if (snapshot != null) {
                    bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 设置key对应的缓存
     *
     * @param key    索引
     * @param bitmap Bitmap格式数据
     * @return 是否写入
     */
    public boolean putDiskCache(String key, Bitmap bitmap) {
        String md5Key = Md5(key);
        try {
            if (mDiskLruCache != null) {
                if (mDiskLruCache.get(md5Key) != null) {
                    return true;
                }
                DiskLruCache.Editor editor = mDiskLruCache.edit(md5Key);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    Bitmap2OutputStream(bitmap, outputStream);
                    if (outputStream != null) {
                        editor.commit();
                        return true;
                    } else {
                        editor.abort();
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void deleteDiskCache() {
        try {
            if (mDiskLruCache != null) {
                mDiskLruCache.delete();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void removeDiskCache(String key) {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.remove(key);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void deleteFile(Context context, String dirName) {
        try {
            DiskLruCache.deleteContents(packDiskCacheFile(context, dirName));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int size() {
        int size = 0;
        if (mDiskLruCache != null) {
            size = (int) mDiskLruCache.size();
        }
        return size;
    }

    public void close() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}

class LruCacheManager {

    private LruCache<String, Bitmap> lruCache;

    public LruCacheManager() {
        this((int) Runtime.getRuntime().maxMemory() / 1024 / 8);
    }

    //设置自定义大小的LruCache
    public LruCacheManager(int maxSize) {
        new LruCache<String, Bitmap>(maxSize * 1024);

        lruCache = new LruCache<String, Bitmap>(maxSize * 1024) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }

        };
    }

    /**
     * 写入索引key对应的缓存
     *
     * @param key    索引
     * @param bitmap 缓存内容
     * @return 写入结果
     */
    public Bitmap putCache(String key, Bitmap bitmap) {
        Bitmap bitmapValue = getCache(key);
        if (bitmapValue == null) {
            if (lruCache != null && bitmap != null)
                bitmapValue = lruCache.put(key, bitmap);
        }
        return bitmapValue;
    }

    /**
     * 获取缓存
     *
     * @param key 索引key对应的缓存
     * @return 缓存
     */
    public Bitmap getCache(String key) {
        if (lruCache != null) {
            return lruCache.get(key);
        }
        return null;
    }

    public void deleteCache() {
        if (lruCache != null)
            lruCache.evictAll();
    }

    public void removeCache(String key) {
        if (lruCache != null)
            lruCache.remove(key);
    }

    public int size() {
        int size = 0;
        if (lruCache != null)
            size += lruCache.size();
        return size;
    }
}

final class DiskLruCache implements Closeable {
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_TMP = "journal.tmp";
    static final String MAGIC = "libcore.io.DiskLruCache";
    static final String VERSION_1 = "1";
    static final long ANY_SEQUENCE_NUMBER = -1;
    private static final String CLEAN = "CLEAN";
    private static final String DIRTY = "DIRTY";
    private static final String REMOVE = "REMOVE";
    private static final String READ = "READ";

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    /*
     * This cache uses a journal file named "journal". A typical journal file
     * looks like this:
     *     libcore.io.DiskLruCache
     *     1
     *     100
     *     2
     *
     *     CLEAN 3400330d1dfc7f3f7f4b8d4d803dfcf6 832 21054
     *     DIRTY 335c4c6028171cfddfbaae1a9c313c52
     *     CLEAN 335c4c6028171cfddfbaae1a9c313c52 3934 2342
     *     REMOVE 335c4c6028171cfddfbaae1a9c313c52
     *     DIRTY 1ab96a171faeeee38496d8b330771a7a
     *     CLEAN 1ab96a171faeeee38496d8b330771a7a 1600 234
     *     READ 335c4c6028171cfddfbaae1a9c313c52
     *     READ 3400330d1dfc7f3f7f4b8d4d803dfcf6
     *
     * The first five lines of the journal form its header. They are the
     * constant string "libcore.io.DiskLruCache", the disk cache's version,
     * the application's version, the value count, and a blank line.
     *
     * Each of the subsequent lines in the file is a record of the state of a
     * cache entry. Each line contains space-separated values: a state, a key,
     * and optional state-specific values.
     *   o DIRTY lines track that an entry is actively being created or updated.
     *     Every successful DIRTY action should be followed by a CLEAN or REMOVE
     *     action. DIRTY lines without a matching CLEAN or REMOVE indicate that
     *     temporary files may need to be deleted.
     *   o CLEAN lines track a cache entry that has been successfully published
     *     and may be read. A publish line is followed by the lengths of each of
     *     its values.
     *   o READ lines track accesses for LRU.
     *   o REMOVE lines track entries that have been deleted.
     *
     * The journal file is appended to as cache operations occur. The journal may
     * occasionally be compacted by dropping redundant lines. A temporary file named
     * "journal.tmp" will be used during compaction; that file should be deleted if
     * it exists when the cache is opened.
     */

    private final File directory;
    private final File journalFile;
    private final File journalFileTmp;
    private final int appVersion;
    private final long maxSize;
    private final int valueCount;
    private long size = 0;
    private Writer journalWriter;
    private final LinkedHashMap<String, Entry> lruEntries
            = new LinkedHashMap<String, Entry>(0, 0.75f, true);
    private int redundantOpCount;

    /**
     * To differentiate between old and current snapshots, each entry is given
     * a sequence number each time an edit is committed. A snapshot is stale if
     * its sequence number is not equal to its entry's sequence number.
     */
    private long nextSequenceNumber = 0;

    /* From java.util.Arrays */
    @SuppressWarnings("unchecked")
    private static <T> T[] copyOfRange(T[] original, int start, int end) {
        final int originalLength = original.length; // For exception priority compatibility.
        if (start > end) {
            throw new IllegalArgumentException();
        }
        if (start < 0 || start > originalLength) {
            throw new ArrayIndexOutOfBoundsException();
        }
        final int resultLength = end - start;
        final int copyLength = Math.min(resultLength, originalLength - start);
        final T[] result = (T[]) Array
                .newInstance(original.getClass().getComponentType(), resultLength);
        System.arraycopy(original, start, result, 0, copyLength);
        return result;
    }

    /**
     * Returns the remainder of 'reader' as a string, closing it when done.
     */
    public static String readFully(Reader reader) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[1024];
            int count;
            while ((count = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, count);
            }
            return writer.toString();
        } finally {
            reader.close();
        }
    }

    /**
     * Returns the ASCII characters up to but not including the next "\r\n", or
     * "\n".
     *
     * @throws EOFException if the stream is exhausted before the next newline
     *                      character.
     */
    public static String readAsciiLine(InputStream in) throws IOException {
        // TODO: support UTF-8 here instead

        StringBuilder result = new StringBuilder(80);
        while (true) {
            int c = in.read();
            if (c == -1) {
                throw new EOFException();
            } else if (c == '\n') {
                break;
            }

            result.append((char) c);
        }
        int length = result.length();
        if (length > 0 && result.charAt(length - 1) == '\r') {
            result.setLength(length - 1);
        }
        return result.toString();
    }

    /**
     * Closes 'closeable', ignoring any checked exceptions. Does nothing if 'closeable' is null.
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Recursively delete everything in {@code dir}.
     */
    // TODO: this should specify paths as Strings rather than as Files
    public static void deleteContents(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IllegalArgumentException("not a directory: " + dir);
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deleteContents(file);
            }
            if (!file.delete()) {
                throw new IOException("failed to delete file: " + file);
            }
        }
    }

    /**
     * This cache uses a single background thread to evict entries.
     */
    private final ExecutorService executorService = new ThreadPoolExecutor(0, 1,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private final Callable<Void> cleanupCallable = new Callable<Void>() {
        @Override
        public Void call() throws Exception {
            synchronized (DiskLruCache.this) {
                if (journalWriter == null) {
                    return null; // closed
                }
                trimToSize();
                if (journalRebuildRequired()) {
                    rebuildJournal();
                    redundantOpCount = 0;
                }
            }
            return null;
        }
    };

    private DiskLruCache(File directory, int appVersion, int valueCount, long maxSize) {
        this.directory = directory;
        this.appVersion = appVersion;
        this.journalFile = new File(directory, JOURNAL_FILE);
        this.journalFileTmp = new File(directory, JOURNAL_FILE_TMP);
        this.valueCount = valueCount;
        this.maxSize = maxSize;
    }

    /**
     * Opens the cache in {@code directory}, creating a cache if none exists
     * there.
     *
     * @param directory  a writable directory
     * @param appVersion
     * @param valueCount the number of values per cache entry. Must be positive.
     * @param maxSize    the maximum number of bytes this cache should use to store
     * @throws IOException if reading or writing the cache directory fails
     */
    public static DiskLruCache open(File directory, int appVersion, int valueCount, long maxSize)
            throws IOException {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        if (valueCount <= 0) {
            throw new IllegalArgumentException("valueCount <= 0");
        }

        // prefer to pick up where we left off
        DiskLruCache cache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
        if (cache.journalFile.exists()) {
            try {
                cache.readJournal();
                cache.processJournal();
                cache.journalWriter = new BufferedWriter(new FileWriter(cache.journalFile, true),
                        IO_BUFFER_SIZE);
                return cache;
            } catch (IOException journalIsCorrupt) {
//                System.logW("DiskLruCache " + directory + " is corrupt: "
//                        + journalIsCorrupt.getMessage() + ", removing");
                cache.delete();
            }
        }

        // create a new empty cache
        directory.mkdirs();
        cache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
        cache.rebuildJournal();
        return cache;
    }

    private void readJournal() throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(journalFile), IO_BUFFER_SIZE);
        try {
            String magic = readAsciiLine(in);
            String version = readAsciiLine(in);
            String appVersionString = readAsciiLine(in);
            String valueCountString = readAsciiLine(in);
            String blank = readAsciiLine(in);
            if (!MAGIC.equals(magic)
                    || !VERSION_1.equals(version)
                    || !Integer.toString(appVersion).equals(appVersionString)
                    || !Integer.toString(valueCount).equals(valueCountString)
                    || !"".equals(blank)) {
                throw new IOException("unexpected journal header: ["
                        + magic + ", " + version + ", " + valueCountString + ", " + blank + "]");
            }

            while (true) {
                try {
                    readJournalLine(readAsciiLine(in));
                } catch (EOFException endOfJournal) {
                    break;
                }
            }
        } finally {
            closeQuietly(in);
        }
    }

    private void readJournalLine(String line) throws IOException {
        String[] parts = line.split(" ");
        if (parts.length < 2) {
            throw new IOException("unexpected journal line: " + line);
        }

        String key = parts[1];
        if (parts[0].equals(REMOVE) && parts.length == 2) {
            lruEntries.remove(key);
            return;
        }

        Entry entry = lruEntries.get(key);
        if (entry == null) {
            entry = new Entry(key);
            lruEntries.put(key, entry);
        }

        if (parts[0].equals(CLEAN) && parts.length == 2 + valueCount) {
            entry.readable = true;
            entry.currentEditor = null;
            entry.setLengths(copyOfRange(parts, 2, parts.length));
        } else if (parts[0].equals(DIRTY) && parts.length == 2) {
            entry.currentEditor = new Editor(entry);
        } else if (parts[0].equals(READ) && parts.length == 2) {
            // this work was already done by calling lruEntries.get()
        } else {
            throw new IOException("unexpected journal line: " + line);
        }
    }

    /**
     * Computes the initial size and collects garbage as a part of opening the
     * cache. Dirty entries are assumed to be inconsistent and will be deleted.
     */
    private void processJournal() throws IOException {
        deleteIfExists(journalFileTmp);
        for (Iterator<Entry> i = lruEntries.values().iterator(); i.hasNext(); ) {
            Entry entry = i.next();
            if (entry.currentEditor == null) {
                for (int t = 0; t < valueCount; t++) {
                    size += entry.lengths[t];
                }
            } else {
                entry.currentEditor = null;
                for (int t = 0; t < valueCount; t++) {
                    deleteIfExists(entry.getCleanFile(t));
                    deleteIfExists(entry.getDirtyFile(t));
                }
                i.remove();
            }
        }
    }

    /**
     * Creates a new journal that omits redundant information. This replaces the
     * current journal if it exists.
     */
    private synchronized void rebuildJournal() throws IOException {
        if (journalWriter != null) {
            journalWriter.close();
        }

        Writer writer = new BufferedWriter(new FileWriter(journalFileTmp), IO_BUFFER_SIZE);
        writer.write(MAGIC);
        writer.write("\n");
        writer.write(VERSION_1);
        writer.write("\n");
        writer.write(Integer.toString(appVersion));
        writer.write("\n");
        writer.write(Integer.toString(valueCount));
        writer.write("\n");
        writer.write("\n");

        for (Entry entry : lruEntries.values()) {
            if (entry.currentEditor != null) {
                writer.write(DIRTY + ' ' + entry.key + '\n');
            } else {
                writer.write(CLEAN + ' ' + entry.key + entry.getLengths() + '\n');
            }
        }

        writer.close();
        journalFileTmp.renameTo(journalFile);
        journalWriter = new BufferedWriter(new FileWriter(journalFile, true), IO_BUFFER_SIZE);
    }

    private static void deleteIfExists(File file) throws IOException {
//        try {
//            Libcore.os.remove(file.getPath());
//        } catch (ErrnoException errnoException) {
//            if (errnoException.errno != OsConstants.ENOENT) {
//                throw errnoException.rethrowAsIOException();
//            }
//        }
        if (file.exists() && !file.delete()) {
            throw new IOException();
        }
    }

    /**
     * Returns a snapshot of the entry named {@code key}, or null if it doesn't
     * exist is not currently readable. If a value is returned, it is moved to
     * the head of the LRU queue.
     */
    public synchronized Snapshot get(String key) throws IOException {
        checkNotClosed();
        validateKey(key);
        Entry entry = lruEntries.get(key);
        if (entry == null) {
            return null;
        }

        if (!entry.readable) {
            return null;
        }

        /*
         * Open all streams eagerly to guarantee that we see a single published
         * snapshot. If we opened streams lazily then the streams could come
         * from different edits.
         */
        InputStream[] ins = new InputStream[valueCount];
        try {
            for (int i = 0; i < valueCount; i++) {
                ins[i] = new FileInputStream(entry.getCleanFile(i));
            }
        } catch (FileNotFoundException e) {
            // a file must have been deleted manually!
            return null;
        }

        redundantOpCount++;
        journalWriter.append(READ + ' ' + key + '\n');
        if (journalRebuildRequired()) {
            executorService.submit(cleanupCallable);
        }

        return new Snapshot(key, entry.sequenceNumber, ins);
    }

    /**
     * Returns an editor for the entry named {@code key}, or null if another
     * edit is in progress.
     */
    public Editor edit(String key) throws IOException {
        return edit(key, ANY_SEQUENCE_NUMBER);
    }

    private synchronized Editor edit(String key, long expectedSequenceNumber) throws IOException {
        checkNotClosed();
        validateKey(key);
        Entry entry = lruEntries.get(key);
        if (expectedSequenceNumber != ANY_SEQUENCE_NUMBER
                && (entry == null || entry.sequenceNumber != expectedSequenceNumber)) {
            return null; // snapshot is stale
        }
        if (entry == null) {
            entry = new Entry(key);
            lruEntries.put(key, entry);
        } else if (entry.currentEditor != null) {
            return null; // another edit is in progress
        }

        Editor editor = new Editor(entry);
        entry.currentEditor = editor;

        // flush the journal before creating files to prevent file leaks
        journalWriter.write(DIRTY + ' ' + key + '\n');
        journalWriter.flush();
        return editor;
    }

    /**
     * Returns the directory where this cache stores its data.
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * Returns the maximum number of bytes that this cache should use to store
     * its data.
     */
    public long maxSize() {
        return maxSize;
    }

    /**
     * Returns the number of bytes currently being used to store the values in
     * this cache. This may be greater than the max size if a background
     * deletion is pending.
     */
    public synchronized long size() {
        return size;
    }

    private synchronized void completeEdit(Editor editor, boolean success) throws IOException {
        Entry entry = editor.entry;
        if (entry.currentEditor != editor) {
            throw new IllegalStateException();
        }

        // if this edit is creating the entry for the first time, every index must have a value
        if (success && !entry.readable) {
            for (int i = 0; i < valueCount; i++) {
                if (!entry.getDirtyFile(i).exists()) {
                    editor.abort();
                    throw new IllegalStateException("edit didn't create file " + i);
                }
            }
        }

        for (int i = 0; i < valueCount; i++) {
            File dirty = entry.getDirtyFile(i);
            if (success) {
                if (dirty.exists()) {
                    File clean = entry.getCleanFile(i);
                    dirty.renameTo(clean);
                    long oldLength = entry.lengths[i];
                    long newLength = clean.length();
                    entry.lengths[i] = newLength;
                    size = size - oldLength + newLength;
                }
            } else {
                deleteIfExists(dirty);
            }
        }

        redundantOpCount++;
        entry.currentEditor = null;
        if (entry.readable | success) {
            entry.readable = true;
            journalWriter.write(CLEAN + ' ' + entry.key + entry.getLengths() + '\n');
            if (success) {
                entry.sequenceNumber = nextSequenceNumber++;
            }
        } else {
            lruEntries.remove(entry.key);
            journalWriter.write(REMOVE + ' ' + entry.key + '\n');
        }

        if (size > maxSize || journalRebuildRequired()) {
            executorService.submit(cleanupCallable);
        }
    }

    /**
     * We only rebuild the journal when it will halve the size of the journal
     * and eliminate at least 2000 ops.
     */
    private boolean journalRebuildRequired() {
        final int REDUNDANT_OP_COMPACT_THRESHOLD = 2000;
        return redundantOpCount >= REDUNDANT_OP_COMPACT_THRESHOLD
                && redundantOpCount >= lruEntries.size();
    }

    /**
     * Drops the entry for {@code key} if it exists and can be removed. Entries
     * actively being edited cannot be removed.
     *
     * @return true if an entry was removed.
     */
    public synchronized boolean remove(String key) throws IOException {
        checkNotClosed();
        validateKey(key);
        Entry entry = lruEntries.get(key);
        if (entry == null || entry.currentEditor != null) {
            return false;
        }

        for (int i = 0; i < valueCount; i++) {
            File file = entry.getCleanFile(i);
            if (!file.delete()) {
                throw new IOException("failed to delete " + file);
            }
            size -= entry.lengths[i];
            entry.lengths[i] = 0;
        }

        redundantOpCount++;
        journalWriter.append(REMOVE + ' ' + key + '\n');
        lruEntries.remove(key);

        if (journalRebuildRequired()) {
            executorService.submit(cleanupCallable);
        }

        return true;
    }

    /**
     * Returns true if this cache has been closed.
     */
    public boolean isClosed() {
        return journalWriter == null;
    }

    private void checkNotClosed() {
        if (journalWriter == null) {
            throw new IllegalStateException("cache is closed");
        }
    }

    /**
     * Force buffered operations to the filesystem.
     */
    public synchronized void flush() throws IOException {
        checkNotClosed();
        trimToSize();
        journalWriter.flush();
    }

    /**
     * Closes this cache. Stored values will remain on the filesystem.
     */
    public synchronized void close() throws IOException {
        if (journalWriter == null) {
            return; // already closed
        }
        for (Entry entry : new ArrayList<Entry>(lruEntries.values())) {
            if (entry.currentEditor != null) {
                entry.currentEditor.abort();
            }
        }
        trimToSize();
        journalWriter.close();
        journalWriter = null;
    }

    private void trimToSize() throws IOException {
        while (size > maxSize) {
//            Map.Entry<String, Entry> toEvict = lruEntries.eldest();
            final Map.Entry<String, Entry> toEvict = lruEntries.entrySet().iterator().next();
            remove(toEvict.getKey());
        }
    }

    /**
     * Closes the cache and deletes all of its stored values. This will delete
     * all files in the cache directory including files that weren't created by
     * the cache.
     */
    public void delete() throws IOException {
        close();
        deleteContents(directory);
    }

    private void validateKey(String key) {
        if (key.contains(" ") || key.contains("\n") || key.contains("\r")) {
            throw new IllegalArgumentException(
                    "keys must not contain spaces or newlines: \"" + key + "\"");
        }
    }

    private static String inputStreamToString(InputStream in) throws IOException {
        return readFully(new InputStreamReader(in, UTF_8));
    }

    /**
     * A snapshot of the values for an entry.
     */
    public final class Snapshot implements Closeable {
        private final String key;
        private final long sequenceNumber;
        private final InputStream[] ins;

        private Snapshot(String key, long sequenceNumber, InputStream[] ins) {
            this.key = key;
            this.sequenceNumber = sequenceNumber;
            this.ins = ins;
        }

        /**
         * Returns an editor for this snapshot's entry, or null if either the
         * entry has changed since this snapshot was created or if another edit
         * is in progress.
         */
        public Editor edit() throws IOException {
            return DiskLruCache.this.edit(key, sequenceNumber);
        }

        /**
         * Returns the unbuffered stream with the value for {@code index}.
         */
        public InputStream getInputStream(int index) {
            return ins[index];
        }

        /**
         * Returns the string value for {@code index}.
         */
        public String getString(int index) throws IOException {
            return inputStreamToString(getInputStream(index));
        }

        @Override
        public void close() {
            for (InputStream in : ins) {
                closeQuietly(in);
            }
        }
    }

    /**
     * Edits the values for an entry.
     */
    public final class Editor {
        private final Entry entry;
        private boolean hasErrors;

        private Editor(Entry entry) {
            this.entry = entry;
        }

        /**
         * Returns an unbuffered input stream to read the last committed value,
         * or null if no value has been committed.
         */
        public InputStream newInputStream(int index) throws IOException {
            synchronized (DiskLruCache.this) {
                if (entry.currentEditor != this) {
                    throw new IllegalStateException();
                }
                if (!entry.readable) {
                    return null;
                }
                return new FileInputStream(entry.getCleanFile(index));
            }
        }

        /**
         * Returns the last committed value as a string, or null if no value
         * has been committed.
         */
        public String getString(int index) throws IOException {
            InputStream in = newInputStream(index);
            return in != null ? inputStreamToString(in) : null;
        }

        /**
         * Returns a new unbuffered output stream to write the value at
         * {@code index}. If the underlying output stream encounters errors
         * when writing to the filesystem, this edit will be aborted when
         * {@link #commit} is called. The returned output stream does not throw
         * IOExceptions.
         */
        public OutputStream newOutputStream(int index) throws IOException {
            synchronized (DiskLruCache.this) {
                if (entry.currentEditor != this) {
                    throw new IllegalStateException();
                }
                return new FaultHidingOutputStream(new FileOutputStream(entry.getDirtyFile(index)));
            }
        }

        /**
         * Sets the value at {@code index} to {@code value}.
         */
        public void set(int index, String value) throws IOException {
            Writer writer = null;
            try {
                writer = new OutputStreamWriter(newOutputStream(index), UTF_8);
                writer.write(value);
            } finally {
                closeQuietly(writer);
            }
        }

        /**
         * Commits this edit so it is visible to readers.  This releases the
         * edit lock so another edit may be started on the same key.
         */
        public void commit() throws IOException {
            if (hasErrors) {
                completeEdit(this, false);
                remove(entry.key); // the previous entry is stale
            } else {
                completeEdit(this, true);
            }
        }

        /**
         * Aborts this edit. This releases the edit lock so another edit may be
         * started on the same key.
         */
        public void abort() throws IOException {
            completeEdit(this, false);
        }

        private class FaultHidingOutputStream extends FilterOutputStream {
            private FaultHidingOutputStream(OutputStream out) {
                super(out);
            }

            @Override
            public void write(int oneByte) {
                try {
                    out.write(oneByte);
                } catch (IOException e) {
                    hasErrors = true;
                }
            }

            @Override
            public void write(byte[] buffer, int offset, int length) {
                try {
                    out.write(buffer, offset, length);
                } catch (IOException e) {
                    hasErrors = true;
                }
            }

            @Override
            public void close() {
                try {
                    out.close();
                } catch (IOException e) {
                    hasErrors = true;
                }
            }

            @Override
            public void flush() {
                try {
                    out.flush();
                } catch (IOException e) {
                    hasErrors = true;
                }
            }
        }
    }

    private final class Entry {
        private final String key;

        /**
         * Lengths of this entry's files.
         */
        private final long[] lengths;

        /**
         * True if this entry has ever been published
         */
        private boolean readable;

        /**
         * The ongoing edit or null if this entry is not being edited.
         */
        private Editor currentEditor;

        /**
         * The sequence number of the most recently committed edit to this entry.
         */
        private long sequenceNumber;

        private Entry(String key) {
            this.key = key;
            this.lengths = new long[valueCount];
        }

        public String getLengths() throws IOException {
            StringBuilder result = new StringBuilder();
            for (long size : lengths) {
                result.append(' ').append(size);
            }
            return result.toString();
        }

        /**
         * Set lengths using decimal numbers like "10123".
         */
        private void setLengths(String[] strings) throws IOException {
            if (strings.length != valueCount) {
                throw invalidLengths(strings);
            }

            try {
                for (int i = 0; i < strings.length; i++) {
                    lengths[i] = Long.parseLong(strings[i]);
                }
            } catch (NumberFormatException e) {
                throw invalidLengths(strings);
            }
        }

        private IOException invalidLengths(String[] strings) throws IOException {
            throw new IOException("unexpected journal line: " + Arrays.toString(strings));
        }

        public File getCleanFile(int i) {
            return new File(directory, key + "." + i);
        }

        public File getDirtyFile(int i) {
            return new File(directory, key + "." + i + ".tmp");
        }
    }
}
