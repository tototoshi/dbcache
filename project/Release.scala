import sbt._
import Keys._

object Publish {

  private val url = "http://github.com/tototoshi/dbcache"

  private object License {
    val name = "Apache License, Version 2.0"
    val url = "http://www.apache.org/licenses/LICENSE-2.0.html"
    val distribution = "repo"
  }

  private object SCM {
    val url = "git@github.com:tototoshi/dbcache.git"
    val connection = "scm:git:git@github.com:tototoshi/dbcache.git"
  }

  private object Developer {
    val id = "tototoshi"
    val name = "Toshiyuki Takahashi"
    val url = "http://tototoshi.github.io"
  }

  private def _publishTo(v: String) = {
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }

  private val _pomExtra =
    <url>{ url }</url>
    <licenses>
      <license>
        <name>{ License.name }</name>
        <url>{ License.url }</url>
        <distribution>{ License.distribution }</distribution>
      </license>
    </licenses>
    <scm>
      <url>{ SCM.url }</url>
      <connection>{ SCM.connection }</connection>
    </scm>
    <developers>
      <developer>
        <id>{ Developer.id }</id>
        <name>{ Developer.name }</name>
        <url>{ Developer.url }</url>
      </developer>
    </developers>

  val settings = Seq(
    publishMavenStyle := true,
    publishTo <<= version { (v: String) => _publishTo(v) },
    publishArtifact in Test := false,
    pomExtra := _pomExtra
  )

  val nonPublishSettings = Seq(
    publishArtifact := false,
    publish := {},
    publishLocal := {}
  )

}
