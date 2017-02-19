package com.pablisco.gradle.rambuild;

import org.gradle.api.*;

class RamBuildPlugin implements Plugin<Project> {

  def RAM_DISK_SIZE_MB = hasProperty('ramDiskSize') ? ramDiskSize.toInteger() : 512
  def RAM_DISK_NAME = "build"

  def file = { new File(it) }
    private Project project

    def run(String command, onSuccess) {
      project.logger.info "Running command: $command"
      command.execute(Collections.emptyList(), project.rootDir).with {
          waitFor()
          exitValue() ? project.logger.error("Error: ${err.text}") : onSuccess(getIn().text)
      }
  }

  def void apply(Project project) {

    // TODO: implement for linux and Windows
      this.project = project
      if (!file("/Volumes/$RAM_DISK_NAME").exists()) {
        project.logger.lifecycle "Creating ramDisk $RAM_DISK_NAME"
        run("hdiutil attach -nomount ram://${RAM_DISK_SIZE_MB * 2048}", { path ->
            run("diskutil erasevolume HFS+ $RAM_DISK_NAME $path", {
                project.logger.info "Shell: $it"
                project.logger.lifecycle "Success: Mounted at $path"
            })
        })
    }

    if (file("/Volumes/$RAM_DISK_NAME/").exists()) {
        project.rootProject.allprojects {
            buildDir = "/Volumes/$RAM_DISK_NAME/gradle/${rootProject.name}/${project.name}"
        }
    }
  }

}
