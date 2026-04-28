/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

package jp.apple.fix.model;

import com.anatawa12.fixRtm.asm.config.MainConfig;
import com.anatawa12.fixRtm.io.FIXFileLoader;
import com.anatawa12.fixRtm.io.FIXModelPack;
import com.anatawa12.fixRtm.io.FIXResource;
import com.anatawa12.fixRtm.ngtlib.renderer.model.CachedPolygonModel;
import com.anatawa12.fixRtm.utils.DigestUtils;
import com.anatawa12.fixRtm.UtilsKt;
import jp.apple.config.AppleConfig;
import jp.ngt.ngtlib.io.FileType;
import jp.ngt.ngtlib.renderer.model.IModelNGT;
import jp.ngt.ngtlib.renderer.model.Material;
import jp.ngt.ngtlib.renderer.model.ModelFormatException;
import jp.ngt.ngtlib.renderer.model.ModelLoader;
import jp.ngt.ngtlib.renderer.model.PolygonModel;
import jp.ngt.ngtlib.renderer.model.VecAccuracy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@SideOnly(Side.CLIENT)
final class CachedModelManager {
    private static final Logger LOGGER = LogManager.getLogger("AppleCachedModel");
    private static final CachedModelManager INSTANCE = new CachedModelManager();

    static CachedModelManager getInstance() {
        return INSTANCE;
    }

