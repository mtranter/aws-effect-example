import java.security.MessageDigest

import sbtassembly.AssemblyPlugin.autoImport.{PathList, assemblyJarName, assemblyMergeStrategy}
import sbtassembly.AssemblyKeys._
import sbt._
import Keys._
import sbtassembly.MergeStrategy

object Assembly {
  
    val mimeTypeMerge = new MergeStrategy {
      override def name: String = "mimetypemerge"
  
      override def apply(tempDir: File, path: String, files: Seq[File]): Either[String, Seq[(File, String)]] = {
        val lines = files flatMap (IO.readLines(_, IO.utf8)) filterNot(_.startsWith("#"))
        val unique = lines.distinct.sorted
        val file = createMergeTarget(tempDir, path)
        IO.writeLines(file, unique, IO.utf8)
        Right(Seq(file -> path))
      }
  
      @inline def createMergeTarget(tempDir: File, path: String): File = {
        val file = new File(tempDir, "sbtMergeTarget-" + sha1string(path) + ".tmp")
        if (file.exists) {
          IO.delete(file)
        }
        file
      }
      private def sha1 = MessageDigest.getInstance("SHA-1")
      private def sha1string(s: String): String = bytesToSha1String(s.getBytes("UTF-8"))
      private def bytesToSha1String(bytes: Array[Byte]): String =
        bytesToString(sha1.digest(bytes))
      private def bytesToString(bytes: Seq[Byte]): String =
        bytes map {"%02x".format(_)} mkString
    }
  
    def settings = Seq(
      test in assembly := {},
      assemblyJarName in assembly := "lambda.jar",
      assemblyMergeStrategy in assembly := {
        case "mime.types" => mimeTypeMerge
        case "META-INF/io.netty.versions.properties" =>
          MergeStrategy.first
        case x => MergeStrategy.defaultMergeStrategy(x)
      }
    )
  }