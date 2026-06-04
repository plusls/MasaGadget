package top.hendrixshen.magiclib.buildLogic.configuration;

import org.gradle.api.Project;

import javax.inject.Inject;

public abstract class MagicLoomTasks implements Runnable {
    @Inject
    protected abstract Project getProject();

    @Override
    public void run() {
        // TODO
    }
}