    private final ExecutorService loaderExecutor;
    private final Set<AsyncCachedModel> trackedModels =
            Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<AsyncCachedModel, Boolean>()));

    private final LinkedHashMap<AsyncCachedModel, LruEntry> lru = new LinkedHashMap<>(16, 0.75F, true);
    private long totalWeight;

    private CachedModelManager() {
        final AtomicInteger threadId = new AtomicInteger();
        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable, "apple-cached-model-loader-" + threadId.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };

        this.loaderExecutor = Executors.newFixedThreadPool(
                Math.max(Runtime.getRuntime().availableProcessors() / 2, 1),
                threadFactory
        );
    }

    PolygonModel loadModel(ResourceLocation resource, VecAccuracy accuracy, Object... args) {
        if (!MainConfig.cachedPolygonModel) {
            return ModelLoader.loadModel__NGTLIB(resource, accuracy, args);
        }

        String fileName = resource.toString();
        try {
            FIXModelPack pack = this.resolvePack(resource);
            PolygonModel cached = CachedPolygonModel.INSTANCE.getCachedModel(pack, resource, accuracy);
            if (cached != null) {
                return this.createCachedModel(pack, resource, accuracy, args, cached);
            }

            InputStream[] streams = this.inputStreams(resource);
            PolygonModel model = ModelLoader.loadModel(streams, fileName, accuracy, args);
            if (model == null) {
                return null;
            }

            CachedPolygonModel.INSTANCE.putCachedModel(pack, resource, accuracy, model);
            return this.createCachedModel(pack, resource, accuracy, args, model);
        } catch (IOException e) {
            throw new ModelFormatException("Failed to load model : " + fileName, e);
        }
    }

    void compactModel(IModelNGT model) {
        if (model instanceof AsyncCachedModel) {
            ((AsyncCachedModel) model).compactLoadedModel();
        }
    }

    boolean prepareModel(IModelNGT model) {
        if (model instanceof AsyncCachedModel) {
            return ((AsyncCachedModel) model).prepare(false);
        }
        return true;
    }

    boolean prepareModelSync(IModelNGT model) {
        if (model instanceof AsyncCachedModel) {
            return ((AsyncCachedModel) model).prepare(true);
        }
        return true;
    }

    List<String> getDebugLines() {
        ModelStateSnapshot modelSnapshot = this.snapshotModels();
        LruSnapshot lruSnapshot = this.snapshotLru();

        return Arrays.asList(
                "",
                "AppleExtended/cached-model",
                String.format(
                        Locale.ROOT,
                        " mem: %.1f/%.1f MiB, loaded=%d, protected=%d, sec=%d",
                        bytesToMiB(lruSnapshot.totalWeight),
                        bytesToMiB(lruSnapshot.maxWeight),
                        lruSnapshot.loadedCount,
                        lruSnapshot.protectedCount,
                        configuredProtectionSeconds()
                ),
                String.format(
                        Locale.ROOT,
                        " state: A=%d, U=%d, Q=%d, L=%d, R=%d, F=%d",
                        modelSnapshot.total,
                        modelSnapshot.unloaded,
                        modelSnapshot.queued,
                        modelSnapshot.loading,
                        modelSnapshot.ready,
                        modelSnapshot.failed
                ),
                ""
        );
    }

    private ModelStateSnapshot snapshotModels() {
        List<AsyncCachedModel> models;
        synchronized (this.trackedModels) {
            models = new ArrayList<>(this.trackedModels);
        }

        int unloaded = 0;
        int queued = 0;
        int loading = 0;
        int ready = 0;
        int failed = 0;

        for (AsyncCachedModel model : models) {
            if (model == null) {
                continue;
            }

            switch (model.currentState()) {
                case UNLOADED:
                    ++unloaded;
                    break;
                case QUEUED:
                    ++queued;
                    break;
                case LOADING:
                    ++loading;
                    break;
                case READY:
                    ++ready;
                    break;
                case FAILED:
                    ++failed;
                    break;
                default:
                    break;
            }
        }

        return new ModelStateSnapshot(models.size(), unloaded, queued, loading, ready, failed);
    }

    private LruSnapshot snapshotLru() {
        synchronized (this.lru) {
            long now = System.currentTimeMillis();
            long protectionMillis = configuredProtectionMillis();
            int protectedCount = 0;
            for (LruEntry entry : this.lru.values()) {
                if (now - entry.lastTouchedAt < protectionMillis) {
                    ++protectedCount;
                }
            }
            return new LruSnapshot(this.lru.size(), protectedCount, this.totalWeight, configuredMaxWeightBytes());
        }
    }

    private void touch(AsyncCachedModel model, long weight) {
        List<AsyncCachedModel> evicted = new ArrayList<>();
        long now = System.currentTimeMillis();
        long protectionMillis = configuredProtectionMillis();
        long maxWeight = configuredMaxWeightBytes();

        synchronized (this.lru) {
            LruEntry oldEntry = this.lru.remove(model);
            if (oldEntry != null) {
                this.totalWeight -= oldEntry.weight;
            }

            this.lru.put(model, new LruEntry(weight, now));
            this.totalWeight += weight;

            java.util.Iterator<Map.Entry<AsyncCachedModel, LruEntry>> iterator = this.lru.entrySet().iterator();
            while (this.totalWeight > maxWeight && this.lru.size() > 1 && iterator.hasNext()) {
                Map.Entry<AsyncCachedModel, LruEntry> entry = iterator.next();
                if (now - entry.getValue().lastTouchedAt < protectionMillis) {
                    break;
                }
                iterator.remove();
                this.totalWeight -= entry.getValue().weight;
                evicted.add(entry.getKey());
            }
        }

        for (AsyncCachedModel evictedModel : evicted) {
            evictedModel.evictLoadedModel("size-limit");
        }
    }

    private void removeFromLru(AsyncCachedModel model) {
        synchronized (this.lru) {
            LruEntry removed = this.lru.remove(model);
            if (removed != null) {
                this.totalWeight -= removed.weight;
            }
        }
    }

    private AsyncCachedModel createCachedModel(
            FIXModelPack pack,
            ResourceLocation resource,
            VecAccuracy accuracy,
            Object[] args,
            PolygonModel model
    ) {
        Object[] copiedArgs = args == null ? new Object[0] : Arrays.copyOf(args, args.length);
        File cacheFile = this.getCacheFile(pack, resource, accuracy);
        long weight = Math.max(cacheFile.length(), 1L);

        AsyncCachedModel cachedModel = new AsyncCachedModel(
                new ModelHeader(resource.toString(), model, weight),
                pack,
                resource,
                accuracy,
                copiedArgs,
                model
        );

        this.trackedModels.add(cachedModel);
        return cachedModel;
    }

    private FIXModelPack resolvePack(ResourceLocation resource) throws IOException {
        for (FIXModelPack pack : FIXFileLoader.INSTANCE.getAllModelPacks()) {
            FIXResource fixResource = pack.getFile(resource);
            if (fixResource == null) {
                continue;
            }

            InputStream stream = fixResource.getInputStream();
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {
                }
            }
            return pack;
        }

        throw new FileNotFoundException(resource.toString());
    }

    private InputStream[] inputStreams(ResourceLocation resource) throws IOException {
        InputStream mainStream = FIXFileLoader.INSTANCE.getInputStream(resource);
        if (!FileType.OBJ.match(resource.getResourcePath())) {
            return new InputStream[]{mainStream};
        }

        String mtlFileName = resource.getResourcePath().replace(".obj", ".mtl");
        ResourceLocation mtlFile = new ResourceLocation(resource.getResourceDomain(), mtlFileName);
        InputStream mtlStream = null;
        try {
            mtlStream = FIXFileLoader.INSTANCE.getInputStream(mtlFile);
        } catch (Exception ignored) {
        }
        return new InputStream[]{mainStream, mtlStream};
    }

    private File getCacheFile(FIXModelPack pack, ResourceLocation resource, VecAccuracy accuracy) {
        File baseDir = new File(UtilsKt.getFixCacheDir(), "polygon-model");
        File packDir = new File(baseDir, pack.getFile().getName());
        String sha1 = DigestUtils.INSTANCE.sha1Hex("cached-model:" + accuracy + ":" + resource);
        return new File(packDir, sha1);
    }

    private static long configuredMaxWeightBytes() {
        return Math.max(16L, AppleConfig.cachedModelMemoryLimitMiB) * 1024L * 1024L;
    }

    private static int configuredProtectionSeconds() {
        return Math.max(0, AppleConfig.cachedModelProtectSeconds);
    }

    private static long configuredProtectionMillis() {
        return configuredProtectionSeconds() * 1000L;
    }

    private static double bytesToMiB(long bytes) {
        return bytes / (1024.0D * 1024.0D);
    }

    private enum LoadState {
        UNLOADED,
        QUEUED,
        LOADING,
        READY,
        FAILED
    }

    private static final class ModelHeader {
        final String modelName;
        final int drawMode;
        final FileType type;
        final Map<String, Material> materials;
        final long weight;

        ModelHeader(String modelName, PolygonModel model, long weight) {
            this.modelName = modelName;
            this.drawMode = model.getDrawMode();
            this.type = model.getType();

            Map<String, Material> copiedMaterials = new HashMap<>();
            Map<String, Material> sourceMaterials = model.getMaterials();
            if (sourceMaterials != null) {
                for (Map.Entry<String, Material> entry : sourceMaterials.entrySet()) {
                    Material material = entry.getValue();
                    if (material != null) {
                        copiedMaterials.put(entry.getKey(), new Material(material.id, material.texture));
                    }
                }
            }
            this.materials = copiedMaterials;
            this.weight = Math.max(weight, 1L);
        }
    }

    private final class AsyncCachedModel extends PolygonModel {
        private final ModelHeader header;
        private final FIXModelPack pack;
        private final ResourceLocation resource;
        private final VecAccuracy accuracy;
        private final Object[] args;

        private final AtomicReference<LoadState> state = new AtomicReference<>(LoadState.READY);

        private volatile PolygonModel loadedModel;
        private volatile PolygonModel pendingModel;

        private Map<String, Material> materials;
        private FileType type;

        private AsyncCachedModel(
                ModelHeader header,
                FIXModelPack pack,
                ResourceLocation resource,
                VecAccuracy accuracy,
                Object[] args,
                PolygonModel initialModel
        ) {
            this.header = header;
            this.pack = pack;
            this.resource = resource;
            this.accuracy = accuracy;
            this.args = args;
            this.loadedModel = initialModel;

            this.fileName = header.modelName;
            this.drawMode = header.drawMode;
            this.type = header.type;
            this.materials = header.materials;
            super.accuracy = accuracy;

            float[] size = initialModel.getSize();
            if (size != null && size.length >= 6) {
                System.arraycopy(size, 0, this.sizeBox, 0, 6);
            }
            this.groupObjects.addAll(initialModel.getGroupObjects());
            this.touchLoadedModel();
        }

        private boolean prepare(boolean sync) {
            this.installPendingModelIfPresent();

            PolygonModel model = this.loadedModel;
            if (model != null && this.state.get() == LoadState.READY) {
                this.touchLoadedModel();
                return true;
            }

            if (sync) {
                return this.loadNowSync();
            }

            this.queueLoad();
            return false;
        }

        private void compactLoadedModel() {
            if (this.loadedModel == null && this.pendingModel == null) {
                return;
            }

            LOGGER.info(
                    "Discarding cached model from memory: model={}, reason=post-init-compact, weight={} bytes",
                    this.header.modelName,
                    this.header.weight
            );
            this.loadedModel = null;
            this.pendingModel = null;
            this.groupObjects.clear();
            this.state.set(LoadState.UNLOADED);
            CachedModelManager.this.removeFromLru(this);
        }

        private boolean loadNowSync() {
            LoadState current = this.state.get();
            if (current == LoadState.LOADING || current == LoadState.QUEUED) {
                return false;
            }

            this.state.set(LoadState.LOADING);
            PolygonModel model = this.restoreModel();
            if (model == null) {
                this.state.set(LoadState.FAILED);
                return false;
            }

            this.pendingModel = model;
            this.state.set(LoadState.READY);
            this.installPendingModelIfPresent();
            return this.loadedModel != null;
        }

        private void queueLoad() {
            LoadState current = this.state.get();
            if (current == LoadState.READY || current == LoadState.LOADING || current == LoadState.QUEUED) {
                return;
            }

            if (!this.state.compareAndSet(current, LoadState.QUEUED)) {
                return;
            }

            LOGGER.info(
                    "Queueing dynamic cached model load: model={}, weight={} bytes",
                    this.header.modelName,
                    this.header.weight
            );
            CachedModelManager.this.loaderExecutor.submit(() -> {
                if (!this.state.compareAndSet(LoadState.QUEUED, LoadState.LOADING)) {
                    return;
                }

                PolygonModel model = this.restoreModel();
                if (model == null) {
                    this.state.set(LoadState.FAILED);
                    LOGGER.warn(
                            "Dynamic cached model load failed: model={}, weight={} bytes",
                            this.header.modelName,
                            this.header.weight
                    );
                    return;
                }

                this.pendingModel = model;
                this.state.set(LoadState.READY);
                LOGGER.info(
                        "Completed dynamic cached model load: model={}, weight={} bytes",
                        this.header.modelName,
                        this.header.weight
                );
            });
        }

        private PolygonModel restoreModel() {
            try {
                PolygonModel model = CachedPolygonModel.INSTANCE.getCachedModel(this.pack, this.resource, this.accuracy);
                if (model != null) {
                    return model;
                }

                PolygonModel fallback = ModelLoader.loadModel__NGTLIB(this.resource, this.accuracy, this.args);
                if (fallback != null) {
                    CachedPolygonModel.INSTANCE.putCachedModel(this.pack, this.resource, this.accuracy, fallback);
                }
                return fallback;
            } catch (Throwable t) {
                LOGGER.warn("Failed to restore cached model asynchronously", t);
                return null;
            }
        }

        private void installPendingModelIfPresent() {
            PolygonModel pending = this.pendingModel;
            if (pending == null) {
                return;
            }

            this.pendingModel = null;
            this.loadedModel = pending;
            this.drawMode = pending.getDrawMode();
            this.type = pending.getType();
            this.materials = pending.getMaterials();

            float[] size = pending.getSize();
            if (size != null && size.length >= 6) {
                System.arraycopy(size, 0, this.sizeBox, 0, 6);
            }

            this.groupObjects.clear();
            this.groupObjects.addAll(pending.getGroupObjects());
            this.touchLoadedModel();
        }

        private void touchLoadedModel() {
            CachedModelManager.this.touch(this, this.header.weight);
        }

        private LoadState currentState() {
            if (this.pendingModel != null && this.state.get() == LoadState.READY) {
                return LoadState.LOADING;
            }
            return this.state.get();
        }

        private void evictLoadedModel(String reason) {
            if (this.state.get() != LoadState.READY) {
                return;
            }

            if (this.loadedModel != null) {
                LOGGER.info(
                        "Discarding cached model from memory: model={}, reason={}, weight={} bytes",
                        this.header.modelName,
                        reason,
                        this.header.weight
                );
            }

            this.loadedModel = null;
            this.pendingModel = null;
            this.groupObjects.clear();
            this.state.set(LoadState.UNLOADED);
        }

        @Override
        protected void parseLine(String currentLine, int lineCount) {
            throw new UnsupportedOperationException("AsyncCachedModel does not parse source data directly");
        }

        @Override
        protected void postInit() {
            throw new UnsupportedOperationException("AsyncCachedModel does not initialize source data directly");
        }

        @Override
        public Map<String, Material> getMaterials() {
            this.installPendingModelIfPresent();
            if (this.loadedModel != null && this.state.get() == LoadState.READY) {
                this.touchLoadedModel();
                return this.materials;
            }
            return this.header.materials;
        }

        @Override
        public FileType getType() {
            this.installPendingModelIfPresent();
            return this.type == null ? this.header.type : this.type;
        }
    }

    private static final class LruEntry {
        final long weight;
        final long lastTouchedAt;

        private LruEntry(long weight, long lastTouchedAt) {
            this.weight = weight;
            this.lastTouchedAt = lastTouchedAt;
        }
    }

    private static final class LruSnapshot {
        final int loadedCount;
        final int protectedCount;
        final long totalWeight;
        final long maxWeight;

        private LruSnapshot(int loadedCount, int protectedCount, long totalWeight, long maxWeight) {
            this.loadedCount = loadedCount;
            this.protectedCount = protectedCount;
            this.totalWeight = totalWeight;
            this.maxWeight = maxWeight;
        }
    }

    private static final class ModelStateSnapshot {
        final int total;
        final int unloaded;
        final int queued;
        final int loading;
        final int ready;
        final int failed;

        private ModelStateSnapshot(int total, int unloaded, int queued, int loading, int ready, int failed) {
            this.total = total;
            this.unloaded = unloaded;
            this.queued = queued;
            this.loading = loading;
            this.ready = ready;
            this.failed = failed;
        }
    }
}
