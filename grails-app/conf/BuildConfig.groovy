grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {

    inherits("global") {
    }

    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        mavenLocal()
        mavenCentral()
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenRepo "http://repo1.maven.org/maven2"
        mavenRepo "https://oss.sonatype.org/content/repositories/google-releases"
        mavenRepo "http://repository.codehaus.org"
        mavenRepo "http://repo.springsource.org/libs-milestone/"
        mavenRepo "http://repository.springsource.com/maven/bundles/release"
        mavenRepo "http://repository.springsource.com/maven/bundles/external"
        mavenRepo "http://repository.springsource.com/maven/libraries/release"
        mavenRepo "http://repository.springsource.com/maven/libraries/external"
        mavenRepo "https://repository.sonatype.org/content/groups/forge/"
        mavenRepo "http://mavenrepo.google-api-java-client.googlecode.com/hg"
        mavenRepo "https://oss.sonatype.org/content/repositories/google-releases"
        mavenRepo 'http://repo.spring.io/milestone'
        mavenRepo 'https://oss.sonatype.org/content/repositories/snapshots'
    }

    dependencies {
    }

    plugins {
        build(':release:3.0.1') { export = false }
        build(':rest-client-builder:1.0.3') { export = false }
        // compile ":ps-messaging:0.3.0" { export = false }
    }
}

grails.plugin.location.'ps-messaging' = "../ps-messaging"
