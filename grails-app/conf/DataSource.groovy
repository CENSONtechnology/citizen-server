import org.opentele.server.core.model.BootStrapUtil

dataSource {
    pooled = true
    logSql = false
}
hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
//    cache.region.factory_class = 'org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory' // hibernate 4
//    cache.region.factory_class = 'net.sf.ehcache.hibernate.SingletonEhCacheRegionFactory' // hibernate 3
}

// environment specific settings
environments {
    
    datamon_test {
        dataSource {
            pooled = true
            driverClassName = "net.sourceforge.jtds.jdbc.Driver"
            dialect = "org.opentele.server.core.util.SQLServerDialect"
            username = "KIHTest"
            password = ""
//            dbCreate = "update"
            url = "jdbc:jtds:sqlserver://172.18.237.101:60101:kihtest"
        }
    }
    development {
        dataSource {
            username = "sa"
            password = ""
            driverClassName = "org.h2.Driver"
            dialect = "org.opentele.server.core.util.H2Dialect"
            // dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            //url = "jdbc:h2:citizenDb;MVCC=TRUE;IGNORECASE=TRUE"
            // url = "jdbc:h2:mem:devDb;MVCC=TRUE;IGNORECASE=TRUE"

            if(BootStrapUtil.isH2DatabaseServerRunning("jdbc:h2:tcp://localhost:8043/clinicianDb", "sa", "")) {
                url = "jdbc:h2:tcp://localhost:8043/clinicianDb"
            } else {
                url = "jdbc:h2:citizenDb;MVCC=TRUE;IGNORECASE=TRUE"
            }

            println "Using database url: ${url}"
        }
    }
    performance {
        dataSource {
            pooled = true
            dialect = "org.opentele.server.core.util.MySQLInnoDBDialect"
            driverClassName = "com.mysql.jdbc.Driver"
            username = "opentele"
            password = "opentele"
            dbCreate = 'create'
            url = "jdbc:mysql://localhost:3306/opentele"
        }
    }
    // Generic Linux based image tested on CentOS/MySQL. All config is supposed
    // to be done using properties on the target server
    demo {
        dataSource {
            pooled = true
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = "org.opentele.server.core.util.MySQLInnoDBDialect"
            username = "opentele"
            password = "opentele"
            url = "jdbc:mysql://localhost:3306/opentele"
        }
    }
    test {
        dataSource {
//            dbCreate = "create-drop"
            username = "sa"
            password = ""
            driverClassName = "org.h2.Driver"
            dialect = "org.opentele.server.core.util.H2Dialect"
            //url = "jdbc:h2:mem:testDb;MVCC=TRUE;IGNORECASE=TRUE"
            url = "jdbc:h2:citizenDb;MVCC=TRUE;IGNORECASE=TRUE"
        }
    }

    production {
        dataSource {
        }
    }
    nord_production {
        dataSource {
//            dbCreate = "create"
        }
    }
    midt_production {
        dataSource {
//            dbCreate = "create"
        }
    }
    hovedstaden_production {
        dataSource {
//            dbCreate = "create"
        }
    }
    hovedstaden_test {
        dataSource {
//            dbCreate = "create"
        }
    }
    nord_staging {
        dataSource {
//            dbCreate = "create"
        }
    }

    nord_education {
        dataSource {
//            dbCreate = "create"
        }
    }
    midt_staging {
        dataSource {
//            dbCreate = "create"
        }
    }
    hovedstaden_staging {
        dataSource {
//            dbCreate = "create"
        }
    }
}
