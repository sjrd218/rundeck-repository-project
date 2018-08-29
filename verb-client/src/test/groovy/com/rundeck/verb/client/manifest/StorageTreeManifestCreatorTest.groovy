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
package com.rundeck.verb.client.manifest

import com.dtolabs.rundeck.core.storage.BaseStreamResource
import com.dtolabs.rundeck.core.storage.ResourceMeta
import com.dtolabs.rundeck.core.storage.StorageConverterPluginAdapter
import com.dtolabs.rundeck.core.storage.StorageTimestamperConverter
import com.rundeck.verb.client.TestUtils
import com.rundeck.verb.client.artifact.StorageTreeArtifactInstaller
import com.rundeck.verb.client.repository.StorageTreeVerbArtifactRepository
import com.rundeck.verb.manifest.ArtifactManifest
import org.rundeck.storage.api.ContentFactory
import org.rundeck.storage.api.HasInputStream
import org.rundeck.storage.conf.TreeBuilder
import org.rundeck.storage.data.DataContent
import org.rundeck.storage.data.DataUtil
import org.rundeck.storage.data.file.FileTreeUtil
import spock.lang.Specification


class StorageTreeManifestCreatorTest extends Specification {

    def "Create Manifest"() {
        setup:
        File repoBase = File.createTempDir()
        println repoBase.absolutePath
//        File artifactBase = new File(repoBase,"content/artifacts")
//        artifactBase.mkdirs()
//        File artifact = new File(artifactBase,"4819d98fea70-0.1.yaml")
//        artifact.createNewFile()
//        artifact << getClass().getClassLoader().getResourceAsStream("rundeck-verb-artifact.yaml")
        def tree = FileTreeUtil.forRoot(repoBase, TestUtils.resourceFactory())
        TreeBuilder tbuilder = TreeBuilder.builder(tree)
        def timestamptree = tbuilder.convert(new StorageConverterPluginAdapter(
                "builtin:timestamp",
                new StorageTimestamperConverter()
        )).build()
        timestamptree.createResource("artifacts/4819d98fea70-0.1.yaml",DataUtil.withStream(getClass().getClassLoader().getResourceAsStream("rundeck-verb-artifact.yaml"),[:],TestUtils.resourceFactory()))

        when:
        StorageTreeManifestCreator manifestCreator = new StorageTreeManifestCreator(timestamptree)
        ArtifactManifest manifest = manifestCreator.createManifest()

        then:
        manifest.entries.size() == 1

    }

}
