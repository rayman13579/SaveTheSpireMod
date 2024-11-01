package at.rayman.saveTheSpire;

import at.rayman.saveTheSpire.util.*;
import basemod.BaseMod;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scannotation.AnnotationDB;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@SpireInitializer
public class SaveTheSpire implements PostInitializeSubscriber {

    public static ModInfo info;

    public static String modID;

    static {
        loadModInfo();
    }

    public static final Logger logger = LogManager.getLogger(modID);

    public static void initialize() {
        new SaveTheSpire();
    }

    public SaveTheSpire() {
        BaseMod.subscribe(this);
        logger.info("Subscribed to BaseMod.");
        downloadSave();
    }

    @Override
    public void receivePostInitialize() {
        Texture icon = TextureLoader.getTexture("saveTheSpire.png");
        BaseMod.registerModBadge(icon, info.Name, GeneralUtils.arrToString(info.Authors), info.Description, null);
    }

    public void downloadSave() {
        logger.info("Downloading save...");
        NetworkService.getInstance().downloadSave()
            .then(message -> logger.info("{}", message))
            .success(r1 ->
                clearSaveDirectory()
                    .then(message -> logger.info("{}", message))
                    .then(r2 -> Zipper.unzip()
                        .then(message -> logger.info("{}", message))))
            .then(r3 -> Zipper.deleteZip()
                .then(message -> logger.info("{}", message)));
        logger.info("Downloading save done");
    }

    private Result clearSaveDirectory() {
        try {
            FileUtils.deleteDirectory(new File(Constants.PREFERENCES_PATH));
            FileUtils.deleteDirectory(new File(Constants.RUNS_PATH));
            FileUtils.deleteDirectory(new File(Constants.SAVES_PATH));
        } catch (IOException e) {
            logger.error("Error clearing save directory", e);
            return Result.error(e.getMessage());
        }
        return Result.success("Cleared directories");
    }

    private static void loadModInfo() {
        Optional<ModInfo> infos = Arrays.stream(Loader.MODINFOS).filter((modInfo) -> {
            AnnotationDB annotationDB = Patcher.annotationDBMap.get(modInfo.jarURL);
            if (annotationDB == null)
                return false;
            Set<String> initializers = annotationDB.getAnnotationIndex().getOrDefault(SpireInitializer.class.getName(), Collections.emptySet());
            return initializers.contains(SaveTheSpire.class.getName());
        }).findFirst();
        if (infos.isPresent()) {
            info = infos.get();
            modID = info.ID;
        } else {
            throw new RuntimeException("Failed to determine mod info/ID based on initializer.");
        }
    }

}
