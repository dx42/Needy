/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dx42.needy

import org.junit.Test

import org.dx42.needy.report.StubReport

class NeedyRunnerTest extends AbstractTestCase {

    private static final String CONFIG_TEXT = """
        needy {
            applications {
                Sample1(url:"file:src/test/resources/sample1-build.gradle")
                Sample_Two(url:"file:src/test/resources/sample2-build.gradle", type:"gradle", description:"sample", componentId:"c1", properties:[log4jVersion:"1.2.14"])
                Sample_Three(url:"file:src/test/resources/sample2-grails-buildconfig.txt", type:"grails2")
            }
        }"""

    private static final String CONFIG_TEXT_WITH_REPORTS = """
        needy {
            applications {
                Sample1(url:"file:src/test/resources/sample1-build.gradle")
                Sample_Two(url:"file:src/test/resources/sample2-build.gradle", properties:[log4jVersion:"1.2.14"])
                Sample_Three(url:"file:src/test/resources/sample2-grails-buildconfig.txt", type:"grails2")
            }
            reports {
                report("org.dx42.needy.report.StubReport") { }
                report("org.dx42.needy.report.StubReport") {
                    outputFile = "report.txt"
                }
            }
        }"""

    private static final List<Dependency> DEPENDENCIES = [
        new Dependency(applicationName:"Sample1", configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
        new Dependency(applicationName:"Sample1", configuration:"compile", group:"log4j", name:"log4j", version:"1.2.14"),
        new Dependency(applicationName:"Sample1", configuration:"compile", group:"org.gmetrics", name:"GMetrics", version:"0.7"),
        new Dependency(applicationName:"Sample1", configuration:"testCompile", group:"junit", name:"junit", version:"4.8.1"),
        new Dependency(applicationName:"Sample1", configuration:"testCompile", group:"commons-cli", name:"commons-cli", version:"1.2"),

        new Dependency(applicationName:"Sample_Two", configuration:"compile", group:"log4j", name:"log4j", version:"1.2.14"),
        new Dependency(applicationName:"Sample_Two", configuration:"compile", group:"org.codenarc", name:"CodeNarc", version:"0.28"),
        new Dependency(applicationName:"Sample_Two", configuration:"testCompile", group:"junit", name:"junit", version:"4.12"),

        new Dependency(applicationName:"Sample_Three", group:"mysql", name:"mysql-connector-java", version:"5.1.24", configuration:"runtime"),
        new Dependency(applicationName:"Sample_Three", group:"org.springframework.integration", name:"spring-integration-core", version:"2.2.5.RELEASE", configuration:"compile")
    ]

    private NeedyRunner needyRunner = new NeedyRunner()

    @Test
    void test_execute_RequiredPropertiesNotInitialized() {
        shouldFailWithMessage("needyConfiguration") { needyRunner.execute() }
    }

    @Test
    void test_execute_NoReports() {
        def needyConfiguration = DslNeedyConfiguration.fromString(CONFIG_TEXT)
        def applicationBuilds = needyConfiguration.getApplicationBuilds()
        log(applicationBuilds)
        needyRunner.needyConfiguration = needyConfiguration
        def result = needyRunner.execute()

        assert result == DEPENDENCIES
    }

    @Test
    void test_execute_Reports() {
        def needyConfiguration = DslNeedyConfiguration.fromString(CONFIG_TEXT_WITH_REPORTS)
        needyRunner.needyConfiguration = needyConfiguration
        def result = needyRunner.execute()

        assert result == DEPENDENCIES
        def report = needyConfiguration.reports
        assert report.size() == 2
        assert report[0] instanceof StubReport
        assert report[0].dependencies == DEPENDENCIES
        assert report[1] instanceof StubReport
        assert report[1].outputFile == "report.txt"
        assert report[1].dependencies == DEPENDENCIES
    }

}
