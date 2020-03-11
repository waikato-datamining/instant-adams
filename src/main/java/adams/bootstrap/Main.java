/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Main.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.bootstrap;

import com.github.fracpete.bootstrapp.core.Template;
import com.github.fracpete.requests4j.Requests;
import com.github.fracpete.requests4j.response.BasicResponse;
import com.github.fracpete.requests4j.response.Response;
import com.github.fracpete.resourceextractor4j.Files;
import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.ArgumentParserException;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.simpleargparse4j.Option.Type;
import org.apache.commons.lang.SystemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for boostrapping ADAMS applications.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Main {

  /** the URL for the ADAMS settings.xml file. */
  public final static String USER_SETTINGS_URL = "https://raw.githubusercontent.com/waikato-datamining/adams-website/master/files/resources/settings.xml";

  /** the URL for the ADAMS base pom.xml file. */
  public final static String ADAMS_BASE_URL = "https://raw.githubusercontent.com/waikato-datamining/adams-base/master/pom.xml";

  /** the URL for the ADAMS addons pom.xml file. */
  public final static String ADAMS_ADDONS_URL = "https://raw.githubusercontent.com/waikato-datamining/adams-addons/master/pom.xml";

  /** the URL for the ADAMS lts pom.xml file. */
  public final static String ADAMS_LTS_URL = "https://raw.githubusercontent.com/waikato-datamining/adams-lts/master/pom.xml";

  /** the URL for the ADAMS spectral base pom.xml file. */
  public final static String ADAMS_SPECTRAL_BASE_URL = "https://raw.githubusercontent.com/waikato-datamining/adams-spectral-base/master/pom.xml";

  /** the environment variable for the instant adams home directory. */
  public final static String HOME_DIR_ENV = "INSTANTADAMS_HOME";

  /** the bundled pom template. */
  public final static String RESOURCES = "adams/bootstrap";

  /** the bundled pom template. */
  public final static String POMTEMPLATE_FILE = "instant-adams.xml";

  /** the alternative maven installation. */
  protected File m_MavenHome;

  /** the maven user settings to use. */
  protected File m_MavenUserSettings;

  /** the actual maven user settings to use. */
  protected File m_ActMavenUserSettings;

  /** the alternative java installation. */
  protected File m_JavaHome;

  /** the output directory. */
  protected File m_OutputDir;

  /** the output directory for maven. */
  protected File m_OutputDirMaven;

  /** the pom template. */
  protected File m_PomTemplate;

  /** the actual POM template to use. */
  protected transient File m_ActPomTemplate;

  /** the name of the projet. */
  protected String m_Name;

  /** whether to call the "clean" goal. */
  protected boolean m_Clean;

  /** the JVM options. */
  protected List<String> m_JVM;

  /** the modules. */
  protected String m_Modules;

  /** the version to use. */
  protected String m_Version;

  /** the external jar files/dirs. */
  protected List<File> m_ExternalJars;

  /** whether to retrieve source jars or not. */
  protected boolean m_Sources;

  /** the external source jar files/dirs. */
  protected List<File> m_ExternalSources;

  /** the dependencies. */
  protected List<String> m_Dependencies;

  /** the dependency files. */
  protected List<File> m_DependencyFiles;

  /** the dependencies. */
  protected List<String> m_AllDependencies;

  /** the main class to launch. */
  protected String m_MainClass;

  /** whether to build .deb package. */
  protected boolean m_Debian;

  /** the custom debian maven snippet to use. */
  protected File m_DebianSnippet;

  /** whether to build .rpm package. */
  protected boolean m_Redhat;

  /** the custom redhat maven snippet to use. */
  protected File m_RedhatSnippet;

  /** whether to list modules. */
  protected boolean m_ListModules;

  /** for logging. */
  protected Logger m_Logger;

  /** whether help got requested. */
  protected boolean m_HelpRequested;

  /**
   * Initializes the object.
   */
  public Main() {
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_MavenHome            = null;
    m_MavenUserSettings    = null;
    m_ActMavenUserSettings = null;
    m_PomTemplate          = null;
    m_ActPomTemplate       = null;
    m_JavaHome             = null;
    m_OutputDir            = null;
    m_OutputDirMaven       = null;
    m_JVM                  = null;
    m_Modules              = null;
    m_ExternalJars         = null;
    m_Sources              = false;
    m_ExternalSources      = null;
    m_AllDependencies      = null;
    m_Dependencies         = null;
    m_DependencyFiles      = null;
    m_MainClass            = "adams.gui.Main";
    m_Name                 = Template.DEFAULT_NAME;
    m_Clean                = false;
    m_Debian               = false;
    m_DebianSnippet        = null;
    m_Redhat               = false;
    m_RedhatSnippet        = null;
    m_ListModules          = false;
    m_Logger               = null;
    m_HelpRequested        = false;
  }

  /**
   * Returns the logger instance to use.
   *
   * @return		the logger
   */
  protected Logger getLogger() {
    if (m_Logger == null)
      m_Logger = Logger.getLogger(getClass().getName());
    return m_Logger;
  }

  /**
   * Sets the alternative maven installation to use.
   *
   * @param dir		the top-level directory (above "bin")
   * @return		itself
   */
  public Main mavenHome(File dir) {
    m_MavenHome = dir;
    return this;
  }

  /**
   * Returns the alternative maven installation to use.
   *
   * @return		the directory, null to use bundled one
   */
  public File getMavenHome() {
    return m_MavenHome;
  }

  /**
   * Sets the alternative maven user settings to use.
   *
   * @param dir		the XML file, null to use default ($HOME/.m2/settings.xml)
   * @return		itself
   */
  public Main mavenUserSettings(File dir) {
    m_MavenUserSettings = dir;
    return this;
  }

  /**
   * Returns the alternative maven user settings to use.
   *
   * @return		the file, null to use default ($HOME/.m2/settings.xml)
   */
  public File getMavenUserSettings() {
    return m_MavenUserSettings;
  }

  /**
   * Sets the alternative java installation to use.
   *
   * @param dir		the top-level directory (above "bin")
   * @return		itself
   */
  public Main javaHome(File dir) {
    m_JavaHome = dir;
    return this;
  }

  /**
   * Returns the alternative java installation to use.
   *
   * @return		the directory, null if using one that class was started with
   */
  public File getJavaHome() {
    return m_JavaHome;
  }

  /**
   * Sets the output directory for the bootstrapped application.
   *
   * @param dir		the directory
   * @return		itself
   */
  public Main outputDir(File dir) {
    m_OutputDir = dir;
    return this;
  }

  /**
   * Returns the output directory for the bootstrapped application.
   *
   * @return		the directory, null if none set
   */
  public File getOutputDir() {
    return m_OutputDir;
  }

  /**
   * Sets the template for the POM to use.
   *
   * @param template	the template
   * @return		itself
   */
  public Main pomTemplate(File template) {
    m_PomTemplate = template;
    return this;
  }

  /**
   * Returns the template for the pom.xml to use.
   *
   * @return		the POM template, null if using the default
   */
  public File getPomTemplate() {
    return m_PomTemplate;
  }

  /**
   * Sets the name for the project.
   *
   * @param name	the name
   * @return		itself
   */
  public Main name(String name) {
    m_Name = name;
    return this;
  }

  /**
   * Returns the name for the project.
   *
   * @return		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Quick check whether there are conflicting LTS and non-LTS modules in the
   * list of modules.
   *
   * @param modules	the modules to check
   * @return		null if valid, otherwise error message
   */
  protected String checkModules(String[] modules) {
    Set<String>		set;
    StringBuilder	result;

    result = new StringBuilder();

    set    = new HashSet<>();
    for (String module: modules) {
      if (module.endsWith("-lts"))
        set.add(module.replaceAll("-lts$", ""));
    }

    for (String module: modules) {
      module = module.trim();
      if (module.endsWith("-lts"))
	continue;
      if (set.contains(module)) {
        if (result.length() > 0)
          result.append(", ");
        result.append(module);
      }
    }

    if (result.length() == 0)
      return null;
    else
      return "Following modules are present as LTS and non-LTS version: " + result.toString();
  }

  /**
   * Sets the modules to use for bootstrapping.
   *
   * @param modules	the modules (comma-separated list)
   * @return		itself
   */
  public Main modules(String modules) {
    String	msg;

    if ((msg = checkModules(modules.split(","))) != null)
      throw new IllegalArgumentException(msg);

    m_Modules = modules;
    return this;
  }

  /**
   * Sets the modules to use for bootstrapping.
   *
   * @param modules	the modules
   * @return		itself
   */
  public Main modules(String... modules) {
    StringBuilder	all;
    String	msg;

    if (modules != null) {
      if ((msg = checkModules(modules)) != null)
	throw new IllegalArgumentException(msg);

      all = new StringBuilder();
      for (String module : modules) {
	if (all.length() > 0)
	  all.append(",");
	all.append(module);
      }
      m_Modules = all.toString();
    }
    else {
      m_Modules = null;
    }
    return this;
  }

  /**
   * Sets the modules to use for bootstrapping.
   *
   * @param modules	the modules
   * @return		itself
   */
  public Main modules(List<String> modules) {
    if (modules != null)
      modules(modules.toArray(new String[0]));
    else
      m_Modules = null;
    return this;
  }

  /**
   * Returns the modules.
   *
   * @return		the modules (comma-separated list), if not yet set
   */
  public String getModules() {
    return m_Modules;
  }

  /**
   * Sets the version of ADAMS to use.
   *
   * @param version	the version
   * @return		itself
   */
  public Main version(String version) {
    m_Version = version;
    return this;
  }

  /**
   * Returns the version of ADAMS to use.
   *
   * @return		the version
   */
  public String getVersion() {
    return m_Version;
  }

  /**
   * Sets the external jar files/dirs to use.
   *
   * @param external	the files/dirs, null to unset
   * @return		itself
   */
  public Main externalJars(List<File> external) {
    m_ExternalJars = external;
    return this;
  }

  /**
   * Sets the external jar files/dirs to use.
   *
   * @param external	the files/dirs, null to unset
   * @return		itself
   */
  public Main externalJars(File... external) {
    if (external == null)
      m_ExternalJars = null;
    else
      externalJars(Arrays.asList(external));
    return this;
  }

  /**
   * Returns the currently set external jar files/dirs.
   *
   * @return		the files/dirs, null if none set
   */
  public List<File> getExternalJars() {
    return m_ExternalJars;
  }

  /**
   * Sets the external source files/dirs to use.
   *
   * @param external	the files/dirs, null to unset
   * @return		itself
   */
  public Main externalSources(List<File> external) {
    m_ExternalSources = external;
    return this;
  }

  /**
   * Sets the external source files/dirs to use.
   *
   * @param external	the files/dirs, null to unset
   * @return		itself
   */
  public Main externalSources(File... external) {
    if (external == null)
      m_ExternalSources = null;
    else
      externalSources(Arrays.asList(external));
    return this;
  }

  /**
   * Returns the currently set external source files/dirs.
   *
   * @return		the files/dirs, null if none set
   */
  public List<File> getExternalSources() {
    return m_ExternalSources;
  }

  /**
   * Sets whether to execute the "clean" goal.
   *
   * @param clean	true if to clean
   * @return		itself
   */
  public Main clean(boolean clean) {
    m_Clean = clean;
    return this;
  }

  /**
   * Returns whether to execute the "clean" goal.
   *
   * @return		true if to clean
   */
  public boolean getClean() {
    return m_Clean;
  }

  /**
   * Sets whether to retrieve the source jars as well.
   *
   * @param sources	true if to get sources
   * @return		itself
   */
  public Main sources(boolean sources) {
    m_Sources = sources;
    return this;
  }

  /**
   * Returns whether to download source jars as well.
   *
   * @return		true if to get sources
   */
  public boolean getSources() {
    return m_Sources;
  }

  /**
   * Sets the JVM options to use for launching the main class.
   *
   * @param options	the options, can be null
   * @return		itself
   */
  public Main jvm(List<String> options) {
    m_JVM = options;
    return this;
  }

  /**
   * Sets the JVM options to use for launching the main class.
   *
   * @param options	the options, can be null
   * @return		itself
   */
  public Main jvm(String... options) {
    if (options != null)
      m_JVM = new ArrayList<>(Arrays.asList(options));
    else
      m_JVM = null;
    return this;
  }

  /**
   * Returns the JVM options.
   *
   * @return		the options, can be null
   */
  public List<String> getJvm() {
    return m_JVM;
  }

  /**
   * Sets the dependencies to use for bootstrapping.
   *
   * @param dependencies	the dependencies, can be null
   * @return		itself
   */
  public Main dependencies(List<String> dependencies) {
    m_Dependencies = dependencies;
    return this;
  }

  /**
   * Sets the dependencies to use for bootstrapping.
   *
   * @param dependencies	the dependencies, can be null
   * @return		itself
   */
  public Main dependencies(String... dependencies) {
    if (dependencies != null)
      m_Dependencies = new ArrayList<>(Arrays.asList(dependencies));
    else
      m_Dependencies = null;
    return this;
  }

  /**
   * Returns the dependencies.
   *
   * @return		the dependencies, can be null
   */
  public List<String> getDependencies() {
    return m_Dependencies;
  }

  /**
   * Sets the dependency files to use for bootstrapping (one dependency per line).
   *
   * @param files	the dependencies, can be null
   * @return		itself
   */
  public Main dependencyFiles(List<File> files) {
    m_DependencyFiles = files;
    return this;
  }

  /**
   * Sets the dependency files to use for bootstrapping (one dependency per line).
   *
   * @param files	the dependency files, can be null
   * @return		itself
   */
  public Main dependencyFiles(File... files) {
    if (files != null)
      m_DependencyFiles = new ArrayList<>(Arrays.asList(files));
    else
      m_DependencyFiles = null;
    return this;
  }

  /**
   * Returns the dependency files.
   *
   * @return		the files, can be null
   */
  public List<File> getDependencyFiles() {
    return m_DependencyFiles;
  }

  /**
   * Sets the main class to launch with the scripts.
   *
   * @param dir		the main class
   * @return		itself
   */
  public Main mainClass(String dir) {
    m_MainClass = dir;
    return this;
  }

  /**
   * Returns the main class to launch with the scripts.
   *
   * @return		the main class
   */
  public String getMainClass() {
    return m_MainClass;
  }

  /**
   * Sets whether to generate .deb package.
   *
   * @param debian	true if to generate .deb
   * @return		itself
   */
  public Main debian(boolean debian) {
    m_Debian = debian;
    return this;
  }

  /**
   * Returns whether to generate .deb package.
   *
   * @return		true if to generate .deb
   */
  public boolean getDebian() {
    return m_Debian;
  }

  /**
   * Sets the file containing the custom maven snippet file for generating the debian package.
   *
   * @param snippet	the file
   * @return		itself
   */
  public Main debianSnippet(File snippet) {
    m_DebianSnippet = snippet;
    return this;
  }

  /**
   * Returns the file containing the custom maven snippet file for generating the debian package.
   *
   * @return		the file
   */
  public File getDebianSnippet() {
    return m_DebianSnippet;
  }

  /**
   * Sets whether to generate .rpm package.
   *
   * @param redhat	true if to generate .rpm
   * @return		itself
   */
  public Main redhat(boolean redhat) {
    m_Redhat = redhat;
    return this;
  }

  /**
   * Returns whether to generate .rpm package.
   *
   * @return		true if to generate .rpm
   */
  public boolean getRedhat() {
    return m_Redhat;
  }

  /**
   * Sets the file containing the custom maven snippet file for generating the redhat package.
   *
   * @param snippet	the file
   * @return		itself
   */
  public Main redhatSnippet(File snippet) {
    m_RedhatSnippet = snippet;
    return this;
  }

  /**
   * Returns the file containing the custom maven snippet file for generating the redhat package.
   *
   * @return		the file
   */
  public File getRedhatSnippet() {
    return m_RedhatSnippet;
  }

  /**
   * Sets whether to list modules.
   *
   * @param listModules	true if to list modules
   * @return		itself
   */
  public Main listModules(boolean listModules) {
    m_ListModules = listModules;
    return this;
  }

  /**
   * Returns whether to list modules.
   *
   * @return		true if to list modules
   */
  public boolean getListModules() {
    return m_ListModules;
  }

  /**
   * Configures and returns the commandline parser.
   *
   * @return		the parser
   */
  protected ArgumentParser getParser() {
    ArgumentParser 		parser;

    parser = new ArgumentParser("Allows bootstrapping of ADAMS applications by simply supplying the modules.");
    parser.addOption("-m", "--maven_home")
      .required(false)
      .type(Type.EXISTING_DIR)
      .dest("maven_home")
      .metaVar("DIR")
      .help("The directory with a local Maven installation to use instead of the bundled one.");
    parser.addOption("-u", "--maven_user_settings")
      .required(false)
      .type(Type.EXISTING_FILE)
      .dest("maven_user_settings")
      .metaVar("FILE")
      .help("The file with the maven user settings to use other than $HOME/.m2/settings.xml.");
    parser.addOption("-j", "--java_home")
      .required(false)
      .type(Type.EXISTING_DIR)
      .dest("java_home")
      .metaVar("DIR")
      .help("The Java home to use for the Maven execution.");
    parser.addOption("-p", "--pom_template")
      .required(false)
      .type(Type.EXISTING_FILE)
      .dest("pom_template")
      .metaVar("FILE")
      .help("The alternative template for the pom.xml to use.");
    parser.addOption("-n", "--name")
      .required(false)
      .setDefault("adams")
      .dest("name")
      .help("The name to use for the project in the pom.xml. Also used as library directory and executable name when generating Debian/Redhat packages.");
    parser.addOption("-M", "--module")
      .required(true)
      .dest("modules")
      .help("The comma-separated list of ADAMS modules to use for the application, e.g.: adams-weka,adams-groovy,adams-excel");
    parser.addOption("-V", "--version")
      .required(true)
      .dest("version")
      .help("The version of ADAMS to use, e.g., '20.1.1' or '20.2.0-SNAPSHOT'.");
    parser.addOption("-d", "--dependency")
      .required(false)
      .multiple(true)
      .dest("dependencies")
      .metaVar("DEPENDENCY")
      .help("The additional maven dependencies to use for bootstrapping ADAMS (group:artifact:version), e.g.: nz.ac.waikato.cms.weka:kfGroovy:1.0.12");
    parser.addOption("-D", "--dependency-file")
      .required(false)
      .multiple(true)
      .type(Type.EXISTING_FILE)
      .dest("dependency_files")
      .metaVar("FILE")
      .help("The file(s) with additional maven dependencies to use for bootstrapping ADAMS (group:artifact:version), one dependency per line.");
    parser.addOption("-J", "--external-jar")
      .required(false)
      .multiple(true)
      .type(Type.EXISTING_FILE_OR_DIRECTORY)
      .dest("external_jars")
      .metaVar("JAR_OR_DIR")
      .help("The external jar or directory with jar files to also include in the application.");
    parser.addOption("-s", "--sources")
      .type(Type.BOOLEAN)
      .setDefault(false)
      .dest("sources")
      .help("If enabled, source jars of all the Maven artifacts will get downloaded as well and stored in a separated directory.");
    parser.addOption("-S", "--external-source")
      .required(false)
      .multiple(true)
      .type(Type.EXISTING_FILE_OR_DIRECTORY)
      .dest("external_sources")
      .metaVar("JAR_OR_DIR")
      .help("The external source jar or directory with source jar files to also include in the application.");
    parser.addOption("-o", "--output_dir")
      .required(true)
      .type(Type.DIRECTORY)
      .dest("output_dir")
      .metaVar("DIR")
      .help("The directory to output the bootstrapped ADAMS application in.");
    parser.addOption("-C", "--clean")
      .type(Type.BOOLEAN)
      .setDefault(false)
      .dest("clean")
      .help("If enabled, the 'clean' goals gets executed.");
    parser.addOption("-v", "--jvm")
      .required(false)
      .multiple(true)
      .dest("jvm")
      .help("The parameters to pass to the JVM before launching the application in the scripts.");
    parser.addOption("-c", "--main_class")
      .required(false)
      .setDefault("adams.gui.Main")
      .dest("main_class")
      .metaVar("CLASSNAME")
      .help("The main class to launch in the scripts.");
    parser.addOption("--deb")
      .type(Type.BOOLEAN)
      .setDefault(false)
      .dest("debian")
      .help("If enabled, a Debian .deb package is generated. Required tools: fakeroot, dpkg-deb");
    parser.addOption("--deb-snippet")
      .type(Type.EXISTING_FILE)
      .required(false)
      .dest("debian_snippet")
      .metaVar("FILE")
      .help("The custom Maven pom.xml snippet for generating a Debian package.");
    parser.addOption("--rpm")
      .type(Type.BOOLEAN)
      .setDefault(false)
      .dest("redhat")
      .help("If enabled, a Redhat .rpm package is generated.");
    parser.addOption("--rpm-snippet")
      .type(Type.EXISTING_FILE)
      .required(false)
      .dest("rpm_snippet")
      .metaVar("FILE")
      .help("The custom Maven pom.xml snippet for generating a Redhat package.");
    parser.addOption("-l", "--list_modules")
      .type(Type.BOOLEAN)
      .setDefault(false)
      .dest("list_modules")
      .help("If enabled, all currently available ADAMS modules are output (all other options get ignored).");

    return parser;
  }

  /**
   * Sets the parsed options.
   *
   * @param ns		the parsed options
   * @return		if successfully set
   */
  protected boolean setOptions(Namespace ns) {
    mavenHome(ns.getFile("maven_home"));
    mavenUserSettings(ns.getFile("maven_user_settings"));
    javaHome(ns.getFile("java_home"));
    outputDir(ns.getFile("output_dir"));
    clean(ns.getBoolean("clean"));
    pomTemplate(ns.getFile("pom_template"));
    jvm(ns.getList("jvm"));
    name(ns.getString("name"));
    modules(ns.getString("modules"));
    version(ns.getString("version"));
    dependencies(ns.getList("dependencies"));
    dependencyFiles(ns.getList("dependency_files"));
    externalJars(ns.getList("external_jars"));
    sources(ns.getBoolean("sources"));
    externalSources(ns.getList("external_sources"));
    mainClass(ns.getString("main_class"));
    debian(ns.getBoolean("debian"));
    debianSnippet(ns.getFile("debian_snippet"));
    redhat(ns.getBoolean("redhat"));
    redhatSnippet(ns.getFile("redhat_snippet"));
    listModules(ns.getBoolean("list_modules"));
    return true;
  }

  /**
   * Returns whether help got requested when setting the options.
   *
   * @return		true if help got requested
   */
  public boolean getHelpRequested() {
    return m_HelpRequested;
  }

  /**
   * Parses the options and configures the object.
   *
   * @param options	the command-line options
   * @return		true if successfully set (or help requested)
   */
  public boolean setOptions(String[] options) {
    return setOptions(options, false);
  }

  /**
   * Parses the options and configures the object.
   *
   * @param options	the command-line options
   * @param noErrors 	whether to suppress exceptions
   * @return		true if successfully set (or help requested)
   */
  public boolean setOptions(String[] options, boolean noErrors) {
    ArgumentParser 	parser;
    Namespace 		ns;

    m_HelpRequested = false;
    parser          = getParser();
    try {
      ns = parser.parseArgs(options, false, noErrors);
    }
    catch (ArgumentParserException e) {
      parser.handleError(e);
      m_HelpRequested = parser.getHelpRequested();
      return m_HelpRequested;
    }

    return setOptions(ns);
  }

  /**
   * Initializes the dependencies.
   *
   * @return		the dependencies
   */
  protected String initDependencies() {
    if (m_Modules.trim().isEmpty())
      return "No modules provided!";

    m_AllDependencies = new ArrayList<>();
    if (m_Modules.contains(",")) {
      for (String module : m_Modules.split(","))
	m_AllDependencies.add("nz.ac.waikato.cms.adams:" + module.trim() + ":" + m_Version);
    }
    else {
      m_AllDependencies.add("nz.ac.waikato.cms.adams:" + m_Modules.trim() + ":" + m_Version);
    }

    if (m_Dependencies != null)
      m_AllDependencies.addAll(m_Dependencies);

    return null;
  }

  /**
   * Returns the home directory for the application.
   * Can be overriding with the {@link #HOME_DIR_ENV} environment variable.
   *
   * @return		the directory
   */
  public String homeDir() {
    String	result;

    if (System.getenv(HOME_DIR_ENV) != null) {
      result = System.getenv(HOME_DIR_ENV);
    }
    else {
      result = System.getProperty("user.home");

      if (!SystemUtils.IS_OS_WINDOWS)
	result += "/.local/share";

      result += "/instant-adams";
    }

    return result;
  }

  /**
   * Initializes the maven user settings.
   *
   * @return		null if sucessful, otherwise error message
   */
  protected String initMavenUserSettings() {
    File	settings;
    Response 	r;

    if (m_MavenUserSettings != null) {
      m_ActMavenUserSettings = m_MavenUserSettings;
    }
    else {
      settings = new File(homeDir() + "/settings.xml");
      if (!settings.exists()) {
        try {
          settings.getParentFile().mkdirs();
	  r = Requests.get(USER_SETTINGS_URL).execute();
	  ((BasicResponse) r).saveBody(settings);
	  m_ActMavenUserSettings = settings;
	}
	catch (Exception e) {
          getLogger().log(Level.SEVERE, "Failed to download Maven user settings from: " + USER_SETTINGS_URL, e);
          return "Failed to download Maven user settings from: " + USER_SETTINGS_URL;
	}
      }
      else {
        m_ActMavenUserSettings = settings;
      }
    }

    return null;
  }

  /**
   * Initializes the pom template.
   *
   * @return		null if sucessful, otherwise error message
   */
  protected String initPomTemplate() {
    String	path;

    if (m_PomTemplate != null) {
      m_ActPomTemplate = m_PomTemplate;
    }
    else {
      try {
	path = Files.extractTo(RESOURCES, POMTEMPLATE_FILE, System.getProperty("java.io.tmpdir"));
	m_ActPomTemplate = new File(path);
      }
      catch (Exception e) {
        getLogger().log(Level.SEVERE, "Failed to extract pom.xml template!", e);
        return "Failed to extract pom.xml template!";
      }
    }

    return null;
  }

  /**
   * Extracts the modules from the pom xml string.
   *
   * @param pom		the pom.xml string to parse
   * @return		the modules that were found
   */
  protected List<String> extractModules(String pom) {
    List<String>	result;
    String[]		lines;

    result = new ArrayList<>();
    lines  = pom.split("\n");
    for (String line: lines) {
      if (line.contains("<module>")) {
        line = line.substring(line.indexOf('>') + 1);
        line = line.substring(0, line.indexOf('<'));
        if (!line.contains("$"))
	  result.add(line);
      }
    }

    Collections.sort(result);

    return result;
  }

  /**
   * Outputs the ADAMS modules in the console.
   *
   * @param url 	the URL to grab
   * @param title 	the title to use in the console
   * @return		null if successful, otherwise error message
   */
  protected String outputModules(String url, String title) {
    BasicResponse	r;
    List<String>	modules;
    int			i;

    try {
      r = Requests.get(url)
	.allowRedirects(true)
	.execute();
      if (r.ok()) {
        modules = extractModules(r.text());
        if (modules.size() == 0) {
          return "Failed to extract any modules from: " + ADAMS_BASE_URL;
	}
	else {
          System.out.println("\n" + title + ":");
          for (i = 0; i < modules.size(); i++) {
            if (i > 0)
              System.out.print(", ");
            System.out.print(modules.get(i));
	  }
	  System.out.println();
	}
      }
      else {
        return "Failed to load URL (status: " + r.statusCode() + ": " + r.statusMessage() + "): " + url;
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to extract modules from: " + url, e);
    }

    return null;
  }

  /**
   * Outputs all available ADAMS modules in the console.
   *
   * @return		null if successful, otherwise error message
   */
  public String outputModules() {
    String		result;

    System.out.println("\nAvailable modules:");

    if ((result = outputModules(ADAMS_BASE_URL, "adams-base")) != null)
      return result;
    if ((result = outputModules(ADAMS_ADDONS_URL, "adams-addons")) != null)
      return result;
    if ((result = outputModules(ADAMS_LTS_URL, "adams-lts")) != null)
      return result;
    if ((result = outputModules(ADAMS_SPECTRAL_BASE_URL, "adams-spectral-base")) != null)
      return result;

    System.out.println("\nNote:\nLTS and non-LTS modules (e.g., 'adams-weka-lts' and 'adams-weka') cannot be mixed.");

    return null;
  }

  /**
   * Performs the bootstrapping.
   *
   * @return		null if successful, otherwise error message
   */
  protected String doExecute() {
    String				result;
    com.github.fracpete.bootstrapp.Main	main;

    if (m_ListModules)
      return outputModules();

    if ((result = initMavenUserSettings()) != null)
      return result;
    if ((result = initDependencies()) != null)
      return result;
    if ((result = initPomTemplate()) != null)
      return result;

    main = new com.github.fracpete.bootstrapp.Main()
      .mainClass(m_MainClass)
      .scripts((m_MainClass != null) && !m_MainClass.trim().isEmpty())
      .javaHome(m_JavaHome)
      .mavenHome(getMavenHome())
      .mavenUserSettings(m_ActMavenUserSettings)
      .name(m_Name)
      .dependencies(m_AllDependencies)
      .dependencyFiles(m_DependencyFiles)
      .externalJars(m_ExternalJars)
      .outputDir(getOutputDir())
      .clean(m_Clean)
      .springBoot(false)
      .launch(false)
      .sources(getSources())
      .externalSources(m_ExternalSources)
      .jvm(m_JVM)
      .debian(m_Debian)
      .debianSnippet(m_DebianSnippet)
      .redhat(m_Redhat)
      .redhatSnippet(m_RedhatSnippet);

    return main.execute();
  }

  /**
   * Performs the bootstrapping.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute() {
    String		result;

    result = doExecute();
    if (result != null)
      getLogger().severe(result);

    return result;
  }

  /**
   * Executes the bootstrapping with the specified command-line arguments.
   *
   * @param args	the options to use
   */
  public static void main(String[] args) {
    Main main = new Main();

    // modules listed?
    main.setOptions(args, true);
    if (main.getListModules()) {
      main.outputModules();
      System.exit(0);
    }

    if (!main.setOptions(args)) {
      System.err.println("Failed to parse options!");
      System.exit(1);
    }
    else if (main.getHelpRequested()) {
      System.exit(0);
    }

    String result = main.execute();
    if (result != null) {
      System.err.println("Failed to perform bootstrapping:\n" + result);
      System.exit(2);
    }
  }
}
