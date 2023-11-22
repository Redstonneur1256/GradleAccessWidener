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

import net.fabricmc.accesswidener.AccessWidener
import net.fabricmc.accesswidener.AccessWidenerClassVisitor
import net.fabricmc.accesswidener.AccessWidenerReader
import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream

abstract class AccessWidenerTransformer : TransformAction<AccessWidenerTransformer.Parameters> {

    interface Parameters : TransformParameters {
        @get:Input
        val wideners: ListProperty<String>
    }

    override fun transform(outputs: TransformOutputs) {
        val widener = AccessWidener()
        val reader = AccessWidenerReader(widener)
        parameters.wideners.get().forEach {
            reader.read(it.toByteArray())
        }

        val targets = widener.targets.map { it.replace('.', '/') + ".class" }.toSet()
        val input = inputArtifact.get().asFile

        val inputJar = JarInputStream(input.inputStream())
        val outputJar = JarOutputStream(outputs.file("widened-${input.name}").outputStream())

        try {
            var entry = inputJar.nextEntry
            while (entry != null) {
                outputJar.putNextEntry(entry)

                if (targets.contains(entry.name)) {
                    outputJar.write(transformClass(widener, inputJar.readBytes()))
                } else {
                    inputJar.copyTo(outputJar)
                }

                entry = inputJar.nextEntry
            }
        } finally {
            inputJar.close()
            outputJar.close()
        }
    }

    private fun transformClass(widener: AccessWidener, bytes: ByteArray): ByteArray {
        val reader = ClassReader(bytes)
        val writer = ClassWriter(0)
        val visitor = AccessWidenerClassVisitor.createClassVisitor(Opcodes.ASM9, writer, widener)
        reader.accept(visitor, 0)
        return writer.toByteArray()
    }

    @get:InputArtifact
    abstract val inputArtifact: Provider<FileSystemLocation>

}