/*
 * MIT License
 *
 * Copyright (c) 2023 Redstonneur1256 (https://github.com/Redstonneur1256)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.redstonneur1256.gaw

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Attribute

class GradleAccessWidener : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("accessWidener", AccessWidenerExtension::class.java)
        extension.paths.convention(project.files())

        project.beforeEvaluate {
            val widened = Attribute.of("widened", Boolean::class.javaObjectType)

            project.dependencies.attributesSchema.attribute(widened)
            project.dependencies.artifactTypes.getByName("jar") {
                it.attributes.attribute(widened, false)
            }
            project.dependencies.registerTransform(AccessWidenerTransformer::class.java) {
                it.from.attribute(widened, false)
                it.to.attribute(widened, true)
                it.parameters.wideners.set(extension.paths.get().map { file -> file.readText() })
            }
            project.configurations.all {
                if (it.isCanBeResolved) {
                    it.attributes.attribute(widened, true)
                }
            }
        }
    }

}