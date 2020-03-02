# instant-adams
Boostrapping ADAMS applications simply from list of modules, without compilation.
Additional jars (binary and sources) can be injected into the process as well.
You can either generate cross-platform applications, by using shell scripts for 
Linux/Mac and batch files for Windows, or Linux packages for Debian (`.deb`) 
and/or Redhat (`.rpm`).

## Command-line

```commandline
Allows bootstrapping of ADAMS applications by simply supplying the modules.

Usage: [--help] [-m MAVEN_HOME]
       [-u MAVEN_USER_SETTINGS]
       [-j JAVA_HOME] [-p POM_TEMPLATE] [-n NAME] -M MODULES -V VERSION
       [-d DEPENDENCIES...] [-D DEPENDENCY_FILES...] [-s] -o OUTPUT_DIR [-C]
       [-v JVM...] [-c MAIN_CLASS] [--deb] [--deb-snippet DEBIAN_SNIPPET] [--rpm]
       [--rpm-snippet RPM_SNIPPET] [-l]

Options:
-m, --maven_home MAVEN_HOME
	The directory with a local Maven installation to use instead of the bundled one.

-u, --maven_user_settings MAVEN_USER_SETTINGS
	The file with the maven user settings to use other than $HOME/.m2/settings.xml.

-j, --java_home JAVA_HOME
	The Java home to use for the Maven execution.

-p, --pom_template POM_TEMPLATE
	The alternative template for the pom.xml to use.

-n, --name NAME
	The name to use for the project in the pom.xml. Also used as library directory and executable name when generating Debian/Redhat packages.

-M, --module MODULES
	The comma-separated list of ADAMS modules to use for the application, e.g.: adams-weka,adams-groovy,adams-excel

-V, --version VERSION
	The version of ADAMS to use, e.g., '20.1.1' or '20.2.0-SNAPSHOT'.

-d, --dependency DEPENDENCIES
	The additional maven dependencies to use for bootstrapping ADAMS (group:artifact:version), e.g.: nz.ac.waikato.cms.weka:kfGroovy:1.0.12

-D, --dependency-file DEPENDENCY_FILES
	The file(s) with additional maven dependencies to use for bootstrapping ADAMS (group:artifact:version), one dependency per line.

-s, --sources
	If enabled, source jars of all the Maven artifacts will get downloaded as well and stored in a separated directory.

-o, --output_dir OUTPUT_DIR
	The directory to output the bootstrapped ADAMS application in.

-C, --clean
	If enabled, the 'clean' goals gets executed.

-v, --jvm JVM
	The parameters to pass to the JVM before launching the application in the scripts.

-c, --main_class MAIN_CLASS
	The main class to launch in the scripts.

--deb
	If enabled, a Debian .deb package is generated. Required tools: fakeroot, dpkg-deb

--deb-snippet DEBIAN_SNIPPET
	The custom Maven pom.xml snippet for generating a Debian package.

--rpm
	If enabled, a Redhat .rpm package is generated.

--rpm-snippet RPM_SNIPPET
	The custom Maven pom.xml snippet for generating a Redhat package.

-l, --list_modules
	If enabled, all currently available ADAMS modules are output (all other options get ignored).
```


## Examples

## Cross-platform

The following examples bootstrap an ADAMS application with support for
Weka, Groovy and Excel:
 
```
java -jar instant-adams-0.1.1-spring-boot.jar \
  -C \
  -M adams-weka,adams-groovy,adams-excel \
  -V 20.2.0-SNAPSHOT \
  -o ./out \
  -v -Xmx1g
```

And the same using Java:

```java
import adams.bootstrap.Main;
import java.io.File;

public class BootstrapTest {
  
  public static void main(String[] args) {
    Main main = new Main()
      .clean(true)
      .modules("adams-weka,adams-groovy,adams-excel")
      .version("20.2.0-SNAPSHOT")
      .outputDir(new File("./out"))
      .jvm("-Xmx1g");
    String result = main.execute();
    if (result != null)
      System.err.println(result);
  }
} 
```

### Debian package

The same, but compiled as Debian (.deb) package:
 
```
java -jar instant-adams-0.1.1-spring-boot.jar \
  -C \
  -M adams-weka,adams-groovy,adams-excel \
  -n adams-test \
  -V 20.2.0-SNAPSHOT \
  -o ./out \
  -v -Xmx1g \
  --deb
```

And the same using Java:

```java
import adams.bootstrap.Main;
import java.io.File;

public class BootstrapTest {
  
  public static void main(String[] args) {
    Main main = new Main()
      .clean(true)
      .modules("adams-weka,adams-groovy,adams-excel")
      .name("adams-test")
      .version("20.2.0-SNAPSHOT")
      .outputDir(new File("./out"))
      .jvm("-Xmx1g")
      .debian(true);
    String result = main.execute();
    if (result != null)
      System.err.println(result);
  }
} 
```

**Note:** The *name* is used as package name and executable for launching the
application, in this case you will get `/usr/bin/weka`.


## Releases

* [0.1.1](https://github.com/waikato-datamining/instant-adams/releases/download/instant-adams-0.1.1/instant-adams-0.1.1-spring-boot.jar)
* [0.1.0](https://github.com/waikato-datamining/instant-adams/releases/download/instant-adams-0.1.0/instant-adams-0.1.0-spring-boot.jar)
* [0.0.1](https://github.com/waikato-datamining/instant-adams/releases/download/instant-adams-0.0.1/instant-adams-0.0.1-spring-boot.jar)


## Maven

```xml
    <dependency>
      <groupId>nz.ac.waikato.cms.adams</groupId>
      <artifactId>instant-adams</artifactId>
      <version>0.1.1</version>
    </dependency>
```
