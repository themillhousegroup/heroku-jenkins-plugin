package com.heroku;

import com.heroku.api.App;
import hudson.model.*;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * @author Ryan Brainard
 */
public class AnvilPushTest extends BaseHerokuBuildStepTest {

    public void testPushDefault() throws Exception {
        FreeStyleProject project = createFreeStyleProject();
        project.scheduleBuild2(0).get();
        project.getSomeWorkspace().child("Procfile").copyFrom(ClassLoader.getSystemResource("Procfile"));

        project.getBuildersList().add(new AnvilPush(apiKey, appName, "", "", "TEST", "", "", "", false));
        FreeStyleBuild build = project.scheduleBuild2(0).get();

        String logs = FileUtils.readFileToString(build.getLogFile());

        assertTrue(logs.contains("Workspace contains"));
        assertTrue(logs.contains("Push complete"));
    }

    public void testPushSerialization() throws Exception {
        FreeStyleProject project = createFreeStyleProject();
        FreeStyleBuild build = project.scheduleBuild2(0).get();

        final BuildListener emptyBuildListener = new NullBuildListener();

        final AnvilPush.RemoteCallable pushRemoteCallable =
                new AnvilPush(apiKey, appName, "", "", "TEST", "", "", "", false)
                        .createRemoteCallable(build, emptyBuildListener, api, new App().named(appName));

        final ByteArrayOutputStream serialization = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(serialization);
        oos.writeObject(pushRemoteCallable);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialization.toByteArray()));
        final AnvilPush.RemoteCallable unserializedPushRemoteCallable = (AnvilPush.RemoteCallable) ois.readObject();
        ois.close();

        build.getWorkspace().act(unserializedPushRemoteCallable);
    }

}
