package com.github.mati1979.play.soyplugin.template;

import com.github.mati1979.play.soyplugin.config.SoyViewConf;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import play.Play;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 19:58
 *
 * An implementation that will recursively search (and resolve)
 * for soy files based on provided templatesLocation path
 */
@ThreadSafe
public class FileSystemTemplateFilesResolver implements TemplateFilesResolver {

    private static final play.Logger.ALogger logger = play.Logger.of("play.soy.view");

    /** a thread safe cache for resolved templates, no need to worry of ddos attack */
    /** friendly */ CopyOnWriteArrayList<URL> cachedFiles = new CopyOnWriteArrayList<>();

    private SoyViewConf soyViewConf;

    public FileSystemTemplateFilesResolver(final SoyViewConf soyViewConf) {
        this.soyViewConf = soyViewConf;
    }

    @Override
    public Collection<URL> resolve() throws IOException {
        Preconditions.checkNotNull(soyViewConf.resolveTemplatesLocation(), "templatesLocation cannot be null!");

        if (soyViewConf.globalHotReloadMode()) {
            final List<URL> files = toFiles(soyViewConf.resolveTemplatesLocation());
            logger.debug("Debug on - resolved files:" + files.size());

            return files;
        }

        //no debug
        synchronized (cachedFiles) {
            if (cachedFiles.isEmpty()) {
                final List<URL> files = toFiles(soyViewConf.resolveTemplatesLocation());
                logger.debug("templates location:" + soyViewConf.resolveTemplatesLocation());
                logger.debug("Using cache resolve, debug off, urls:" + files.size());
                cachedFiles.addAll(files);
            }
        }

        return cachedFiles;
    }

    @Override
    public Optional<URL> resolve(final @Nullable String templateFileName) throws IOException {
        if (templateFileName == null) {
            return Optional.absent();
        }

        final Collection<URL> files = resolve();

        final URL templateFile = Iterables.find(files, new Predicate<URL>() {

            @Override
            public boolean apply(final URL url) {
                final String fileName = url.getFile();
                final File file = new File(fileName);

                return file.toURI().toString().endsWith(normalizeTemplateName(templateFileName));
            }

        }, null);

        return Optional.fromNullable(templateFile);
    }

    private String normalizeTemplateName(final String templateFileName) {
        String normalizedTemplateName = templateFileName;
        if (!templateFileName.endsWith(dotWithExtension())) {
            normalizedTemplateName = templateFileName + dotWithExtension();
        }

        return normalizedTemplateName;
    }

    private List<URL> toFiles(final String templatesLocation) {
        final List<URL> templateFiles = Lists.newArrayList();
        try {
            final File baseDirectory = Play.application().getFile(templatesLocation);
            if (baseDirectory.isDirectory()) {
                templateFiles.addAll(findSoyFiles(baseDirectory, soyViewConf.resolveRecursive()));
            } else {
                throw new IllegalArgumentException("Soy template base directory:" + templatesLocation + "' is not a directory");
            }
        } catch (final IOException e) {
            throw new IllegalArgumentException("Error with soy template base directory:" + templatesLocation, e);
        }

        return templateFiles;
    }

    protected List<URL> findSoyFiles(final File baseDirectory, final boolean recursive) throws MalformedURLException {
        final List<URL> soyFiles = new ArrayList<>();
        findSoyFiles(soyFiles, baseDirectory, recursive);

        return soyFiles;
    }

    protected void findSoyFiles(final List<URL> soyFiles, final File baseDirectory, final boolean recursive) throws MalformedURLException {
        final File[] files = baseDirectory.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(dotWithExtension())) {
                        soyFiles.add(file.toURI().toURL());
                    }
                } else if (file.isDirectory() && recursive) {
                    findSoyFiles(soyFiles, file, recursive);
                }
            }
        } else {
            throw new IllegalArgumentException("Unable to retrieve contents of:" + baseDirectory);
        }
    }

    private String dotWithExtension() {
        return "." + soyViewConf.resolveFilesExtension();
    }

}
