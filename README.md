# instant-adams
Boostrapping ADAMS applications simply from list of modules, without compilation.
Additional jars (binary and sources) can be injected into the process as well.
You can either generate cross-platform applications, by using shell scripts for 
Linux/Mac and batch files for Windows, or Linux packages for Debian (`.deb`) 
and/or Redhat (`.rpm`).

## Command-line

```commandline
Allows bootstrapping of ADAMS applications by simply supplying the modules.


Usage: [--help] [-m DIR] [-u FILE] [-j DIR] [-p FILE] [-n NAME]
       -M MODULES -V VERSION [-d DEPENDENCY...] [-D FILE...]
       [-J JAR_OR_DIR...] [-s] [-S JAR_OR_DIR...] -o DIR [-C]
       [-v JVM...] [-c CLASSNAME] [--deb] [--deb-snippet FILE] [--rpm]
       [--rpm-snippet FILE] [-l]

Options:
-m, --maven_home DIR
	The directory with a local Maven installation to use instead of the
	bundled one.

-u, --maven_user_settings FILE
	The file with the maven user settings to use other than
	$HOME/.m2/settings.xml.

-j, --java_home DIR
	The Java home to use for the Maven execution.

-p, --pom_template FILE
	The alternative template for the pom.xml to use.

-n, --name NAME
	The name to use for the project in the pom.xml. Also used as library
	directory and executable name when generating Debian/Redhat packages.

-M, --module MODULES
	The comma-separated list of ADAMS modules to use for the application,
	e.g.: adams-weka,adams-groovy,adams-excel

-V, --version VERSION
	The version of ADAMS to use, e.g., '20.1.1' or '20.2.0-SNAPSHOT'.

-d, --dependency DEPENDENCY
	The additional maven dependencies to use for bootstrapping ADAMS
	(group:artifact:version), e.g.: nz.ac.waikato.cms.weka:kfGroovy:1.0.12

-D, --dependency-file FILE
	The file(s) with additional maven dependencies to use for bootstrapping
	ADAMS (group:artifact:version), one dependency per line.

-J, --external-jar JAR_OR_DIR
	The external jar or directory with jar files to also include in the
	application.

-s, --sources
	If enabled, source jars of all the Maven artifacts will get downloaded
	as well and stored in a separated directory.

-S, --external-source JAR_OR_DIR
	The external source jar or directory with source jar files to also
	include in the application.

-o, --output_dir DIR
	The directory to output the bootstrapped ADAMS application in.

-C, --clean
	If enabled, the 'clean' goals gets executed.

-v, --jvm JVM
	The parameters to pass to the JVM before launching the application in
	the scripts.

-c, --main_class CLASSNAME
	The main class to launch in the scripts.

--deb
	If enabled, a Debian .deb package is generated. Required tools: fakeroot,
	dpkg-deb

--deb-snippet FILE
	The custom Maven pom.xml snippet for generating a Debian package.

--rpm
	If enabled, a Redhat .rpm package is generated.

--rpm-snippet FILE
	The custom Maven pom.xml snippet for generating a Redhat package.

-l, --list_modules
	If enabled, all currently available ADAMS modules are output (all other
	options get ignored).
```


## Examples

## Cross-platform

The following examples bootstrap an ADAMS application (from the 20.1.1 release) 
with support for Weka, Groovy and Excel:
 
```
java -jar instant-adams-0.1.3-spring-boot.jar \
  -C \
  -M adams-weka,adams-groovy,adams-excel \
  -V 20.1.1 \
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
      .version("20.1.1")
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
java -jar instant-adams-0.1.3-spring-boot.jar \
  -C \
  -M adams-weka,adams-groovy,adams-excel \
  -n adams-test \
  -V 20.1.1 \
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
      .version("20.1.1")
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

* [0.1.3](https://github.com/waikato-datamining/instant-adams/releases/download/instant-adams-0.1.3/instant-adams-0.1.3-spring-boot.jar)
* [0.1.2](https://github.com/waikato-datamining/instant-adams/releases/download/instant-adams-0.1.2/instant-adams-0.1.2-spring-boot.jar)
* [0.1.1](https://github.com/waikato-datamining/instant-adams/releases/download/instant-adams-0.1.1/instant-adams-0.1.1-spring-boot.jar)
* [0.1.0](https://github.com/waikato-datamining/instant-adams/releases/download/instant-adams-0.1.0/instant-adams-0.1.0-spring-boot.jar)
* [0.0.1](https://github.com/waikato-datamining/instant-adams/releases/download/instant-adams-0.0.1/instant-adams-0.0.1-spring-boot.jar)


## Maven

```xml
    <dependency>
      <groupId>nz.ac.waikato.cms.adams</groupId>
      <artifactId>instant-adams</artifactId>
      <version>0.1.3</version>
    </dependency>
```
