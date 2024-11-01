package at.rayman.saveTheSpire;

import at.rayman.saveTheSpire.util.NetworkService;
import at.rayman.saveTheSpire.util.Zipper;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import static at.rayman.saveTheSpire.SaveTheSpire.logger;

@SpirePatch(clz = CardCrawlGame.class, method = "dispose")

public class UploadSavePatch {

    @SpireInsertPatch(rloc = 12)
    public static void uploadSave() {
        logger.info("Uploading save...");
        Zipper.zip()
            .then(message -> logger.info("{}", message))
            .success(result -> NetworkService.uploadSave()
                .then(message -> logger.info("{}", message)))
            .then(r -> Zipper.deleteZip()
                .then(message -> logger.info("{}", message)));
        logger.info("Uploading save done");
    }

}
