# Work In Progress [And only working on Mac so far]
The state of this plugin is so much in ple-alpha that it has the potential to open a gap in the space-time continuum, 
or at the very least it could mess up your computer or project. **Use at your own risk.**
Also, Windows and Linux support will come in the future. 

# ramBuild
A small plugin to make builds in gradle run on ram instead of the hard drive

# Install

If you are using Gradle 2.1 or higher you can use the new syntax:

    plugins {
      id "com.pablisco.gradle.rambuild" version "0.1"
    }

Otherwise you can add ramBuild to your build on the traditional way:

    buildscript {
      repositories {
        maven {
          url "https://plugins.gradle.org/m2/"
        }
      }
      dependencies {
        classpath "gradle.plugin.com.pablisco.gradle:plugin:+" // <- change for latest version
      }
    }

    apply plugin: "com.pablisco.gradle.rambuild"
    
And that's it, now you are running your build from ram. It's recommended to do this on the root project to avoid conflicts.

# Customise

It's possible to change how the plugin works, like this on you gradle script:

    ramBuild {
        size = "1g" // This is used to determine the size of the drive (standard {k|m|g} pattern for KB, MB and GB)
        name = "unicorn" // This is the name used when the drive is mounted
    }
    
# Why do I need this?

Given you have a generous amount of ram (higher than 4GB) this can help gradle run about 30% faster (comparing SATA SSD to ddr3). 
But more importantly, this allows your ssd drive to receive less pounding from the compiler. Given that a typical project 
has thousands of files on top of dependencies, we are looking at a large pile of tiny files that get copied and moved 
around like there is no tomorrow. 
