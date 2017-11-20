import org.apache.log4j.DailyRollingFileAppender

grails.config.locations = ["file:${userHome}/.opentele/citizen.properties", "file:/etc/opentele/citizen.properties", "file:/etc/opentele/citizen-logging-config.groovy", "file:c:/kihdatamon/settings/datamon-citizen-config.properties"]

logging.suffix = ""

grails.session.timeout.default = 30

grails.project.groupId = appName

grails.mime.file.extensions = true
grails.mime.use.accept.header = true
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
	xml: ['text/xml', 'application/xml'],
	text: 'text/plain',
	js: 'text/javascript',
	rss: 'application/rss+xml',
	atom: 'application/atom+xml',
	css: 'text/css',
	csv: 'text/csv',
	all: '*/*',
	json: ['application/json','text/json'],
	form: 'application/x-www-form-urlencoded',
	multipartForm: 'multipart/form-data'
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']



// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// enable query caching by default
grails.hibernate.cache.queries = false

video {
    enabled = false
}

help {
    image {
        contentTypes = ["image/jpeg","image/pjpeg","image/png","image/x-png"]
        uploadPath = "../helpimages" //In production this path should be configured to a full local path. NOT a path relatvie to web-app!
        providedImagesPath = "helpimg-upload"
        overwriteExistingFiles = true
    }
}

continuousCtg {
    enabled = false
    url = ""
}

defaultLocale = new Locale("da", "DK")

// CORS setup
cors.url.pattern = '*'
cors.expose.headers = 'AccountIsLocked'
cors.headers = ['Access-Control-Allow-Headers': "origin, authorization, accept, content-type, x-requested-with, client-version"]

def pluginDir = org.codehaus.groovy.grails.plugins.GrailsPluginUtils.getPluginDirForName('opentele-server-core-plugin')?.path
grails.plugin.databasemigration.changelogLocation = "$pluginDir/grails-app/migrations"

grails.plugin.databasemigration.dropOnStart = false
grails.plugin.databasemigration.updateOnStart = false

auditLog.sessionAvailable = false

