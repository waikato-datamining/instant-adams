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

import com.github.fracpete.requests4j.Requests;
import com.github.fracpete.requests4j.response.BasicResponse;
import com.github.fracpete.requests4j.response.Response;
import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.ArgumentParserException;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.simpleargparse4j.Option.Type;
import org.apache.commons.lang.SystemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  /** the environment variable for the instant adams home directory. */
  public final static String HOME_DIR_ENV = "INSTANTADAMS_HOME";

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

  /** the JVM options. */
  protected List<String> m_JVM;

  /** the modules. */
  protected String m_Modules;

  /** the version to use. */
  protected String m_Version;

  /** whether to retrieve source jars or not. */
  protected boolean m_Sources;

  /** the dependencies. */
  protected List<String> m_Dependencies;

  /** the dependency files. */
  protected List<File> m_DependencyFiles;

  /** the dependencies. */
  protected List<String> m_AllDependencies;

  /** the main class to launch. */
  protected String m_MainClass;

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
    m_JavaHome             = null;
    m_OutputDir            = null;
    m_OutputDirMaven       = null;
    m_JVM                  = null;
    m_Modules              = null;
    m_Sources              = false;
    m_AllDependencies      = null;
    m_Dependencies         = null;
    m_DependencyFiles      = null;
    m_MainClass            = "adams.gui.Main";
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
   * Sets the modules to use for bootstrapping.
   *
   * @param modules	the modules (comma-separated list)
   * @return		itself
   */
  public Main modules(String modules) {
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

    if (modules != null) {
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
      .help("The directory with a local Maven installation to use instead of the bundled one.");
    parser.addOption("-u", "--maven_user_settings")
      .required(false)
      .type(Type.EXISTING_FILE)
      .dest("maven_user_settings")
      .help("The file with the maven user settings to use other than $HOME/.m2/settings.xml.");
    parser.addOption("-j", "--java_home")
      .required(false)
      .type(Type.EXISTING_DIR)
      .dest("java_home")
      .help("The Java home to use for the Maven execution.");
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
      .help("The additional maven dependencies to use for bootstrapping ADAMS (group:artifact:version), e.g.: nz.ac.waikato.cms.weka:kfGroovy:1.0.12");
    parser.addOption("-D", "--dependency-file")
      .required(false)
      .multiple(true)
      .type(Type.EXISTING_FILE)
      .dest("dependency_files")
      .help("The file(s) with additional maven dependencies to use for bootstrapping ADAMS (group:artifact:version), one dependency per line.");
    parser.addOption("-s", "--sources")
      .type(Type.BOOLEAN)
      .setDefault(false)
      .dest("sources")
      .help("If enabled, source jars of all the Maven artifacts will get downloaded as well and stored in a separated directory.");
    parser.addOption("-o", "--output_dir")
      .required(true)
      .type(Type.DIRECTORY)
      .dest("output_dir")
      .help("The directory to output the bootstrapped ADAMS application in.");
    parser.addOption("-v", "--jvm")
      .required(false)
      .multiple(true)
      .dest("jvm")
      .help("The parameters to pass to the JVM before launching the application in the scripts.");
    parser.addOption("-c", "--main_class")
      .required(false)
      .setDefault("adams.gui.Main")
      .dest("main_class")
      .help("The main class to launch in the scripts.");

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
    jvm(ns.getList("jvm"));
    modules(ns.getString("modules"));
    version(ns.getString("version"));
    dependencies(ns.getList("dependencies"));
    dependencyFiles(ns.getList("dependency_files"));
    sources(ns.getBoolean("sources"));
    mainClass(ns.getString("main_class"));
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
    ArgumentParser 	parser;
    Namespace 		ns;

    m_HelpRequested = false;
    parser          = getParser();
    try {
      ns = parser.parseArgs(options);
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
      for (String module : m_Modules.replace(" ", "").split(","))
	m_AllDependencies.add("nz.ac.waikato.cms.adams:" + module + ":" + m_Version);
    }
    else {
      m_AllDependencies.add("nz.ac.waikato.cms.adams:" + m_Modules + ":" + m_Version);
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
   * Performs the bootstrapping.
   *
   * @return		null if successful, otherwise error message
   */
  protected String doExecute() {
    String				result;
    com.github.fracpete.bootstrapp.Main	main;

    if ((result = initMavenUserSettings()) != null)
      return result;
    if ((result = initDependencies()) != null)
      return result;

    main = new com.github.fracpete.bootstrapp.Main()
      .mainClass(m_MainClass)
      .scripts((m_MainClass != null) && !m_MainClass.trim().isEmpty())
      .javaHome(m_JavaHome)
      .mavenHome(getMavenHome())
      .mavenUserSettings(m_ActMavenUserSettings)
      .dependencies(m_AllDependencies)
      .dependencyFiles(m_DependencyFiles)
      .outputDir(getOutputDir())
      .springBoot(false)
      .launch(false)
      .sources(getSources())
      .jvm(m_JVM);

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
