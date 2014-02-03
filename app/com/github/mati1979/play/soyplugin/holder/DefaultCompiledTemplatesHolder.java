package com.github.mati1979.play.soyplugin.holder;

import com.github.mati1979.play.soyplugin.compile.EmptyTofuCompiler;
import com.github.mati1979.play.soyplugin.compile.TofuCompiler;
import com.github.mati1979.play.soyplugin.template.EmptyTemplateFilesResolver;
import com.github.mati1979.play.soyplugin.template.TemplateFilesResolver;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.template.soy.tofu.SoyTofu;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import static com.github.mati1979.play.soyplugin.config.PlayConfAccessor.COMPILE_PRECOMPILE_TEMPLATES;
import static com.github.mati1979.play.soyplugin.config.PlayConfAccessor.GLOBAL_HOT_RELOAD_MODE;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 02/11/2013
 * Time: 14:04
 */
public class DefaultCompiledTemplatesHolder implements CompiledTemplatesHolder {

    private static final play.Logger.ALogger logger = play.Logger.of(DefaultCompiledTemplatesHolder.class);

    private TofuCompiler tofuCompiler = new EmptyTofuCompiler();

    private TemplateFilesResolver templatesFileResolver = new EmptyTemplateFilesResolver();

    private Optional<SoyTofu> compiledTemplates = Optional.absent();

    private boolean hotReloadMode = GLOBAL_HOT_RELOAD_MODE;

    private boolean preCompileTemplates = COMPILE_PRECOMPILE_TEMPLATES;

    public DefaultCompiledTemplatesHolder(final TofuCompiler tofuCompiler, final TemplateFilesResolver templatesFileResolver) throws IOException {
        this.tofuCompiler = tofuCompiler;
        this.templatesFileResolver = templatesFileResolver;
        init();
    }

    public DefaultCompiledTemplatesHolder() throws IOException {
        init();
    }

    public Optional<SoyTofu> compiledTemplates() throws IOException {
        if (shouldCompileTemplates()) {
            this.compiledTemplates = Optional.fromNullable(compileTemplates());
        }

        return compiledTemplates;
    }

    private boolean shouldCompileTemplates() {
        return isHotReloadMode() || !compiledTemplates.isPresent();
    }

    public void init() throws IOException {
        logger.debug("TemplatesHolder init...");
        if (preCompileTemplates) {
            this.compiledTemplates = Optional.fromNullable(compileTemplates());
        }
    }

    private SoyTofu compileTemplates() throws IOException {
        Preconditions.checkNotNull(templatesFileResolver, "templatesRenderer cannot be null!");
        Preconditions.checkNotNull(tofuCompiler, "tofuCompiler cannot be null!");

        final Collection<URL> templateFiles = templatesFileResolver.resolve();
        if (templateFiles != null && templateFiles.size() > 0) {
            logger.debug("Compiling templates, no:{}", templateFiles.size());

            return tofuCompiler.compile(templateFiles);
        }

        throw new IOException("0 template files have been found, check your templateFilesResolver!");
    }

    public void setHotReloadMode(final boolean hotReloadMode) {
        this.hotReloadMode = hotReloadMode;
    }

    public boolean isHotReloadMode() {
        return hotReloadMode;
    }

    public boolean isHotReloadModeOff() {
        return !hotReloadMode;
    }

    public void setTofuCompiler(TofuCompiler tofuCompiler) {
        this.tofuCompiler = tofuCompiler;
    }

    public void setTemplatesFileResolver(TemplateFilesResolver templatesFileResolver) {
        this.templatesFileResolver = templatesFileResolver;
    }

    public void setPreCompileTemplates(boolean preCompileTemplates) {
        this.preCompileTemplates = preCompileTemplates;
    }

}
