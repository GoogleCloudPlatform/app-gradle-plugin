package com.google.cloud.tools.gradle.appengine;

import com.google.cloud.tools.gradle.appengine.flexible.AppEngineFlexiblePlugin;
import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardPlugin;
import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.api.ProjectConfigurationException;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.plugins.UnknownPluginException;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Test AppEnginePlugin environment choosing configuration logic. */
public class AppEnginePluginTest {
  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();

  private Project setUpTestProject(final String buildFileName) throws IOException {
    final Path buildFile = testProjectDir.getRoot().toPath().resolve("build.gradle");
    final InputStream buildFileContent =
        getClass()
            .getClassLoader()
            .getResourceAsStream(
                "projects/AppEnginePluginTest/Extension/" + buildFileName + ".gradle");
    Files.copy(buildFileContent, buildFile);

    final Project project =
        ProjectBuilder.builder().withProjectDir(testProjectDir.getRoot()).build();
    project.getPluginManager().apply(JavaPlugin.class);
    project.getPluginManager().apply(WarPlugin.class);
    project.getPluginManager().apply(AppEnginePlugin.class);
    ((ProjectInternal) project).evaluate();

    return project;
  }

  @Test(expected = UnknownPluginException.class)
  public void testExplicitStandardConfiguration() throws IOException {
    final Project project = setUpTestProject("explicit-standard-environment");
    final PluginContainer pluginContainer = project.getPlugins();
    final AppEngineStandardPlugin standardPlugin =
        pluginContainer.getPlugin(AppEngineStandardPlugin.class);
    Assert.assertNotNull(standardPlugin);
    final AppEngineFlexiblePlugin flexiblePlugin =
        pluginContainer.getPlugin(AppEngineFlexiblePlugin.class);
    Assert.fail("Flexible plugin should not be found");
  }

  @Test(expected = UnknownPluginException.class)
  public void testExplicitFlexibleConfiguration() throws IOException {
    final Project project = setUpTestProject("explicit-flexible-environment");
    final PluginContainer pluginContainer = project.getPlugins();
    final AppEngineFlexiblePlugin flexiblePlugin =
        pluginContainer.getPlugin(AppEngineFlexiblePlugin.class);
    Assert.assertNotNull(flexiblePlugin);
    final AppEngineStandardPlugin standardPlugin =
        pluginContainer.getPlugin(AppEngineStandardPlugin.class);
    Assert.fail("Standard plugin should not be found");
  }

  @Test(expected = ProjectConfigurationException.class)
  public void testExplicitConfigurationThrowsExceptionForWrongEnvironmentValue()
      throws IOException {
    final Project project = setUpTestProject("explicit-unknown-environment");
    Assert.fail("Configuration should fail due to wrong environment value");
  }

  @Test(expected = UnknownPluginException.class)
  public void testImplicitStandardConfiguration() throws IOException {
    final Path webInf = testProjectDir.getRoot().toPath().resolve("src/main/webapp/WEB-INF");
    Files.createDirectories(webInf);
    final File appengineWebXml = Files.createFile(webInf.resolve("appengine-web.xml")).toFile();
    Files.write(appengineWebXml.toPath(), "<appengine-web-app/>".getBytes(Charsets.UTF_8));

    final Project project =
        ProjectBuilder.builder().withProjectDir(testProjectDir.getRoot()).build();
    project.getPluginManager().apply(JavaPlugin.class);
    project.getPluginManager().apply(WarPlugin.class);
    project.getPluginManager().apply(AppEnginePlugin.class);
    ((ProjectInternal) project).evaluate();

    final PluginContainer pluginContainer = project.getPlugins();
    final AppEngineStandardPlugin standardPlugin =
        pluginContainer.getPlugin(AppEngineStandardPlugin.class);
    Assert.assertNotNull(standardPlugin);
    final AppEngineFlexiblePlugin flexiblePlugin =
        pluginContainer.getPlugin(AppEngineFlexiblePlugin.class);
    Assert.fail("Flexible plugin should not be found");
  }

  @Test(expected = UnknownPluginException.class)
  public void testImplicitFlexibleConfiguration() throws IOException {
    final Project project =
        ProjectBuilder.builder().withProjectDir(testProjectDir.getRoot()).build();
    project.getPluginManager().apply(JavaPlugin.class);
    project.getPluginManager().apply(WarPlugin.class);
    project.getPluginManager().apply(AppEnginePlugin.class);
    ((ProjectInternal) project).evaluate();

    final PluginContainer pluginContainer = project.getPlugins();
    final AppEngineFlexiblePlugin flexiblePlugin =
        pluginContainer.getPlugin(AppEngineFlexiblePlugin.class);
    Assert.assertNotNull(flexiblePlugin);
    final AppEngineStandardPlugin standardPlugin =
        pluginContainer.getPlugin(AppEngineStandardPlugin.class);
    Assert.fail("Standard plugin should not be found");
  }
}
