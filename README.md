# instant-adams
Boostrapping ADAMS applications simply from list of modules, without compilation.

## Command-line

```commandline
Allows bootstrapping of ADAMS applications by simply supplying the modules.

Usage: [--help] [-m MAVEN_HOME]
       [-u MAVEN_USER_SETTINGS]
       [-j JAVA_HOME] -M MODULES -V VERSION [-d DEPENDENCIES...]
       [-D DEPENDENCY_FILES...] [-s] -o OUTPUT_DIR [-v JVM...] [-c MAIN_CLASS]

Options:
-m, --maven_home MAVEN_HOME
	The directory with a local Maven installation to use instead of the bundled one.

-u, --maven_user_settings MAVEN_USER_SETTINGS
	The file with the maven user settings to use other than $HOME/.m2/settings.xml.

-j, --java_home JAVA_HOME
	The Java home to use for the Maven execution.

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

-v, --jvm JVM
	The parameters to pass to the JVM before launching the application in the scripts.

-c, --main_class MAIN_CLASS
	The main class to launch in the scripts.
```


## Example

The following examples boostrap an ADAMS application with 
```
java -jar instant-adams-0.0.1-spring-boot.jar \
  -M adams-weka,adams-groovy,adams-excel \
  -V 20.2.0-SNAPSHOT \
  -o ./out \
  -v -Xmx1g
```

And using Java:

```java
import adams.bootstrap.Main;
import java.io.File;

public class BootstrapTest {
  
  public static void main(String[] args) {
    Main main = new Main()
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


## Releases

* [0.0.1](https://github.com/waikato-datamining/instant-adams/releases/download/instant-adams-0.0.1/instant-adams-0.0.1-spring-boot.jar)


## Maven

```xml
    <dependency>
      <groupId>nz.ac.waikato.cms.adams</groupId>
      <artifactId>instant-adams</artifactId>
      <version>0.0.1</version>
    </dependency>
```
