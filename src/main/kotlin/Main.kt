import java.io.File

private const val POM_FILE_NAME = "pom.xml"
private const val SET_VERSION_PARAMETER = "-setVersion="

fun main(arguments: Array<String>) {

    if (arguments.size != 1) {
        throw RuntimeException("Required one argument with maven project path")
    }
    val xmlParser = XmlParser()
    val mavenPomVersionFinder = MavenPomVersionFinder()
    val mavenPomVersionUpdater = MavenPomVersionUpdater()

    //    todo Move belowe code to separate class. Then it can be tested easier.
    // todo extract lambda do function
    val versionParameter = arguments.toMutableList().stream().filter { argument -> argument.contains("-setVersion=") }
        .map { argument -> argument.replace(SET_VERSION_PARAMETER, "") }.findAny()

    // fixme Improve readability initialization mavenVersionGenerator
    val mavenVersionGenerator = versionParameter.map { pomFixedVersion -> MavenFixedVersionGenerator(pomFixedVersion) }
        .map { MavenPomNextVersionGenerator() }.orElseThrow()

    val pomFileVersionUpdater =
        PomFileVersionUpdater(xmlParser, mavenPomVersionFinder, mavenVersionGenerator, mavenPomVersionUpdater)

    File(arguments[0]).walk().forEach { pomXmlFile ->
        if (POM_FILE_NAME == pomXmlFile.name) {
            pomFileVersionUpdater.updatePomVersion(pomXmlFile)
        }
    }
}


