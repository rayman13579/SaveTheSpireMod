package at.rayman.savethespire;

import at.rayman.savethespire.util.NetworkService;
import at.rayman.savethespire.util.Zipper;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import static at.rayman.savethespire.SaveTheSpire.logger;

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
