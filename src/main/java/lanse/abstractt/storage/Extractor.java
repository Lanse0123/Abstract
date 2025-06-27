package lanse.abstractt.storage;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class Extractor {
    public static void extractFile(ArchiveInputStream i, File targetDir) throws IOException {
        ArchiveEntry entry = null;
        while ((entry = i.getNextEntry()) != null) {
            if (!i.canReadEntryData(entry)) {
                // log something?
                continue;
            }
            File f = new File(targetDir.getName() + "/" + entry.getName());
            if (entry.isDirectory()) {
                if (!f.isDirectory() && !f.mkdirs()) {
                    throw new IOException("failed to create directory " + f);
                }
            } else {
                File parent = f.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("failed to create directory " + parent);
                }
                try (OutputStream o = Files.newOutputStream(f.toPath())) {
                    IOUtils.copy(i, o);
                    f.setExecutable(true); //TODO: not safe for generically, but works for everything we currently extract
                }
            }
        }
    }
}
