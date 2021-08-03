package dev.binclub.fps.client.utils.gl

import dev.binclub.fps.client.utils.RenderBindable
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11.GL_FALSE
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL40.*
import org.lwjgl.opengl.GL30.*
import java.lang.StringBuilder

/**
 * @author cookiedragon234 05/Jul/2020
 */
class GlShader private constructor(
	val programId: Int,
	val vertexShaderId: Int,
	val fragmentShaderId: Int
): RenderBindable {
	val uniforms = HashMap<String, Int>()
	
	override inline fun bind() {
		glUseProgram(programId)
	}
	
	override inline fun unbind() {
		glUseProgram(0)
	}
	
	
	fun createUniform(uniformName: String): Int =
		uniforms.getOrPut(uniformName) {
			val loc = glGetUniformLocation(programId, uniformName)
			if (loc < 0) error("Couldn't find uniform $uniformName")
			loc
		}
	
	operator fun set(uniformName: String, value: Int) {
		glUniform1i(createUniform(uniformName), value)
	}
	operator fun set(uniformName: String, value: Boolean) {
		glUniform1i(createUniform(uniformName), if (value) 1 else 0)
	}
	operator fun set(uniformName: String, value: Vec3d) {
		glUniform3f(createUniform(uniformName), value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
	}
	
	fun finalize() {
		unbind()
		glDeleteProgram(programId)
	}
	
	companion object {
		fun createShader(name: String): GlShader {
			val source = GlShader::class.java.getResourceAsStream("/shaders/$name.shader")
				.readBytes()
				.toString(Charsets.UTF_8)
			
			val vertexSource = StringBuilder(source.length / 2)
			val fragmentSource = StringBuilder(source.length / 2)
			
			var mode = -1 // 0 = vertex, 1 = fragment
			
			for (line in source.lineSequence()) {
				when (line.trim()) {
					"#shader vert" -> {
						mode = 0
					}
					"#shader frag" -> {
						mode = 1
					}
					else -> {
						when (mode) {
							0 -> vertexSource.append(line).append('\n')
							1 -> fragmentSource.append(line).append('\n')
							else -> {
								error("Mode ($mode), line ($line)")
							}
						}
					}
				}
			}
			
			if (vertexSource.isBlank() || fragmentSource.isBlank()) {
				error("Invalid sources")
			}
			
			val vertexId = glCreateShader(GL_VERTEX_SHADER)
			val fragmentId = glCreateShader(GL_FRAGMENT_SHADER)
			
			glShaderSource(vertexId, vertexSource)
			glShaderSource(fragmentId, fragmentSource)
			
			glCompileShader(vertexId)
			glCompileShader(fragmentId)
			
			if (glGetShaderi(vertexId, GL_COMPILE_STATUS) == GL_FALSE) {
				error("Couldn't compile vertex shader $name (${glGetShaderInfoLog(vertexId, 1024)})")
			}
			if (glGetShaderi(fragmentId, GL_COMPILE_STATUS) == GL_FALSE) {
				error("Couldn't compile fragment shader $name (${glGetShaderInfoLog(fragmentId, 1024)})")
			}
			
			val programId = glCreateProgram()
			
			val shader = GlShader(programId, vertexId, fragmentId)
			
			glAttachShader(programId, vertexId)
			glAttachShader(programId, fragmentId)
			glLinkProgram(programId)
			
			if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
				error("Couldn't link shader $name (${glGetProgramInfoLog(programId, 1024)})")
			}
			
			glDetachShader(programId, vertexId)
			glDetachShader(programId, fragmentId)
			
			glValidateProgram(programId)
			if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE) {
				error("Couldn't validate program $name (${glGetProgramInfoLog(programId, 1024)})")
			}
			
			return shader
		}
	}
}
