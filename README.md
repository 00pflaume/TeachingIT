# TeachingIt
TeachingIt is a self-learn CMS which is intended to be used by schools to help students to learn better on their own.

## What exactly is TeachingIt
I created TeachingIt for my school as a school project. TeachingIt is a webserver written in Java, which has a very rich api and plugin-system. Developers can add their own functionality through plugins.

## Installation
### Installation Requirements
* [Java 7](https://www.java.com/download/) or higher
* A MySQL server
* A TeachingIt theme. I created a very simple example theme which can be found [here](https://github.com/Simonsator/TeachingIt-Standard-Theme/)
### How To Install
Execute the jar file. The first time it is starting it will create a file called "config.properties". Please enter all required data into this file. After that you need to place a "TeachingIt theme" into the theme folder and name it theme.jar. I created an example theme which can be found [here](https://github.com/Simonsator/TeachingIt-Standard-Theme/). You might want to install some plugins now to add some functionality to TeachingIt. One plugin you might want to install is [essentials](https://github.com/Simonsator/TeachingIt-Essentials/). It adds a way for users to create an account and to log into one. After that you just need to start the server again by executing the TeachingIt jar file again.
## Commands
The following commands are included in TeachingIt_
* stop - Stops the server
## Building
TeachingIt uses [Maven](https://maven.apache.org/) to handle dependencies & building.
#### Requirements
* Java 8 JDK or newer
* Git