// set per-environment serverURL stem for creating absolute links
environments {
	development {
		grails.logging.jul.usebridge = true

		// TODO : Skal tilpasses for citizen server
		// Support for remote tomcat deployment to tomcat development server
		tomcat.deploy.username="deployer"
		tomcat.deploy.password="iequiemuuc5ooBuo"
		tomcat.deploy.url="http://opentele-devel.silverbullet.dk/manager/text"

        video.enabled = true

        video.serviceURL = 'https://silverbullet.vconf.dk/services/v1_1/VidyoPortalUserService/'
        video.client.serviceURL = 'https://silverbullet.vconf.dk/services/VidyoPortalGuestService/'
        video.client.version = 2

        //video.portalURL = 'http://silverbullet.medisat.dk/'
        //video.serviceURL = 'http://silverbullet.medisat.dk/services/v1_1/VidyoPortalUserService/'
        //video.client.serviceURL = 'http://silverbullet.medisat.dk/services/VidyoPortalGuestService/'
        //video.client.version = 3

        continuousCtg {
            enabled = true
            url = "http://localhost:4000/continuous_ctg/"
        }

    }
    demo {
        languageTag = 'en-US'

        video.enabled = true
        video.serviceURL = 'https://silverbullet.vconf.dk/services/v1_1/VidyoPortalUserService/'
        video.client.serviceURL = 'https://silverbullet.vconf.dk/services/VidyoPortalGuestService/'
        video.client.version = 2

        continuousCtg {
            enabled = true
            url = "http://localhost:4000/continuous_ctg/"
        }
    }
    performance {
    }
    test {
    }
    datamon_test {
        measurement.results.tables.css = 'measurement_results_tables_rn.css'
        grails.app.context = "/"
        grails.logging.jul.usebridge = false
        grails.serverURL = "https://datamon-test.rn.dk/opentele-citizen"

        video.enabled = true
        video.serviceURL = 'https://silverbullet.vconf.dk/services/v1_1/VidyoPortalUserService/'
        video.client.serviceURL = 'https://silverbullet.vconf.dk/services/VidyoPortalGuestService/'
        video.client.version = 2

        running.ctg.messaging.enabled = true
    }
    production {
    }
    nord_production {
        measurement.results.tables.css = 'measurement_results_tables_rn.css'

        grails.app.context = "/"
        grails.logging.jul.usebridge = false
        grails.serverURL = "https://datamon-rn.rn.dk/opentele-citizen"
    }
    midt_production {

        grails.app.context = "/"
        grails.logging.jul.usebridge = false
        grails.serverURL = "https://datamon-rm.rn.dk/opentele-citizen"

        running.ctg.messaging.enabled = true

    }
    hovedstaden_production {
        grails.session.timeout.default = 60

        grails.app.context = "/"
        grails.logging.jul.usebridge = false
        grails.serverURL = "https://datamon-rh.rn.dk/opentele-citizen"

        video.enabled = true
        video.serviceURL = 'https://regionh.vconf.dk/services/v1_1/VidyoPortalUserService/'
        video.client.serviceURL = 'https://regionh.vconf.dk/services/VidyoPortalGuestService/'
        video.client.version = 2

        help {
            image {
                uploadPath = "C:/billeder"
                providedImagesPath = "C:/billeder"
            }
        }
    }

    hovedstaden_test {
        grails.session.timeout.default = 60

        grails.app.context = "/"
        grails.logging.jul.usebridge = false
        grails.serverURL = "https://datamon-tst-rh.rn.dk/opentele-citizen"

        video.enabled = true
        video.serviceURL = 'https://regionh.vconf.dk/services/v1_1/VidyoPortalUserService/'
        video.client.serviceURL = 'https://regionh.vconf.dk/services/VidyoPortalGuestService/'
        video.client.version = 2

        help {
            image {
                uploadPath = "C:/billeder"
                providedImagesPath = "C:/billeder"
            }
        }
    }

    nord_staging {
        measurement.results.tables.css = 'measurement_results_tables_rn.css'

        grails.app.context = "/"
        grails.logging.jul.usebridge = false
        grails.serverURL = "https://datamon-stag-rn.rn.dk/opentele-citizen"
    }
    nord_education {
        measurement.results.tables.css = 'measurement_results_tables_rn.css'

        grails.app.context = "/"
        grails.logging.jul.usebridge = false
        grails.serverURL = "https://datamon-edu-rn.rn.dk"
    }
    midt_staging {

        grails.app.context = "/"
        grails.logging.jul.usebridge = false
        grails.serverURL = "https://datamon-stag-rm.rn.dk/opentele-citizen"
        running.ctg.messaging.enabled = true
    }
    hovedstaden_staging {
        grails.session.timeout.default = 60

        grails.app.context = "/"
        grails.logging.jul.usebridge = false
        grails.serverURL = "https://datamon-stag-rh.rn.dk/opentele-citizen"

        video.enabled = true
        video.serviceURL = 'https://regionh.vconf.dk/services/v1_1/VidyoPortalUserService/'
        video.client.serviceURL = 'https://regionh.vconf.dk/services/VidyoPortalGuestService/'
        video.client.version = 2

        help {
            image {
                uploadPath = "C:/billeder"
                providedImagesPath = "C:/billeder"
            }
        }
    }
}


// Logging
String logDirectory = "${System.getProperty('catalina.base') ?: '.'}/logs"
String commonPattern = "%d [%t] %-5p %c{2} %x - %m%n"
def appContext = { logging.suffix != "" ? "-${logging.suffix}" : "" }

