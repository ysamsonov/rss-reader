# RSS Reader
![Travis build](https://travis-ci.org/ysamsonov/rss-reader.svg?branch=develop)


# Usage
First of all, clone this repo and go to the project directory.  
After that there are several options how to run.

## Pre-requirements
You need installed JDK 11 on your machine.

## Run from IntelliJ IDEA (or other ide)
1. Enable Annotation processing  
IntelliJ IDEA: 
    * Go to Preferences
    * Select tab Build, Execution, Deployment
    * Select Enable annotation processing
1. Go to `com.github.ysamsonov.rssreader.Application`
1. Run it use debug/normal mode

## Run from Gradle 
For Linux/MacOS use bash command:  
```bash
./gradlew :rss-reader-core:runApp
```
For Windows use cmd command:  
```batch
gradlew.bat :rss-reader-core:runApp
```

## Build and run application
1. Need to build application see [Build](#build).
2. And run using Java 11  
```bash
java -jar rss-reader-core.jar
```

# Options
The following options are available for application.  

|Name|Description|Default value|
|---|---|---|
|`rssreader.config.location`|Configuration file location|`$(pwd)/reader-config.json`|
|`rssreader.feed.synchronizer.pool.size`|Pool size for feed synchronization|4|
|`rssreader.feed.writer.batch.size`|Batch size for feed writer|10|

Example of using:
```bash
java -Drssreader.feed.synchronizer.pool.size=1 -jar rss-reader-core.jar
```

# Build
<ol>
<li>
Run command to build Fat Jar

For Linux/MacOS use bash command:  
```bash
./gradlew :rss-reader-core:fatJar
```
For Windows use cmd command:  
```batch
gradlew.bat :rss-reader-core:fatJar
```
</li>

<li>Go to <code>rss-reader-core/build/libs</code> folder and take jar-file</li>
</ol>

# Contributing
Contributions are always welcome!

# Licensing
This project is licensed under [GNU General Public License v3.0](LICENSE).
