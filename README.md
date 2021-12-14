# Jaguar

## 1) Introduction
Jaguar (JAva coveraGe faUlt locAlization Ranking) is an open source **SFL** tool for Java, which is available as an Eclipse plug-in and as a command line tool.
Jaguar is composed of Jaguar Runner, which collects and generates lists of suspicious program elements, and Jaguar Viewer, which presents the visual information for debugging.
Jaguar supports both control-flow and data-flow spectra (i.e., source code lines and definition-use associations, respectively).

### Jaguar Runner
Jaguar Runner collects control-flow spectrum using the **JaCoCo** coverage tool (eclemma.org/jacoco). To collect data flow spectra, Jaguar uses the ba-dua coverage tool (github.com/saeg/ba-dua). The **ba-dua** tool uses the **Bitwise Algorithm** (BA), which tracks duas at low execution costs by using inexpensive data structures. BA enables the tracking of only those duas that are potentially covered at each node visited during a program execution. Jaguar invokes unit tests of a subject program and collects spectra data for each element covered during the test execution. It iterates over the classes, methods, and then lines or definition-use associations (duas).
Jaguar then calculates the suspiciousness score of each element (line or dua) according to a chosen ranking metric.
### Jaguar Viewer
Jaguar Viewer provides visual information of the SFL lists for debugging within the Eclipse IDE. It colors the suspicious program entities according to their suspiciousness scores. The colors represent four score levels: red (danger) are the most suspicious entities, orange (warning) are those with high suspiciousness, yellow (caution) are the entities with a moderate suspiciousness level, and green (safety) are the least suspicious ones. The color schema was based on that of the Tarantula tool. Additionally, we included the orange color, as done to increase the distinction among the program elements. The suspiciousness range for the colors is:

red ≥ 0.75 

0.75 > orange ≥ 0.5 

0.5 > yellow ≥ 0.25 

and green < 0.25

The Jaguar Viewer also has two filtering widgets to facilitate debugging: a text search box to filter by terms of the source code and a slider to narrow down entities by suspiciousness score. 



Note:
SFL - Spectrum-based fault localization is a promising auto- mated debugging approach due to its relatively low overhead in test execution time. SFL techniques use data collected during a test suite execution, called program spectra, to infer which source code elements are more likely to contain a bug


## 2)	The html
### A. To run the project you need follow this steps:
a) In https://github.com/SIN5005-EngSoft2021/jaguar.git clone the project using
````
$git clone
````

b) Now use 
````
$make prepare
````
and to build the project:
````
$make build_core
````
The first time the command $make prepare is executed takes longer.

c) Now to run a jaguar default example, use 
````
$make run
`````

 
### B. Jaguar home screen elements:
I) Heuristic: Heuristic used to detect possible snippets of suspicious code. It is possible to configure this one.

II) Requirement type - LINE: parse line by line.

III) Test: displays how many tests passed and failed.

IV) Line color Susp: Filter to show lines with a certain suspension value.

V) CLASS : Analyzed class.

VI) BAR: color symbolized by the suspension level.

VII) SUSP: Suspension of code.

VIII) C.E.F.: Number of test cases that failed and executed the suspicious code snippet.

IX) C.E.P. : Number of test cases that passed and ran the suspect component.

X) C.N.F: Number of test cases that did not run the suspect component but failed.

XI) C.N.P.: Number of test cases that passed and did not run the suspect component.

XII) Items per table: Filter how many project classes will be presented. If there are more classes than the filtered amount, use the arrows at the bottom of the page to explore the remaining classes. Clicking on the link in CLASS displays the suspicious lines.

XIII) LOC: parsed line of code. By clicking on the value in LOC, the user will be directed to the suspicious line; When hovering over the line, a description of the suspect will appear.

XIV) Method Signature: signature of the method where the suspicious line is contained.


### C. Modifying the Heuristic
It is possible to change the Jaguar heuristics through the parameters: 
I) help : find out more information about Jaguar.

II) dataflow / –df: run dataflow analysis/collection.

III) outputType: / --ot: output only XML or HTML.

IV) output / --o: output file name.

V) projectDir / --p: location of the file to be analyzed by Jaguar.

VI) testsListFile / --tf: file containing the test list.

VII) includes / --i: all project packages that will be instrumented in the controlflow and dataflow analysis.

VIII) loglevel/ --l: log level for analysis ( ERROR, INFO, DEBUG, TRACE).

IX) classpath/ --cp: project classpath.

X) heuristic / -h : By default use Tarantula.

XI) classDir/ --c : Path of compiled classes.

XII) testDir/ --t: Path of compiled tests.


### D.	To change the parameters simplified:

I) cat Makefile.

II) Look for “Run a simple example use of Jaguar Core”.

III) Copy the displayed link -> Script Shell that runs Jaguar.
IV) $vim link.

V) Add, for example, the new heuristic to be used: -- heuristic "Op".

This can be done for dataflow and controlflow.

### E.	Bellow, we have other heuristics
DRTHeuristic

JaccardHeuristic

Kulczynski2Heuristic

McConHeuristic

MinusHeuristic

OchiaHeuristic

OpHeuristic

TarantulaHeuristic

Wong3Heuristic

ZoltarHeurisitc

## 3)	Build/plugins

Add the plugin to build/plugins in your pom.xml
`````
<plugins>
        <plugin> 
        <groupId>br.usp.each.saeg.jaguar.maven.plugin</groupId>
        <artifactId>jaguar-maven-plugin</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <executions> 
         <execution>
         <id>Jaguar-DataFlow</id>
         <goals>
               <goal>jaguarVerify </goal> 
         </goals>
         <configuration>
               <logLevel>ERROR</logLevel>
                <type>DataFlow</type>
                <format>XML</format>
           </configuration> 
           </execution>
          </executions>
          </plugin>
</plugins>
`````

## Additional Parameters

There are additional parameters to modify report generation. They are:

logLevel: log level for analysis (TRACE, INFO, DEBUG or ERROR ) 

type: DataFlow or  ControlFlow

format: XML or HTML
