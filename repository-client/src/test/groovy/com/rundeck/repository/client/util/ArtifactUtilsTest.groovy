/*
 * Copyright 2018 Rundeck, Inc. (http://rundeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rundeck.repository.client.util

import com.rundeck.plugin.template.FilesystemArtifactTemplateGenerator
import com.rundeck.plugin.template.PluginType
import com.rundeck.repository.artifact.ArtifactType
import com.rundeck.repository.client.TestUtils
import spock.lang.Shared
import spock.lang.Specification


class ArtifactUtilsTest extends Specification {

    @Shared
    FilesystemArtifactTemplateGenerator generator

    def "get meta from uploaded file"() {
        setup:
        File buildDir = File.createTempDir()
        generator = new FilesystemArtifactTemplateGenerator()
        generator.generate("MyJavaPlugin", PluginType.java,"Notification",buildDir.absolutePath)
        generator.generate("MyScriptPlugin", PluginType.script,"FileCopier",buildDir.absolutePath)
        generator.generate("ManualZipScriptPlugin", PluginType.script,"FileCopier",buildDir.absolutePath)
        TestUtils.buildGradle(new File(buildDir, "myjavaplugin"))
        TestUtils.gradlePluginZip(new File(buildDir,"myscriptplugin"))
        TestUtils.zipDir(new File(buildDir,"manualzipscriptplugin").absolutePath)


        when:
        def jarMeta = ArtifactUtils.getMetaFromUploadedFile(new File(buildDir,"myjavaplugin/build/libs/myjavaplugin-0.1.0.jar"))
        def scriptMeta = ArtifactUtils.getMetaFromUploadedFile(new File(buildDir,"myscriptplugin/build/libs/myscriptplugin-0.1.0-SNAPSHOT.zip"))
        def manualZipScriptMeta = ArtifactUtils.getMetaFromUploadedFile(new File(buildDir,"manualzipscriptplugin.zip"))

        then:
        jarMeta.name == "MyJavaPlugin"
        jarMeta.artifactType == ArtifactType.JAVA_PLUGIN
        scriptMeta.name == "MyScriptPlugin"
        scriptMeta.artifactType == ArtifactType.SCRIPT_PLUGIN
        manualZipScriptMeta.name == "ManualZipScriptPlugin"
        manualZipScriptMeta.artifactType == ArtifactType.SCRIPT_PLUGIN



    }
}
