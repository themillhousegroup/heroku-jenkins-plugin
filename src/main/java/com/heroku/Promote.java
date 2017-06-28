package com.heroku;

import com.heroku.api.App;
import com.heroku.api.HerokuAPI;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.List;

import static com.heroku.HerokuPlugin.Feature.CISAURUS;

/**
 * @author Ryan Brainard
 */
public class Promote extends AbstractHerokuBuildStep {

    private String sourceAppName;

    @DataBoundConstructor
    public Promote(String apiKey, String sourceAppName, String targetAppName) {
        super(apiKey, targetAppName);
        this.sourceAppName = sourceAppName;
    }

    // Overriding and delegating to parent because Jelly only looks at concrete class when rendering views
    @Override
    public String getApiKey() {
        return super.getApiKey();
    }

    public String getSourceAppName() {
        return sourceAppName;
    }

    public String getTargetAppName() {
        return super.getAppName();
    }

    @Override
    protected boolean perform(AbstractBuild build, Launcher launcher, final BuildListener listener, HerokuAPI api, final App targetApp) throws IOException, InterruptedException {
		listener.error(getSourceAppName() + " is using a JANVIL feature which is no longer supported"); 
		return false;
    }

    @Override
    public PromoteDescriptor getDescriptor() {
        return (PromoteDescriptor) super.getDescriptor();
    }

    @Extension
    public static final class PromoteDescriptor extends AbstractHerokuBuildStepDescriptor {

        public String getDisplayName() {
            return "Heroku: Promote";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return HerokuPlugin.get().hasFeature(CISAURUS);
        }
    }
}