log4j = {
    appenders {
        console name: "stdout",
                layout: pattern(conversionPattern: commonPattern)
        appender new DailyRollingFileAppender(
                name:"stacktrace", datePattern: "'.'yyyy-MM-dd",
                file:"${logDirectory}/stacktrace-citizen${appContext()}.log",
                layout: pattern(conversionPattern: commonPattern))
        appender new DailyRollingFileAppender(
                name:"opentele", datePattern: "'.'yyyy-MM-dd",
                file:"${logDirectory}/opentele-citizen${appContext()}.log",
                layout: pattern(conversionPattern: commonPattern))
        appender new DailyRollingFileAppender(
                name:"statistics", datePattern: "'.'yyyy-MM-dd",
                file:"${logDirectory}/opentele-session-statistics.log",
                layout: pattern(conversionPattern: commonPattern))
    }

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping', // URL mapping
            'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration,
            'org.codehaus.groovy.grails.commons.cfg',
            'org.springframework',
            'org.hibernate',
            'org.apache',
            'net.sf.ehcache.hibernate',
            'grails.app.services.org.grails.plugin.resource',
            'grails.app.taglib.org.grails.plugin.resource',
            'grails.app.resourceMappers.org.grails.plugin.resource',
            'grails.app.service.grails.buildtestdata.BuildTestDataService',
            'grails.app.buildtestdata',
            'grails.app.services.grails.buildtestdata',
            'grails.buildtestdata.DomainInstanceBuilder'

    info additivity: false, statistics: ['grails.app.filters.org.opentele.server.SessionStatisticsFilters']

    root {
        error 'opentele', 'stdout'
    }



    environments {
        development {
            debug 'grails.app',
                    'org.opentele',
                    'grails.app.jobs'
//            debug 'org.hibernate.SQL'
//           trace 'org.hibernate.type'
        }
        performance {
            debug 'grails.app',
                  'org.opentele'
        }
        test {
            debug 'grails.app',
                    'org.opentele'
        }
        datamon_test {
            info    'grails.app',
                    'org.opentele'
        }
        winbuild {
            debug   'grails.app',
                    'org.opentele'
        }
        nord_production {
            info    'grails.app',
                    'org.opentele'
        }
        midt_production {
            info    'grails.app',
                    'org.opentele'
        }
        hovedstaden_production {
            info    'grails.app',
                    'org.opentele'
        }
        nord_staging {
            info    'grails.app',
                    'org.opentele'
        }
        nord_education {
            info    'grails.app',
                    'org.opentele'
        }
        midt_staging {
            info    'grails.app',
                    'org.opentele'
        }
        hovedstaden_staging {
            info    'grails.app',
                    'org.opentele'
        }
        hovedstaden_test {
            info    'grails.app',
                    'org.opentele'
        }
    }
}

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'org.opentele.server.model.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'org.opentele.server.model.UserRole'
grails.plugin.springsecurity.authority.className = 'org.opentele.server.model.Role'
grails.plugin.springsecurity.useBasicAuth = true
grails.plugin.springsecurity.basic.realmName = "OpenTele Server"
grails.plugin.springsecurity.useSecurityEventListener = true
grails.plugin.springsecurity.password.algorithm = 'SHA-256'
grails.plugin.springsecurity.password.hash.iterations = 1

grails.plugin.springsecurity.securityConfigType = 'Annotation'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        '/patient-api.html': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/dbconsole/**': [org.opentele.server.core.model.types.PermissionName.WEB_LOGIN],
]

// nonAuthFilter - See https://jira.silverbullet.dk/browse/KIH-1708
grails.plugin.springsecurity.filterChain.chainMap = [
        '/currentVersion': 'nonAuthFilter',
        '/patient-api.html': 'nonAuthFilter',
        '/isAlive': 'nonAuthFilter',
        '/isAlive/json': 'nonAuthFilter',
        '/isAlive/html': 'nonAuthFilter',
        '/**': 'JOINED_FILTERS,-exceptionTranslationFilter,-sessionManagementFilter,-rememberMeAuthenticationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter'
]
grails.plugin.springsecurity.providerNames = [
        'caseInsensitivePasswordAuthenticationProvider',
        'anonymousAuthenticationProvider',
]

passwordRetryGracePeriod=120
passwordMaxAttempts=3
reminderEveryMinutes=15
